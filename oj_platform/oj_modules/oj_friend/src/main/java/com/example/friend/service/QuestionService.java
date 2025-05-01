package com.example.friend.service;

import com.example.common.entity.model.TableResult;
import com.example.friend.model.question.HotQuestionVO;
import com.example.friend.model.question.QuestionDetailVO;
import com.example.friend.model.question.QuestionShowDTO;

import java.util.List;

public interface QuestionService {
    TableResult list(QuestionShowDTO questionShowDTO);

    QuestionDetailVO detail(Long questionId);

    String nextQuestion(Long questionId);

    String prevQuestion(Long questionId);

    List<HotQuestionVO> getHotQuestion();
}
