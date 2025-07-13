package com.example.system.service;

import com.example.system.model.contest.*;

import java.util.List;

public interface ContestService {
    List<ContestListVO> list(ContestShowDTO contestShowDTO);

    String add(ContestAddDTO contestAddDTO);

    boolean questionAdd(ContestQuestionAddDTO contestQuestionAddDTO);

    ContestDetailVO detail(Long contestId);

    int edit(ContestEditDTO contestEditDTO);

    int questionDelete(Long contestId, Long questionId);

    int delete(Long contestId);

    int publish(Long contestId);

    int cancelPublish(Long contestId);
}