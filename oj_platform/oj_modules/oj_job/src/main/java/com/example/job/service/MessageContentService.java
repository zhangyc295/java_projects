package com.example.job.service;

import com.example.job.entity.MessageContent;

import java.util.List;

public interface MessageContentService {
    boolean batchInsert(List<MessageContent> list);
}
