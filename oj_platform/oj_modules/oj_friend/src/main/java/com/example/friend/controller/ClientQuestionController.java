package com.example.friend.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.friend.aspect.CheckStatus;
import com.example.friend.model.client.ClientSubmitDTO;
import com.example.friend.service.ClientQuestionService;
import com.example.openfeign.model.SubmitResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/question")
public class ClientQuestionController extends DatabaseController {
    @Autowired
    private ClientQuestionService clientQuestionService;


    //用户提交代码 (通过 openfeign 服务间调用)
    @PostMapping("/submit")
    @Operation(summary = "用户提交代码", description = "提交代码")
    @ApiResponse(responseCode = "1000", description = "提交成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3500", description = "当前题目不支持此语言")
    public Result<SubmitResultVO> submit(@RequestBody ClientSubmitDTO clientSubmitDTO) {
        return clientQuestionService.submit(clientSubmitDTO);
    }


    @CheckStatus
    //用户提交代码 (通过rabbitmq)
    @PostMapping("/rabbitmq/submit")
    @Operation(summary = "用户提交代码", description = "提交代码")
    @ApiResponse(responseCode = "1000", description = "提交成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3500", description = "当前题目不支持此语言")
    public Result<Void> submitByRabbitMQ(@RequestBody ClientSubmitDTO clientSubmitDTO) {
        return convertToResult(clientQuestionService.submitByRabbitMQ(clientSubmitDTO));
    }

    @CheckStatus
    //前端获取判题结果
    @GetMapping("/result")
    @Operation(summary = "获取判题结果", description = "获取判题结果")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public Result<SubmitResultVO> getQuestionResult(Long questionId, Long contestId, String submitTime) {
        return clientQuestionService.getQuestionResult(contestId, questionId, submitTime);
    }
}
