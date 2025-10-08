package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.VisitorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访问日志Mapper
 */
@Mapper
public interface VisitorLogMapper extends BaseMapper<VisitorLog> {
}

