package com.example.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.job.entity.ClientContest;
import com.example.job.entity.RankInfo;

import java.util.List;

public interface UserContestMapper extends BaseMapper<ClientContest> {
    void updateUserScore(List<RankInfo> rankInfoList);
}
