package com.zjz.mini.uri.run.infrastructure.aop.annotation;

import com.zjz.mini.uri.run.infrastructure.aop.handler.PreventHandler;

import java.lang.annotation.*;

/**
 * 防刷
 * @author hkz329
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Prevent {

    /**
     * 限制的时间值（秒）
     *
     * @return
     */
    long time() default 30;

    /**
     * 提示
     */
    String message() default "";

    /**
     * 处理策略
     * @return
     */
    Class<? extends PreventHandler> strategy() default PreventHandler.class;
}
