package com.example.system.manager;

import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.system.model.contest.Contest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContestCacheManager {

    @Autowired
    private RedisService redisService;

    public void addCache(Contest contest) {
        redisService.leftPushForList(getContestListKey(), contest.getContestId());
        redisService.setCacheObject(getDetailKey(contest.getContestId()), contest);
    }

    public void deleteCache(Long contestId) {
        redisService.removeForList(getContestListKey(), contestId);
        redisService.deleteObject(getDetailKey(contestId));
        redisService.deleteObject(getContestQuestionListKey(contestId));
    }

    private String getContestListKey() {
        return RedisConstants.CONTEST_UNFINISHED_LIST;
    }

    private String getDetailKey(Long contestId) {
        return RedisConstants.CONTEST_DETAIL + contestId;
    }

    private String getContestQuestionListKey(Long contestId) {
        return RedisConstants.CONTEST_QUESTION_LIST + contestId;
    }
}
