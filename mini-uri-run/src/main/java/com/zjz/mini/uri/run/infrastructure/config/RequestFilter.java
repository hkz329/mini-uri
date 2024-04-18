package com.zjz.mini.uri.run.infrastructure.config;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * 请求过滤
 * @author hkz329
 */
public class RequestFilter implements Filter {


    /**
     * OncePerRequestFilter 对比
     */


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
