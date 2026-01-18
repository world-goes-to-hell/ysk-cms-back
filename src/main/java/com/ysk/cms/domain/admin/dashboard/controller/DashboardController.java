package com.ysk.cms.domain.admin.dashboard.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.admin.dashboard.dto.DashboardResponseDto;
import com.ysk.cms.domain.admin.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대시보드", description = "대시보드 통계 및 현황 API")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "대시보드 데이터 조회", description = "통계, 최신 게시글, 최근 활동 등 대시보드에 필요한 전체 데이터를 조회합니다.")
    @GetMapping
    public ApiResponse<DashboardResponseDto> getDashboard() {
        return ApiResponse.success(dashboardService.getDashboardData());
    }

    @Operation(summary = "사이트별 대시보드 데이터 조회", description = "특정 사이트의 대시보드 데이터를 조회합니다.")
    @GetMapping("/sites/{siteCode}")
    public ApiResponse<DashboardResponseDto> getDashboardBySite(@PathVariable String siteCode) {
        return ApiResponse.success(dashboardService.getDashboardDataBySite(siteCode));
    }
}
