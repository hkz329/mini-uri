package com.zjz.mini.uri.run.domain.service;

import cn.hutool.core.util.URLUtil;
import com.zjz.mini.uri.common.core.R;
import com.zjz.mini.uri.common.util.UrlUtils;

/**
 * 短链 Base
 *
 * @author 19002
 */
public abstract class ShortUrlBase implements IShortUrlExec {


    @Override
    public String generateShortUrl(String url) {
        // 校验 url
        checkUrl(url);
        // 计算hash
        long hash = toHash(url);
        // 转码
        String encodeUrl = toEncode(hash);
        // 后续处理
        String shortUrl = doProcess(encodeUrl, url, url);
        //
        return shortUrl;
    }

    protected abstract void checkUrl(String url);

    protected abstract long toHash(String url);

    protected abstract String toEncode(long hash);

    protected abstract String doProcess(String shortUrl, String longUrl,String originalUrl);

    @Override
    public R redirect(String url) {
        return null;
    }



}
