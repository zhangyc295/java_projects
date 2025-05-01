package com.example.friend.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ResultCode;
import com.example.common.redis.RedisService;
import com.example.common.security.exception.ServiceException;
import com.example.friend.mapper.QuestionMapper;
import com.example.friend.model.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManager {
    @Autowired
    private RedisService redisService;

    @Autowired
    private QuestionMapper questionMapper;

    public Long getListSize() {
        return redisService.getListSize(RedisConstants.QUESTION_LIST);
    }

    public void refreshCache() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId).orderByDesc(Question::getCreateTime));
        if (CollectionUtil.isEmpty(questionList)) {
            return;
        }
        List<Long> IdList = questionList.stream().map(Question::getQuestionId).toList();
        redisService.rightPushAll(RedisConstants.QUESTION_LIST, IdList);
        // 新创建的题目在前面
    }

    public Long prevQuestion(Long questionId) {
        Long index = redisService.indexOf(RedisConstants.QUESTION_LIST, questionId);
        if (index == 0) {
            throw new ServiceException(ResultCode.FIRST_QUESTION);
        }
        return redisService.index(RedisConstants.QUESTION_LIST, index - 1, Long.class);
    }

    public Long nextQuestion(Long questionId) {
        Long index = redisService.indexOf(RedisConstants.QUESTION_LIST, questionId);
        if (index == getListSize() - 1) {
            throw new ServiceException(ResultCode.LAST_QUESTION);
        }
        return redisService.index(RedisConstants.QUESTION_LIST, index + 1, Long.class);
    }
}
