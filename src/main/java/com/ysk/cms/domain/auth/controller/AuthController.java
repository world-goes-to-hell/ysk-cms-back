package com.ysk.cms.domain.auth.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.auth.dto.LoginRequest;
import com.ysk.cms.domain.auth.dto.LoginResponse;
import com.ysk.cms.domain.auth.dto.TokenRefreshRequest;
import com.ysk.cms.domain.auth.dto.TokenRefreshResponse;
import com.ysk.cms.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "로그인, 토큰 갱신, 사용자 정보 조회")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
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
}
