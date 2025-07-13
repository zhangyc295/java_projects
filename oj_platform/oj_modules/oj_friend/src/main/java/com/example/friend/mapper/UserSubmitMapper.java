package com.example.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.friend.model.client.UserSubmit;

import java.util.List;

public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

    UserSubmit selectQuestionResult(Long userId, Long contestId, Long questionId, String submitTime);

    List<Long> selectHostQuestionList();
}