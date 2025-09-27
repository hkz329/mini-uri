package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.framework.common.core.R;
import com.zjz.mini.uri.run.application.MiniUriService;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;
import com.zjz.mini.uri.run.infrastructure.aop.handler.GenShortUrlPreventHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


/**
 * MiniUriController
 * @author hkz329
 */
@Controller
public class MiniUriController {

    @Value("${server.host}")
    private String host;

    @Resource
    private MiniUriService miniUriService;

//    /**
//     * 首页：返回 Thymeleaf 模板
//     */
//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }

    /**
     * 生成短链
     * @param req
     * @return
     */
    @Prevent(time = 5, message = "5秒内不允许重复生成", strategy = GenShortUrlPreventHandler.class)
    @ResponseBody
    @PostMapping("/generate")
    public R<String> generateShortURL(@RequestBody @Validated GenerateUrlReq req, HttpServletRequest request) {
        String shortURL = miniUriService.generateShortURL(req);
        String base = resolveBaseUrl(request);
        if (base == null || base.isBlank()) {
            base = host;
        }
        String full = joinUrl(base, shortURL);
        return R.ok(full);
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

    private String resolveBaseUrl(HttpServletRequest request) {
        if (request == null) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    private String joinUrl(String base, String path) {
        if (path == null || path.isBlank()) return base;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        if (base == null || base.isBlank()) return path;
        return UriComponentsBuilder.fromUriString(base)
                .path(path.startsWith("/") ? path : "/" + path)
                .build()
                .toUriString();
    }




    /**
     * 处理 robots.txt 请求
     * @return robots.txt 文件内容
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> robots() {
        try {
            ClassPathResource resource = new ClassPathResource("static/robots.txt");
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 处理 sitemap.xml 请求
     * @return sitemap.xml 文件内容
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> sitemap() {
        try {
            ClassPathResource resource = new ClassPathResource("static/sitemap.xml");
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(content);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
