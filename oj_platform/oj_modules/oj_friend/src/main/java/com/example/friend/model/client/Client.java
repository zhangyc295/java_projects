package com.example.friend.model.client;

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
@TableName("oj_user")
public class Client extends TimeBase {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    private String nickName;
    private String userName;
    private String userPassword;

    private String headImage;
    private Integer gender;
    private String telephone;
    private String code;
    private String email;
    private String school;
    private String major;
    private String introduction;
    private Integer status;

}
