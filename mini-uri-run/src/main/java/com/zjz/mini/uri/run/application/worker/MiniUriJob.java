package com.zjz.mini.uri.run.application.worker;

import com.zjz.mini.uri.run.domain.repository.UrlMappingRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务，清除过期的记录
 * @author hkz329
 */
@Component
@EnableAsync
@Slf4j
public class MiniUriJob {


    @Resource
    private UrlMappingRepository UrlMappingRepository;

    @Async
    @Scheduled(cron = "*/10 * * * *")
    public void task() {
        log.info("start job======= {}", LocalDateTime.now());
        int count = this.UrlMappingRepository.deleteExpired();
        log.info("end job======{},deleted count:{}", LocalDateTime.now(), count);
    }

}
