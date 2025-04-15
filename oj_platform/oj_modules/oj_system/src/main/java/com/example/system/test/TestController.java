package com.example.system.test;

import com.example.common.redis.RedisService;
import com.example.system.model.admin.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/test")
@RestController
@Slf4j
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/list")
    public List<?> list() {
        return testService.list();
    }

    @RequestMapping("/add")
    public String redisAdd() {
        Admin admin = new Admin();
        admin.setAdminAccount("adminRedis");
        admin.setAdminPassword("666666");
        admin.setAdminId(666666L);
        redisService.setCacheObject("user1", admin);
        return redisService.getCacheObject("user1", Admin.class).toString();
    }

    @GetMapping("/log")
    public String log() {
        log.info("test log info");
        log.error("test log error ");
        return "success";
    }

    @GetMapping("/valid")
    public String valid(@Validated @RequestBody TestDTO testDTO) {
        return "success";
    }
}
