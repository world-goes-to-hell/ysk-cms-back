package com.ysk.cms.domain.atchfile.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.atchfile.dto.*;
import com.ysk.cms.domain.atchfile.service.AtchFileService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Tag(name = "첨부파일 관리", description = "파일 업로드, 다운로드, 삭제")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AtchFileController {

    private final AtchFileService atchFileService;

    @Operation(summary = "전체 첨부파일 목록 조회")
    @GetMapping("/atch-files")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ApiResponse<PageResponse<AtchFileListDto>> getAllFiles(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(atchFileService.getFileList(pageable));
    }

    @Operation(summary = "사이트별 첨부파일 목록 조회")
    @GetMapping("/sites/{siteCode}/atch-files")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<AtchFileListDto>> getFilesBySite(
            @PathVariable String siteCode,
            @RequestParam(required = false) com.ysk.cms.domain.atchfile.entity.AtchFileType fileType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (fileType != null) {
            return ApiResponse.success(atchFileService.getFileListBySiteAndType(siteCode, fileType, pageable));
        }
        return ApiResponse.success(atchFileService.getFileListBySite(siteCode, pageable));
    }

    @Operation(summary = "첨부파일 검색")
    @GetMapping("/sites/{siteCode}/atch-files/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<AtchFileListDto>> searchFiles(
            @PathVariable String siteCode,
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(atchFileService.searchFiles(siteCode, keyword, pageable));
    }

    @Operation(summary = "첨부파일 상세 조회")
    @GetMapping("/atch-files/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<AtchFileDto> getFile(@PathVariable Long id) {
        return ApiResponse.success(atchFileService.getFile(id));
    }

    @Operation(summary = "사이트별 첨부파일 상세 조회")
    @GetMapping("/sites/{siteCode}/atch-files/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<AtchFileDto> getFileBySite(
            @PathVariable String siteCode,
            @PathVariable Long id) {
        return ApiResponse.success(atchFileService.getFileBySite(siteCode, id));
    }

    @Operation(summary = "첨부파일 업로드 (사이트 지정)")
    @PostMapping(value = "/sites/{siteCode}/atch-files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<AtchFileDto> uploadFile(
            @PathVariable String siteCode,
            @RequestParam("file") MultipartFile file,
            @Valid AtchFileUploadRequest request) {
        return ApiResponse.success(atchFileService.uploadFile(siteCode, file, request));
    }

    @Operation(summary = "첨부파일 업로드 (공용)")
    @PostMapping(value = "/atch-files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ApiResponse<AtchFileDto> uploadCommonFile(
            @RequestParam("file") MultipartFile file,
            @Valid AtchFileUploadRequest request) {
        return ApiResponse.success(atchFileService.uploadFile(null, file, request));
    }

    @Operation(summary = "다중 첨부파일 업로드")
    @PostMapping(value = "/sites/{siteCode}/atch-files/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<List<AtchFileDto>> uploadMultipleFiles(
            @PathVariable String siteCode,
            @RequestParam("files") List<MultipartFile> files,
            @Valid AtchFileUploadRequest request) {
        return ApiResponse.success(atchFileService.uploadMultipleFiles(siteCode, files, request));
    }

    @Operation(summary = "첨부파일 정보 수정")
    @PutMapping("/atch-files/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR')")
    public ApiResponse<AtchFileDto> updateFile(
            @PathVariable Long id,
            @Valid @RequestBody AtchFileUpdateRequest request) {
        return ApiResponse.success(atchFileService.updateFile(id, request));
    }

    @Operation(summary = "첨부파일 삭제")
    @DeleteMapping("/atch-files/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        atchFileService.deleteFile(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "첨부파일 다운로드")
    @GetMapping("/atch-files/{id}/download")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        AtchFileDto file = atchFileService.getFile(id);
        InputStream inputStream = atchFileService.downloadFile(id);

        String encodedFilename = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(new InputStreamResource(inputStream));
    }

    @Operation(summary = "첨부파일 Presigned URL 조회")
    @GetMapping("/atch-files/{id}/presigned-url")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<Map<String, String>> getPresignedUrl(@PathVariable Long id) {
        String url = atchFileService.getPresignedUrl(id);
        return ApiResponse.success(Map.of("url", url));
    }

    @Operation(summary = "첨부파일 공개 조회 (이미지 미리보기용)")
    @GetMapping("/public/files/{id}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long id) {
        AtchFileDto file = atchFileService.getFile(id);
        InputStream inputStream = atchFileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .cacheControl(org.springframework.http.CacheControl.maxAge(7, java.util.concurrent.TimeUnit.DAYS))
                .body(new InputStreamResource(inputStream));
    }
}
