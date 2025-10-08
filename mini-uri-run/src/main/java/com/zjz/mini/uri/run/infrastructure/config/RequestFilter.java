package com.zjz.mini.uri.run.infrastructure.config;

import com.zjz.mini.uri.run.infrastructure.HttpContextHolder;
import com.zjz.mini.uri.run.infrastructure.MyHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 请求过滤
 *
 * @author hkz329
 */
@Slf4j
@Component
public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // RequestFilter 实例化的时候调用此方法，进行加载filterConfig
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestURI = request.getRequestURI();

            // 跳过静态资源，避免虚拟线程环境下的状态冲突
            if (isStaticResource(requestURI)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            LocalDateTime start = LocalDateTime.now();
            log.info("doFilter start,Time:{}", start);
            HttpContextHolder.setHttpRequest(servletRequest);
            HttpServletRequestWrapper httpServletRequestWrapper = new MyHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            HttpContextHolder.setHttpResponse(servletResponse);
            filterChain.doFilter(httpServletRequestWrapper, servletResponse);
            log.info("doFilter end,Time:{}, consume:{} ms", LocalDateTime.now(), Duration.between(start, LocalDateTime.now()).toMillis());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            HttpContextHolder.remove();
        }
    }

    /**
     * 判断是否为静态资源
     */
    private boolean isStaticResource(String uri) {
        return uri.startsWith("/static/") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/img/") ||
            uri.startsWith("/images/") ||
            uri.startsWith("/fonts/") ||
            uri.endsWith(".css") ||
            uri.endsWith(".js") ||
            uri.endsWith(".ico") ||
            uri.endsWith(".png") ||
            uri.endsWith(".jpg") ||
            uri.endsWith(".jpeg") ||
            uri.endsWith(".gif") ||
            uri.endsWith(".svg") ||
            uri.endsWith(".woff") ||
            uri.endsWith(".woff2") ||
            uri.endsWith(".ttf") ||
            uri.equals("/favicon.ico") ||
            uri.equals("/robots.txt");
    }

    @Override
    public void destroy() {

    }
}
