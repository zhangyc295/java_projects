package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.model.contest.Contest;
import com.example.system.model.contest.ContestEditDTO;
import com.example.system.model.contest.ContestListVO;
import com.example.system.model.contest.ContestShowDTO;

import java.util.List;

public interface ContestMapper extends BaseMapper<Contest> {
    List<ContestListVO> selectContestList(ContestShowDTO contestShowDTO);

}
