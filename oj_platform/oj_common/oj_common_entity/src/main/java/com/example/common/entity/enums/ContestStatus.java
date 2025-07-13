package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum ContestStatus {
    UNPUBLISH(0, "未发布"),
    PUBLISH(1, "已发布");

    private final Integer value;
    private final String description;

    ContestStatus(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
