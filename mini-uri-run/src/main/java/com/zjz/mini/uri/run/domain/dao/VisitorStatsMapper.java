package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.VisitorStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访问统计Mapper
 */
@Mapper
public interface VisitorStatsMapper extends BaseMapper<VisitorStats> {
}

