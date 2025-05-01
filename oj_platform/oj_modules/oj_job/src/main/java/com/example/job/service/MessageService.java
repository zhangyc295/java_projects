package com.example.job.service;

import com.example.job.entity.Message;
import com.example.job.entity.MessageContent;

import java.util.List;

public interface MessageService {
    boolean batchInsert(List<Message> list);
}
