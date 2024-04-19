package com.zjz.mini.uri.run.domain.service;


import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import com.zjz.mini.uri.run.domain.repository.UrlMappingRepository;
import jakarta.annotation.Resource;

/**
 * 支撑服务
 * @author hkz329
 */

public class ShortUrlSupport {

    @Resource
    private UrlMappingRepository urlMappingRepository;

    public boolean addUrlMapping(UrlMapping urlMapping) {
        return this.urlMappingRepository.addUrlMapping(urlMapping);
    }

    public UrlMapping getByShortUrl(String shortUrl) {
        return this.urlMappingRepository.getByShortUrl(shortUrl);
    }
}
