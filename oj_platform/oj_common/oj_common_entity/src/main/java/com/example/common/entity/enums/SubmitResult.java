package com.example.common.entity.enums;

import lombok.Getter;

// 答题结果
@Getter
public enum SubmitResult {
    ERROR(0),
    PASS(1),
    UNSUBMIT(2),
    JUDGING(3);    //判题中

    private final int value;
    SubmitResult(int value) {
        this.value = value;
    }
}
