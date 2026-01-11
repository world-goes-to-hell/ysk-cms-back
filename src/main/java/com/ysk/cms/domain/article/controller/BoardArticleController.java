package com.ysk.cms.domain.article.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.article.dto.BoardArticleAnswerRequest;
import com.ysk.cms.domain.article.dto.BoardArticleCreateRequest;
import com.ysk.cms.domain.article.dto.BoardArticleDto;
import com.ysk.cms.domain.article.dto.BoardArticleListDto;
import com.ysk.cms.domain.article.dto.BoardArticleUpdateRequest;
import com.ysk.cms.domain.article.entity.ArticleStatus;
import com.ysk.cms.domain.article.service.BoardArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 관리", description = "게시글 작성, 수정, 삭제, 검색")
@RestController
@RequestMapping("/api/sites/{siteCode}/boards/{boardCode}/articles")
@RequiredArgsConstructor
public class BoardArticleController {

    private final BoardArticleService articleService;

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<BoardArticleListDto>> getArticles(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @RequestParam(required = false) ArticleStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (status != null) {
            return ApiResponse.success(articleService.getArticlesByStatus(siteCode, boardCode, status, pageable));
        }
        return ApiResponse.success(articleService.getArticles(siteCode, boardCode, pageable));
    }

    @Operation(summary = "공지 게시글 목록 조회")
    @GetMapping("/pinned")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<BoardArticleListDto>> getPinnedArticles(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(articleService.getPinnedArticles(siteCode, boardCode, pageable));
    }

    @Operation(summary = "게시글 검색")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<BoardArticleListDto>> searchArticles(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(articleService.searchArticles(siteCode, boardCode, keyword, pageable));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<BoardArticleDto> getArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "false") boolean incrementView) {
        if (incrementView) {
            return ApiResponse.success(articleService.getArticleAndIncrementView(siteCode, boardCode, articleId));
        }
        return ApiResponse.success(articleService.getArticle(siteCode, boardCode, articleId));
    }

    @Operation(summary = "게시글 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<BoardArticleDto> createArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @Valid @RequestBody BoardArticleCreateRequest request) {
        return ApiResponse.success(articleService.createArticle(siteCode, boardCode, request));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<BoardArticleDto> updateArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @Valid @RequestBody BoardArticleUpdateRequest request) {
        return ApiResponse.success(articleService.updateArticle(siteCode, boardCode, articleId, request));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<Void> deleteArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId) {
        articleService.deleteArticle(siteCode, boardCode, articleId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "게시글 발행")
    @PatchMapping("/{articleId}/publish")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<BoardArticleDto> publishArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId) {
        return ApiResponse.success(articleService.publishArticle(siteCode, boardCode, articleId));
    }

    @Operation(summary = "게시글 답변 등록/수정 (Q&A용)")
    @PutMapping("/{articleId}/answer")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<BoardArticleDto> answerArticle(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @RequestBody BoardArticleAnswerRequest request) {
        return ApiResponse.success(articleService.answerArticle(siteCode, boardCode, articleId, request));
    }
}
