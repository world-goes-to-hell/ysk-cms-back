package com.ysk.cms.domain.admin.user.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.admin.user.dto.*;
import com.ysk.cms.domain.admin.user.dto.*;
import com.ysk.cms.domain.admin.user.entity.UserStatus;
import com.ysk.cms.domain.admin.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관리", description = "사용자 CRUD API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 목록 조회", description = "사용자 목록을 페이지네이션으로 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<UserDto>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(userService.getUsers(role, status, keyword, pageable));
    }

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUser(id));
    }

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ApiResponse<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success("사용자가 생성되었습니다.", userService.createUser(request));
    }

    @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ApiResponse.success("사용자 정보가 수정되었습니다.", userService.updateUser(id, request));
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("사용자가 삭제되었습니다.");
    }

    @Operation(summary = "사용자 상태 변경", description = "사용자의 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request
    ) {
        userService.updateUserStatus(id, request.getStatus());
        return ApiResponse.success("사용자 상태가 변경되었습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @PatchMapping("/{id}/password")
    public ApiResponse<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(id, request);
        return ApiResponse.success("비밀번호가 변경되었습니다.");
    }

    @Operation(summary = "비밀번호 초기화", description = "사용자의 비밀번호를 임시 비밀번호로 초기화합니다.")
    @PostMapping("/{id}/reset-password")
    public ApiResponse<ResetPasswordResponse> resetPassword(@PathVariable Long id) {
        return ApiResponse.success("비밀번호가 초기화되었습니다.", userService.resetPassword(id));
    }
}
