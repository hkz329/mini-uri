package com.zjz.mini.uri.run.infrastructure.config;

import com.zjz.mini.uri.framework.common.core.BusinessException;
import com.zjz.mini.uri.framework.common.core.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author 19002
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusException(HttpServletRequest request, BusinessException ex) {
        log.error("process url {} failed", request.getRequestURL().toString(), ex);
        return R.fail(500, ex.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleParamsValidationException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return R.fail(500, message);
    }
}
