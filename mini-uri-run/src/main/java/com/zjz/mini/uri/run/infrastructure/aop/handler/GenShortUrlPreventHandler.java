package com.zjz.mini.uri.run.infrastructure.aop.handler;


import cn.hutool.core.codec.Base64;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjz.mini.uri.framework.common.core.BusinessException;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import com.zjz.mini.uri.run.infrastructure.HttpContextHolder;
import com.zjz.mini.uri.run.infrastructure.aop.annotation.Prevent;
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
        GenerateUrlReq req =(GenerateUrlReq) args[0];
        String originalUrl = req.getOriginalUrl();
        log.info("clientIP:{}", clientIP);
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
