package com.zjz.mini.uri.run.config;

import com.zjz.mini.uri.common.core.BusinessException;
import com.zjz.mini.uri.common.core.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author 19002
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R handleBusException(HttpServletRequest request, BusinessException ex) {
        log.error("process url {} failed", request.getRequestURL().toString(), ex);
        return R.fail(500, ex.getMsg());
    }
}
