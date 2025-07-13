package com.example.system.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 关键注解：强制转为字符串
    private Long userId;
    private String userName;
    private String nickName;

    private Integer gender;
    private String telephone;
    private String email;
    private String school;
    private String major;
    private String introduction;
    private Integer status;
}
