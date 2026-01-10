package com.ysk.cms.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_EXPIRING_HEADER = "X-Token-Expiring-Soon";
    private static final String TOKEN_EXPIRES_IN_HEADER = "X-Token-Expires-In";
    private static final long EXPIRATION_WARNING_THRESHOLD = 5 * 60 * 1000; // 5분

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());

                // 토큰 만료 임박 경고 헤더 추가
                addExpirationWarningHeader(response, token);
            }
        } catch (BusinessException e) {
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, e.getErrorCode());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void addExpirationWarningHeader(HttpServletResponse response, String token) {
        try {
            long timeUntilExpiration = jwtTokenProvider.getTimeUntilExpiration(token);
            long secondsRemaining = timeUntilExpiration / 1000;

            // 만료까지 남은 시간(초) 헤더에 추가
            response.setHeader(TOKEN_EXPIRES_IN_HEADER, String.valueOf(secondsRemaining));

            // 5분 이내 만료 예정이면 경고 헤더 추가
            if (jwtTokenProvider.isTokenExpiringSoon(token, EXPIRATION_WARNING_THRESHOLD)) {
                response.setHeader(TOKEN_EXPIRING_HEADER, "true");
                log.debug("토큰이 {}초 후 만료됩니다. 갱신을 권장합니다.", secondsRemaining);
            }
        } catch (Exception e) {
            log.warn("토큰 만료 시간 확인 중 오류: {}", e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
