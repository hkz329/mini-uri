package com.zjz.mini.uri.run.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 认证响应DTO
 *
 * @author hkz329
 */
@Data
@Accessors(chain = true)
public class AuthResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    @Data
    @Accessors(chain = true)
    public static class UserInfoVO {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String phone;
        private String avatarUrl;
        private Integer userType;
        private LocalDateTime expireTime;
    }
}