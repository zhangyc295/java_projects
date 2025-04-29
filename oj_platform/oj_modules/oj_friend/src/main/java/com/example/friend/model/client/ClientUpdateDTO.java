package com.example.friend.model.client;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientUpdateDTO {
    private String nickName;
    private String userName;
//    private String userPassword;

    private Integer gender;
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$",
            message = "手机号码格式不正确")
    private String telephone;
    private String email;
    private String school;
    private String major;
    private String introduction;

    private String headImage;
}
