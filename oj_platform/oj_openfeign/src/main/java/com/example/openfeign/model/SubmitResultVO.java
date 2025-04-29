package com.example.openfeign.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitResultVO {
    //是够通过标识
    private Integer pass; // 0  未通过  1 通过

    private String errorMessage; //异常信息

    private List<SubmitResultDetail> submitResultDetail;

    @JsonIgnore
    private Integer score;
}


