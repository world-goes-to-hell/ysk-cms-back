package com.ysk.cms.domain.auth.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.auth.dto.LoginRequest;
import com.ysk.cms.domain.auth.dto.LoginResponse;
import com.ysk.cms.domain.auth.dto.RegisterRequest;
import com.ysk.cms.domain.auth.dto.RegisterResponse;
import com.ysk.cms.domain.auth.dto.RoleDto;
import com.ysk.cms.domain.auth.dto.TokenRefreshRequest;
import com.ysk.cms.domain.auth.dto.TokenRefreshResponse;
import com.ysk.cms.domain.auth.service.AuthService;
import com.ysk.cms.security.annotation.SkipMenuAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "인증", description = "로그인, 토큰 갱신, 사용자 정보 조회")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SkipMenuAuth
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(summary = "회원가입", description = "새 관리자 계정을 등록합니다. 승인 후 로그인이 가능합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }

    @Operation(summary = "현재 사용자 정보", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> me(@AuthenticationPrincipal UserDetails userDetails) {
        LoginResponse.UserInfo userInfo = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @Operation(summary = "역할 목록 조회", description = "회원가입 시 선택 가능한 역할 목록을 조회합니다.")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getRoles() {
        List<RoleDto> roles = authService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
}
