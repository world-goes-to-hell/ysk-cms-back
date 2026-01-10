package com.ysk.cms.domain.post.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.post.dto.PostCreateRequest;
import com.ysk.cms.domain.post.dto.PostDto;
import com.ysk.cms.domain.post.dto.PostListDto;
import com.ysk.cms.domain.post.dto.PostUpdateRequest;
import com.ysk.cms.domain.post.entity.PostStatus;
import com.ysk.cms.domain.post.service.PostService;
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
@RequestMapping("/api/sites/{siteCode}/boards/{boardCode}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<PostListDto>> getPosts(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @RequestParam(required = false) PostStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (status != null) {
            return ApiResponse.success(postService.getPostsByStatus(siteCode, boardCode, status, pageable));
        }
        return ApiResponse.success(postService.getPosts(siteCode, boardCode, pageable));
    }

    @Operation(summary = "공지 게시글 목록 조회")
    @GetMapping("/pinned")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<PostListDto>> getPinnedPosts(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(postService.getPinnedPosts(siteCode, boardCode, pageable));
    }

    @Operation(summary = "게시글 검색")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<PostListDto>> searchPosts(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(postService.searchPosts(siteCode, boardCode, keyword, pageable));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PostDto> getPost(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "false") boolean incrementView) {
        if (incrementView) {
            return ApiResponse.success(postService.getPostAndIncrementView(siteCode, boardCode, postId));
        }
        return ApiResponse.success(postService.getPost(siteCode, boardCode, postId));
    }

    @Operation(summary = "게시글 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<PostDto> createPost(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @Valid @RequestBody PostCreateRequest request) {
        return ApiResponse.success(postService.createPost(siteCode, boardCode, request));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<PostDto> updatePost(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request) {
        return ApiResponse.success(postService.updatePost(siteCode, boardCode, postId, request));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<Void> deletePost(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long postId) {
        postService.deletePost(siteCode, boardCode, postId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "게시글 발행")
    @PatchMapping("/{postId}/publish")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<PostDto> publishPost(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @PathVariable Long postId) {
        return ApiResponse.success(postService.publishPost(siteCode, boardCode, postId));
    }
}
