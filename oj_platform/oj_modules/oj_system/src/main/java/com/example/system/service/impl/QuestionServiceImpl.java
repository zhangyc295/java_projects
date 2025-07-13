package com.example.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.enums.QuestionDeleteFlag;
import com.example.common.entity.enums.ResultCode;
import com.example.common.security.exception.ServiceException;
import com.example.system.elasticsearch.QuestionRepository;
import com.example.system.manager.QuestionCacheManager;
import com.example.system.mapper.QuestionMapper;
import com.example.system.model.question.*;
import com.example.system.model.question.es.QuestionES;
import com.example.system.service.QuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    @Override
    public List<QuestionListVO> list(QuestionShowDTO questionShowDTO) {
        String str = questionShowDTO.getSelectedQuestionId();
        //排除已选择id
        if (StrUtil.isNotEmpty(str)) {
            String[] splitArray = str.split("_");
            Set<Long> selectedIdSet = Arrays.stream(splitArray).map(Long::valueOf).collect(Collectors.toSet());
            questionShowDTO.setSelectedQuestionIdSet(selectedIdSet);
        }
        PageHelper.startPage(questionShowDTO.getPageNumber(), questionShowDTO.getPageSize());
        //        if (questionVOList == null || questionVOList.isEmpty()) {
//            return TableResult.empty();
//        }
        return questionMapper.selectQuestionList(questionShowDTO);
    }

    @Override
    public boolean add(QuestionAddDTO questionAddDTO) {
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>().eq(Question::getTitle, questionAddDTO.getTitle()));
        if (!CollectionUtil.isEmpty(questions)) {
            throw new ServiceException(ResultCode.RESOURCE_EXISTS);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddDTO, question);
        int insert = questionMapper.insert(question);
        if (insert <= 0) {
            return false;
        }
        // 插入ES
        updateQuestionES(question);
        // 插入缓存
        questionCacheManager.addQuestion(question.getQuestionId());
        return true;
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

        updateQuestionES(question);
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

        updateQuestionES(question);
        // 删除缓存
        questionCacheManager.deleteQuestion(question.getQuestionId());
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

        updateQuestionES(question);

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

        // 插入ES
        updateQuestionES(question);
        // 插入缓存
        questionCacheManager.addQuestion(question.getQuestionId());
        return questionMapper.updateById(question);
    }

    private void updateQuestionES(Question question) {
        QuestionES questionES = new QuestionES();
        BeanUtils.copyProperties(question, questionES);
        questionRepository.save(questionES);
    }
}
