package com.example.friend.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ContestListType;
import com.example.common.entity.enums.ResultCode;
import com.example.common.redis.RedisService;
import com.example.common.security.exception.ServiceException;
import com.example.friend.mapper.ClientContestMapper;
import com.example.friend.mapper.ContestMapper;
import com.example.friend.mapper.ContestQuestionMapper;
import com.example.friend.model.client.ClientContest;
import com.example.friend.model.contest.*;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ContestCacheManager {

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ContestQuestionMapper contestQuestionMapper;

    @Autowired
    private ClientContestMapper clientContestMapper;

    @Autowired
    private RedisService redisService;

    public Long getListSize(Integer contestListType, Long userId) {
        String contestListKey = getContestListKey(contestListType, userId);
        return redisService.getListSize(contestListKey);
    }

    public Long getContestQuestionListSize(Long contestId) {
        String contestQuestionListKey = getContestQuestionListKey(contestId);
        return redisService.getListSize(contestQuestionListKey);
    }

    public Long getRankListSize(Long contestId) {
        return redisService.getListSize(getRankListKey(contestId));
    }

    public List<ContestListVO> getContestVOList(ContestShowDTO contestShowDTO, Long userId) {
        int start = (contestShowDTO.getPageNumber() - 1) * contestShowDTO.getPageSize();
        int end = start + contestShowDTO.getPageSize() - 1; //下标需要 -1

        String contestListKey = getContestListKey(contestShowDTO.getType(), userId);
        List<Long> contestIdList = redisService.getCacheListByRange(contestListKey, start, end, Long.class);
        List<ContestListVO> contestVOList = assembleContestVOList(contestIdList);
        if (CollectionUtil.isEmpty(contestVOList)) {

            //说明redis中数据可能有问题 从数据库中查数据并且重新刷新缓存
            contestVOList = getExamListByDB(contestShowDTO, userId); //从数据库中获取数据
            refreshCache(contestShowDTO.getType(), userId);
        }
        return contestVOList;
    }
    public List<ContestRankVO> getContestRankVOList(ContestRankDTO contestRankDTO) {
        int start = (contestRankDTO.getPageNumber() - 1) * contestRankDTO.getPageSize();
        int end = start + contestRankDTO.getPageSize() - 1; //下标需要 -1
        return redisService.getCacheListByRange(getRankListKey(contestRankDTO.getContestId()), start, end, ContestRankVO.class);
    }

    public List<Long> getAllUserContestList(Long userId) {
        String contestListKey = RedisConstants.CLIENT_CONTEST_LIST + userId;
        List<Long> userContestIdList = redisService.getCacheListByRange(contestListKey, 0, -1, Long.class);
        if (CollectionUtil.isNotEmpty(userContestIdList)) {
            return userContestIdList;
        }
        List<ClientContest> userContestList = clientContestMapper.selectList(new LambdaQueryWrapper<ClientContest>()
                .eq(ClientContest::getUserId, userId));
        if (CollectionUtil.isEmpty(userContestList)) {
            return null;
        }
        refreshCache(ContestListType.USER_CONTEST_LIST.getValue(), userId);
        return userContestList.stream().map(ClientContest::getContestId).collect(Collectors.toList());
    }

    //
    public void addUserContestCache(Long userId, Long contestId) {
        String userContestListKey = getUserContestListKey(userId);
        redisService.leftPushForList(userContestListKey, contestId);
    }

    public Long getFirstQuestion(Long contestId) {
        return redisService.index(getContestQuestionListKey(contestId), 0, Long.class);
    }

    public Long prevQuestion(Long contestId, Long questionId) {
        Long index = redisService.indexOf(getContestQuestionListKey(contestId), questionId);
        if (index == 0) {
            throw new ServiceException(ResultCode.FIRST_QUESTION);
        }
        return redisService.index(getContestQuestionListKey(contestId), index - 1, Long.class);
    }

    public Long nextQuestion(Long contestId, Long questionId) {
        Long index = redisService.indexOf(getContestQuestionListKey(contestId), questionId);
        long lastIndex = getContestQuestionListSize(contestId) - 1;
        if (index == lastIndex) {
            throw new ServiceException(ResultCode.LAST_QUESTION);
        }
        return redisService.index(getContestQuestionListKey(contestId), index + 1, Long.class);
    }

    //刷新缓存逻辑
    public void refreshCache(Integer contestListType, Long userId) {
        List<Contest> contestList = new ArrayList<>();
        if (ContestListType.CONTEST_UNFINISHED_LIST.getValue().equals(contestListType)) {
            //查询未完赛的竞赛列表
            contestList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                    .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                    .gt(Contest::getEndTime, LocalDateTime.now())
                    .eq(Contest::getStatus, 1)
                    .orderByDesc(Contest::getCreateTime));
        } else if (ContestListType.CONTEST_FINISHED_LIST.getValue().equals(contestListType)) {
            //查询历史竞赛
            contestList = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                    .select(Contest::getContestId, Contest::getTitle, Contest::getStartTime, Contest::getEndTime)
                    .le(Contest::getEndTime, LocalDateTime.now())
                    .eq(Contest::getStatus, 1)
                    .orderByDesc(Contest::getCreateTime));
        } else if (ContestListType.USER_CONTEST_LIST.getValue().equals(contestListType)) {
            List<ContestListVO> contestVOList = clientContestMapper.selectMyContestList(userId);
            contestList = BeanUtil.copyToList(contestVOList, Contest.class);
            //查询我的竞赛列表
        }
        // 没有数据时
        if (CollectionUtil.isEmpty(contestList)) {
            return;
        }

        Map<String, Contest> contestMap = new HashMap<>();
        List<Long> contestIdList = new ArrayList<>();
        for (Contest contest : contestList) {
            contestMap.put(getDetailKey(contest.getContestId()), contest);
            contestIdList.add(contest.getContestId());
        }
        redisService.multiSet(contestMap);  //刷新详情缓存
        redisService.deleteObject(getContestListKey(contestListType, userId));
        redisService.rightPushAll(getContestListKey(contestListType, userId), contestIdList);      //刷新列表缓存
    }

    public void refreshContestQuestionCache(Long contestId) {
        List<ContestQuestion> contestQuestionList = contestQuestionMapper.selectList(new LambdaQueryWrapper<ContestQuestion>()
                .select(ContestQuestion::getQuestionId)
                .eq(ContestQuestion::getContestId, contestId)
                .orderByAsc(ContestQuestion::getQuestionOrder));
        if (CollectionUtil.isEmpty(contestQuestionList)) {
            return;
        }
        List<Long> contestQuestionIdList = contestQuestionList.stream().map(ContestQuestion::getQuestionId).toList();
        redisService.rightPushAll(getContestQuestionListKey(contestId), contestQuestionIdList);
        //节省 redis缓存资源  过期时间
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        redisService.expire(getContestQuestionListKey(contestId), seconds, TimeUnit.SECONDS);
    }

    public void refreshContestRankCache(Long contestId) {
        List<ContestRankVO> contestRankVOList = clientContestMapper.selectContestRankList(contestId);
        if (CollectionUtil.isEmpty(contestRankVOList)) {
            return;
        }
        redisService.rightPushAll(getRankListKey(contestId), contestRankVOList);
    }

    private List<ContestListVO> getExamListByDB(ContestShowDTO contestShowDTO, Long userId) {
        PageHelper.startPage(contestShowDTO.getPageNumber(), contestShowDTO.getPageSize());
//        return contestMapper.selectContestList(contestShowDTO);
        if (ContestListType.USER_CONTEST_LIST.getValue().equals(contestShowDTO.getType())) {
            //查询我的竞赛列表
            return clientContestMapper.selectMyContestList(userId);
        } else {
            //查询C端的竞赛列表
            return contestMapper.selectContestList(contestShowDTO);
        }
    }

    private List<ContestListVO> assembleContestVOList(List<Long> contestIdList) {
        if (CollectionUtil.isEmpty(contestIdList)) {
            //说明redis当中没数据 从数据库中查数据并且重新刷新缓存
            return null;
        }
        //拼接redis当中key的方法 并且将拼接好的key存储到一个list中
        List<String> detailKeyList = new ArrayList<>();
        for (Long contestId : contestIdList) {
            detailKeyList.add(getDetailKey(contestId));
        }
        List<ContestListVO> contestVOList = redisService.multiGet(detailKeyList, ContestListVO.class);
        CollUtil.removeNull(contestVOList);
        if (CollectionUtil.isEmpty(contestVOList) || contestVOList.size() != contestIdList.size()) {
            //说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
            return null;
        }
        return contestVOList;
    }

    private String getContestListKey(Integer contestListType, Long userId) {
        if (ContestListType.CONTEST_UNFINISHED_LIST.getValue().equals(contestListType)) {
            return RedisConstants.CONTEST_UNFINISHED_LIST;
        } else if (ContestListType.CONTEST_FINISHED_LIST.getValue().equals(contestListType)) {
            return RedisConstants.CONTEST_FINISHED_LIST;
        } else {
            return RedisConstants.CLIENT_CONTEST_LIST + userId;
        }
    }


    private String getDetailKey(Long contestId) {
        return RedisConstants.CONTEST_DETAIL + contestId;
    }

    private String getUserContestListKey(Long clientId) {
        return RedisConstants.CLIENT_CONTEST_LIST + clientId;
    }

    private String getContestQuestionListKey(Long contestId) {
        return RedisConstants.CONTEST_QUESTION_LIST + contestId;
    }

    public String getRankListKey(Long contestId) {
        return RedisConstants.CONTEST_RANK_LIST + contestId;
    }
}