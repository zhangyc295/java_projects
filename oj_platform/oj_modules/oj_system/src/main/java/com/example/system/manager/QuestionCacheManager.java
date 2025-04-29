package com.example.system.manager;

import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManager {
    @Autowired
    private RedisService redisService;

    public void addQuestion(Long questionId){
        redisService.leftPushForList(RedisConstants.QUESTION_LIST, questionId);
    }
    public void deleteQuestion(Long questionId){
        redisService.removeForList(RedisConstants.QUESTION_LIST, questionId);
    }
}
