package com.ysk.cms.domain.reply.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.article.entity.BoardArticle;
import com.ysk.cms.domain.article.repository.BoardArticleRepository;
import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.repository.BoardRepository;
import com.ysk.cms.domain.reply.dto.BoardArticleReplyCreateRequest;
import com.ysk.cms.domain.reply.dto.BoardArticleReplyDto;
import com.ysk.cms.domain.reply.dto.BoardArticleReplyUpdateRequest;
import com.ysk.cms.domain.reply.entity.BoardArticleReply;
import com.ysk.cms.domain.reply.repository.BoardArticleReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardArticleReplyService {

    private final BoardArticleReplyRepository replyRepository;
    private final BoardArticleRepository articleRepository;
    private final BoardRepository boardRepository;

    /**
     * 게시글의 댓글 목록 조회 (트리 구조)
     */
    public List<BoardArticleReplyDto> getReplies(String siteCode, String boardCode, Long articleId) {
        // 게시글 존재 확인
        articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        List<BoardArticleReply> replies = replyRepository.findByArticleIdAndParentIsNull(articleId);
        return replies.stream()
                .map(BoardArticleReplyDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글의 댓글 목록 조회 (페이징 - 부모 댓글만)
     */
    public PageResponse<BoardArticleReplyDto> getRepliesPaged(String siteCode, String boardCode, Long articleId, Pageable pageable) {
        // 게시글 존재 확인
        articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        Page<BoardArticleReply> replyPage = replyRepository.findByArticleIdAndParentIsNull(articleId, pageable);
        return PageResponse.of(replyPage.map(BoardArticleReplyDto::from));
    }

    /**
     * 게시글의 댓글 목록 조회 (플랫 페이징 - 전체 댓글)
     */
    public PageResponse<BoardArticleReplyDto> getRepliesPagedFlat(String siteCode, String boardCode, Long articleId, Pageable pageable) {
        // 게시글 존재 확인
        articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        Page<BoardArticleReply> replyPage = replyRepository.findAllByArticleIdFlat(articleId, pageable);
        return PageResponse.of(replyPage.map(BoardArticleReplyDto::fromFlat));
    }

    /**
     * 댓글 상세 조회
     */
    public BoardArticleReplyDto getReply(String siteCode, String boardCode, Long articleId, Long replyId) {
        BoardArticleReply reply = replyRepository.findBySiteCodeAndBoardCodeAndArticleIdAndReplyId(
                        siteCode, boardCode, articleId, replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPLY_NOT_FOUND));
        return BoardArticleReplyDto.from(reply);
    }

    /**
     * 댓글 생성
     */
    @Transactional
    public BoardArticleReplyDto createReply(String siteCode, String boardCode, Long articleId, BoardArticleReplyCreateRequest request) {
        // 게시판의 댓글 기능 활성화 여부 확인
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (!Boolean.TRUE.equals(board.getUseComment())) {
            throw new BusinessException(ErrorCode.COMMENT_DISABLED);
        }

        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        BoardArticleReply parent = null;
        if (request.getParentId() != null) {
            parent = replyRepository.findByIdAndArticleId(request.getParentId(), articleId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_REPLY_NOT_FOUND));
        }

        BoardArticleReply reply = BoardArticleReply.builder()
                .article(article)
                .parent(parent)
                .content(request.getContent())
                .author(request.getAuthor())
                .isSecret(request.getIsSecret())
                .build();

        // depth 계산 및 설정
        reply.calculateAndSetDepth();

        // 먼저 저장하여 ID 생성
        BoardArticleReply savedReply = replyRepository.save(reply);

        // path 계산 및 설정 (ID가 필요하므로 저장 후 수행)
        savedReply.calculateAndSetPath();
        savedReply = replyRepository.save(savedReply);

        return BoardArticleReplyDto.from(savedReply);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public BoardArticleReplyDto updateReply(String siteCode, String boardCode, Long articleId, Long replyId, BoardArticleReplyUpdateRequest request) {
        BoardArticleReply reply = replyRepository.findBySiteCodeAndBoardCodeAndArticleIdAndReplyId(
                        siteCode, boardCode, articleId, replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPLY_NOT_FOUND));

        reply.update(request.getContent(), request.getIsSecret());
        return BoardArticleReplyDto.from(reply);
    }

    /**
     * 댓글 삭제
     * - 자식 댓글이 있는 경우: soft delete (내용 삭제 후 "삭제된 댓글" 표시)
     * - 자식 댓글이 없는 경우: 실제 삭제
     */
    @Transactional
    public void deleteReply(String siteCode, String boardCode, Long articleId, Long replyId) {
        BoardArticleReply reply = replyRepository.findBySiteCodeAndBoardCodeAndArticleIdAndReplyId(
                        siteCode, boardCode, articleId, replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPLY_NOT_FOUND));

        if (reply.hasChildren()) {
            // 자식 댓글이 있으면 soft delete
            reply.softDelete();
        } else {
            // 자식 댓글이 없으면 실제 삭제
            replyRepository.delete(reply);
            // 부모 댓글이 삭제된 상태이고 더 이상 자식이 없으면 부모도 삭제
            cleanupDeletedParent(reply.getParent());
        }
    }

    /**
     * 삭제된 부모 댓글 정리
     * 부모가 이미 삭제 상태이고 더 이상 자식이 없으면 실제 삭제
     */
    private void cleanupDeletedParent(BoardArticleReply parent) {
        if (parent != null && parent.getDeleted() && !parent.hasChildren()) {
            BoardArticleReply grandParent = parent.getParent();
            replyRepository.delete(parent);
            // 재귀적으로 상위 부모도 확인
            cleanupDeletedParent(grandParent);
        }
    }

    /**
     * 게시글의 댓글 수 조회
     */
    public Long getReplyCount(Long articleId) {
        return replyRepository.countByArticleId(articleId);
    }
}
