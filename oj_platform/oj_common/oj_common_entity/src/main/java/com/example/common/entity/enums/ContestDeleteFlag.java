package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum ContestDeleteFlag {
    NORMAL(0, "未删除"),
    DELETED(1, "已删除");

    private final int code;
    private final String description;

    ContestDeleteFlag(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
