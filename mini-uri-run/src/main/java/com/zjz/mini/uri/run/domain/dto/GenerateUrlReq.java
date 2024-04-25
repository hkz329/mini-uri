package com.zjz.mini.uri.run.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "url 不能为空")
    private String originalUrl;
    /**
     * 过期时间默认 1天
     */
    private Integer expireTime = 1;
}
