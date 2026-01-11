package com.main.ioteacher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String basePath = "file:/Users/kimnoah/IdeaProjects/ioteacher/uploads/";

        registry.addResourceHandler("/file/uploads/image/**")
                .addResourceLocations(basePath + "image/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/file/uploads/videos/**")
                .addResourceLocations(basePath + "videos/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/file/uploads/userProfile/**")
                .addResourceLocations(basePath + "userProfile/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/file/uploads/**")
                .addResourceLocations(basePath)
                .setCachePeriod(3600);
    }
}
