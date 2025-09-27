package com.zjz.mini.uri.run.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户认证信息表
 *
 * @TableName user_auth
 * @author hkz329
 */
@TableName(value = "user_auth")
@Data
@Accessors(chain = true)
public class UserAuth implements Serializable {
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 认证类型：password-密码，github-GitHub，wechat-微信，sms-短信
     */
    @TableField(value = "auth_type")
    private String authType;

    /**
     * 认证标识：用户名/邮箱/手机号/第三方ID
     */
    @TableField(value = "auth_key")
    private String authKey;

    /**
     * 认证值：密码hash/第三方token等
     */
    @TableField(value = "auth_value")
    private String authValue;

    /**
     * 额外信息：第三方用户信息等
     */
    @TableField(value = "extra_info")
    private String extraInfo;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 认证类型枚举
     */
    public enum AuthType {
        PASSWORD("password", "密码认证"),
        GITHUB("github", "GitHub登录"),
        WECHAT("wechat", "微信登录"),
        SMS("sms", "短信登录");

        private final String code;
        private final String desc;

        AuthType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 状态枚举
     */
    public enum Status {
        DISABLED(0, "禁用"),
        NORMAL(1, "正常");

        private final Integer code;
        private final String desc;

        Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}