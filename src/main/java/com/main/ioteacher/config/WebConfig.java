package com.main.ioteacher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ✅ 실제 파일 경로 수정됨 (kimnoah + IdeaProjects)
        String basePath = "file:/Users/kimnoah/IdeaProjects/ioteacher/uploads/";

        // ✅ 이미지 업로드 접근
        registry.addResourceHandler("/uploads/image/**", "/api/uploads/image/**")
                .addResourceLocations(basePath + "image/")
                .setCachePeriod(3600);

        // ✅ 동영상 업로드 접근
        registry.addResourceHandler("/uploads/videos/**", "/api/uploads/videos/**")
                .addResourceLocations(basePath + "videos/")
                .setCachePeriod(3600);

        // ✅ 프로필 이미지 업로드 접근
        registry.addResourceHandler("/uploads/userProfile/**", "/api/uploads/userProfile/**")
                .addResourceLocations(basePath + "userProfile/")
                .setCachePeriod(3600);

        // ✅ 공통 uploads 경로
        registry.addResourceHandler("/uploads/**", "/api/uploads/**")
                .addResourceLocations(basePath)
                .setCachePeriod(3600);

        System.out.println("📁 Static mapping path: " + basePath);
    }
}