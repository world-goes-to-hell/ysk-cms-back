package com.ysk.cms.domain.public_api.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.admin.site.dto.SiteSimpleDto;
import com.ysk.cms.domain.admin.site.entity.SiteStatus;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import com.ysk.cms.security.annotation.SkipMenuAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공개 사이트 API (인증 불필요)
 */
@Tag(name = "공개 API - 사이트", description = "로그인 전 사이트 목록 조회")
@RestController
@RequestMapping("/api/public/sites")
@RequiredArgsConstructor
@SkipMenuAuth
public class PublicSiteController {

    private final SiteRepository siteRepository;

    @Operation(summary = "활성 사이트 목록", description = "로그인 가능한 활성 사이트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SiteSimpleDto>>> getActiveSites() {
        List<SiteSimpleDto> sites = siteRepository.findAllByStatus(SiteStatus.ACTIVE)
                .stream()
                .map(SiteSimpleDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(sites));
    }
}
