package com.doubao.config;

// ControllerConfig.java - 统一管理控制器映射


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ControllerConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 确保URL路径匹配配置正确
        configurer.setUseTrailingSlashMatch(false);
    }
}