package com.example.common.entity.enums;

import lombok.Getter;

@Getter
public enum ContestListType {

    CONTEST_UNFINISHED_LIST(0),

    CONTEST_FINISHED_LIST(1),

    USER_CONTEST_LIST(2);
    // 我的竞赛列表
    private final Integer value;

    ContestListType(Integer value) {
        this.value = value;
    }
}