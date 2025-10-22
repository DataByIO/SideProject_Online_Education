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

        // ✅ 1. 정적 리소스 및 프론트 라우트는 완전 스킵
        if (isStaticOrMainResource(path)) {
            log.debug("[JWT] Skip static or main resource: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. OPTIONS 요청 무시 (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 3. 공개 엔드포인트 (API + 프론트 라우트) 스킵
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 4. Authorization 헤더 또는 파라미터에서 토큰 추출
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

        // ✅ 5. 토큰 검증
        String username;
        try {
            username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token)) {
                unauthorized(response, "토큰이 만료되었거나 유효하지 않습니다.");
                return;
            }
        } catch (Exception e) {
            log.warn("[JWT] 토큰 파싱 오류: {}", e.getMessage());
            unauthorized(response, "유효하지 않은 토큰 형식입니다.");
            return;
        }

        // ✅ 6. SecurityContext에 인증 정보 설정
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("[JWT] 인증 완료: {}", username);
        }

        filterChain.doFilter(request, response);
    }

    /** ✅ 메인화면, 로그인/회원가입, 정적 자원 모두 인증 제외 */
    private boolean isStaticOrMainResource(String path) {
        return path.equals("/")
                || path.equals("/index.html")
                || path.equals("/login")           // ✅ Vue 로그인 화면
                || path.equals("/register")        // ✅ Vue 회원가입 화면
                || path.startsWith("/assets/")
                || path.startsWith("/static/")
                || path.startsWith("/uploads/")
                || path.startsWith("/api/uploads/")
                || path.startsWith("/favicon.ico");
    }

    /** ✅ 백엔드 API 중 공개 엔드포인트 */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return (path.startsWith("/api/courses") && method.equals("GET"))
                || (path.startsWith("/api/reviews") && method.equals("GET"))
                || (path.startsWith("/api/external-programs") && method.equals("GET"))
                || path.startsWith("/api/users/login")
                || path.startsWith("/api/users/register")
                || path.startsWith("/api/auth");
    }

    /** ✅ 401 Unauthorized 응답 처리 */
    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
