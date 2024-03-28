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
    public R generateShortUrl(String url) {
        // 校验 url
        if (UrlUtils.checkURL(url)) {

        }
        return null;
    }

    @Override
    public R redirect(String url) {
        return null;
    }



}
