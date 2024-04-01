package com.zjz.mini.uri.run.domain.service.strategy;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HashUtil;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import com.zjz.mini.uri.run.domain.service.ShortUrlBase;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void checkUrl(String url) {
        // todo 校验
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
            // 此种情况布隆过滤器存在误判，即布隆过滤器中可能存在也可能不存在该key
            // 布隆过滤器中存在，查redis 缓存
            String redisLongUrl = this.redisTemplate.opsForValue().get(shortUrl) + "";
            if (originUrl.equals(redisLongUrl)) {
                // 重置缓存过期时间
                this.redisTemplate.expire(shortUrl, 1, TimeUnit.HOURS);
                return shortUrl;
            }
            // 没有缓存,重新hash
            longUrl += DUPLICATE;
            shortUrl = doProcess(toEncode(toHash(longUrl)), longUrl, originUrl);
        } else {
            // 布隆过滤器不包含的一定不存在
            // 存数据库
            // fixme 此处 build_type 先写死
            super.addUrlMapping(new UrlMapping().setShort_url(shortUrl).setLong_url(originUrl).setBuild_type(0).setCreate_time(LocalDateTime.now()));
            FILTER.add(shortUrl);
            // 存缓存
            // fixme 此处 缓存时间先写死
            this.redisTemplate.opsForValue().set(shortUrl, originUrl, 1, TimeUnit.HOURS);
        }

        return shortUrl;
    }
}
