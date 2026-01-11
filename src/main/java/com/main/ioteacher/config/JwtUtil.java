package com.main.ioteacher.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final String secret;
    private final CustomUserDetailsService userDetailsService;
    private final SecretKey key;

    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;  // 7일
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 14; // 14일

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            CustomUserDetailsService userDetailsService
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }

        this.secret = secret;
        this.userDetailsService = userDetailsService;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        log.info("JWT initialized with HS256 algorithm");
    }

    /** Access Token 생성 */
    public String generateAccessToken(String userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /** Claims 가져오기 */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** userId(subject) 추출 */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** role 추출 */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /** SecurityContext Authentication 생성 */
    public Authentication getAuthentication(String token) {
        String userId = extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
    /*              프로젝트 전체에서 사용되는 필수 메서드 복원              */
    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */

    /** 기존 컨트롤러/서비스가 요구하는 userId 반환 메서드 */
    public String getUserId(String token) {
        return extractUsername(token);
    }

    /** Authorization 헤더에서 userId 추출 */
    public String getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (validateToken(token)) {
                return extractUsername(token);
            }
        }

        return null;
    }
}
