package com.ysk.cms.domain.media.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.media.dto.*;
import com.ysk.cms.domain.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "미디어 관리", description = "파일 업로드, 다운로드, 삭제")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "전체 미디어 목록 조회")
    @GetMapping("/media")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ApiResponse<PageResponse<MediaListDto>> getAllMedia(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(mediaService.getMediaList(pageable));
    }

    @Operation(summary = "사이트별 미디어 목록 조회")
    @GetMapping("/sites/{siteCode}/media")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<MediaListDto>> getMediaBySite(
            @PathVariable String siteCode,
            @RequestParam(required = false) com.ysk.cms.domain.media.entity.MediaType mediaType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (mediaType != null) {
            return ApiResponse.success(mediaService.getMediaListBySiteAndType(siteCode, mediaType, pageable));
        }
        return ApiResponse.success(mediaService.getMediaListBySite(siteCode, pageable));
    }

    @Operation(summary = "미디어 검색")
    @GetMapping("/sites/{siteCode}/media/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<MediaListDto>> searchMedia(
            @PathVariable String siteCode,
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(mediaService.searchMedia(siteCode, keyword, pageable));
    }

    @Operation(summary = "미디어 상세 조회")
    @GetMapping("/media/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<MediaDto> getMedia(@PathVariable Long id) {
        return ApiResponse.success(mediaService.getMedia(id));
    }

    @Operation(summary = "사이트별 미디어 상세 조회")
    @GetMapping("/sites/{siteCode}/media/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<MediaDto> getMediaBySite(
            @PathVariable String siteCode,
            @PathVariable Long id) {
        return ApiResponse.success(mediaService.getMediaBySite(siteCode, id));
    }

    @Operation(summary = "미디어 업로드 (사이트 지정)")
    @PostMapping(value = "/sites/{siteCode}/media/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<MediaDto> uploadMedia(
            @PathVariable String siteCode,
            @RequestParam("file") MultipartFile file,
            @Valid MediaUploadRequest request) {
        return ApiResponse.success(mediaService.uploadMedia(siteCode, file, request));
    }

    @Operation(summary = "미디어 업로드 (공용)")
    @PostMapping(value = "/media/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ApiResponse<MediaDto> uploadCommonMedia(
            @RequestParam("file") MultipartFile file,
            @Valid MediaUploadRequest request) {
        return ApiResponse.success(mediaService.uploadMedia(null, file, request));
    }

    @Operation(summary = "다중 미디어 업로드")
    @PostMapping(value = "/sites/{siteCode}/media/upload-multiple", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<List<MediaDto>> uploadMultipleMedia(
            @PathVariable String siteCode,
            @RequestParam("files") List<MultipartFile> files,
            @Valid MediaUploadRequest request) {
        return ApiResponse.success(mediaService.uploadMultipleMedia(siteCode, files, request));
    }

    @Operation(summary = "미디어 정보 수정")
    @PutMapping("/media/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<MediaDto> updateMedia(
            @PathVariable Long id,
            @Valid @RequestBody MediaUpdateRequest request) {
        return ApiResponse.success(mediaService.updateMedia(id, request));
    }

    @Operation(summary = "미디어 삭제")
    @DeleteMapping("/media/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    public ApiResponse<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "미디어 다운로드")
    @GetMapping("/media/{id}/download")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Resource> downloadMedia(@PathVariable Long id) {
        MediaDto media = mediaService.getMedia(id);
        InputStream inputStream = mediaService.downloadMedia(id);

        String encodedFilename = URLEncoder.encode(media.getOriginalName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(org.springframework.http.MediaType.parseMediaType(media.getMimeType()))
                .body(new InputStreamResource(inputStream));
    }

    @Operation(summary = "미디어 Presigned URL 조회")
    @GetMapping("/media/{id}/presigned-url")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<java.util.Map<String, String>> getPresignedUrl(@PathVariable Long id) {
        String url = mediaService.getPresignedUrl(id);
        return ApiResponse.success(java.util.Map.of("url", url));
    }
}
