package com.zjz.mini.uri.run.domain.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


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
    @Range(min = 1, max = 90, message = "过期时间必须在 1-90 天之间")
    private Integer expireTime = 1;
}
