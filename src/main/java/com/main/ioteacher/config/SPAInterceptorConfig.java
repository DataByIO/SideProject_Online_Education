package com.main.ioteacher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SPAInterceptorConfig implements WebMvcConfigurer {

    private final SPAFallbackInterceptor spaFallbackInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(spaFallbackInterceptor)
                .addPathPatterns("/**");
    }
}
