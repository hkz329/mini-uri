package com.zjz.mini.uri.run.domain.service.strategy;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HashUtil;
import cn.hutool.json.JSONUtil;
import com.zjz.mini.uri.framework.common.core.BusinessException;
import com.zjz.mini.uri.framework.common.util.UrlUtils;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import com.zjz.mini.uri.run.domain.service.ShortUrlBase;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${miniuri.cache.timeout}")
    private Long TIMEOUT;
    //创建布隆过滤器
    private static final BitMapBloomFilter FILTER = BloomFilterUtil.createBitMap(10);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void checkUrl(String url) {
        if (!UrlUtils.checkURL(url)) {
            throw new BusinessException("url 格式错误");
        }
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

    /**
     * 处理逻辑
     * @param shortUrl 短链接
     * @param longUrl  长链接
     * @param originUrl 初始长链接
     * @return
     */
    @Override
    protected String doProcess(String shortUrl, String longUrl, String originUrl) {
        if (shortUrl.length() == 1) {
            longUrl += DUPLICATE;
            shortUrl = doProcess(toEncode(toHash(longUrl)), longUrl, originUrl);
        } else if (FILTER.contains(shortUrl)) { // 防止 hash 冲突
            // 此种情况布隆过滤器存在误判，即布隆过滤器中可能存在也可能不存在该key
            // 布隆过滤器中存在，查redis 缓存
            String redisLongUrl = this.redisTemplate.opsForValue().get(shortUrl) + "";
            if (originUrl.equals(redisLongUrl)) {
                // 重置缓存过期时间
                this.redisTemplate.expire(shortUrl, TIMEOUT, TimeUnit.HOURS);
                return shortUrl;
            }
            // 没有缓存,重新hash
            longUrl += DUPLICATE;
            shortUrl = doProcess(toEncode(toHash(longUrl)), longUrl, originUrl);
        } else {
            // 布隆过滤器不包含的一定不存在
            // 存数据库
            // fixme 此处 build_type 先写死
            UrlMapping urlMapping = new UrlMapping().setShortUrl(shortUrl).setLongUrl(originUrl).setBuildType(0);
            log.info("start to insert into mysql,data:{}", JSONUtil.toJsonStr(urlMapping));
            super.addUrlMapping(urlMapping);
            FILTER.add(shortUrl);
            // 存缓存
            log.info("start to insert into redis,data:{}", JSONUtil.toJsonStr(urlMapping));
            this.redisTemplate.opsForValue().set(shortUrl, originUrl, TIMEOUT, TimeUnit.HOURS);
        }

        return shortUrl;
    }

    @Override
    protected String redirectToLong(String shortUrl) {
        // 查缓存
        String longUrl = (String) this.redisTemplate.opsForValue().get(shortUrl);
        if (null != longUrl) {
            return longUrl;
        }
        UrlMapping urlMapping = super.getByShortUrl(shortUrl);
        longUrl = urlMapping.getLongUrl();
        if (null != urlMapping.getLongUrl()) {
            //数据库有此短链接，添加缓存
           this.redisTemplate.opsForValue().set(shortUrl, longUrl, TIMEOUT, TimeUnit.HOURS);
        }
        return longUrl;
    }
}
