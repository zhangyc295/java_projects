package com.example.common.entity.enums;

import lombok.Getter;

// 代码可选编程语言类型
@Getter
public enum LanguageType {
    Java(0),
    Python(1);

    private final Integer value;

    LanguageType(Integer value) {
        this.value = value;
    }
}
