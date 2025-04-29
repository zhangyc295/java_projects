package com.example.friend.service;

import com.example.common.entity.model.TableResult;
import com.example.friend.model.quesition.QuestionDetailVO;
import com.example.friend.model.quesition.QuestionShowDTO;

public interface QuestionService {
    TableResult list(QuestionShowDTO questionShowDTO);

    QuestionDetailVO detail(Long questionId);

    String nextQuestion(Long questionId);

    String prevQuestion(Long questionId);
}
