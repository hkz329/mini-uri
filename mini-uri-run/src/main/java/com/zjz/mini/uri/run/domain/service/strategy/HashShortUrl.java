package com.zjz.mini.uri.run.domain.service.strategy;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HashUtil;
import com.zjz.mini.uri.run.domain.service.ShortUrlBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 基于 Hash 算法
 * @author hkz329
 */
@Service
@Slf4j
public class HashShortUrl extends ShortUrlBase {
    @Override
    protected void checkUrl(String url) {

    }

    @Override
    protected long toHash(String url) {
        int i = HashUtil.murmur32(url.getBytes());
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        return num;
    }

    @Override
    protected String toEncode(long hash) {
        String encode = Base62.encode(String.valueOf(hash));
        return encode;
    }
}
