package com.zjz.mini.uri.framework.infrastructure;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class HttpContextHolder {

    private static final ThreadLocal<ServletRequest> REQUEST_THREAD_LOCAL_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<ServletResponse> RESPONSE_THREAD_LOCAL_HOLDER = new ThreadLocal<>();

    public static void remove() {
        REQUEST_THREAD_LOCAL_HOLDER.remove();
        RESPONSE_THREAD_LOCAL_HOLDER.remove();
    }

    public static ServletResponse getHttpResponse() {

        return RESPONSE_THREAD_LOCAL_HOLDER.get();
    }

    public static void setHttpResponse(ServletResponse response) {
        RESPONSE_THREAD_LOCAL_HOLDER.set(response);
    }

    public static ServletRequest getHttpRequest() {
        return REQUEST_THREAD_LOCAL_HOLDER.get();
    }

    public static void setHttpRequest(ServletRequest request) {

        REQUEST_THREAD_LOCAL_HOLDER.set(request);
    }

}
