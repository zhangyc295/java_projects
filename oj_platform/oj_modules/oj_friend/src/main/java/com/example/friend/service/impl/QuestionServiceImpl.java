package com.example.friend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.enums.QuestionDeleteFlag;
import com.example.common.entity.model.TableResult;
import com.example.friend.elasticsearch.QuestionRepository;
import com.example.friend.manager.QuestionCacheManager;
import com.example.friend.mapper.QuestionMapper;
import com.example.friend.mapper.UserSubmitMapper;
import com.example.friend.model.question.*;
import com.example.friend.model.question.es.QuestionES;
import com.example.friend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public TableResult list(QuestionShowDTO questionShowDTO) {
        long count = questionRepository.count();
        if (count <= 0) {
            refreshQuestion();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(questionShowDTO.getPageNumber() - 1, questionShowDTO.getPageSize(), sort);
        Integer difficulty = questionShowDTO.getDifficulty();
        String keyword = questionShowDTO.getKeyword();

        Page<QuestionES> questionESPage;
        if (difficulty == null && StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findAll(pageable);
            System.out.println(questionESPage);
        } else if (StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else if (difficulty == null) {
            questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else {
            questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }
        long totalElements = questionESPage.getTotalElements();
        if (totalElements <= 0) {
            return TableResult.empty();
        }
        List<QuestionES> esList = questionESPage.getContent();
        List<QuestionListVO> voList = BeanUtil.copyToList(esList, QuestionListVO.class);
        return TableResult.success(voList, totalElements);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
//        System.out.println(questionId);
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        refreshQuestion();
        BeanUtil.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public String nextQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize == 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.nextQuestion(questionId).toString();
    }

    @Override
    public String prevQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize == 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.prevQuestion(questionId).toString();
    }

    public List<HotQuestionVO> getHotQuestion() {
        List<Long> HostQuestionList = userSubmitMapper.selectHostQuestionList();
        return questionMapper.selectTitleByIds(HostQuestionList);
    }

    // 从数据库查询 并存到ES中
    private void refreshQuestion() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getDeleteFlag, QuestionDeleteFlag.NORMAL.getCode()));
//        System.out.println(questionList.size());
//        System.out.println(questionList.get(0).getTitle());
//        System.out.println(questionList.get(0).getQuestionId());
        if (CollectionUtil.isEmpty(questionList)) {
            return;
        }
        List<QuestionES> questionESList = BeanUtil.copyToList(questionList, QuestionES.class);
//        questionESList.forEach(questionES -> System.out.println(questionES.getQuestionId() + ": " + questionES.getTitle()));
        questionRepository.saveAll(questionESList);
    }



}
