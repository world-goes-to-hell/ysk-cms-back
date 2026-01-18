package com.ysk.cms.domain.admin.contents.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.admin.contents.dto.ContentsCreateRequest;
import com.ysk.cms.domain.admin.contents.dto.ContentsDto;
import com.ysk.cms.domain.admin.contents.dto.ContentsListDto;
import com.ysk.cms.domain.admin.contents.dto.ContentsUpdateRequest;
import com.ysk.cms.domain.admin.contents.service.ContentsService;
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

@Tag(name = "컨텐츠 관리", description = "정적 페이지(컨텐츠) 생성, 수정, 삭제, 계층 구조")
@RestController
@RequestMapping("/api/sites/{siteCode}/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    @Operation(summary = "컨텐츠 목록 조회")
    @GetMapping
    public ApiResponse<PageResponse<ContentsListDto>> getContents(
            @PathVariable String siteCode,
            @PageableDefault(size = 20, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.success(contentsService.getContentsBySite(siteCode, pageable));
    }

    @Operation(summary = "루트 컨텐츠 목록 조회 (트리 구조)")
    @GetMapping("/tree")
    public ApiResponse<List<ContentsDto>> getContentsTree(@PathVariable String siteCode) {
        return ApiResponse.success(contentsService.getRootContents(siteCode));
    }

    @Operation(summary = "발행된 컨텐츠 목록 조회")
    @GetMapping("/published")
    public ApiResponse<List<ContentsListDto>> getPublishedContents(@PathVariable String siteCode) {
        return ApiResponse.success(contentsService.getPublishedContents(siteCode));
    }

    @Operation(summary = "컨텐츠 상세 조회")
    @GetMapping("/{contentsId}")
    public ApiResponse<ContentsDto> getContents(
            @PathVariable String siteCode,
            @PathVariable Long contentsId) {
        return ApiResponse.success(contentsService.getContents(siteCode, contentsId));
    }

    @Operation(summary = "슬러그로 컨텐츠 조회")
    @GetMapping("/slug/{slug}")
    public ApiResponse<ContentsDto> getContentsBySlug(
            @PathVariable String siteCode,
            @PathVariable String slug) {
        return ApiResponse.success(contentsService.getContentsBySlug(siteCode, slug));
    }

    @Operation(summary = "하위 컨텐츠 목록 조회")
    @GetMapping("/{parentId}/children")
    public ApiResponse<List<ContentsListDto>> getChildContents(
            @PathVariable String siteCode,
            @PathVariable Long parentId) {
        return ApiResponse.success(contentsService.getChildContents(siteCode, parentId));
    }

    @Operation(summary = "컨텐츠 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContentsDto> createContents(
            @PathVariable String siteCode,
            @Valid @RequestBody ContentsCreateRequest request) {
        return ApiResponse.success(contentsService.createContents(siteCode, request));
    }

    @Operation(summary = "컨텐츠 수정")
    @PutMapping("/{contentsId}")
    public ApiResponse<ContentsDto> updateContents(
            @PathVariable String siteCode,
            @PathVariable Long contentsId,
            @Valid @RequestBody ContentsUpdateRequest request) {
        return ApiResponse.success(contentsService.updateContents(siteCode, contentsId, request));
    }

    @Operation(summary = "컨텐츠 삭제")
    @DeleteMapping("/{contentsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteContents(
            @PathVariable String siteCode,
            @PathVariable Long contentsId) {
        contentsService.deleteContents(siteCode, contentsId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "컨텐츠 발행")
    @PatchMapping("/{contentsId}/publish")
    public ApiResponse<ContentsDto> publishContents(
            @PathVariable String siteCode,
            @PathVariable Long contentsId) {
        return ApiResponse.success(contentsService.publishContents(siteCode, contentsId));
    }
}
