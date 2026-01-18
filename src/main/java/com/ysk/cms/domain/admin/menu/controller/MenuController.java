package com.ysk.cms.domain.admin.menu.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.admin.menu.dto.*;
import com.ysk.cms.domain.admin.menu.dto.*;
import com.ysk.cms.domain.admin.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "메뉴 관리", description = "사이트별 메뉴 CRUD 및 트리 구조 관리")
@RestController
@RequestMapping("/api/sites/{siteCode}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 전체 목록", description = "사이트의 모든 메뉴를 조회합니다.")
    @GetMapping
    public ApiResponse<List<MenuDto>> getMenus(@PathVariable String siteCode) {
        return ApiResponse.success(menuService.getMenus(siteCode));
    }

    @Operation(summary = "메뉴 트리 조회", description = "사이트의 메뉴를 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ApiResponse<List<MenuTreeDto>> getMenuTree(@PathVariable String siteCode) {
        return ApiResponse.success(menuService.getMenuTree(siteCode));
    }

    @Operation(summary = "활성 메뉴 트리 조회", description = "사이트의 활성화된 메뉴만 트리 구조로 조회합니다.")
    @GetMapping("/tree/active")
    public ApiResponse<List<MenuTreeDto>> getActiveMenuTree(@PathVariable String siteCode) {
        return ApiResponse.success(menuService.getActiveMenuTree(siteCode));
    }

    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다.")
    @GetMapping("/{menuId}")
    public ApiResponse<MenuDto> getMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId) {
        return ApiResponse.success(menuService.getMenu(siteCode, menuId));
    }

    @Operation(summary = "하위 메뉴 조회", description = "특정 메뉴의 하위 메뉴를 조회합니다.")
    @GetMapping("/{parentId}/children")
    public ApiResponse<List<MenuDto>> getChildMenus(@PathVariable Long parentId) {
        return ApiResponse.success(menuService.getChildMenus(parentId));
    }

    @Operation(summary = "메뉴 생성", description = "새로운 메뉴를 생성합니다.")
    @PostMapping
    public ApiResponse<MenuDto> createMenu(
            @PathVariable String siteCode,
            @Valid @RequestBody MenuCreateRequest request) {
        return ApiResponse.success("메뉴가 생성되었습니다.", menuService.createMenu(siteCode, request));
    }

    @Operation(summary = "메뉴 수정", description = "메뉴 정보를 수정합니다.")
    @PutMapping("/{menuId}")
    public ApiResponse<MenuDto> updateMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId,
            @Valid @RequestBody MenuUpdateRequest request) {
        return ApiResponse.success("메뉴가 수정되었습니다.", menuService.updateMenu(siteCode, menuId, request));
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다. 하위 메뉴도 함께 삭제됩니다.")
    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteMenu(
            @PathVariable String siteCode,
            @PathVariable Long menuId) {
        menuService.deleteMenu(siteCode, menuId);
        return ApiResponse.success("메뉴가 삭제되었습니다.");
    }

    @Operation(summary = "메뉴 정렬", description = "메뉴의 순서와 부모를 일괄 변경합니다.")
    @PutMapping("/sort")
    public ApiResponse<Void> sortMenus(
            @PathVariable String siteCode,
            @Valid @RequestBody MenuSortRequest request) {
        menuService.sortMenus(siteCode, request);
        return ApiResponse.success("메뉴 순서가 변경되었습니다.");
    }
}
