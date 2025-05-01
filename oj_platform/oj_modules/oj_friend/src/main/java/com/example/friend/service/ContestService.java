package com.example.friend.service;

import com.example.common.entity.model.TableResult;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestRankDTO;
import com.example.friend.model.contest.ContestShowDTO;

import java.util.List;

public interface ContestService {
    List<ContestListVO> list(ContestShowDTO contestShowDTO);

    TableResult redisList(ContestShowDTO contestShowDTO);

    String firstQuestion(Long contestId);

    String nextQuestion(Long contestId, Long questionId);

    String prevQuestion(Long contestId, Long questionId);

    TableResult rankList(ContestRankDTO contestRankDTO);
}
