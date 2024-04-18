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

    /**
     * 生成短链
     * @param req
     * @return
     */
    public String generateShortURL(GenerateUrlReq req) {
        HashShortUrl bean = SpringUtil.getBean(HashShortUrl.class);
        String shortUrl = bean.generateShortUrl(req.getOriginalUrl());
        return shortUrl;
    }

    /**
     * 重定向
     * @param shortUrl
     * @return
     */
    public String redirect(String shortUrl) {
        HashShortUrl bean = SpringUtil.getBean(HashShortUrl.class);
        String longUrl = bean.redirect(shortUrl);
        return longUrl;
    }
}
