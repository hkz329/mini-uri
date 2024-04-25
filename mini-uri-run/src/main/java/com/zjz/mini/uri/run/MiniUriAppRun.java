package com.zjz.mini.uri.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AppRun
 * @author 19002
 */
@SpringBootApplication
@EnableScheduling
public class MiniUriAppRun {

    public static void main(String[] args) {
        SpringApplication.run(MiniUriAppRun.class);
    }
}
