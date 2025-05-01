package com.example.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;

//-- 消息对应表
@Getter
@Setter
@TableName("oj_message")
public class Message extends TimeBase {
    @TableId(type = IdType.ASSIGN_ID)
    private Long messageId;
    private Long contentId;
    private Long sendId;
//    @TableField(exist = false)
    private Long receiveId;
}
