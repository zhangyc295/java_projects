package com.example.friend.controller;

import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.friend.aspect.CheckStatus;
import com.example.friend.model.contest.ContestDTO;
import com.example.friend.model.contest.ContestShowDTO;
import com.example.friend.service.ClientContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/contest")
public class ClientContestController extends DatabaseController {

    @Autowired
    private ClientContestService clientContestService;

    @CheckStatus
    // 限制用户功能
    @PostMapping("/enter")
    @Operation(summary = "竞赛报名", description = "用户报名竞赛")
    @ApiResponse(responseCode = "1000", description = "用户报名成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    @ApiResponse(responseCode = "3212", description = "已成功报名，无法重复操作")
    public Result<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ContestDTO contestDTO) {
//        System.out.println(contestDTO);
//        System.out.println(contestDTO.getContestId());
        return convertToResult(clientContestService.enter(token, contestDTO.getContestId()));
    }

    @GetMapping("/list")
    @Operation(summary = "获取竞赛信息", description = "我的竞赛列表")
    @ApiResponse(responseCode = "1000", description = "获取竞赛信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(ContestShowDTO contestShowDTO) {
       return clientContestService.list(contestShowDTO);
    }
}
