package com.example.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("oj_contest")
public class Contest extends TimeBase {
    @TableId(type = IdType.ASSIGN_ID)
    private Long contestId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer status;

    private Integer deleteFlag;  //0未删除1已删除
}
