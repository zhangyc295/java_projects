package com.example.system.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListVO {
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
