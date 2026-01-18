package com.ysk.cms.domain.admin.auth.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.auth.dto.LoginRequest;
import com.ysk.cms.domain.admin.auth.dto.LoginResponse;
import com.ysk.cms.domain.admin.auth.dto.RegisterRequest;
import com.ysk.cms.domain.admin.auth.dto.RegisterResponse;
import com.ysk.cms.domain.admin.auth.dto.RoleDto;
import com.ysk.cms.domain.admin.auth.dto.TokenRefreshRequest;
import com.ysk.cms.domain.admin.auth.dto.TokenRefreshResponse;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import com.ysk.cms.domain.admin.user.entity.Role;
import com.ysk.cms.domain.admin.user.entity.User;
import com.ysk.cms.domain.admin.user.entity.UserStatus;
import com.ysk.cms.domain.admin.user.repository.RoleRepository;
import com.ysk.cms.domain.admin.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 사용자 상태 체크
        switch (user.getStatus()) {
            case PENDING:
                throw new BusinessException(ErrorCode.USER_PENDING);
            case INACTIVE:
                throw new BusinessException(ErrorCode.USER_INACTIVE);
            case SUSPENDED:
                throw new BusinessException(ErrorCode.USER_SUSPENDED);
            case LOCKED:
                throw new BusinessException(ErrorCode.USER_LOCKED);
            case ACTIVE:
                break;
        }

        // 사이트 검증
        Site site = siteRepository.findByCodeAndNotDeleted(request.getSiteCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        // SUPER_ADMIN이 아닌 경우 사이트 접근 권한 체크
        if (!user.isSuperAdmin() && !user.hasAccessToSite(request.getSiteCode())) {
            throw new BusinessException(ErrorCode.SITE_ACCESS_DENIED);
        }

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
                .site(LoginResponse.SiteInfo.builder()
                        .id(site.getId())
                        .code(site.getCode())
                        .name(site.getName())
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

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 아이디 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 선택된 역할 조회
        Role selectedRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        // 사용자 생성 (PENDING 상태)
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .position(request.getPosition())
                .status(UserStatus.PENDING)
                .build();

        user.addRole(selectedRole);
        User savedUser = userRepository.save(user);

        log.info("[회원가입] 새 사용자 등록: {} (역할: {}, 승인 대기중)", savedUser.getUsername(), selectedRole.getName());

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .status(savedUser.getStatus().name())
                .message("회원가입이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다.")
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto::from)
                .collect(Collectors.toList());
    }
}
