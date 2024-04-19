package com.zjz.mini.uri.run.infrastructure.aop.aspect;

import cn.hutool.extra.spring.SpringUtil;
import com.zjz.mini.uri.framework.common.util.JoinPointUtils;
import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;
import com.zjz.mini.uri.run.infrastructure.aop.handler.PreventHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
public class PreventAspect {

    @Pointcut("@annotation(com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doAdvice(JoinPoint jp) throws Throwable {
        Method method = JoinPointUtils.getMethod(jp);
        Prevent annotation = method.getAnnotation(Prevent.class);
        // 方法全名
        String methodFullName = method.getDeclaringClass().getName() + method.getName();
        Object[] args = jp.getArgs();
        // 自定义策略
        Class<? extends PreventHandler> strategy = annotation.strategy();
        boolean isPreventHandler;
        if (PreventHandler.class.equals(strategy)) {
            throw new RuntimeException("接口防刷无效的处理策略");
        } else {
            isPreventHandler = PreventHandler.class.isAssignableFrom(strategy);
        }
        if (isPreventHandler) {
            PreventHandler handler = SpringUtil.getBean(strategy);
            handler.handle(annotation, methodFullName, args);
        }
    }
}
