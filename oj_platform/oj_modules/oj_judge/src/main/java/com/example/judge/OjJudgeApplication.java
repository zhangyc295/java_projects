package com.example.judge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.**.mapper")
public class OjJudgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjJudgeApplication.class, args);
    }
}
