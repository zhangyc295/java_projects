package com.example.common.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {

    SUCCESS(1000, "操作成功"),

    ERROR(2000, "服务器异常，请稍后重试"),

    FAILED(3000, "操作失败"),
    FAILED_UNAUTHORIZED(3001, "操作未授权"),
    FAILED_PARAMS(3002, "参数校验失败"),

    RESOURCE_EXISTS(3101, "资源已存在"),
    RESOURCE_NOT_EXISTS(3102, "资源不存在"),

    USER_EXISTS(3103, "用户已存在"),
    USER_NOT_EXISTS(3104, "用户不存在"),
    FAILED_LOGIN(3105, "用户名或密码错误"),
    USER_BANNED(3106, "账户已禁用，请联系管理员");

    private final int code;
    private final String msg;
}
