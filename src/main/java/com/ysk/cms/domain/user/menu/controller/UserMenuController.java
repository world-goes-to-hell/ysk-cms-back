package com.ysk.cms.domain.user.menu.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.user.menu.dto.*;
import com.ysk.cms.domain.user.menu.service.UserMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 메뉴 관리", description = "사이트별 사용자 메뉴 CRUD 및 트리 구조 관리")
@RestController
@RequestMapping("/api/sites/{siteCode}/user-menus")
@RequiredArgsConstructor
public class UserMenuController {

    private final UserMenuService userMenuService;

    @Operation(summary = "사용자 메뉴 전체 목록", description = "사이트의 모든 사용자 메뉴를 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserMenuDto>> getMenus(@PathVariable String siteCode) {
        return ApiResponse.success(userMenuService.getMenus(siteCode));
    }

    @Operation(summary = "사용자 메뉴 트리 조회", description = "사이트의 사용자 메뉴를 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ApiResponse<List<UserMenuTreeDto>> getMenuTree(@PathVariable String siteCode) {
        return ApiResponse.success(userMenuService.getMenuTree(siteCode));
    }

    @Operation(summary = "활성 사용자 메뉴 트리 조회", description = "사이트의 활성화된 사용자 메뉴만 트리 구조로 조회합니다.")
    @GetMapping("/tree/active")
    public ApiResponse<List<UserMenuTreeDto>> getActiveMenuTree(@PathVariable String siteCode) {
        return ApiResponse.success(userMenuService.getActiveMenuTree(siteCode));
    }

    @Operation(summary = "사용자 메뉴 상세 조회", description = "특정 사용자 메뉴의 상세 정보를 조회합니다.")
    @GetMapping("/{menuId}")
    public ApiResponse<UserMenuDto> getMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId) {
        return ApiResponse.success(userMenuService.getMenu(siteCode, menuId));
    }

    @Operation(summary = "하위 사용자 메뉴 조회", description = "특정 사용자 메뉴의 하위 메뉴를 조회합니다.")
    @GetMapping("/{parentId}/children")
    public ApiResponse<List<UserMenuDto>> getChildMenus(@PathVariable Long parentId) {
        return ApiResponse.success(userMenuService.getChildMenus(parentId));
    }

    @Operation(summary = "사용자 메뉴 생성", description = "새로운 사용자 메뉴를 생성합니다.")
    @PostMapping
    public ApiResponse<UserMenuDto> createMenu(
            @PathVariable String siteCode,
            @Valid @RequestBody UserMenuCreateRequest request) {
        return ApiResponse.success("사용자 메뉴가 생성되었습니다.", userMenuService.createMenu(siteCode, request));
    }

    @Operation(summary = "사용자 메뉴 수정", description = "사용자 메뉴 정보를 수정합니다.")
    @PutMapping("/{menuId}")
    public ApiResponse<UserMenuDto> updateMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId,
            @Valid @RequestBody UserMenuUpdateRequest request) {
        return ApiResponse.success("사용자 메뉴가 수정되었습니다.", userMenuService.updateMenu(siteCode, menuId, request));
    }

    @Operation(summary = "사용자 메뉴 삭제", description = "사용자 메뉴를 삭제합니다. 하위 메뉴도 함께 삭제됩니다.")
    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId) {
        userMenuService.deleteMenu(siteCode, menuId);
        return ApiResponse.success("사용자 메뉴가 삭제되었습니다.");
    }

    @Operation(summary = "사용자 메뉴 정렬", description = "사용자 메뉴의 순서와 부모를 일괄 변경합니다.")
    @PutMapping("/sort")
    public ApiResponse<Void> sortMenus(
            @PathVariable String siteCode,
            @Valid @RequestBody UserMenuSortRequest request) {
        userMenuService.sortMenus(siteCode, request);
        return ApiResponse.success("사용자 메뉴 순서가 변경되었습니다.");
    }
}
