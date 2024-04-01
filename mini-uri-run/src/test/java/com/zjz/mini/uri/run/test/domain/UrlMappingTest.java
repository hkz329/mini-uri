package com.zjz.mini.uri.run.test.domain;

import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import com.zjz.mini.uri.run.domain.repository.UrlMappingMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Slf4j
public class UrlMappingTest {

    @Resource
    private UrlMappingMapper urlMappingMapper;


    @Test
    public void test_insert() {
        UrlMapping entity = new UrlMapping();
        entity.setBuild_type(0);
        entity.setLong_url("https://www.zhangjinzhao.com/blog-cicd/#github-action-%E8%87%AA%E5%8A%A8%E6%9E%84%E5%BB%BA%E9%83%A8%E7%BD%B2");
        entity.setShort_url("https://www.zhangjinzhao.com/asas12n1n2");
        entity.setCreate_time(LocalDateTime.now());
        int insert = this.urlMappingMapper.insert(entity);
        log.info("test_insert，res:{}", insert);
    }
}
