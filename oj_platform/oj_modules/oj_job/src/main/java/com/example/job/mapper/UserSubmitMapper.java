package com.example.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.job.entity.RankInfo;
import com.example.job.entity.UserSubmit;

import java.util.List;
import java.util.Set;

public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

    List<RankInfo> selectUserScoreList(Set<Long> contestIdSet);
}