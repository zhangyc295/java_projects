package com.example.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.enums.QuestionDeleteFlag;
import com.example.common.entity.enums.ResultCode;
import com.example.common.security.exception.ServiceException;
import com.example.system.mapper.QuestionMapper;
import com.example.system.model.question.*;
import com.example.system.service.QuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionListVO> list(QuestionShowDTO questionShowDTO) {
        PageHelper.startPage(questionShowDTO.getPageNumber(), questionShowDTO.getPageSize());
        //        if (questionVOList == null || questionVOList.isEmpty()) {
//            return TableResult.empty();
//        }
        return questionMapper.selectQuestionList(questionShowDTO);
    }

    @Override
    public int add(QuestionAddDTO questionAddDTO) {
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>().eq(Question::getTitle, questionAddDTO.getTitle()));
        if (!CollectionUtil.isEmpty(questions)) {
            throw new ServiceException(ResultCode.RESOURCE_EXISTS);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddDTO, question);
        return questionMapper.insert(question);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtils.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question question = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (question == null) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        question.setTitle(questionEditDTO.getTitle());
        question.setDifficulty(questionEditDTO.getDifficulty());
        question.setTimeLimit(questionEditDTO.getTimeLimit());
        question.setSpaceLimit(questionEditDTO.getSpaceLimit());
        question.setContent(questionEditDTO.getContent());
        question.setQuestionCase(questionEditDTO.getQuestionCase());
        question.setDefaultCode(questionEditDTO.getDefaultCode());
        question.setMainFunc(questionEditDTO.getMainFunc());
        return questionMapper.updateById(question);
    }

    @Override
    public int deleteToBin(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question.getDeleteFlag() == QuestionDeleteFlag.IN_RECYCLE_BIN.getCode()
                || question.getDeleteFlag() == QuestionDeleteFlag.DELETED.getCode()) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        question.setDeleteFlag(QuestionDeleteFlag.IN_RECYCLE_BIN.getCode());
        return questionMapper.updateById(question);
    }

    @Override
    public List<QuestionDeletedVO> deletedList(QuestionDeletedDTO questionDeletedDTO) {
        //        if (questionVOList == null || questionVOList.isEmpty()) {
//            return TableResult.empty();
//        }
        return questionMapper.selectDeletedQuestionList(questionDeletedDTO);
    }

    @Override
    public int deleteBin(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question.getDeleteFlag() == QuestionDeleteFlag.NORMAL.getCode()
                || question.getDeleteFlag() == QuestionDeleteFlag.DELETED.getCode()) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        question.setDeleteFlag(QuestionDeleteFlag.DELETED.getCode());
        return questionMapper.updateById(question);
    }

    @Override
    public int recoverBin(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question.getDeleteFlag() == QuestionDeleteFlag.NORMAL.getCode()
                || question.getDeleteFlag() == QuestionDeleteFlag.DELETED.getCode()) {
            throw new ServiceException(ResultCode.RESOURCE_NOT_EXISTS);
        }
        question.setDeleteFlag(QuestionDeleteFlag.NORMAL.getCode());
        return questionMapper.updateById(question);
    }
}
