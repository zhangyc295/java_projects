package com.example.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.entity.enums.ContestDeleteFlag;
import com.example.common.entity.enums.ContestStatus;
import com.example.common.entity.enums.ResultCode;
import com.example.common.security.exception.ServiceException;
import com.example.system.manager.ContestCacheManager;
import com.example.system.mapper.ContestMapper;
import com.example.system.mapper.ContestQuestionMapper;
import com.example.system.mapper.QuestionMapper;
import com.example.system.model.contest.*;
import com.example.system.model.question.Question;
import com.example.system.model.question.QuestionListVO;
import com.example.system.service.ContestService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ContestServiceImpl extends ServiceImpl<ContestQuestionMapper, ContestQuestion> implements ContestService {
    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ContestQuestionMapper contestQuestionMapper;

    @Autowired
    private ContestCacheManager contestCacheManager;

    @Override
    public List<ContestListVO> list(ContestShowDTO contestShowDTO) {
        PageHelper.startPage(contestShowDTO.getPageNumber(), contestShowDTO.getPageSize());
        return contestMapper.selectContestList(contestShowDTO);
    }

    @Override
    public String add(ContestAddDTO contestAddDTO) {
        List<Contest> contests = contestMapper.selectList(new LambdaQueryWrapper<Contest>().eq(Contest::getTitle, contestAddDTO.getTitle()));
        if (!CollectionUtil.isEmpty(contests)) {
            throw new ServiceException(ResultCode.SAME_CONTEST_TITLE);
        }
        if (contestAddDTO.getTitle() == null || contestAddDTO.getTitle().isEmpty()) {
            throw new ServiceException(ResultCode.CONTEST_HAS_NO_CONTENT);
        }
        checkTime(contestAddDTO);

        Contest contest = new Contest();
        BeanUtils.copyProperties(contestAddDTO, contest);
        contestMapper.insert(contest);
        return contest.getContestId().toString();
    }

    private static void checkTime(ContestAddDTO contestDTO) {
        if (contestDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.START_Error_Time);
        }
        if (contestDTO.getStartTime().isAfter(contestDTO.getEndTime())) {
            throw new ServiceException(ResultCode.END_Error_Time);
        }
    }

    @Override
    public boolean questionAdd(ContestQuestionAddDTO contestQuestionAddDTO) {
        Contest contest = getContest(contestQuestionAddDTO.getContestId());
        Set<Long> questionIdSet = contestQuestionAddDTO.getQuestionId();
        if (questionIdSet == null || questionIdSet.isEmpty()) {
            return true;
        }
        List<Question> questions = questionMapper.selectBatchIds(questionIdSet);
        if (CollectionUtil.isEmpty(questions) || questions.size() != questionIdSet.size()) {
            throw new ServiceException(ResultCode.QUESTION_NOT_EXISTS);
        }
        List<ContestQuestion> contestQuestionsList = getContestQuestionList(questionIdSet, contest);
        // 批量插入 extends ServiceImpl<ContestQuestionMapper, ContestQuestion>
        return saveBatch(contestQuestionsList);
    }

    @Override
    public ContestDetailVO detail(Long contestId) {
        ContestDetailVO contestDetailVO = new ContestDetailVO();
        Contest contest = getContest(contestId);
        BeanUtils.copyProperties(contest, contestDetailVO);
        List<ContestQuestion> contestQuestionsList = contestQuestionMapper.selectList(new LambdaQueryWrapper<ContestQuestion>().
                select(ContestQuestion::getQuestionId).eq(ContestQuestion::getContestId, contestId).
                orderByAsc(ContestQuestion::getQuestionOrder));  //获取信息
        if (CollectionUtil.isEmpty(contestQuestionsList)) {
            return contestDetailVO;  //基本信息
        }
        // select * from oj_question in ( )
        List<Long> questionIdList = contestQuestionsList.stream().map(ContestQuestion::getQuestionId).toList();
//        System.out.println(questionIdList.size());
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));
//        System.out.println(questions.size());
        List<QuestionListVO> questionListVOList = new ArrayList<>();
        // BeanUtils.copyProperties(questions, questionListVOList); 只能拷贝单个对象

        for (Question question : questions) {
            QuestionListVO vo = new QuestionListVO();
            BeanUtils.copyProperties(question, vo);
            questionListVOList.add(vo);
        }

        contestDetailVO.setContestQuestionList(questionListVOList);
//        System.out.println(contestDetailVO.getContestQuestionList().size());
        return contestDetailVO;
    }

    @Override
    public int edit(ContestEditDTO contestEditDTO) {
        Contest contest = getContest(contestEditDTO.getContestId());
        contest.setTitle(contestEditDTO.getTitle());
        contest.setStartTime(contestEditDTO.getStartTime());
        contest.setEndTime(contestEditDTO.getEndTime());
        // 查询是否有别的 contest 使用了这个 title
        List<Contest> sameTitleContests = contestMapper.selectList(new LambdaQueryWrapper<Contest>()
                .eq(Contest::getTitle, contestEditDTO.getTitle())
                .ne(Contest::getContestId, contestEditDTO.getContestId()) // 排除自己
        );
        if (!sameTitleContests.isEmpty()) {
            throw new ServiceException(ResultCode.SAME_CONTEST_TITLE);
        }
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
        checkTime(contestEditDTO);
        return contestMapper.updateById(contest);
    }

    @Override
    public int questionDelete(Long contestId, Long questionId) {
        Contest contest = getContest(contestId);
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
        return contestQuestionMapper.delete(new LambdaQueryWrapper<ContestQuestion>()
                .eq(ContestQuestion::getQuestionId, questionId).eq(ContestQuestion::getContestId, contestId));
    }

    @Override
    public int delete(Long contestId) {
        Contest contest = getContest(contestId);
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
        if (contest.getDeleteFlag() == ContestDeleteFlag.DELETED.getCode()) {
            throw new ServiceException(ResultCode.CONTEST_NOT_EXISTS);
        }
        contest.setDeleteFlag(ContestDeleteFlag.DELETED.getCode());
        //删除对应的题目信息
        contestQuestionMapper.delete(new LambdaQueryWrapper<ContestQuestion>().eq(ContestQuestion::getContestId, contestId));

        //删除基本信息(修改deleteFLag)
        return contestMapper.updateById(contest);
    }

    @Override
    public int publish(Long contestId) {
        Contest contest = getContest(contestId);
        Long count = contestQuestionMapper.selectCount(new LambdaQueryWrapper<ContestQuestion>()
                .eq(ContestQuestion::getContestId, contestId));
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
        if (count == 0) {
            throw new ServiceException(ResultCode.CONTEST_HAS_NO_QUESTION);
        }
//        contest.setStatus(1);
        contest.setStatus(ContestStatus.PUBLISH.getValue());
        // 将新发布的竞赛存储到redis中
        contestCacheManager.addCache(contest);

        return contestMapper.updateById(contest);
    }

    @Override
    public int cancelPublish(Long contestId) {
        Contest contest = getContest(contestId);
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.CONTEST_START);
        }
//        contest.setStatus(0);
        contest.setStatus(ContestStatus.UNPUBLISH.getValue());

        // 将发布的竞赛信息从redis中删除
        contestCacheManager.deleteCache(contestId);

        return contestMapper.updateById(contest);
    }

    private static List<ContestQuestion> getContestQuestionList(Set<Long> questionIdSet, Contest contest) {
        int num = 1;
        List<ContestQuestion> contestQuestionsList = new ArrayList<>();
        for (Long questionId : questionIdSet) {
//            Question question = questionMapper.selectById(questionId);
//            if (question == null) {
//                throw new ServiceException(ResultCode.QUESTION_NOT_EXISTS);
//            }
            ContestQuestion contestQuestion = new ContestQuestion();
            contestQuestion.setContestId(contest.getContestId());
            contestQuestion.setQuestionId(questionId);
            contestQuestion.setQuestionOrder(num++);
//            contestQuestionMapper.insert(contestQuestion);
            contestQuestionsList.add(contestQuestion);
        }
        return contestQuestionsList;
    }

    //判断竞赛是否存在
    private Contest getContest(Long contestId) {
//        Contest contest = contestMapper.selectById(contestId);
        Contest contest = contestMapper.selectOne(new LambdaQueryWrapper<Contest>().eq(Contest::getContestId, contestId)
                .eq(Contest::getDeleteFlag, 0)
        );
        if (contest == null) {
            throw new ServiceException(ResultCode.CONTEST_NOT_EXISTS);
        }
        return contest;
    }
}
