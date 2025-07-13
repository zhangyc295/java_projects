package com.example.judge.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.judge.service.JudgeService;
import com.example.openfeign.model.JudgeSubmitDTO;
import com.example.openfeign.model.SubmitResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/judge")
public class JudgeController extends DatabaseController {

    @Autowired
    private JudgeService judgeService;

    @PostMapping("/doJudgeJavaCode")
    Result<SubmitResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO) {
        return Result.success(judgeService.doJudgeJavaCode(judgeSubmitDTO));
    }
}
