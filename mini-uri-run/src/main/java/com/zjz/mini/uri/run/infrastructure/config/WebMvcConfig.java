package com.zjz.mini.uri.run.infrastructure.config;

import com.zjz.mini.uri.run.infrastructure.interceptor.VisitorStatsInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private VisitorStatsInterceptor visitorStatsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册访问统计拦截器
        registry.addInterceptor(visitorStatsInterceptor)
            .addPathPatterns("/**")  // 拦截所有请求
            // 排除静态资源（直接在注册时排除，避免进入拦截器）
            .excludePathPatterns(
                "/static/**",
                "/css/**",
                "/js/**",
                "/img/**",
                "/images/**",
                "/fonts/**",
                "/favicon.ico",
                "/robots.txt",
                "/sitemap.xml",
                "*.css",
                "*.js",
                "*.ico",
                "*.png",
                "*.jpg",
                "*.jpeg",
                "*.gif",
                "*.svg",
                "*.woff",
                "*.woff2",
                "*.ttf",
                "/error"
            )
            // 排除健康检查和监控端点
            .excludePathPatterns(
                "/actuator/**"
            )
            .order(1);  // 设置优先级
    }
}

