package com.ysk.cms.domain.article.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.atchfile.entity.AtchFile;
import com.ysk.cms.domain.atchfile.repository.AtchFileRepository;
import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.repository.BoardRepository;
import com.ysk.cms.domain.article.dto.*;
import com.ysk.cms.domain.article.entity.BoardArticle;
import com.ysk.cms.domain.article.entity.ArticleStatus;
import com.ysk.cms.domain.article.repository.BoardArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardArticleService {

    private final BoardArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final AtchFileRepository atchFileRepository;

    public PageResponse<BoardArticleListDto> getArticles(String siteCode, String boardCode, Pageable pageable) {
        Page<BoardArticle> articlePage = articleRepository.findBySiteCodeAndBoardCode(siteCode, boardCode, pageable);
        return PageResponse.of(articlePage.map(BoardArticleListDto::from));
    }

    public PageResponse<BoardArticleListDto> getArticlesByStatus(String siteCode, String boardCode, ArticleStatus status, Pageable pageable) {
        Page<BoardArticle> articlePage = articleRepository.findBySiteCodeAndBoardCodeAndStatus(siteCode, boardCode, status, pageable);
        return PageResponse.of(articlePage.map(BoardArticleListDto::from));
    }

    public PageResponse<BoardArticleListDto> getPinnedArticles(String siteCode, String boardCode, Pageable pageable) {
        Page<BoardArticle> articlePage = articleRepository.findPinnedArticles(siteCode, boardCode, pageable);
        return PageResponse.of(articlePage.map(BoardArticleListDto::from));
    }

    public PageResponse<BoardArticleListDto> searchArticles(String siteCode, String boardCode, String keyword, Pageable pageable) {
        Page<BoardArticle> articlePage = articleRepository.searchByKeyword(siteCode, boardCode, keyword, pageable);
        return PageResponse.of(articlePage.map(BoardArticleListDto::from));
    }

    public BoardArticleDto getArticle(String siteCode, String boardCode, Long articleId) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        List<AtchFile> attachments = atchFileRepository.findByArticleId(articleId);
        return BoardArticleDto.from(article, attachments);
    }

    @Transactional
    public BoardArticleDto getArticleAndIncrementView(String siteCode, String boardCode, Long articleId) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        article.incrementViewCount();
        List<AtchFile> attachments = atchFileRepository.findByArticleId(articleId);
        return BoardArticleDto.from(article, attachments);
    }

    @Transactional
    public BoardArticleDto createArticle(String siteCode, String boardCode, BoardArticleCreateRequest request) {
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        BoardArticle article = BoardArticle.builder()
                .board(board)
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .isPinned(request.getIsPinned())
                .isSecret(request.getIsSecret())
                .status(request.getStatus())
                .build();

        if (request.getStatus() == ArticleStatus.PUBLISHED) {
            article.publish();
        }

        BoardArticle savedArticle = articleRepository.save(article);

        // 첨부파일 연결
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            linkAttachments(savedArticle, request.getAttachmentIds());
        }

        List<AtchFile> attachments = atchFileRepository.findByArticleId(savedArticle.getId());
        return BoardArticleDto.from(savedArticle, attachments);
    }

    @Transactional
    public BoardArticleDto updateArticle(String siteCode, String boardCode, Long articleId, BoardArticleUpdateRequest request) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        article.update(
                request.getTitle(),
                request.getContent(),
                request.getAuthor(),
                request.getIsPinned(),
                request.getIsSecret(),
                request.getStatus(),
                request.getAnswer()
        );

        // 첨부파일 연결 업데이트
        if (request.getAttachmentIds() != null) {
            // 기존 첨부파일 연결 해제
            List<AtchFile> existingFiles = atchFileRepository.findByArticleId(articleId);
            for (AtchFile file : existingFiles) {
                file.setArticle(null);
            }
            // 새 첨부파일 연결
            if (!request.getAttachmentIds().isEmpty()) {
                linkAttachments(article, request.getAttachmentIds());
            }
        }

        List<AtchFile> attachments = atchFileRepository.findByArticleId(articleId);
        return BoardArticleDto.from(article, attachments);
    }

    private void linkAttachments(BoardArticle article, List<Long> attachmentIds) {
        List<AtchFile> files = atchFileRepository.findAllById(attachmentIds);
        for (AtchFile file : files) {
            file.setArticle(article);
        }
    }

    @Transactional
    public void deleteArticle(String siteCode, String boardCode, Long articleId) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        article.delete();
    }

    @Transactional
    public BoardArticleDto publishArticle(String siteCode, String boardCode, Long articleId) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        article.publish();
        return BoardArticleDto.from(article);
    }

    @Transactional
    public BoardArticleDto answerArticle(String siteCode, String boardCode, Long articleId, BoardArticleAnswerRequest request) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        article.updateAnswer(request.getAnswer());
        return BoardArticleDto.from(article);
    }
}
