package com.ysk.cms.domain.site.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.site.dto.SiteCreateRequest;
import com.ysk.cms.domain.site.dto.SiteDto;
import com.ysk.cms.domain.site.dto.SiteUpdateRequest;
import com.ysk.cms.domain.site.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사이트 관리", description = "멀티 사이트 생성, 수정, 삭제")
@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @Operation(summary = "사이트 목록 조회", description = "페이지네이션된 사이트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SiteDto>>> findAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SiteDto> sites = siteService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(sites));
    }

    @Operation(summary = "사이트 전체 목록 조회", description = "페이지네이션 없이 전체 사이트 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<SiteDto>>> findAllList() {
        List<SiteDto> sites = siteService.findAllList();
        return ResponseEntity.ok(ApiResponse.success(sites));
    }

    @Operation(summary = "사이트 상세 조회", description = "사이트 코드로 사이트 상세 정보를 조회합니다.")
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<SiteDto>> findByCode(@PathVariable String code) {
        SiteDto site = siteService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success(site));
    }

    @Operation(summary = "사이트 생성", description = "새로운 사이트를 생성합니다.")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SiteDto>> create(@Valid @RequestBody SiteCreateRequest request) {
        SiteDto site = siteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("사이트가 생성되었습니다.", site));
    }

    @Operation(summary = "사이트 수정", description = "사이트 정보를 수정합니다.")
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN')")
    public ResponseEntity<ApiResponse<SiteDto>> update(
            @PathVariable String code,
            @Valid @RequestBody SiteUpdateRequest request) {
        SiteDto site = siteService.update(code, request);
        return ResponseEntity.ok(ApiResponse.success("사이트가 수정되었습니다.", site));
    }

    @Operation(summary = "사이트 삭제", description = "사이트를 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String code) {
        siteService.delete(code);
        return ResponseEntity.ok(ApiResponse.success("사이트가 삭제되었습니다."));
    }
}
