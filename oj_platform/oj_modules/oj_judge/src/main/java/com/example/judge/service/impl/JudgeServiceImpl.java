package com.example.judge.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.JudgeConstants;
import com.example.common.entity.enums.CodeRunStatus;
import com.example.common.entity.enums.SubmitResult;
import com.example.judge.mapper.UserSubmitMapper;
import com.example.judge.model.SandBoxExecuteResult;
import com.example.judge.model.UserSubmit;
import com.example.judge.service.JudgeService;
import com.example.judge.service.SandBoxPoolService;
import com.example.judge.service.SandBoxService;
import com.example.openfeign.model.JudgeSubmitDTO;
import com.example.openfeign.model.SubmitResultDetail;
import com.example.openfeign.model.SubmitResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    private SandBoxService sandBoxService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;
    @Autowired
    private SandBoxPoolService  sandBoxPoolService;

    @Override
    public SubmitResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        // docker执行代码  直接创建容器执行
        // SandBoxExecuteResult sandBoxExecuteResult =
        //        sandBoxService.executeJavaCode(judgeSubmitDTO.getUserId(), judgeSubmitDTO.getSubmitCode(), judgeSubmitDTO.getInputList());

        // 使用容器池
        SandBoxExecuteResult sandBoxExecuteResult =
                sandBoxPoolService.executeJavaCode(judgeSubmitDTO.getUserId(), judgeSubmitDTO.getSubmitCode(), judgeSubmitDTO.getInputList());
        SubmitResultVO submitResultVO = new SubmitResultVO();
        if (sandBoxExecuteResult != null && CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())) {
            submitResultVO = questionJudge(judgeSubmitDTO, sandBoxExecuteResult, submitResultVO);
        } else {
            submitResultVO.setPass(SubmitResult.ERROR.getValue());
            if (sandBoxExecuteResult == null) {
                submitResultVO.setErrorMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            } else {
                submitResultVO.setErrorMessage(sandBoxExecuteResult.getErrorMessage());
            }
            submitResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        saveUserSubmit(judgeSubmitDTO, submitResultVO);
        // 操作数据库

        return submitResultVO;
    }

    private SubmitResultVO questionJudge(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult,
                                         SubmitResultVO submitResultVO) {
        //比对输出结果是否一致
        List<String> executeOutputList = sandBoxExecuteResult.getOutputList();
        //执行结果
        List<String> outputList = judgeSubmitDTO.getOutputList();

        if (executeOutputList.size() != outputList.size()) {
            submitResultVO.setScore(JudgeConstants.ERROR_SCORE);
            submitResultVO.setPass(SubmitResult.ERROR.getValue());
            submitResultVO.setErrorMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return submitResultVO;
        }

        List<SubmitResultDetail> submitResultDetailList = new ArrayList<>();
        boolean passed = true;  // 是否通过全部测试用例
        for (int i = 0; i < outputList.size(); i++) {
            String output = outputList.get(i);
            String executeOutput = executeOutputList.get(i);
            String input = judgeSubmitDTO.getInputList().get(i);
            SubmitResultDetail submitResultDetail = new SubmitResultDetail();
            submitResultDetail.setInput(input);
            submitResultDetail.setOutput(output);
            submitResultDetail.setExecuteOutput(executeOutput);
            submitResultDetailList.add(submitResultDetail);
            // submitResultDetailList为返回用户端的列表
            if (!output.equals(executeOutput)) {
                passed = false;
            }
        }
        submitResultVO.setSubmitResultDetail(submitResultDetailList);
        // 测试用例未全部通过
        if (!passed) {
            submitResultVO.setScore(JudgeConstants.ERROR_SCORE);
            submitResultVO.setPass(SubmitResult.ERROR.getValue());
            submitResultVO.setErrorMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return submitResultVO;
        }

        // 超出时间限制
        if (sandBoxExecuteResult.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) {
            System.out.println("实际运行空间：" + sandBoxExecuteResult.getUseMemory());
            System.out.println("限制运行时间：" +judgeSubmitDTO.getSpaceLimit());

            submitResultVO.setScore(JudgeConstants.ERROR_SCORE);
            submitResultVO.setPass(SubmitResult.ERROR.getValue());
            submitResultVO.setErrorMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            return submitResultVO;
        }

        // 超出空间限制
        if (sandBoxExecuteResult.getUseTime() > judgeSubmitDTO.getTimeLimit()) {
            System.out.println("实际运行时间：" +sandBoxExecuteResult.getUseTime());
            System.out.println("限制运行时间：" +judgeSubmitDTO.getTimeLimit());
            submitResultVO.setScore(JudgeConstants.ERROR_SCORE);
            submitResultVO.setPass(SubmitResult.ERROR.getValue());
            submitResultVO.setErrorMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            return submitResultVO;
        }

        // 通过
        submitResultVO.setScore(JudgeConstants.DEFAULT_SCORE * judgeSubmitDTO.getDifficulty());
        submitResultVO.setPass(SubmitResult.PASS.getValue());
        return submitResultVO;
    }

    private void saveUserSubmit(JudgeSubmitDTO judgeSubmitDTO, SubmitResultVO submitResultVO) {
        UserSubmit userSubmit = new UserSubmit();
        BeanUtils.copyProperties(submitResultVO, userSubmit);
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setContestId(judgeSubmitDTO.getContestId());
        userSubmit.setProgramLanguage(judgeSubmitDTO.getProgramLanguage());
        userSubmit.setSubmitCode(judgeSubmitDTO.getSubmitCode());
        userSubmit.setCreatedBy(judgeSubmitDTO.getUserId());
        userSubmit.setCaseJudgeResult(JSON.toJSONString(submitResultVO.getSubmitResultDetail()));
        // 只保留一条数据
        // 删除用户id 题目id 竞赛id（如有）对应的提交数据
        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>().eq(UserSubmit::getUserId, judgeSubmitDTO.getUserId())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                .isNull(judgeSubmitDTO.getContestId() == null, UserSubmit::getContestId)
                .eq(judgeSubmitDTO.getContestId() != null, UserSubmit::getContestId, judgeSubmitDTO.getContestId()));
        userSubmitMapper.insert(userSubmit);
    }
}
