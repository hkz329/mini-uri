package com.zjz.mini.uri.framework.infrastructure.aop.dto;

import lombok.Data;

/**
 * 生成短链请求
 * @author hkz329
 */
@Data
public class GenerateUrlReq {

    /**
     * 原始 url
     */
    private String originalUrl;
    /**
     * 过期时间默认 1天
     */
    private Integer expireTime = 1;
}
