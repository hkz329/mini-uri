package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.framework.common.core.R;
import com.zjz.mini.uri.run.application.MiniUriService;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;
import com.zjz.mini.uri.run.infrastructure.aop.handler.GenShortUrlPreventHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * MiniUriController
 * @author hkz329
 */
@RestController
public class MiniUriController {

    @Value("${server.host}")
    private String host;

    @Resource
    private MiniUriService miniUriService;

    /**
     * 生成短链
     * @param req
     * @return
     */
    @Prevent(time = 5, message = "5秒内不允许重复生成", strategy = GenShortUrlPreventHandler.class)
    @PostMapping("/generate")
    public R generateShortURL(@RequestBody @Validated GenerateUrlReq req) {
        String shortURL = miniUriService.generateShortURL(req);
        return R.ok(host + shortURL);
    }

    /**
     * 跳转重定向
     * @param shortUrl
     * @param response
     */
    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable("shortUrl") String shortUrl, HttpServletResponse response) {
        String longUrl = this.miniUriService.redirect(shortUrl);
        Optional.ofNullable(longUrl).ifPresentOrElse(e -> {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location",e);
        },()->{
            // 如果 longUrl 为空跳回源站
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location","/");
        });
    }
}
