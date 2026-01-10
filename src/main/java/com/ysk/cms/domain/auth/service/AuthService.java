package com.ysk.cms.domain.auth.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.auth.dto.LoginRequest;
import com.ysk.cms.domain.auth.dto.LoginResponse;
import com.ysk.cms.domain.auth.dto.TokenRefreshRequest;
import com.ysk.cms.domain.auth.dto.TokenRefreshResponse;
import com.ysk.cms.domain.user.entity.User;
import com.ysk.cms.domain.user.repository.UserRepository;
import com.ysk.cms.security.CustomUserDetails;
import com.ysk.cms.security.jwt.JwtProperties;
import com.ysk.cms.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 마지막 로그인 시간 업데이트
        user.updateLastLoginAt();

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .roles(authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 리프레시 토큰 검증
        jwtTokenProvider.validateToken(refreshToken);

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "리프레시 토큰이 아닙니다.");
        }

        // 사용자 정보 조회
        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        // 새로운 토큰 발급
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse.UserInfo getCurrentUser(String username) {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
