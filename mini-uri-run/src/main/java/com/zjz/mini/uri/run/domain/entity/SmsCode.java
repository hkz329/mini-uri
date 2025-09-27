package com.zjz.mini.uri.run.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信验证码表
 *
 * @TableName sms_code
 * @author hkz329
 */
@TableName(value = "sms_code")
@Data
@Accessors(chain = true)
public class SmsCode implements Serializable {
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 验证码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 验证码类型：login-登录，register-注册
     */
    @TableField(value = "code_type")
    private String codeType;

    /**
     * 是否已使用：0-未使用，1-已使用
     */
    @TableField(value = "used")
    private Integer used;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 验证码类型枚举
     */
    public enum CodeType {
        LOGIN("login", "登录"),
        REGISTER("register", "注册");

        private final String code;
        private final String desc;

        CodeType(String code, String desc) {
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
     * 使用状态枚举
     */
    public enum Used {
        UNUSED(0, "未使用"),
        USED(1, "已使用");

        private final Integer code;
        private final String desc;

        Used(Integer code, String desc) {
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