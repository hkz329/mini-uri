package com.zjz.mini.uri.framework.mybatis.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * mybatis 自动装配
 *
 * @author hkz329
 */
@AutoConfiguration
@MapperScan(value = "${miniuri.info.base-package}", annotationClass = Mapper.class, lazyInitialization = "${mybatis-plus.lazy-initialization:false}")
public class MiniUriMybatisAutoConfiguration {


    /**
     * 分页配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
