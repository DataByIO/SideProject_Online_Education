package com.main.ioteacher.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            log.warn("JWT secret length is too short for HS256. length={}", (secret == null ? 0 : secret.length()));
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT initialized with HS256 algorithm");
    }

    private final long accessTokenValidity = 1000L * 60 * 60 * 24 * 7;  // 7일
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 14; // 14일 (2주)

    private final CustomUserDetailsService userDetailsService;

    public JwtUtil(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    /** ✅ Access Token 생성 */
    public String generateAccessToken(String userId, String role) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + accessTokenValidity);

        String token = Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256) // ✅ HS256 고정
                .compact();
        log.debug("Created access token for userId={}, exp={}", userId, exp);
        return token;
    }

    /** ✅ Refresh Token 생성 */
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256) // ✅ HS256 고정
                .compact();
    }

    /** ✅ 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // ✅ 발급과 동일 키
                    .build()
                    .parseClaimsJws(token); // 파싱 성공 = 유효
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired at {}: {}", e.getClaims().getExpiration(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Illegal JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage(), e);
        }
        return false;
    }

    /** ✅ 토큰에서 userId(subject) 추출 */
    public String getUserId(String token) {
        return getAllClaims(token).getSubject();
    }

    /** ✅ 토큰에서 role 추출 */
    public String getRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }
    /** ✅ 내부 claim 파싱 */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // ✅ HS256 키
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** SecurityContext에 올릴 Authentication 생성 */
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** ✅ HTTP 요청 헤더에서 토큰을 추출하고 userId(subject) 반환 */
    public String getUserIdFromRequest(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (validateToken(token)) {
                return getUserId(token);
            }
        }
        return null;
    }

}



