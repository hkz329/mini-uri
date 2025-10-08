package com.zjz.mini.uri.run.application.worker;

import com.zjz.mini.uri.run.application.VisitorStatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 访问统计同步定时任务
 */
@Slf4j
@Component
public class VisitorStatsSyncJob {

    @Resource
    private VisitorStatsService visitorStatsService;

    /**
     * 每天凌晨1点执行同步
     * 同步前一天（昨天）的完整统计数据
     * 例如：10月8日凌晨1点执行，同步10月7日全天的数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncYesterdayStats() {
        log.info("开始执行访问统计数据同步任务（前一天数据）...");
        try {
            visitorStatsService.syncStatsToDatabase();
            log.info("访问统计数据同步任务完成，已同步前一天数据");
        } catch (Exception e) {
            log.error("访问统计数据同步任务失败", e);
        }
    }

    /**
     * 每周日凌晨2点执行补偿性同步
     * 同步最近7天的数据，防止因任务失败导致的数据丢失
     *
     * 场景：
     * 1. 定时任务执行失败
     * 2. 服务重启导致部分数据未同步
     * 3. 手动修复历史数据
     */
    @Scheduled(cron = "0 0 2 ? * SUN")
    public void syncRecentDaysCompensation() {
        log.info("开始执行访问统计补偿性同步任务（最近7天）...");
        try {
            visitorStatsService.syncRecentDays(7);
            log.info("访问统计补偿性同步任务完成");
        } catch (Exception e) {
            log.error("访问统计补偿性同步任务失败", e);
        }
    }

//    /**
//     * 每天早上6点同步当天已有数据（可选，用于实时查看今日统计）
//     * 注意：这会同步当天凌晨0点到6点的数据，仅用于提前查看今日趋势
//     * 凌晨1点会再次同步前一天的完整数据
//     */
//    @Scheduled(cron = "0 0 6 * * ?")
//    public void syncTodayStats() {
//        log.info("开始同步当天访问统计数据（今日实时）...");
//        try {
//            LocalDate today = LocalDate.now();
//            visitorStatsService.syncStatsByDate(today);
//            log.info("当天访问统计数据同步完成");
//        } catch (Exception e) {
//            log.error("当天访问统计数据同步失败", e);
//        }
//    }
}

