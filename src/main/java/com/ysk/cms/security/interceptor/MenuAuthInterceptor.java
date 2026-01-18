package com.ysk.cms.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.menu.entity.Menu;
import com.ysk.cms.domain.admin.menu.repository.MenuRepository;
import com.ysk.cms.security.annotation.SkipMenuAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuAuthInterceptor implements HandlerInterceptor {

    private static final String MENU_ID_HEADER = "X-Menu-Id";

    private final MenuRepository menuRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // HandlerMethod가 아닌 경우 통과 (정적 리소스 등)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // @SkipMenuAuth 체크 (메서드 또는 클래스 레벨)
        if (shouldSkipAuth(handlerMethod)) {
            return true;
        }

        // X-Menu-Id 헤더 확인
        String menuIdHeader = request.getHeader(MENU_ID_HEADER);
        if (!StringUtils.hasText(menuIdHeader)) {
            // 헤더가 없으면 기존 방식대로 통과 (점진적 적용을 위해)
            log.debug("[MenuAuth] X-Menu-Id header not present, skipping menu auth check");
            return true;
        }

        // 메뉴 ID 파싱
        Long menuId;
        try {
            menuId = Long.parseLong(menuIdHeader);
        } catch (NumberFormatException e) {
            log.warn("[MenuAuth] Invalid menu ID format: {}", menuIdHeader);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 메뉴 ID 형식입니다.");
            return false;
        }

        // 메뉴 조회
        Optional<Menu> menuOpt = menuRepository.findById(menuId);
        if (menuOpt.isEmpty()) {
            log.warn("[MenuAuth] Menu not found: {}", menuId);
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "메뉴를 찾을 수 없습니다.");
            return false;
        }

        Menu menu = menuOpt.get();

        // 메뉴에 roles 설정이 없으면 통과
        if (!StringUtils.hasText(menu.getRoles())) {
            log.debug("[MenuAuth] Menu {} has no role restrictions", menuId);
            return true;
        }

        // 현재 인증 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[MenuAuth] User not authenticated");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
            return false;
        }

        // 사용자 역할 추출
        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toSet());

        // 메뉴 허용 역할 파싱
        Set<String> allowedRoles = Arrays.stream(menu.getRoles().split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        // 역할 매칭 확인
        boolean hasAccess = userRoles.stream().anyMatch(allowedRoles::contains);

        if (!hasAccess) {
            log.warn("[MenuAuth] Access denied - User roles: {}, Menu allowed roles: {}", userRoles, allowedRoles);
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "해당 메뉴에 접근 권한이 없습니다.");
            return false;
        }

        log.debug("[MenuAuth] Access granted - User: {}, Menu: {}", authentication.getName(), menu.getName());
        return true;
    }

    private boolean shouldSkipAuth(HandlerMethod handlerMethod) {
        // 메서드 레벨 체크
        if (handlerMethod.hasMethodAnnotation(SkipMenuAuth.class)) {
            return true;
        }
        // 클래스 레벨 체크
        return handlerMethod.getBeanType().isAnnotationPresent(SkipMenuAuth.class);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorCode errorCode = switch (status) {
            case HttpServletResponse.SC_UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case HttpServletResponse.SC_FORBIDDEN -> ErrorCode.ACCESS_DENIED;
            case HttpServletResponse.SC_NOT_FOUND -> ErrorCode.RESOURCE_NOT_FOUND;
            default -> ErrorCode.INVALID_INPUT_VALUE;
        };

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
