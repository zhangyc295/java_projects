package com.example.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.friend.model.contest.Contest;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestShowDTO;

import java.util.List;

public interface ContestMapper extends BaseMapper<Contest> {
    List<ContestListVO> selectContestList(ContestShowDTO contestShowDTO);
}
