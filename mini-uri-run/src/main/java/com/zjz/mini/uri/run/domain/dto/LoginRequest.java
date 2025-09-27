package com.zjz.mini.uri.run.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 *
 * @author hkz329
 */
@Data
public class LoginRequest {

    /**
     * 登录标识（用户名/邮箱/手机号）
     */
    @NotBlank(message = "登录标识不能为空")
    private String loginKey;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 记住我
     */
    private Boolean rememberMe = false;
}