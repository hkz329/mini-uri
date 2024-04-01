package com.zjz.mini.uri.run.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * url 映射表
 * @TableName url_mapping
 */
@TableName(value ="url_mapping")
@Data
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
    private String long_url;

    /**
     * 短链接
     */
    @TableField(value = "short_url")
    private String short_url;

    /**
     * 生成类型
     */
    @TableField(value = "build_type")
    private Integer build_type;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime create_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
