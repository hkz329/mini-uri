package com.zjz.mini.uri.run.domain.service.strategy;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HashUtil;
import com.zjz.mini.uri.run.domain.service.ShortUrlBase;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 基于 Hash 算法
 * @author hkz329
 */
@Service
@Slf4j
public class HashShortUrl extends ShortUrlBase {

    //自定义长链接防重复字符串
    private static final String DUPLICATE = "$";

    //创建布隆过滤器
    private static final BitMapBloomFilter FILTER = BloomFilterUtil.createBitMap(10);

    @Resource
    private RedisTemplate redisTemplate;
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

    @Override
    protected String doProcess(String shortUrl, String longUrl, String originUrl) {
        if (shortUrl.length() == 1) {
            longUrl += DUPLICATE;
            shortUrl = doProcess(toEncode(toHash(longUrl)), longUrl, originUrl);
        } else if (FILTER.contains(shortUrl)) {

        } else {
            FILTER.add(shortUrl);
            this.redisTemplate.opsForValue().set(shortUrl, originUrl, 1, TimeUnit.HOURS);
        }

        return shortUrl;
    }
}
