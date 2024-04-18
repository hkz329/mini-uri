package com.zjz.mini.uri.run.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpContextHolder {

    private static final ThreadLocal<HttpServletRequest> REQUEST_THREAD_LOCAL_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> RESPONSE_THREAD_LOCAL_HOLDER = new ThreadLocal<>();

    public static void remove() {
        REQUEST_THREAD_LOCAL_HOLDER.remove();
        RESPONSE_THREAD_LOCAL_HOLDER.remove();
    }

    public static HttpServletResponse getHttpResponse() {

        return RESPONSE_THREAD_LOCAL_HOLDER.get();
    }

    public static void setHttpResponse(HttpServletResponse response) {
        RESPONSE_THREAD_LOCAL_HOLDER.set(response);
    }

    public static HttpServletRequest getHttpRequest() {
        return REQUEST_THREAD_LOCAL_HOLDER.get();
    }

    public static void setHttpRequest(HttpServletRequest request) {

        REQUEST_THREAD_LOCAL_HOLDER.set(request);
    }

}
