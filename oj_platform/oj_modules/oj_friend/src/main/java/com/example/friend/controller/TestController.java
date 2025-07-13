package com.example.friend.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.message.AliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController extends DatabaseController {
    @Autowired
    private AliService aliService;

    @GetMapping("/sendCode")
    public Result<Void> sentCode(String telephone, String code) {
        return convertToResult(aliService.sendPhoneCode(telephone, code));
    }
}
