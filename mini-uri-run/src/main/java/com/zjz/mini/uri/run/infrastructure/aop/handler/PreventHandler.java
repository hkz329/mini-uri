package com.zjz.mini.uri.run.infrastructure.aop.handler;

import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;

/**
 * 防刷策略接口
 * @author hkz329
 */
public interface PreventHandler {
    void handle(Prevent prevent, String methodFullName, Object[] args);
}
