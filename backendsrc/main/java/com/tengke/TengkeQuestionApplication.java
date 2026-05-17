package com.tengke;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 腾科刷题系统启动类
 */
@SpringBootApplication
@MapperScan("com.tengke.mapper")
public class TengkeQuestionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TengkeQuestionApplication.class, args);
    }
}
