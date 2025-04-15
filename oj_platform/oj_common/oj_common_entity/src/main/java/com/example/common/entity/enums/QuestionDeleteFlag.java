package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum QuestionDeleteFlag {
    NORMAL(0, "未删除"),
    IN_RECYCLE_BIN(1, "回收站"),
    DELETED(2, "已删除");

    private final int code;
    private final String description;

    QuestionDeleteFlag(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
