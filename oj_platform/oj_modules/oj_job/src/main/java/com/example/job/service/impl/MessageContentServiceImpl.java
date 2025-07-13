package com.example.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.job.entity.MessageContent;
import com.example.job.mapper.MessageContentMapper;
import com.example.job.service.MessageContentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageContentServiceImpl extends ServiceImpl<MessageContentMapper, MessageContent> implements MessageContentService {

    @Override
    // 批量插入数据
    public boolean batchInsert(List<MessageContent> list) {
        return saveBatch(list);
    }

}
