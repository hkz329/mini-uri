package com.zjz.mini.uri.run.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UrlMappingMapper extends BaseMapper<UrlMapping> {


    @Delete("delete from mini_uri.url_mapping where expire_time < now()")
    int deleteExpired();
}
