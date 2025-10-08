package com.zjz.mini.uri.run.application.worker;

import com.zjz.mini.uri.run.application.VisitorStatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 访问统计同步定时任务
 * 每天凌晨1点将Redis数据同步到MySQL
 */
@Slf4j
@Component
public class VisitorStatsSyncJob {

    @Resource
    private VisitorStatsService visitorStatsService;

    /**
     * 每天凌晨1点执行同步
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncStats() {
        log.info("开始执行访问统计数据同步任务...");
        try {
            visitorStatsService.syncStatsToDatabase();
            log.info("访问统计数据同步任务完成");
        } catch (Exception e) {
            log.error("访问统计数据同步任务失败", e);
        }
    }

    /**
     * 每小时同步一次（可选，用于更及时的数据备份）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncStatsHourly() {
        log.debug("开始执行小时级访问统计数据同步...");
        try {
            visitorStatsService.syncStatsToDatabase();
            log.debug("小时级访问统计数据同步完成");
        } catch (Exception e) {
            log.error("小时级访问统计数据同步失败", e);
        }
    }
}

