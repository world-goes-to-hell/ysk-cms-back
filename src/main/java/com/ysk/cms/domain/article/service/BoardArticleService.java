package com.ysk.cms.domain.article.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardArticleService {

    private final BoardArticleRepository articleRepository;
    private final BoardRepository boardRepository;

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
        return BoardArticleDto.from(article);
    }

    @Transactional
    public BoardArticleDto getArticleAndIncrementView(String siteCode, String boardCode, Long articleId) {
        BoardArticle article = articleRepository.findByIdAndSiteCodeAndBoardCode(articleId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        article.incrementViewCount();
        return BoardArticleDto.from(article);
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
        return BoardArticleDto.from(savedArticle);
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

        return BoardArticleDto.from(article);
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
