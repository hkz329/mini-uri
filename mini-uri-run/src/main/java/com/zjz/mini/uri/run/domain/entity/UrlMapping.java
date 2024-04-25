package com.zjz.mini.uri.run.domain.entity;


import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * url 映射表
 *
 * @TableName url_mapping
 */
@TableName(value = "url_mapping")
@Data
@Accessors(chain = true)
public class UrlMapping implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 长链接
     */
    @TableField(value = "long_url")
    private String longUrl;

    /**
     * 短链接
     */
    @TableField(value = "short_url")
    private String shortUrl;

    /**
     * 生成类型
     */
    @TableField(value = "build_type")
    private Integer buildType;

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

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
