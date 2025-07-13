package com.example.friend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.enums.LanguageType;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.enums.SubmitResult;
import com.example.common.entity.model.Result;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.security.exception.ServiceException;
import com.example.friend.elasticsearch.QuestionRepository;
import com.example.friend.mapper.QuestionMapper;
import com.example.friend.mapper.UserSubmitMapper;
import com.example.friend.model.client.ClientSubmitDTO;
import com.example.friend.model.client.UserSubmit;
import com.example.friend.model.question.Question;
import com.example.friend.model.question.QuestionCase;
import com.example.friend.model.question.es.QuestionES;
import com.example.friend.rabbitmq.JudgeProducer;
import com.example.friend.service.ClientQuestionService;
import com.example.openfeign.RemoteJudgeService;
import com.example.openfeign.model.JudgeSubmitDTO;
import com.example.openfeign.model.SubmitResultDetail;
import com.example.openfeign.model.SubmitResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClientQuestionServiceImpl implements ClientQuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private RemoteJudgeService remoteJudgeService;
    @Autowired
    private JudgeProducer judgeProducer;
    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public Result<SubmitResultVO> submit(ClientSubmitDTO clientSubmitDTO) {
        Integer programLanguage = clientSubmitDTO.getProgramLanguage();
        if (LanguageType.Java.getValue().equals(programLanguage)) {
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(clientSubmitDTO);
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new ServiceException(ResultCode.NOT_SUPPORT);
    }

    @Override
    public boolean submitByRabbitMQ(ClientSubmitDTO clientSubmitDTO) {
        Integer programLanguage = clientSubmitDTO.getProgramLanguage();
//        System.out.println(programLanguage);
//        System.out.println(LanguageType.Java.getValue());
        if (LanguageType.Java.getValue().equals(programLanguage)) {
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(clientSubmitDTO);
            judgeProducer.produceMsg(judgeSubmitDTO);
            // 判题结果无法拿到 信息提交到mq 被judge服务获取
            return true;
        }
        throw new ServiceException(ResultCode.NOT_SUPPORT);
    }

    @Override
    public Result<SubmitResultVO> getQuestionResult(Long contestId, Long questionId, String submitTime) {
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        UserSubmit userSubmit = userSubmitMapper.selectQuestionResult(userId, contestId, questionId, submitTime);
        SubmitResultVO resultVO = new SubmitResultVO();
        if (userSubmit == null) {
            resultVO.setPass(SubmitResult.JUDGING.getValue());
        }else {
            resultVO.setPass(userSubmit.getPass());
            resultVO.setErrorMessage(userSubmit.getErrorMessage());
            String caseJudgeResult = userSubmit.getCaseJudgeResult();
            if (StrUtil.isNotEmpty(caseJudgeResult)) {
                resultVO.setSubmitResultDetail(JSON.parseArray(caseJudgeResult, SubmitResultDetail.class));
            }
        }
        return Result.success(resultVO);
    }

    private JudgeSubmitDTO assembleJudgeSubmitDTO(ClientSubmitDTO clientSubmitDTO) {
        Long questionId = clientSubmitDTO.getQuestionId();
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, judgeSubmitDTO);
        } else {
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question, judgeSubmitDTO);
            questionES = new QuestionES();
            BeanUtil.copyProperties(question, questionES);
            questionRepository.save(questionES);
        }
        judgeSubmitDTO.setUserId(ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class));
        judgeSubmitDTO.setContestId(clientSubmitDTO.getContestId());
        judgeSubmitDTO.setProgramLanguage(LanguageType.Java.getValue());

        judgeSubmitDTO.setSubmitCode(codeConnect(clientSubmitDTO.getSubmitCode(), questionES.getMainFunc()));
        List<QuestionCase> questionCaseList = JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).toList();
        List<String> outputList = questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setInputList(inputList);
        judgeSubmitDTO.setOutputList(outputList);
        return judgeSubmitDTO;
    }

    // 代码块拼接
    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.CODE_FORMAT_ERROR);
    }
}
