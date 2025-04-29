package com.example.job.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.job.entity.Contest;
import com.example.job.mapper.ContestMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ContestXxlJob {

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private RedisService redisService;

    @XxlJob("contestListOrganizeHandler")
    public void contestListOrganizeHandler() {

        log.info("contestListOrganizeHandler --------------------------------------");
        //未完赛竞赛列表
        List<Contest> unFinishedList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                .gt(Contest::getEndTime, LocalDateTime.now()).eq(Contest::getStatus, 1).orderByDesc(Contest::getCreateTime));
        refreshCache(RedisConstants.CONTEST_UNFINISHED_LIST, unFinishedList);

        //历史竞赛列表
        List<Contest> historyList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                .le(Contest::getEndTime, LocalDateTime.now()).eq(Contest::getStatus, 1).orderByDesc(Contest::getCreateTime));
        refreshCache(RedisConstants.CONTEST_FINISHED_LIST, historyList);
    }

    public void refreshCache(String contestListKey, List<Contest> examList) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }

        Map<String, Contest> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Contest contest : examList) {
            examMap.put(getDetailKey(contest.getContestId()), contest);
            examIdList.add(contest.getContestId());
        }
        redisService.multiSet(examMap); //刷新详情缓存
        redisService.deleteObject(contestListKey);
        redisService.rightPushAll(contestListKey, examIdList); //刷新列表缓存
    }

    private String getDetailKey(Long contestId) {
        return RedisConstants.CONTEST_DETAIL + contestId;
    }


}

