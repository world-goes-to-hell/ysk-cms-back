package com.ysk.cms.security.jwt;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes())
        );
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN, jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN, jwtProperties.getRefreshTokenExpiration());
    }

    private String createToken(Authentication authentication, String tokenType, long expiration) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date validity = new Date(now + expiration);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(TOKEN_TYPE_KEY, tokenType)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .filter(auth -> !auth.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다.");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다.");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return REFRESH_TOKEN.equals(claims.get(TOKEN_TYPE_KEY));
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰 만료까지 남은 시간(밀리초)을 반환합니다.
     * @return 남은 시간 (밀리초), 이미 만료된 경우 0 이하
     */
    public long getTimeUntilExpiration(String token) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * 토큰이 곧 만료되는지 확인합니다. (기본: 5분 이내)
     */
    public boolean isTokenExpiringSoon(String token, long thresholdMillis) {
        long timeUntilExpiration = getTimeUntilExpiration(token);
        return timeUntilExpiration > 0 && timeUntilExpiration <= thresholdMillis;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
