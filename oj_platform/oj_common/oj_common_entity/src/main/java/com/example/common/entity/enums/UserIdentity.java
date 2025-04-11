package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum UserIdentity {
    ORDINARY(1, "普通用户"),
    ADMIN(2, "管理员");

    public final Integer value;
    public final String description;

    UserIdentity(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
