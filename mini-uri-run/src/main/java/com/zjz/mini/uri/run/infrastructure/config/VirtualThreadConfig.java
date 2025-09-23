package com.zjz.mini.uri.run.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 虚拟线程配置
 * 用于非数据库的I/O密集型操作（如缓存、网络请求等）
 * @author hkz329
 */
@Configuration
public class VirtualThreadConfig {

    /**
     * 缓存操作专用虚拟线程执行器
     * 适用于Redis等缓存操作，响应快，无需背压控制
     */
    @Bean("cacheTaskExecutor")
    public Executor cacheTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    
    /**
     * 通用I/O操作虚拟线程执行器
     * 适用于文件读取、网络请求等非数据库操作
     */
    @Bean("ioTaskExecutor")
    public Executor ioTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
