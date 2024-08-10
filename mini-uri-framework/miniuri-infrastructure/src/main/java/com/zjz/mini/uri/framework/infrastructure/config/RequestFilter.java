package com.zjz.mini.uri.framework.infrastructure.config;

import com.zjz.mini.uri.framework.infrastructure.HttpContextHolder;
import com.zjz.mini.uri.framework.infrastructure.MyHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    @Override
    public void destroy() {

    }
}
