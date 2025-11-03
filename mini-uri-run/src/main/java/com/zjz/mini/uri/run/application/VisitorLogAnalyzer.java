package com.zjz.mini.uri.run.application;

import com.zjz.mini.uri.run.domain.dao.VisitorLogMapper;
import com.zjz.mini.uri.run.domain.entity.VisitorLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class VisitorLogAnalyzer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private VisitorLogMapper visitorLogMapper;

    // Redis Key 前缀
    private static final String REDIS_KEY_PREFIX_PV = "visitor:pv:";
    private static final String REDIS_KEY_PREFIX_UV = "visitor:uv:";

    // 日志文件路径
    private static final String LOG_FILE_PATH = "logs/visitor-stats.log";
    private static final String PROCESSED_OFFSET_KEY = "visitor:log:offset";

    // 日期格式
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    @Scheduled(cron = "0 */30 * * * ?")
    public void analyzeLogFile() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                log.debug("日志文件不存在: {}", LOG_FILE_PATH);
                return;
            }

            // 获取上次处理的位置（断点续传）
            long lastOffset = getLastProcessedOffset();
            long fileSize = logFile.length();

            if (fileSize <= lastOffset) {
                log.debug("没有新的日志需要处理");
                return;
            }

            log.info("开始解析访问日志: 从 {} 到 {}", lastOffset, fileSize);

            // 解析新增的日志行
            int processedLines = processLogFile(logFile, lastOffset);

            // 保存新的偏移量
            saveProcessedOffset(fileSize);

            log.info("日志解析完成: 处理 {} 条记录", processedLines);
        } catch (Exception e) {
            log.error("解析访问日志失败", e);
        }
    }

    /**
     * 处理日志文件
     */
    private int processLogFile(File logFile, long startOffset) throws IOException {
        int count = 0;

        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
            raf.seek(startOffset);

            String line;
            while ((line = raf.readLine()) != null) {
                try {
                    // 日志格式: 2025-11-03 10:00:00.123|VISITOR_EVENT|/|首页|127.0.0.1|Mozilla/5.0|http://google.com
                    if (line.contains("VISITOR_EVENT")) {
                        processLogLine(line);
                        count++;
                    }
                } catch (Exception e) {
                    log.warn("解析日志行失败: {}", line, e);
                }
            }
        }

        return count;
    }

    /**
     * 解析单行日志并统计
     */
    private void processLogLine(String line) {
        try {
            // 分割日志行
            String[] parts = line.split("\\|");
            if (parts.length < 7) return;

            String timestamp = parts[0];  // 2025-11-03 10:00:00.123
            String pagePath = parts[2];   // /
            // String pageName = parts[3]; // 首页（暂未使用）
            String clientIp = parts[4];   // 127.0.0.1
            String userAgent = parts[5];  // Mozilla/5.0
            String referer = parts[6];    // http://google.com

            // 提取日期
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DATETIME_FORMATTER);
            String dateStr = dateTime.format(DATE_FORMATTER);

            // 生成访客 ID
            String visitorId = md5(clientIp + "|" + userAgent);

            // 1. Redis PV 计数
            String pvKey = REDIS_KEY_PREFIX_PV + pagePath + ":" + dateStr;
            stringRedisTemplate.opsForValue().increment(pvKey);
            stringRedisTemplate.expire(pvKey, 7, TimeUnit.DAYS);

            // 2. Redis UV 计数（Set 去重）
            String uvKey = REDIS_KEY_PREFIX_UV + pagePath + ":" + dateStr;
            stringRedisTemplate.opsForSet().add(uvKey, visitorId);
            stringRedisTemplate.expire(uvKey, 7, TimeUnit.DAYS);

            // 3. 记录访问日志到数据库（保持和原来 VisitorStatsService 一致）
            recordVisitorLog(pagePath, visitorId, clientIp, userAgent, referer, dateTime);

        } catch (Exception e) {
            log.warn("处理日志行失败: {}", line, e);
        }
    }

    /**
     * 记录访问日志到数据库（保持和原来的逻辑一致）
     */
    private void recordVisitorLog(String pagePath, String visitorId,
                                   String clientIp, String userAgent,
                                   String referer, LocalDateTime visitTime) {
        try {
            VisitorLog visitorLog = new VisitorLog()
                    .setPagePath(pagePath)
                    .setVisitorId(visitorId)
                    .setIpAddress(clientIp)
                    .setUserAgent(userAgent)
                    .setReferer(referer)
                    .setVisitTime(visitTime);
            visitorLogMapper.insert(visitorLog);
        } catch (Exception e) {
            log.error("记录访问日志失败", e);
        }
    }

    /**
     * 获取上次处理的偏移量
     */
    private long getLastProcessedOffset() {
        String offset = stringRedisTemplate.opsForValue().get(PROCESSED_OFFSET_KEY);
        return offset != null ? Long.parseLong(offset) : 0L;
    }

    /**
     * 保存处理偏移量
     */
    private void saveProcessedOffset(long offset) {
        stringRedisTemplate.opsForValue().set(PROCESSED_OFFSET_KEY, String.valueOf(offset));
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

