package com.zjz.mini.uri.framework.common.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class JoinPointUtils {
    public static Class<?> getClass(JoinPoint jp) {
        return jp.getTarget().getClass();
    }

    public static Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return getClass(jp).getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }
}
