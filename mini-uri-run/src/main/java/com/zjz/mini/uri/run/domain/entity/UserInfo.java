package com.zjz.mini.uri.run.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户基础信息表
 *
 * @TableName user_info
 * @author hkz329
 */
@TableName(value = "user_info")
@Data
@Accessors(chain = true)
public class UserInfo implements Serializable {
    
    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 头像URL
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 用户状态：0-禁用，1-正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 用户类型：1-免费用户，2-付费用户，3-VIP用户
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * 付费到期时间
     */
    @TableField(value = "expire_time")
    private LocalDateTime expireTime;

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
     * 用户状态枚举
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

    /**
     * 用户类型枚举
     */
    public enum UserType {
        FREE(1, "免费用户"),
        PREMIUM(2, "付费用户"),
        VIP(3, "VIP用户");

        private final Integer code;
        private final String desc;

        UserType(Integer code, String desc) {
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