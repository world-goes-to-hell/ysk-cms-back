package com.ysk.cms.domain.page.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.page.dto.PageCreateRequest;
import com.ysk.cms.domain.page.dto.PageDto;
import com.ysk.cms.domain.page.dto.PageListDto;
import com.ysk.cms.domain.page.dto.PageUpdateRequest;
import com.ysk.cms.domain.page.service.PageService;
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

@Tag(name = "페이지 관리", description = "정적 페이지 생성, 수정, 삭제, 계층 구조")
@RestController
@RequestMapping("/api/sites/{siteCode}/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @Operation(summary = "페이지 목록 조회")
    @GetMapping
    public ApiResponse<PageResponse<PageListDto>> getPages(
            @PathVariable String siteCode,
            @PageableDefault(size = 20, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.success(pageService.getPagesBySite(siteCode, pageable));
    }

    @Operation(summary = "루트 페이지 목록 조회 (트리 구조)")
    @GetMapping("/tree")
    public ApiResponse<List<PageDto>> getPageTree(@PathVariable String siteCode) {
        return ApiResponse.success(pageService.getRootPages(siteCode));
    }

    @Operation(summary = "발행된 페이지 목록 조회")
    @GetMapping("/published")
    public ApiResponse<List<PageListDto>> getPublishedPages(@PathVariable String siteCode) {
        return ApiResponse.success(pageService.getPublishedPages(siteCode));
    }

    @Operation(summary = "페이지 상세 조회")
    @GetMapping("/{pageId}")
    public ApiResponse<PageDto> getPage(
            @PathVariable String siteCode,
            @PathVariable Long pageId) {
        return ApiResponse.success(pageService.getPage(siteCode, pageId));
    }

    @Operation(summary = "슬러그로 페이지 조회")
    @GetMapping("/slug/{slug}")
    public ApiResponse<PageDto> getPageBySlug(
            @PathVariable String siteCode,
            @PathVariable String slug) {
        return ApiResponse.success(pageService.getPageBySlug(siteCode, slug));
    }

    @Operation(summary = "하위 페이지 목록 조회")
    @GetMapping("/{parentId}/children")
    public ApiResponse<List<PageListDto>> getChildPages(
            @PathVariable String siteCode,
            @PathVariable Long parentId) {
        return ApiResponse.success(pageService.getChildPages(siteCode, parentId));
    }

    @Operation(summary = "페이지 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PageDto> createPage(
            @PathVariable String siteCode,
            @Valid @RequestBody PageCreateRequest request) {
        return ApiResponse.success(pageService.createPage(siteCode, request));
    }

    @Operation(summary = "페이지 수정")
    @PutMapping("/{pageId}")
    public ApiResponse<PageDto> updatePage(
            @PathVariable String siteCode,
            @PathVariable Long pageId,
            @Valid @RequestBody PageUpdateRequest request) {
        return ApiResponse.success(pageService.updatePage(siteCode, pageId, request));
    }

    @Operation(summary = "페이지 삭제")
    @DeleteMapping("/{pageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deletePage(
            @PathVariable String siteCode,
            @PathVariable Long pageId) {
        pageService.deletePage(siteCode, pageId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "페이지 발행")
    @PatchMapping("/{pageId}/publish")
    public ApiResponse<PageDto> publishPage(
            @PathVariable String siteCode,
            @PathVariable Long pageId) {
        return ApiResponse.success(pageService.publishPage(siteCode, pageId));
    }
}
