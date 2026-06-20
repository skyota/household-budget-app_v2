package com.kakeibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KakeiboApplication {
    public static void main(String[] args) {
        SpringApplication.run(KakeiboApplication.class, args);
    }
}
