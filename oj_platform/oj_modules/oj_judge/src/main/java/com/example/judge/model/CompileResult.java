package com.example.judge.model;

import lombok.Data;

@Data
public class CompileResult {

    private boolean compiled;  //编译是否成功

    private String errorMessage;  //编译输出信息 （错误信息）
}