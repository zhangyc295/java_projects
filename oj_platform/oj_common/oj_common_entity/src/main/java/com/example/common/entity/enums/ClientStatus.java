package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum ClientStatus {
    NORMAL(1, "正常"),
    LIMIT(0, "拉黑");

    private final Integer value;
    private final String description;

    ClientStatus(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
