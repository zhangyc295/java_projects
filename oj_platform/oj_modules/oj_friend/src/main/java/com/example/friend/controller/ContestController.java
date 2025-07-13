package com.example.friend.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.friend.model.contest.ContestListVO;
import com.example.friend.model.contest.ContestRankDTO;
import com.example.friend.model.contest.ContestShowDTO;
import com.example.friend.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contest")
public class ContestController extends DatabaseController {

    @Autowired
    private ContestService contestService;

    @GetMapping("/semiLogin/list")
    @Operation(summary = "获取竞赛信息", description = "竞赛列表")
    @ApiResponse(responseCode = "1000", description = "获取竞赛信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(ContestShowDTO contestShowDTO) {
        List<ContestListVO> list = contestService.list(contestShowDTO);
        return convertToTableResult(list);
    }

    @GetMapping("/semiLogin/redis/list")
    @Operation(summary = "获取竞赛信息", description = "竞赛列表")
    @ApiResponse(responseCode = "1000", description = "获取竞赛信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult redisList(ContestShowDTO contestShowDTO) {
//        System.out.println(contestShowDTO.getStartTime());
//        System.out.println(contestShowDTO.getEndTime());
//        System.out.println(contestShowDTO.getType());
        return contestService.redisList(contestShowDTO);
    }

    @GetMapping("/rank/list")
    @Operation(summary = "获取竞赛排名信息", description = "竞赛排名信息")
    @ApiResponse(responseCode = "1000", description = "获取竞赛信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult rankList(ContestRankDTO contestRankDTO) {
        return contestService.rankList(contestRankDTO);
    }


    @GetMapping("/getFirstQuestion")
    @Operation(summary = "获取竞赛题目信息", description = "竞赛第一题")
    @ApiResponse(responseCode = "1000", description = "获取竞赛第一题信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public Result<String> firstQuestion(Long contestId) {
        return Result.success(contestService.firstQuestion(contestId));
    }

    @GetMapping("/prev")
    @Operation(summary = "获取上一题题目详细信息", description = "上一题题目信息")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3400", description = "当前题目已经是第一题")
    public Result<String> prev(Long contestId, Long questionId) {
        return Result.success(contestService.prevQuestion(contestId,questionId));
    }

    @GetMapping("/next")
    @Operation(summary = "获取下一题题目详细信息", description = "下一题题目信息")
    @ApiResponse(responseCode = "1000", description = "获取题目信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3401", description = "当前题目已经是最后一题")
    public Result<String> next(Long contestId, Long questionId) {
        return Result.success(contestService.nextQuestion(contestId,questionId));
    }
}
