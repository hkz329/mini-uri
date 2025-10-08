package com.zjz.mini.uri.run.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 国际化配置类
 * 支持根据浏览器语言自动切换界面语言
 */
@Configuration
public class LocaleConfig {

    /**
     * 配置消息源（MessageSource）
     * 用于加载国际化资源文件
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // 设置资源文件基础名（classpath:messages.properties）
        messageSource.setBasename("messages");

        // 设置编码格式为 UTF-8
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // 设置缓存时间（秒），-1 表示永久缓存，开发时可设置为 0
        messageSource.setCacheSeconds(3600);

        // 当找不到对应的消息key时，不抛出异常，返回key本身
        messageSource.setUseCodeAsDefaultMessage(true);

        // 设置默认语言
        messageSource.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

        // 当请求的语言不存在时，不使用系统默认语言
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    /**
     * 配置语言解析器
     * 使用 AcceptHeaderLocaleResolver 根据 HTTP 请求头的 Accept-Language 自动识别语言
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        // 设置默认语言为中文
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

        // 设置支持的语言列表
        List<Locale> supportedLocales = Arrays.asList(
            Locale.SIMPLIFIED_CHINESE,  // 中文
            Locale.US,                   // 英文
            Locale.ENGLISH,
            Locale.JAPAN,                // 日文
            Locale.KOREA,                // 韩文
            Locale.TRADITIONAL_CHINESE   // 繁体中文
        );
        resolver.setSupportedLocales(supportedLocales);

        return resolver;
    }
}

