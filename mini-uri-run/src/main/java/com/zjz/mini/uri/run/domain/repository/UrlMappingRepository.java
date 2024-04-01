package com.zjz.mini.uri.run.domain.repository;

import com.zjz.mini.uri.run.domain.dao.UrlMappingDao;
import com.zjz.mini.uri.run.domain.dao.UrlMappingMapper;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class UrlMappingRepository {

    @Resource
    private UrlMappingMapper urlMappingMapper;

    @Resource
    private UrlMappingDao urlMappingDao;

    public int addUrlMapping(UrlMapping urlMapping) {
        return this.urlMappingMapper.insert(urlMapping);
    }
}
