package com.example.friend.service;

import com.example.common.entity.model.TableResult;
import com.example.friend.model.contest.ContestShowDTO;

public interface ClientContestService {
    int enter(String token, Long contestId);

    TableResult list(ContestShowDTO contestShowDTO);
}
