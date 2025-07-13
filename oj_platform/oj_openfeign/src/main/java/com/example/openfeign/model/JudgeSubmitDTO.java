package com.example.openfeign.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JudgeSubmitDTO {

    private Long userId;

    private Long contestId;

    //编程语言类型（0 java 1 C++）
    private Integer programLanguage;

    private Long questionId;

    //题目难度
    private Integer difficulty;

    //时间限制 ms
    private Long timeLimit;

    //空间限制 kb
    private Long spaceLimit;

    //拼装完整的用户代码  用户提交的代码 + main函数
    private String submitCode;

    //输入数据
    private List<String> inputList;

    //期望输出数据
    private List<String> outputList;
}
