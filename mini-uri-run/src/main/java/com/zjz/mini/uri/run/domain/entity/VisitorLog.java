package com.zjz.mini.uri.run.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访问日志表
 *
 * @TableName visitor_log
 */
@TableName(value = "visitor_log")
@Data
@Accessors(chain = true)
public class VisitorLog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 页面路径
     */
    @TableField(value = "page_path")
    private String pagePath;

    /**
     * 访客标识(基于IP+UA生成)
     */
    @TableField(value = "visitor_id")
    private String visitorId;

    /**
     * IP地址
     */
    @TableField(value = "ip_address")
    private String ipAddress;

    /**
     * 浏览器标识
     */
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 来源页面
     */
    @TableField(value = "referer")
    private String referer;

    /**
     * 访问时间
     */
    @TableField(value = "visit_time")
    private LocalDateTime visitTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

