package com.example.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.job.entity.Message;
import com.example.job.mapper.MessageMapper;
import com.example.job.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Override
    public boolean batchInsert(List<Message> list) {
        return saveBatch(list);

    }
}
