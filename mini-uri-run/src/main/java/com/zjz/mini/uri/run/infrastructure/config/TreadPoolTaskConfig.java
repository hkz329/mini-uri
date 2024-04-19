package com.zjz.mini.uri.run.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
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
    private static final int queueCapacity = 500;
    /**
     * 线程池名前缀
     */
    private static final String threadNamePrefix = "common-io-task";

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationMillis(60);
        executor.initialize();
        return executor;
    }
}
