package com.example.system.service;

import com.example.system.model.question.*;

import java.util.List;

public interface QuestionService {
    List<QuestionListVO> list(QuestionShowDTO questionShowDTO);

    boolean add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(Long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int deleteToBin(Long questionId);

    List<QuestionDeletedVO> deletedList(QuestionDeletedDTO questionDeletedDTO);

    int deleteBin(Long questionId);

    int recoverBin(Long questionId);
}
