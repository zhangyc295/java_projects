package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.model.question.*;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    List<QuestionListVO> selectQuestionList(QuestionShowDTO questionShowDTO);

    List<QuestionDeletedVO> selectDeletedQuestionList(QuestionDeletedDTO questionDeletedDTO);
}
