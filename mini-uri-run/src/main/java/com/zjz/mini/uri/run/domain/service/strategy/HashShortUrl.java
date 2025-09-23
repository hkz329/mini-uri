package com.zjz.mini.uri.run.domain.service.strategy;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HashUtil;
import com.zjz.mini.uri.framework.common.core.BusinessException;
import com.zjz.mini.uri.framework.common.util.UrlUtils;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import com.zjz.mini.uri.run.domain.service.ShortUrlBase;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 * 基于 Hash 算法
 *
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

    // 混合执行器配置：根据操作类型选择合适的线程模型
    
    /**
     * 数据库操作专用线程池（有界，防止连接池耗尽）
     */
    @Resource
    @Qualifier("databaseTaskExecutor")
    private ThreadPoolTaskExecutor databaseTaskExecutor;
    
    /**
     * 缓存操作专用虚拟线程（无界，适合快速I/O）
     */
    @Resource
    @Qualifier("cacheTaskExecutor")
    private Executor cacheTaskExecutor;

    /**
     * 获取数据库操作执行器
     * 数据库操作必须使用有界线程池，防止连接池耗尽
     */
    private Executor getDatabaseExecutor() {
        return databaseTaskExecutor;
    }
    
    /**
     * 获取缓存操作执行器
     * 缓存操作使用虚拟线程，响应快，无资源限制
     */
    private Executor getCacheExecutor() {
        return cacheTaskExecutor;
    }

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
     *
     * @param shortUrl  短链接
     * @param longUrl   长链接
     * @param originUrl 初始长链接
     * @return
     */
    @Override
    protected String doProcess(String shortUrl, String longUrl, String originUrl) {
        // 最大重试次数，防止无限循环
        final int MAX_RETRY_COUNT = 10;
        int retryCount = 0;
        String currentLongUrl = longUrl;
        String currentShortUrl = shortUrl;

        while (retryCount < MAX_RETRY_COUNT) {
            // 如果短链接长度为1，直接重新生成
            if (currentShortUrl.length() == 1) {
                currentLongUrl += DUPLICATE;
                currentShortUrl = toEncode(toHash(currentLongUrl));
                retryCount++;
                continue;
            }

            if (FILTER.contains(currentShortUrl)) { // 防止 hash 冲突
                // 此种情况布隆过滤器存在误判，即布隆过滤器中可能存在也可能不存在该key
                // 布隆过滤器中存在，查redis 缓存
                String redisLongUrl = this.redisTemplate.opsForValue().get(currentShortUrl) + "";
                if (originUrl.equals(redisLongUrl)) {
                    // 重置缓存过期时间
                    this.redisTemplate.expire(currentShortUrl, TIMEOUT, TimeUnit.HOURS);
                    return currentShortUrl;
                }
                // 没有缓存,重新hash
                retryCount++;

                // 虚拟线程友好的随机策略
                // 虚拟线程的hashCode可能相同，增加更多随机因子
                Thread currentThread = Thread.currentThread();
                int threadHash = currentThread.hashCode();
                long threadId = currentThread.threadId(); // JDK21新增的threadId()方法
                long nanoTime = System.nanoTime();
                int objectHash = System.identityHashCode(this);
                // 组合多个随机因子，提高虚拟线程环境下的唯一性
                currentLongUrl = longUrl + DUPLICATE + threadHash + threadId + objectHash + (nanoTime & 0xFFF);
                currentShortUrl = toEncode(toHash(currentLongUrl));

                if (retryCount >= MAX_RETRY_COUNT) {
                    log.warn("Hash collision retry count exceeded maximum limit for URL: {}, retryCount: {}", originUrl, retryCount);
                }
            } else {
                // 布隆过滤器不包含的一定不存在
                // 存数据库
                final String finalShortUrl = currentShortUrl;
                // 混合执行模式：数据库用传统线程池，缓存用虚拟线程
                this.getDatabaseExecutor().execute(() -> {
                    try {
                        // 数据库操作：使用传统线程池
                        UrlMapping urlMapping = new UrlMapping().setShortUrl(finalShortUrl).setLongUrl(originUrl).setBuildType(0);
                        FILTER.add(finalShortUrl);
                        super.addUrlMapping(urlMapping);
                        
                        // 缓存操作：使用虚拟线程异步执行
                        this.getCacheExecutor().execute(() -> {
                            try {
                                this.redisTemplate.opsForValue().set(finalShortUrl, originUrl, TIMEOUT, TimeUnit.HOURS);
                                log.debug("Cache set successfully for shortUrl: {}", finalShortUrl);
                            } catch (Exception cacheEx) {
                                log.warn("Failed to set cache for shortUrl: {}, but database operation succeeded", finalShortUrl, cacheEx);
                            }
                        });
                        
                    } catch (Exception e) {
                        log.error("Failed to process URL mapping for shortUrl: {}", finalShortUrl, e);
                        // 数据库失败时，异步清理缓存
                        this.getCacheExecutor().execute(() -> {
                            try {
                                this.redisTemplate.delete(finalShortUrl);
                            } catch (Exception cacheEx) {
                                log.warn("Failed to clean cache for shortUrl: {}", finalShortUrl, cacheEx);
                            }
                        });
                    }
                });
                return currentShortUrl;
            }
        }

        // 如果达到最大重试次数，使用时间戳作为后缀生成唯一短链接
        String timestampSuffix = String.valueOf(System.currentTimeMillis() % 100000);
        currentShortUrl = toEncode(toHash(originUrl + timestampSuffix));

        // 最后一次尝试
        if (!FILTER.contains(currentShortUrl)) {
            final String finalShortUrl = currentShortUrl;
            this.getDatabaseExecutor().execute(() -> {
                try {
                    log.info("doProcess final attempt execute, shortUrl:{}", finalShortUrl);
                    UrlMapping urlMapping = new UrlMapping().setShortUrl(finalShortUrl).setLongUrl(originUrl).setBuildType(0);
                    FILTER.add(finalShortUrl);
                    this.redisTemplate.opsForValue().set(finalShortUrl, originUrl, TIMEOUT, TimeUnit.HOURS);
                    super.addUrlMapping(urlMapping);
                } catch (Exception e) {
                    log.error("Failed to process URL mapping in final attempt for shortUrl: {}", finalShortUrl, e);
                    this.redisTemplate.delete(finalShortUrl);
                }
            });
            return currentShortUrl;
        } else {
            log.error("Failed to generate unique short URL after maximum retries for: {}", originUrl);
            throw new BusinessException("生成短链接失败，请稍后重试");
        }
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
//            this.redisTemplate.opsForValue().set(shortUrl, longUrl, TIMEOUT, TimeUnit.HOURS);
        }
        return longUrl;
    }

    /**
     * 带有过期时间的暂时这样
     * @param url
     * @param expireTime
     * @return
     */

    @Override
    public String generateShortUrl(String url, Integer expireTime) {
        this.checkUrl(url);
        long hash = this.toHash(url);
        String shortUrl = this.toEncode(hash);
        return doProcess(shortUrl, url, url, expireTime);
    }

    protected String doProcess(String shortUrl, String longUrl, String originUrl, Integer expireTime) {
        // 最大重试次数，防止无限循环
        final int MAX_RETRY_COUNT = 10;
        int retryCount = 0;
        String currentLongUrl = longUrl;
        String currentShortUrl = shortUrl;

        while (retryCount < MAX_RETRY_COUNT) {
            // 如果短链接长度为1，直接重新生成
            if (currentShortUrl.length() == 1) {
                currentLongUrl += DUPLICATE;
                currentShortUrl = toEncode(toHash(currentLongUrl));
                retryCount++;
                continue;
            }

            // 使用 setIfAbsent 保证原子性，防止并发问题
            Boolean success = this.redisTemplate.opsForValue().setIfAbsent(currentShortUrl, originUrl, expireTime, TimeUnit.DAYS);

            if (Boolean.TRUE.equals(success)) {
                // 成功设置缓存，说明是第一次创建，异步入库
                final String finalShortUrl = currentShortUrl;
                this.getDatabaseExecutor().execute(() -> {
                    log.info("doProcess start execute, shortUrl:{}", finalShortUrl);
                    UrlMapping urlMapping = new UrlMapping()
                            .setShortUrl(finalShortUrl)
                            .setLongUrl(originUrl)
                            .setBuildType(0)
                            .setExpireTime(LocalDateTime.now().plusDays(expireTime));
                    try {
                        super.addUrlMapping(urlMapping);
                    } catch (Exception e) {
                        // 如果数据库写入失败，应删除缓存以保证数据一致性，允许重试
                        log.error("Failed to add URL mapping to DB, removing from Redis. shortUrl: {}", finalShortUrl, e);
                        this.redisTemplate.delete(finalShortUrl);
                    }
                });
                return currentShortUrl;
            } else {
                // 缓存中已存在该 shortUrl，说明发生了哈希冲突，需要重新生成
                retryCount++;

                // 虚拟线程友好的随机策略
                // 虚拟线程的hashCode可能相同，增加更多随机因子
                Thread currentThread = Thread.currentThread();
                int threadHash = currentThread.hashCode();
                long threadId = currentThread.threadId(); // JDK21新增的threadId()方法
                long nanoTime = System.nanoTime();
                int objectHash = System.identityHashCode(this);
                // 组合多个随机因子，提高虚拟线程环境下的唯一性
                currentLongUrl = longUrl + DUPLICATE + threadHash + threadId + objectHash + (nanoTime & 0xFFF);
                currentShortUrl = toEncode(toHash(currentLongUrl));

                if (retryCount >= MAX_RETRY_COUNT) {
                    log.warn("Hash collision retry count exceeded maximum limit for URL: {}, retryCount: {}", originUrl, retryCount);
                }
            }
        }

        // 如果达到最大重试次数，使用时间戳作为后缀生成唯一短链接
        String timestampSuffix = String.valueOf(System.currentTimeMillis() % 100000);
        currentShortUrl = toEncode(toHash(originUrl + timestampSuffix));

        // 最后一次尝试，如果还是失败则抛出异常
        Boolean finalSuccess = this.redisTemplate.opsForValue().setIfAbsent(currentShortUrl, originUrl, expireTime, TimeUnit.DAYS);
        if (Boolean.TRUE.equals(finalSuccess)) {
            final String finalShortUrl = currentShortUrl;
            this.getDatabaseExecutor().execute(() -> {
                log.info("doProcess final attempt execute, shortUrl:{}", finalShortUrl);
                UrlMapping urlMapping = new UrlMapping()
                        .setShortUrl(finalShortUrl)
                        .setLongUrl(originUrl)
                        .setBuildType(0)
                        .setExpireTime(LocalDateTime.now().plusDays(expireTime));
                try {
                    super.addUrlMapping(urlMapping);
                } catch (Exception e) {
                    log.error("Failed to add URL mapping to DB in final attempt, removing from Redis. shortUrl: {}", finalShortUrl, e);
                    this.redisTemplate.delete(finalShortUrl);
                }
            });
            return currentShortUrl;
        } else {
            log.error("Failed to generate unique short URL after maximum retries for: {}", originUrl);
            throw new BusinessException("生成短链接失败，请稍后重试");
        }
    }
}
