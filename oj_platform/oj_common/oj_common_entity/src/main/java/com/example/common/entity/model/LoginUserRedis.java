package com.example.common.entity.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRedis {

    private String nickName;
    private Integer identity;
    // 1表示普通用户  2表示管理员
    private String headImage;
}
