package com.example.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.friend.model.message.MessageContentVO;
import com.example.friend.model.message.MessageContent;

import java.util.List;

public interface MessageContentMapper extends BaseMapper<MessageContent> {
    List<MessageContentVO> selectMessageContentList(Long userId);
}
