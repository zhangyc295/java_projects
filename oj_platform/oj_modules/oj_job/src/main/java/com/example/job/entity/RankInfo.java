package com.example.job.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankInfo {

    private Long contestId;
    private Long userId;
    private int score;
    private int contestRank;
}
