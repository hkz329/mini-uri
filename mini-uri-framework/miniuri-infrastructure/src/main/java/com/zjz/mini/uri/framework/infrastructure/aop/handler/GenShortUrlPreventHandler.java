package com.zjz.mini.uri.framework.infrastructure.aop.handler;


import cn.hutool.core.codec.Base64;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjz.mini.uri.framework.common.core.BusinessException;
import com.zjz.mini.uri.framework.infrastructure.HttpContextHolder;
import com.zjz.mini.uri.framework.infrastructure.aop.annotation.Prevent;
import com.zjz.mini.uri.framework.infrastructure.aop.dto.GenerateUrlReq;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 生成短链接口防刷
 *
 * @author hkz329
 */
@Component
public class GenShortUrlPreventHandler implements PreventHandler {

    private static final Logger log = LoggerFactory.getLogger(GenShortUrlPreventHandler.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(Prevent prevent, String methodFullName, Object[] args){
        ServletRequest request = HttpContextHolder.getHttpRequest();
        String clientIP = JakartaServletUtil.getClientIP((HttpServletRequest) request);
        GenerateUrlReq req = null;
        try {
            String json = new ObjectMapper().writeValueAsString(args[0]);
            req = new ObjectMapper().readValue(json, GenerateUrlReq.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        GenerateUrlReq req =(GenerateUrlReq) args[0];
        String originalUrl = req.getOriginalUrl();
        long expire = prevent.time();
        String encode = Base64.encode(methodFullName + clientIP + originalUrl);
        Object resp = this.redisTemplate.opsForValue().get(encode);

        if (resp == null) {
            this.redisTemplate.opsForValue().set(encode, originalUrl, expire, TimeUnit.SECONDS);
        } else {
            String message = StringUtils.isNotBlank(prevent.message()) ? prevent.message() : expire + "秒内不允许重复请求!";
            throw new BusinessException(message);
        }
    }
}
