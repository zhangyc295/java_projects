package com.example.judge.service;

import com.example.openfeign.model.JudgeSubmitDTO;
import com.example.openfeign.model.SubmitResultVO;

public interface JudgeService {
    SubmitResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
