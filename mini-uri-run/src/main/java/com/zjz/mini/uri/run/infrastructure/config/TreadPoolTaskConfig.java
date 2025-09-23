package com.zjz.mini.uri.run.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 混合线程池配置
 * - HTTP请求：虚拟线程（Spring Boot自动配置）
 * - 数据库操作：传统线程池（资源有限，需要背压控制）
 * @author hkz329
 */
@Configuration
@EnableAsync
public class TreadPoolTaskConfig {

    public static final int cpuNum = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数（默认线程数）
     */
    private static final int corePoolSize = cpuNum;
    /**
     * 最大线程数
     */
    private static final int maxPoolSize = cpuNum * 2;
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private static final int keepAliveTime = 30;
    /**
     * 缓冲队列大小
     */
    private static final int queueCapacity = 1000;
    /**
     * 线程池名前缀（保留用于通用任务）
     */
    private static final String threadNamePrefix = "common-io-task";

    /**
     * 数据库操作专用线程池
     * 特点：有界、可控、与数据库连接池匹配
     */
    @Bean("databaseTaskExecutor")
    public ThreadPoolTaskExecutor databaseTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix("db-task-");
        // 数据库操作拒绝策略：调用者运行，避免任务丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationMillis(60);
        // 允许核心线程超时，提高资源利用率
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }

    /**
     * 通用任务执行器（保持兼容性）
     * 实际指向数据库任务执行器
     */
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        return databaseTaskExecutor();
    }
}
