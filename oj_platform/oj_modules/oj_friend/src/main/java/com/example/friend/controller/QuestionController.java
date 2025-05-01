package com.example.friend.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.friend.aspect.CheckStatus;
import com.example.friend.model.question.HotQuestionVO;
import com.example.friend.model.question.QuestionDetailVO;
import com.example.friend.model.question.QuestionShowDTO;
import com.example.friend.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends DatabaseController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/semiLogin/list")
    @Operation(summary = "获取题目信息", description = "题目列表")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(QuestionShowDTO questionShowDTO) {
        return questionService.list(questionShowDTO);
    }

    @CheckStatus
    @GetMapping("/detail")
    @Operation(summary = "获取题目详细信息", description = "题目信息")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public Result<QuestionDetailVO> detail(Long questionId) {
        return Result.success(questionService.detail(questionId));
    }

    @GetMapping("/prev")
    @Operation(summary = "获取上一题题目详细信息", description = "上一题题目信息")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3400", description = "当前题目已经是第一题")
    public Result<String> prev(Long questionId) {
        return Result.success(questionService.prevQuestion(questionId));
    }

    @GetMapping("/next")
    @Operation(summary = "获取下一题题目详细信息", description = "下一题题目信息")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3401", description = "当前题目已经是最后一题")
    public Result<String> next(Long questionId) {
        return Result.success(questionService.nextQuestion(questionId));
    }

    @GetMapping("/semiLogin/hotList")
    @Operation(summary = "题目热榜", description = "题目提交次数排行")
    @ApiResponse(responseCode = "1000", description = "获取信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult hotList() {
        return convertToTableResult(questionService.getHotQuestion());
    }
}
