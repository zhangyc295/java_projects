package com.example.friend.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.PageBase;
import com.example.common.entity.model.TableResult;
import com.example.friend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/message")
public class MessageController extends DatabaseController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public TableResult list(PageBase pageBase) {
        return messageService.list(pageBase);
    }
}
