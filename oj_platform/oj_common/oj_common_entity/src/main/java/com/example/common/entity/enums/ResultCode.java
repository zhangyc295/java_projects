package com.example.common.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {

    SUCCESS(1000, "操作成功"),

    ERROR(2000, "服务器异常，请稍后重试"),
    ERROR_TOKEN(2001, "token解析异常，请稍后重试"),

    FAILED(3000, "操作失败"),
    FAILED_UNAUTHORIZED(3001, "操作未授权"),
    FAILED_PARAMS(3002, "参数校验失败"),

    RESOURCE_EXISTS(3101, "资源已存在"),
    RESOURCE_NOT_EXISTS(3102, "资源不存在"),

    USER_EXISTS(3103, "用户已存在"),
    USER_NOT_EXISTS(3104, "用户不存在"),
    FAILED_LOGIN(3105, "用户名或密码错误"),
    USER_BANNED(3106, "账户已禁用，请联系管理员"),
    USER_TELEPHONE_NOT_EXISTS(3107,"用户手机号不存在"),
    FREQUENT_OPERATION(3108, "操作频繁，请稍后重试"),
    FREQUENT_OPERATION_ONE_DAY(3109, "操作频繁，已超出当天请求次数限制，请明日再试"),
    TELEPHONE_CODE_INVALID(3110, "验证码已过期"),
    TELEPHONE_CODE_ERROR(3111, "验证码输入有误"),
    FAILED_SEND_CODE(3112, "验证码发送失败"),
    USERNAME_EXISTS(3113, "该用户名已被占用"),
    USERNAME_NOT_EXISTS(3114, "该用户名不存在"),
    PASSWORD_ERROR(3115, "密码错误，请重试"),
    PASSWORD_NOT_EXISTS(3116, "该用户尚未设置密码，请使用验证码登录"),

    START_Error_Time(3201, "竞赛开始时间不得早于当前时间"),
    END_Error_Time(3202, "竞赛开始时间不得晚于结束时间"),
    SAME_CONTEST_TITLE(3203, "竞赛名称不能重复"),

    CONTEST_NOT_EXISTS(3204, "所选竞赛不存在"),
    QUESTION_NOT_EXISTS(3205, "所选题目不存在"),
    CONTEST_START(3206, "所选竞赛已经开始，不允许操作"),
    CONTEST_HAS_NO_QUESTION(3207, "所选竞赛不包含任何题目信息"),
    CONTEST_HAS_NO_CONTENT(3208, "竞赛信息不能为空"),
    CONTEST_ENTER(3212, "已成功报名，无法重复操作"),

    PASSWORD_EMPTY(3300, "密码不能为空"),
    PASSWORD_LENGTH_INVALID(3301, "密码长度必须在6到18位之间"),
    PASSWORD_NOT_SAME(3302, "两次输入的密码不一致"),
    FILE_UPLOAD_LIMIT(3303,"当天修改头像次数已达上限"),
    FAILED_FILE_UPLOAD(3305,"头像上传失败"),
    FILE_LARGE(3306,"头像大小不得超过1MB"),

    FIRST_QUESTION(3400,"当前题目已经是第一题"),
    LAST_QUESTION(3401,"当前题目已经是最后一题"),
    CODE_FORMAT_ERROR(3402,"请检查提交的代码格式"),

    NOT_SUPPORT(3500,"当前题目不支持此语言"),

    FAILED_RABBIT_PRODUCE(3600,"消息队列生产异常");


    private final int code;
    private final String msg;
}
