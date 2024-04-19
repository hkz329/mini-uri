package com.zjz.mini.uri.run.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zjz.mini.uri.run.domain.dao.UrlMappingDao;
import com.zjz.mini.uri.run.domain.dao.UrlMappingMapper;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlMappingRepository {

    @Resource
    private UrlMappingMapper urlMappingMapper;

    @Resource
    private UrlMappingDao urlMappingDao;

    public boolean addUrlMapping(UrlMapping urlMapping) {
        return this.urlMappingDao.saveOrUpdate(urlMapping);
    }

    public UrlMapping getByShortUrl(String shortUrl) {
        LambdaQueryWrapper<UrlMapping> queryWrapper = Wrappers.lambdaQuery(UrlMapping.class);
        queryWrapper.eq(UrlMapping::getShort_url, shortUrl);
        return Optional.ofNullable(this.urlMappingMapper.selectOne(queryWrapper)).orElseGet(UrlMapping::new);
    }
}
