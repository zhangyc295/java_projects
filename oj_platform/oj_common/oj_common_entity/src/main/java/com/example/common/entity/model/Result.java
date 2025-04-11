package com.example.common.entity.model;

import com.example.common.entity.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {
    private int code;
    private String msg;
    private T data;    //接口返回内容

    public static <T> Result<T> success() {
        return formResult(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return formResult(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> fail() {
        return formResult(ResultCode.FAILED, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return formResult(resultCode, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return assembleResult(code, msg, null);
    }

    private static <T> Result<T> formResult(ResultCode resultCode, T data) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(data);
        return result;
    }

    private static <T> Result<T> assembleResult(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
