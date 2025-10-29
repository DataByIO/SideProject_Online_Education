package com.main.ioteacher.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String method = request.getMethod();
        final String path = request.getRequestURI();

        // ✅ 정적 리소스 스킵
        if (isStaticOrMainResource(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ OPTIONS 스킵 (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 공개 엔드포인트 스킵 (로그인/회원가입 등)
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Authorization 헤더 확인
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = request.getParameter("token");
        }

        if (token == null || token.isBlank()) {
            log.warn("[JWT] Authorization 헤더 없음: {}", path);
            unauthorized(response, "인증 토큰이 누락되었습니다.");
            return;
        }

        // ✅ 토큰 검증
        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token)) {
                unauthorized(response, "토큰이 만료되었거나 유효하지 않습니다.");
                return;
            }

            // ✅ SecurityContext 등록
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("[JWT] 인증 완료: {}", username);
            }
        } catch (Exception e) {
            log.warn("[JWT] 토큰 파싱 오류: {}", e.getMessage());
            unauthorized(response, "유효하지 않은 토큰 형식입니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isStaticOrMainResource(String path) {
        return path.equals("/") || path.equals("/index.html") ||
                path.equals("/login") || path.equals("/register") || path.equals("/feed") ||  // ✅ feed 추가
                path.startsWith("/assets/") || path.startsWith("/static/") ||
                path.startsWith("/uploads/") || path.startsWith("/api/uploads/") ||
                path.equals("/favicon.ico");
    }

    /** ✅ 로그인하지 않아도 접근 가능한 API */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return
                // Auth 관련
                path.startsWith("/api/auth/register") ||
                        path.startsWith("/api/auth/login") ||
                        path.startsWith("/api/auth/send-verification") ||
                        path.startsWith("/api/auth/send-reset-verification") ||
                        path.startsWith("/api/auth/verify-reset-code") ||
                        path.startsWith("/api/auth/reset-password") ||
                        path.startsWith("/api/auth/verify-code") ||
                        path.startsWith("/api/auth/refresh") ||

                        // User 관련
                        path.startsWith("/api/users/check-duplicate") ||

                        // 강의/외부교육 목록은 GET만 허용
                        (path.startsWith("/api/courses") && method.equals("GET")) ||
                        (path.startsWith("/api/external-programs") && method.equals("GET")) ||

                        // ✅ 커뮤니티 조회용(GET)은 허용
                        (path.startsWith("/api/community") && method.equals("GET")) ||

                        // ✅ 정적 리소스 및 공개 페이지
                        path.equals("/") ||
                        path.equals("/index.html") ||
                        path.equals("/login") ||
                        path.equals("/register") ||
                        path.equals("/feed") || // ✅ feed 추가
                        path.startsWith("/uploads/") ||
                        path.startsWith("/assets/") ||
                        path.startsWith("/static/") ||
                        path.equals("/favicon.ico");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
