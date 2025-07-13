package com.example.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_contest_mapping")
public class ClientContest extends TimeBase {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    private Long userContestId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long contestId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Integer score;
    private Integer contestRank;
}
