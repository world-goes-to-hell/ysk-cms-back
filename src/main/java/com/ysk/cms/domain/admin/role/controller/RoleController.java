package com.ysk.cms.domain.admin.role.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.admin.role.dto.CreateRoleRequest;
import com.ysk.cms.domain.admin.role.dto.RoleDto;
import com.ysk.cms.domain.admin.role.dto.UpdateRoleRequest;
import com.ysk.cms.domain.admin.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "권한 관리", description = "역할(Role) CRUD API")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "역할 목록 조회", description = "모든 역할 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<RoleDto>> getRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @Operation(summary = "역할 상세 조회", description = "특정 역할의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<RoleDto> getRole(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRole(id));
    }

    @Operation(summary = "역할 생성", description = "새로운 역할을 생성합니다.")
    @PostMapping
    public ApiResponse<RoleDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.success("역할이 생성되었습니다.", roleService.createRole(request));
    }

    @Operation(summary = "역할 수정", description = "역할 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<RoleDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ApiResponse.success("역할 정보가 수정되었습니다.", roleService.updateRole(id, request));
    }

    @Operation(summary = "역할 삭제", description = "역할을 삭제합니다. 시스템 기본 역할은 삭제할 수 없습니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success("역할이 삭제되었습니다.");
    }
}
