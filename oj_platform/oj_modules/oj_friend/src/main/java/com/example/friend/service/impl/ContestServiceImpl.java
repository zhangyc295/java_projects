package com.example.friend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.model.TableResult;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.friend.manager.ClientCacheManager;
import com.example.friend.manager.ContestCacheManager;
import com.example.friend.mapper.ClientContestMapper;
import com.example.friend.mapper.ContestMapper;
import com.example.friend.model.client.ClientDetailVO;
import com.example.friend.model.contest.ContestRankDTO;
import com.example.friend.model.contest.ContestRankVO;
import com.example.friend.model.contest.ContestShowDTO;
import com.example.friend.service.ContestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.friend.model.contest.ContestListVO;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {

    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private ContestCacheManager contestCacheManager;
    @Autowired
    private ClientContestMapper clientContestMapper;
    @Autowired
    private ClientCacheManager clientCacheManager;

    @Override
    public List<ContestListVO> list(ContestShowDTO contestShowDTO) {
        PageHelper.startPage(contestShowDTO.getPageNumber(), contestShowDTO.getPageSize());
        return contestMapper.selectContestList(contestShowDTO);
    }

    @Override
    public TableResult redisList(ContestShowDTO contestShowDTO) {
        if (contestShowDTO.getStartTime() != "" || contestShowDTO.getEndTime() != "") {
            // 有时间筛选，直接走数据库
            List<ContestListVO> contestVOList = list(contestShowDTO);
            assembleContestVOList(contestVOList);
            long total = contestVOList.size(); // 可改为用分页查询的总数
            return TableResult.success(contestVOList, total);
        }
        //从redis当中获取  竞赛列表的数据
        Long total = contestCacheManager.getListSize(contestShowDTO.getType(), null);
        List<ContestListVO> contestVOList;
        if (total == null || total <= 0) {
            contestVOList = list(contestShowDTO);
            // 刷新缓存
            contestCacheManager.refreshCache(contestShowDTO.getType(), null);
            total = new PageInfo<>(contestVOList).getTotal();
        } else {
            contestVOList = contestCacheManager.getContestVOList(contestShowDTO, null);
            total = contestCacheManager.getListSize(contestShowDTO.getType(), null);
        }
        if (CollectionUtil.isEmpty(contestVOList)) {
            return TableResult.empty();
        }
        assembleContestVOList(contestVOList);
        return TableResult.success(contestVOList, total);
    }

    @Override
    public TableResult rankList(ContestRankDTO contestRankDTO) {
        //从redis当中获取  排名列表的数据
        Long total = contestCacheManager.getRankListSize(contestRankDTO.getContestId());
        List<ContestRankVO> contestRankVOList;
        if (total == null || total <= 0) {
            PageHelper.startPage(contestRankDTO.getPageNumber(), contestRankDTO.getPageSize());
            contestRankVOList = clientContestMapper.selectContestRankList(contestRankDTO.getContestId());
            contestCacheManager.refreshContestRankCache(contestRankDTO.getContestId());
            total = new PageInfo<>(contestRankVOList).getTotal();
        } else {
            contestRankVOList = contestCacheManager.getContestRankVOList(contestRankDTO);
        }
        if (CollectionUtil.isEmpty(contestRankVOList)) {
            return TableResult.empty();
        }
        assembleContestRankVOList(contestRankVOList);
//        System.out.println(contestRankVOList.get(0).getNickName());
        return TableResult.success(contestRankVOList, total);
    }

    private void assembleContestRankVOList(List<ContestRankVO> contestRankVOList) {
        if (CollectionUtil.isEmpty(contestRankVOList)) {
            return;
        }
        for (ContestRankVO contestRankVO : contestRankVOList) {
            Long userId = contestRankVO.getUserId();
            ClientDetailVO client = clientCacheManager.getClientById(userId);
            contestRankVO.setNickName(client.getNickName());
        }
    }

    @Override
    public String firstQuestion(Long contestId) {
        Long listSize = contestCacheManager.getContestQuestionListSize(contestId);
        if (listSize == null || listSize == 0) {
            contestCacheManager.refreshContestQuestionCache(contestId);
        }
        return contestCacheManager.getFirstQuestion(contestId).toString();
    }

    @Override
    public String nextQuestion(Long contestId, Long questionId) {
        Long listSize = contestCacheManager.getContestQuestionListSize(contestId);
        if (listSize == null || listSize == 0) {
            contestCacheManager.refreshContestQuestionCache(contestId);
        }
        return contestCacheManager.nextQuestion(contestId, questionId).toString();
    }

    @Override
    public String prevQuestion(Long contestId, Long questionId) {
        Long listSize = contestCacheManager.getContestQuestionListSize(contestId);
        if (listSize == null || listSize == 0) {
            contestCacheManager.refreshContestQuestionCache(contestId);
        }
        return contestCacheManager.prevQuestion(contestId, questionId).toString();
    }

    private void assembleContestVOList(List<ContestListVO> contestVOList) {
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
//        System.out.println(userId);
        List<Long> list = contestCacheManager.getAllUserContestList(userId);
//        System.out.println(list);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
//        System.out.println(contestVOList);
        for (ContestListVO contestListVO : contestVOList) {

            if (list.contains(contestListVO.getContestId())) {
                contestListVO.setEnter(true);
//                System.out.println("确定执行");
            }
        }
    }

}
