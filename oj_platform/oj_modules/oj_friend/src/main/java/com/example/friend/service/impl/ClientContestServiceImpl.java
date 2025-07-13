package com.example.friend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.enums.ContestListType;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.model.TableResult;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.security.exception.ServiceException;
import com.example.common.security.service.TokenService;
import com.example.friend.manager.ContestCacheManager;
import com.example.friend.mapper.ClientContestMapper;
import com.example.friend.mapper.ContestMapper;
import com.example.friend.model.client.ClientContest;
import com.example.friend.model.contest.Contest;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestShowDTO;
import com.example.friend.service.ClientContestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientContestServiceImpl implements ClientContestService {

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ClientContestMapper clientContestMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ContestCacheManager contestCacheManager;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public int enter(String token, Long contestId) {
        Contest contest = contestMapper.selectById(contestId);
        if (contest == null) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
//        Long userId = tokenService.getUserId(token, secret);
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        ClientContest clientContest = clientContestMapper.selectOne(new LambdaQueryWrapper<ClientContest>()
                .eq(ClientContest::getContestId, contestId).eq(ClientContest::getUserId, userId));
        if (clientContest != null) {
            throw new ServiceException(ResultCode.CONTEST_ENTER);
        }
        contestCacheManager.addUserContestCache(userId, contestId);
        clientContest = new ClientContest();
        clientContest.setContestId(contestId);
        clientContest.setUserId(userId);
        return clientContestMapper.insert(clientContest);
    }

    @Override
    public TableResult list(ContestShowDTO contestShowDTO) {
//        if (contestShowDTO.getStartTime() != null || contestShowDTO.getEndTime() != null) {
//            // 有时间筛选，直接走数据库
//            List<ContestListVO> contestVOList = list(contestShowDTO);
//            long total = contestVOList.size(); // 可改为用分页查询的总数
//            return TableResult.success(contestVOList, total);
//        }

        //从redis当中获取  我的竞赛列表的数据
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
//        System.out.println(userId);
        Long total = contestCacheManager.getListSize(ContestListType.USER_CONTEST_LIST.getValue(), userId);
//        System.out.println(total);
        contestShowDTO.setType(ContestListType.USER_CONTEST_LIST.getValue());
        List<ContestListVO> contestVOList;
        if (total == null || total <= 0) {
            PageHelper.startPage(contestShowDTO.getPageNumber(), contestShowDTO.getPageSize());
            contestVOList = clientContestMapper.selectMyContestList(userId);

            // 刷新缓存
            contestCacheManager.refreshCache(ContestListType.USER_CONTEST_LIST.getValue(), userId);
            total = new PageInfo<>(contestVOList).getTotal();
        } else {
            contestShowDTO.setType(ContestListType.USER_CONTEST_LIST.getValue());
            contestVOList = contestCacheManager.getContestVOList(contestShowDTO, userId);
            total = contestCacheManager.getListSize(contestShowDTO.getType(), userId);
        }
        if (CollectionUtil.isEmpty(contestVOList)) {
            return TableResult.empty();
        }
//        assembleContestVOList(contestVOList);
        return TableResult.success(contestVOList, total);
    }
}
