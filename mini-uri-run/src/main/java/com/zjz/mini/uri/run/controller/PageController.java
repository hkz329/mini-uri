package com.zjz.mini.uri.run.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由控制器
 *
 * @author hkz329
 */
@Controller
public class PageController {

    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * 登录页面
     */
    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }


}
