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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.warn("🔎 REQUEST URI = {}", path);
        log.warn("🔎 METHOD = {}", request.getMethod());
        log.warn("🔎 AUTH HEADER = {}", request.getHeader("Authorization"));

        // OPTIONS는 패스
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 토큰 없으면 그냥 다음 필터 진행 (익명 요청 허용)
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String userId = jwtUtil.extractUsername(token);

            if (userId != null && jwtUtil.validateToken(token)) {

                if (SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails details = userDetailsService.loadUserByUsername(userId);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.warn("✅ JWT 인증 성공 userId={}", userId);
                }
            }
        } catch (Exception e) {
            log.warn("❌ Invalid token: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }
}
