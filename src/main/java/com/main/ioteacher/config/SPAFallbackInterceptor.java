package com.main.ioteacher.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SPAFallbackInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String path = request.getRequestURI();

        // 🔥 /api 경로 포함되면 API 요청으로 처리
        if (path.contains("/api/")) return true;

        // 정적 리소스는 그대로 통과
        if (path.startsWith("/assets/")
                || path.startsWith("/file/")
                || path.startsWith("/uploads/")
                || path.contains(".")) {
            return true;
        }

        // GET 요청이 아닐 경우 통과
        if (!"GET".equalsIgnoreCase(request.getMethod())) return true;

        // Vue SPA 처리
        request.getRequestDispatcher("/index.html").forward(request, response);
        return false;
    }
}
