package com.zjz.mini.uri.run.infrastructure.aop.handler;

import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;
import org.springframework.stereotype.Component;

/**
 * 生成短链接口防刷
 * @author hkz329
 */
@Component
public class GenShortUrlPreventHandler implements PreventHandler{


    @Override
    public void handel(Prevent prevent, String methodFullName) {

    }
}
