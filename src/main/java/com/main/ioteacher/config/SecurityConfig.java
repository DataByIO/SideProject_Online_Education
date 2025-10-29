package com.main.ioteacher.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ✅ 정적 리소스 및 공개 페이지
                        .requestMatchers("/", "/index.html", "/login", "/register", "/feed",  // ← feed 추가
                                "/assets/**", "/static/**",
                                "/favicon.ico", "/uploads/**", "/api/uploads/**")
                        .permitAll()

                        // ✅ 공개 API
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/check-duplicate",
                                "/api/external-programs/**",
                                "/api/courses/**"
                        ).permitAll()

                        // ✅ 커뮤니티 조회 관련은 공개
                        .requestMatchers(HttpMethod.GET, "/api/community/**").permitAll()

                        // ✅ 커뮤니티 글쓰기/수정/삭제/신고 등은 보호
                        .requestMatchers(HttpMethod.POST, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/**").authenticated()
                        .requestMatchers("/api/community/reports/**").authenticated()

                        // ✅ 사용자 관련 (마이페이지 등 보호)
                        .requestMatchers("/api/users/me", "/api/users/profile", "/api/users/password").authenticated()

                        // ✅ 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ 영상 스트리밍
                        .requestMatchers(HttpMethod.GET, "/api/video/stream/**").authenticated()

                        // ✅ OPTIONS (CORS Preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ 나머지는 보호
                        .anyRequest().authenticated()
                )

                // ✅ JWT 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"Unauthorized (401): 인증이 필요합니다.\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"Forbidden (403): 접근 권한이 없습니다.\"}");
                        })
                )

                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    /** ✅ CORS 설정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://ioteacher.com",
                "http://ioteacher.com"
        ));
        c.setAllowCredentials(true);
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "lang", "*"));
        c.setExposedHeaders(List.of("*"));
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
