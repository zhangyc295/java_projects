package com.example.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.friend.model.client.ClientContest;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestRankVO;

import java.util.List;

public interface ClientContestMapper extends BaseMapper <ClientContest>{
    List<ContestListVO> selectMyContestList(Long userId);

    List<ContestRankVO> selectContestRankList(Long contestId);
}
