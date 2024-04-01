package com.zjz.mini.uri.run.application;

import cn.hutool.extra.spring.SpringUtil;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import com.zjz.mini.uri.run.domain.service.strategy.HashShortUrl;
import org.springframework.stereotype.Service;

/**
 * Application Service
 * @author hkz329
 */
@Service
public class MiniUriService {

    public String generateShortURL(GenerateUrlReq req) {
        HashShortUrl bean = SpringUtil.getBean(HashShortUrl.class);
        String shortUrl = bean.generateShortUrl(req.getOriginalUrl());
        return shortUrl;
    }
}
