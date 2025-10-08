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
                .order(1);  // 设置优先级
    }
}

