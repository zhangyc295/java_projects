package com.example.friend.model.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("oj_message_content")
public class MessageContent extends TimeBase {
    @TableId(type = IdType.ASSIGN_ID)
    private Long contentId;
    private String messageTitle;
    private String messageContent;
}
