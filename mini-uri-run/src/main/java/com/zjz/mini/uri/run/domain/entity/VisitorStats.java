package com.zjz.mini.uri.run.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 访问统计表
 *
 * @TableName visitor_stats
 */
@TableName(value = "visitor_stats")
@Data
@Accessors(chain = true)
public class VisitorStats implements Serializable {
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
     * 页面名称
     */
    @TableField(value = "page_name")
    private String pageName;

    /**
     * 访问次数(PV)
     */
    @TableField(value = "visit_count")
    private Long visitCount;

    /**
     * 独立访客数(UV)
     */
    @TableField(value = "unique_count")
    private Long uniqueCount;

    /**
     * 统计日期
     */
    @TableField(value = "stat_date")
    private LocalDate statDate;

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
}

