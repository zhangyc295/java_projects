package com.example.openfeign.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitResultDetail {

    private String input;

    private String output; //期望输出

    private String executeOutput;   //实际执行输出

}