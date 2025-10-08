package com.zjz.mini.uri.run.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjz.mini.uri.run.domain.dao.VisitorLogMapper;
import com.zjz.mini.uri.run.domain.dao.VisitorStatsMapper;
import com.zjz.mini.uri.run.domain.entity.VisitorLog;
import com.zjz.mini.uri.run.domain.entity.VisitorStats;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 访问统计服务
 * 使用 Redis + MySQL 实现高性能访问统计
 * Redis 存储实时计数，定时同步到 MySQL
 */
@Slf4j
@Service
public class VisitorStatsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private VisitorStatsMapper visitorStatsMapper;

    @Resource
    private VisitorLogMapper visitorLogMapper;

    // Redis Key 前缀
    private static final String REDIS_KEY_PREFIX_PV = "visitor:pv:";
    private static final String REDIS_KEY_PREFIX_UV = "visitor:uv:";

    /**
     * 记录访问（异步处理，不影响页面响应）
     *
     * @param pagePath 页面路径
     * @param pageName 页面名称
     * @param request  请求对象
     */
    @Async
    public void recordVisit(String pagePath, String pageName, HttpServletRequest request) {
        try {
            String today = LocalDate.now().toString();
            String visitorId = generateVisitorId(request);

            // 1. Redis PV 计数（每次访问 +1）
            String pvKey = REDIS_KEY_PREFIX_PV + pagePath + ":" + today;
            stringRedisTemplate.opsForValue().increment(pvKey);
            // 设置过期时间（保留7天数据）
            stringRedisTemplate.expire(pvKey, 7, TimeUnit.DAYS);

            // 2. Redis UV 计数（使用 Set 去重，同一天同一访客只计数一次）
            String uvKey = REDIS_KEY_PREFIX_UV + pagePath + ":" + today;
            Long addedCount = stringRedisTemplate.opsForSet().add(uvKey, visitorId);
            stringRedisTemplate.expire(uvKey, 7, TimeUnit.DAYS);

            // 3. 记录访问日志（可选，用于详细分析）
            // addedCount > 0 表示是新访客
            if (addedCount != null && addedCount > 0) {
                recordVisitorLog(pagePath, visitorId, request);
            }

            log.debug("记录访问: page={}, visitor={}, isNew={}", pagePath, visitorId, addedCount != null && addedCount > 0);
        } catch (Exception e) {
            log.error("记录访问失败: path={}", pagePath, e);
        }
    }

    /**
     * 记录访问日志
     */
    private void recordVisitorLog(String pagePath, String visitorId, HttpServletRequest request) {
        try {
            VisitorLog log = new VisitorLog()
                    .setPagePath(pagePath)
                    .setVisitorId(visitorId)
                    .setIpAddress(getClientIp(request))
                    .setUserAgent(request.getHeader("User-Agent"))
                    .setReferer(request.getHeader("Referer"))
                    .setVisitTime(LocalDateTime.now());
            visitorLogMapper.insert(log);
        } catch (Exception e) {
            log.error("记录访问日志失败", e);
        }
    }

    /**
     * 同步 Redis 数据到 MySQL（定时任务调用）
     * 建议每天凌晨执行一次
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncStatsToDatabase() {
        try {
            log.info("开始同步访问统计数据到数据库...");
            String today = LocalDate.now().toString();

            // 扫描所有PV的Key
            Set<String> pvKeys = stringRedisTemplate.keys(REDIS_KEY_PREFIX_PV + "*:" + today);
            if (pvKeys == null || pvKeys.isEmpty()) {
                log.info("没有需要同步的数据");
                return;
            }

            int successCount = 0;
            for (String pvKey : pvKeys) {
                try {
                    // 解析 Key: visitor:pv:/index:2025-10-06
                    String[] parts = pvKey.split(":");
                    if (parts.length < 4) continue;

                    String pagePath = parts[2];
                    String dateStr = parts[3];
                    LocalDate statDate = LocalDate.parse(dateStr);

                    // 获取PV和UV
                    String pvValue = stringRedisTemplate.opsForValue().get(pvKey);
                    Long pv = pvValue != null ? Long.parseLong(pvValue) : 0L;

                    String uvKey = REDIS_KEY_PREFIX_UV + pagePath + ":" + dateStr;
                    Long uv = stringRedisTemplate.opsForSet().size(uvKey);
                    if (uv == null) uv = 0L;

                    // 查询是否已存在
                    LambdaQueryWrapper<VisitorStats> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(VisitorStats::getPagePath, pagePath)
                            .eq(VisitorStats::getStatDate, statDate);
                    VisitorStats existing = visitorStatsMapper.selectOne(wrapper);

                    if (existing != null) {
                        // 更新
                        existing.setVisitCount(pv)
                                .setUniqueCount(uv);
                        visitorStatsMapper.updateById(existing);
                    } else {
                        // 插入
                        VisitorStats stats = new VisitorStats()
                                .setPagePath(pagePath)
                                .setPageName(getPageName(pagePath))
                                .setVisitCount(pv)
                                .setUniqueCount(uv)
                                .setStatDate(statDate);
                        visitorStatsMapper.insert(stats);
                    }
                    successCount++;
                } catch (Exception e) {
                    log.error("同步单条数据失败: key={}", pvKey, e);
                }
            }
            log.info("访问统计数据同步完成，成功同步 {} 条记录", successCount);
        } catch (Exception e) {
            log.error("同步访问统计数据失败", e);
            throw e;
        }
    }

    /**
     * 获取页面统计数据
     *
     * @param pagePath 页面路径
     * @param days     最近N天
     * @return 统计数据列表
     */
    public List<VisitorStats> getStats(String pagePath, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        LambdaQueryWrapper<VisitorStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitorStats::getPagePath, pagePath)
                .between(VisitorStats::getStatDate, startDate, endDate)
                .orderByDesc(VisitorStats::getStatDate);

        return visitorStatsMapper.selectList(wrapper);
    }

    /**
     * 获取今日实时统计（从Redis读取）
     *
     * @param pagePath 页面路径
     * @return Map包含pv和uv
     */
    public Map<String, Long> getTodayStats(String pagePath) {
        String today = LocalDate.now().toString();
        Map<String, Long> result = new HashMap<>();

        // PV
        String pvKey = REDIS_KEY_PREFIX_PV + pagePath + ":" + today;
        String pvValue = stringRedisTemplate.opsForValue().get(pvKey);
        result.put("pv", pvValue != null ? Long.parseLong(pvValue) : 0L);

        // UV
        String uvKey = REDIS_KEY_PREFIX_UV + pagePath + ":" + today;
        Long uv = stringRedisTemplate.opsForSet().size(uvKey);
        result.put("uv", uv != null ? uv : 0L);

        return result;
    }

    /**
     * 获取总统计
     */
    public Map<String, Long> getTotalStats(String pagePath) {
        LambdaQueryWrapper<VisitorStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitorStats::getPagePath, pagePath);
        List<VisitorStats> list = visitorStatsMapper.selectList(wrapper);

        long totalPv = list.stream().mapToLong(VisitorStats::getVisitCount).sum();
        long totalUv = list.stream().mapToLong(VisitorStats::getUniqueCount).sum();

        Map<String, Long> result = new HashMap<>();
        result.put("totalPv", totalPv);
        result.put("totalUv", totalUv);
        return result;
    }

    /**
     * 生成访客唯一标识（基于 IP + User-Agent）
     */
    private String generateVisitorId(HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String raw = ip + "|" + (userAgent != null ? userAgent : "");
        return md5(raw);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * MD5 加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据路径获取页面名称
     */
    private String getPageName(String pagePath) {
        return switch (pagePath) {
            case "/" -> "首页";
            case "/index" -> "首页";
            default -> pagePath;
        };
    }
}

