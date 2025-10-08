package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.framework.common.core.R;
import com.zjz.mini.uri.run.application.VisitorStatsService;
import com.zjz.mini.uri.run.domain.entity.VisitorStats;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 访问统计查询接口
 * 提供统计数据的查询功能
 */
@RestController
@RequestMapping("/stats")
public class VisitorStatsController {

    @Resource
    private VisitorStatsService visitorStatsService;

    /**
     * 获取今日实时统计
     *
     * @param pagePath 页面路径，默认为首页 "/"
     * @return PV和UV数据
     */
    @GetMapping("/today")
    public R<Map<String, Long>> getTodayStats(@RequestParam(defaultValue = "/") String pagePath) {
        Map<String, Long> stats = visitorStatsService.getTodayStats(pagePath);
        return R.ok(stats);
    }

    /**
     * 获取历史统计
     *
     * @param pagePath 页面路径，默认为首页 "/"
     * @param days     最近N天，默认7天
     * @return 统计数据列表
     */
    @GetMapping("/history")
    public R<List<VisitorStats>> getHistoryStats(
            @RequestParam(defaultValue = "/") String pagePath,
            @RequestParam(defaultValue = "7") int days) {
        List<VisitorStats> stats = visitorStatsService.getStats(pagePath, days);
        return R.ok(stats);
    }

    /**
     * 获取总统计
     *
     * @param pagePath 页面路径，默认为首页 "/"
     * @return 总PV和总UV
     */
    @GetMapping("/total")
    public R<Map<String, Long>> getTotalStats(@RequestParam(defaultValue = "/",name = "pagePath") String pagePath) {
        Map<String, Long> stats = visitorStatsService.getTotalStats(pagePath);
        return R.ok(stats);
    }

    /**
     * 获取综合统计（今日+历史+总计）
     *
     * @param pagePath 页面路径，默认为首页 "/"
     * @param days     历史天数，默认7天
     * @return 综合统计数据
     */
    @GetMapping("/summary")
    public R<Map<String, Object>> getSummary(
            @RequestParam(defaultValue = "/") String pagePath,
            @RequestParam(defaultValue = "7") int days) {
        Map<String, Object> result = new HashMap<>();

        // 今日数据
        result.put("today", visitorStatsService.getTodayStats(pagePath));

        // 历史数据
        result.put("history", visitorStatsService.getStats(pagePath, days));

        // 总计数据
        result.put("total", visitorStatsService.getTotalStats(pagePath));

        return R.ok(result);
    }

    /**
     * 手动触发数据同步（管理员功能）
     */
    @PostMapping("/sync")
    public R<String> syncStats() {
        try {
            visitorStatsService.syncStatsToDatabase();
            return R.ok("数据同步成功");
        } catch (Exception e) {
            return R.fail(500, "数据同步失败: " + e.getMessage());
        }
    }
}

