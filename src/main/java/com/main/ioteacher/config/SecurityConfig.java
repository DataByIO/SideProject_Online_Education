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
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /* 정적 파일 허용 */
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/vite.svg",
                                "/assets/**",
                                "/robots.txt",
                                "/manifest.json",
                                "/file/uploads/**",
                                "/uploads/**"
                        ).permitAll()

                        /* 🔥 인증 불필요 API (정확하게 제한) */
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/check-duplicate").permitAll()

                        /* 강의 목록 GET만 공개 */
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()

                        /* 외부 교육 프로그램 목록 GET만 공개 */
                        .requestMatchers(HttpMethod.GET, "/api/external-programs/**").permitAll()

                        /* 커뮤니티 공개 GET만 허용 */
                        .requestMatchers(HttpMethod.GET, "/api/community/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/view").permitAll()

                        /* 🔥 중요: enrollments 는 인증 필요! */
                        .requestMatchers("/api/enrollments/**").authenticated()

                        /* 사용자 정보 관련 */
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users/password").authenticated()

                        /* 관리자 API */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        /* OPTIONS */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        /* 그 외 /api/** 는 인증 필수 */
                        .requestMatchers("/api/**").authenticated()

                        /* 나머지는 SPA 라우팅 */
                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\": \"Forbidden\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://ioteacher.com",
                "http://ioteacher.com",

                // ✅ 운영 프론트가 http://www.ioteacher.com 이므로 CORS 허용에 추가
                "http://www.ioteacher.com"
        ));
        c.setAllowCredentials(true);
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }
}
