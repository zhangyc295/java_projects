package com.example.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.friend.model.question.HotQuestionVO;
import com.example.friend.model.question.Question;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    List<HotQuestionVO> selectTitleByIds(List<Long> ids);
}
