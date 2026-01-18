package com.ysk.cms.domain.admin.reply.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.admin.reply.dto.BoardArticleReplyCreateRequest;
import com.ysk.cms.domain.admin.reply.dto.BoardArticleReplyDto;
import com.ysk.cms.domain.admin.reply.dto.BoardArticleReplyUpdateRequest;
import com.ysk.cms.domain.admin.reply.service.BoardArticleReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시글 댓글 관리", description = "게시글 댓글 작성, 수정, 삭제")
@RestController
@RequestMapping("/api/sites/{siteCode}/boards/{boardCode}/articles/{articleId}/replies")
@RequiredArgsConstructor
public class BoardArticleReplyController {

    private final BoardArticleReplyService replyService;

    @Operation(summary = "댓글 목록 조회 (트리 구조)")
    @GetMapping
    public ApiResponse<List<BoardArticleReplyDto>> getReplies(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId) {
        return ApiResponse.success(replyService.getReplies(siteCode, boardCode, articleId));
    }

    @Operation(summary = "댓글 목록 조회 (페이징)")
    @GetMapping("/paged")
    public ApiResponse<PageResponse<BoardArticleReplyDto>> getRepliesPaged(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "true") boolean flat,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        if (flat) {
            return ApiResponse.success(replyService.getRepliesPagedFlat(siteCode, boardCode, articleId, pageable));
        }
        return ApiResponse.success(replyService.getRepliesPaged(siteCode, boardCode, articleId, pageable));
    }

    @Operation(summary = "댓글 상세 조회")
    @GetMapping("/{replyId}")
    public ApiResponse<BoardArticleReplyDto> getReply(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @PathVariable Long replyId) {
        return ApiResponse.success(replyService.getReply(siteCode, boardCode, articleId, replyId));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BoardArticleReplyDto> createReply(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @Valid @RequestBody BoardArticleReplyCreateRequest request) {
        return ApiResponse.success(replyService.createReply(siteCode, boardCode, articleId, request));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{replyId}")
    public ApiResponse<BoardArticleReplyDto> updateReply(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @PathVariable Long replyId,
            @Valid @RequestBody BoardArticleReplyUpdateRequest request) {
        return ApiResponse.success(replyService.updateReply(siteCode, boardCode, articleId, replyId, request));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteReply(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long articleId,
            @PathVariable Long replyId) {
        replyService.deleteReply(siteCode, boardCode, articleId, replyId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "댓글 수 조회")
    @GetMapping("/count")
    public ApiResponse<Long> getReplyCount(@PathVariable Long articleId) {
        return ApiResponse.success(replyService.getReplyCount(articleId));
    }
}
