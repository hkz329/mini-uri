package com.zjz.mini.uri.run.infrastructure.interceptor;

import com.zjz.mini.uri.run.application.VisitorStatsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 访问统计拦截器
 * 拦截页面访问，自动记录统计数据
 */
@Slf4j
@Component
public class VisitorStatsInterceptor implements HandlerInterceptor {

    @Resource
    private VisitorStatsService visitorStatsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String path = request.getRequestURI();
            
            // 只统计HTML页面访问，排除静态资源和API
            if (shouldRecord(path)) {
                String pageName = getPageName(path);
                // 异步记录，不阻塞请求
                visitorStatsService.recordVisit(path, pageName, request);
            }
        } catch (Exception e) {
            // 统计失败不影响正常业务
            log.error("访问统计失败", e);
        }
        return true;
    }

    /**
     * 判断是否需要统计该路径
     */
    private boolean shouldRecord(String path) {
        // 排除静态资源
        if (path.startsWith("/static/") || 
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/img/") ||
            path.startsWith("/favicon.ico")) {
            return false;
        }
        
        // 排除API接口
        if (path.startsWith("/api/") || 
            path.startsWith("/generate") ||
            path.startsWith("/stats")) {
            return false;
        }
        
        // 排除健康检查等
        if (path.equals("/robots.txt") || 
            path.equals("/sitemap.xml") ||
            path.equals("/actuator/health")) {
            return false;
        }
        
        // 统计首页和其他HTML页面
        return true;
    }

    /**
     * 获取页面名称
     */
    private String getPageName(String path) {
        return switch (path) {
            case "/", "/index" -> "首页";
            default -> path;
        };
    }
}

