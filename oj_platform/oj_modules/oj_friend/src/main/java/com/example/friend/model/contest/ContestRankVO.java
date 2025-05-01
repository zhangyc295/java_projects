package com.example.friend.model.contest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContestRankVO {
    private Long userId;
    private String nickName;
    private int ContestRank;
    private int score;
}
