package com.ysk.cms.domain.activity.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.activity.dto.ActivityLogDto;
import com.ysk.cms.domain.activity.entity.ActivityType;
import com.ysk.cms.domain.activity.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "활동 로그", description = "사용자 활동 로그 조회 API")
@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Operation(summary = "전체 활동 로그 조회", description = "모든 사이트의 활동 로그를 페이지네이션으로 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<ActivityLogDto>> getAllActivityLogs(
            @RequestParam(required = false) ActivityType activityType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(activityLogService.getAllActivityLogs(activityType, targetType, keyword, pageable));
    }

    @Operation(summary = "사이트별 활동 로그 조회", description = "특정 사이트의 활동 로그를 페이지네이션으로 조회합니다.")
    @GetMapping("/sites/{siteCode}")
    public ApiResponse<PageResponse<ActivityLogDto>> getActivityLogsBySite(
            @PathVariable String siteCode,
            @RequestParam(required = false) ActivityType activityType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(activityLogService.getActivityLogsBySite(siteCode, activityType, targetType, keyword, pageable));
    }

    @Operation(summary = "사용자별 활동 로그 조회", description = "특정 사용자의 활동 로그를 페이지네이션으로 조회합니다.")
    @GetMapping("/users/{userId}")
    public ApiResponse<PageResponse<ActivityLogDto>> getActivityLogsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(activityLogService.getActivityLogsByUser(userId, pageable));
    }
}
