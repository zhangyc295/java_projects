package com.example.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminVO {
    @Schema(description = "用户账号")
    private String account;

    @Schema(description = "用户密码")
    private String password;
}
