package com.example.system.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.system.mapper.ContestMapper;
import com.example.system.model.contest.*;
import com.example.system.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/contest")
@RestController
@Tag(name = "竞赛接口")
public class ContestController extends DatabaseController {
    @Autowired
    private ContestService contestService;

    @GetMapping("/list")
    @Operation(summary = "获取竞赛信息", description = "竞赛列表")
    @ApiResponse(responseCode = "1000", description = "获取竞赛信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(ContestShowDTO contestShowDTO) {
//        System.out.println(contestShowDTO.getStartTime());
        List<ContestListVO> list = contestService.list(contestShowDTO);
        return convertToTableResult(list);
    }

    // 新增不包含题目的竞赛
    @PostMapping("/add")
    @Operation(summary = "添加竞赛", description = "新增竞赛")
    @ApiResponse(responseCode = "1000", description = "新增成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3201", description = "竞赛开始时间不得早于当前时间")
    @ApiResponse(responseCode = "3202", description = "竞赛开始时间不得晚于结束时间")
    @ApiResponse(responseCode = "3203", description = "竞赛名称不能重复")
    @ApiResponse(responseCode = "3208", description = "竞赛信息不能为空")
    public Result<String> add(@Validated @RequestBody ContestAddDTO contestAddDTO) {
        return Result.success(contestService.add(contestAddDTO));
    }

    // 新增包含题目的竞赛
    @PostMapping("/question/add")
    @Operation(summary = "添加包含题目的竞赛", description = "新增竞赛")
    @ApiResponse(responseCode = "1000", description = "新增成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3204", description = "所选竞赛不存在")
    @ApiResponse(responseCode = "3205", description = "所选题目不存在")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    public Result<Void> questionAdd(@RequestBody ContestQuestionAddDTO contestQuestionAddDTO) {
//        System.out.println(contestQuestionAddDTO.getContestId());
//        System.out.println(contestQuestionAddDTO.getQuestionId());
        return convertToResult(contestService.questionAdd(contestQuestionAddDTO));
    }


    @GetMapping("/detail")
    @Operation(summary = "查询竞赛信息", description = "详细信息")
    @ApiResponse(responseCode = "1000", description = "查询成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public Result<ContestDetailVO> detail(Long contestId) {
//        System.out.println(contestId);
        return Result.success(contestService.detail(contestId));
    }

    @PutMapping("/edit")
    @Operation(summary = "编辑竞赛基本信息", description = "编辑竞赛")
    @ApiResponse(responseCode = "1000", description = "编辑成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3201", description = "竞赛开始时间不得早于当前时间")
    @ApiResponse(responseCode = "3202", description = "竞赛开始时间不得晚于结束时间")
    @ApiResponse(responseCode = "3203", description = "竞赛名称不能重复")
    @ApiResponse(responseCode = "3208", description = "竞赛信息不能为空")
    public Result<Void> edit(@RequestBody ContestEditDTO contestEditDTO) {
        return convertToResult(contestService.edit(contestEditDTO));
    }

    @DeleteMapping("/question/delete")
    @Operation(summary = "删除竞赛题目信息", description = "删除信息")
    @ApiResponse(responseCode = "1000", description = "删除成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3201", description = "竞赛开始时间不得早于当前时间")
    @ApiResponse(responseCode = "3202", description = "竞赛开始时间不得晚于结束时间")
    @ApiResponse(responseCode = "3203", description = "竞赛名称不能重复")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    public Result<Void> questionDelete(Long contestId, Long questionId) {
        return convertToResult(contestService.questionDelete(contestId, questionId));
    }

    @PutMapping("/delete")
    @Operation(summary = "删除竞赛信息", description = "删除竞赛")
    @ApiResponse(responseCode = "1000", description = "删除成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3204", description = "所选竞赛不存在")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    @ApiResponse(responseCode = "3203", description = "竞赛名称不能重复")
    public Result<Void> delete(Long contestId) {
        return convertToResult(contestService.delete(contestId));
    }

    @PutMapping("/publish")
    @Operation(summary = "发布竞赛信息", description = "发布竞赛")
    @ApiResponse(responseCode = "1000", description = "发布成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3204", description = "所选竞赛不存在")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    @ApiResponse(responseCode = "3207", description = "所选竞赛不包含任何题目信息")

    public Result<Void> publish(Long contestId) {
        return convertToResult(contestService.publish(contestId));
    }

    @PutMapping("/cancelPublish")
    @Operation(summary = "取消竞赛发布", description = "取消发布")
    @ApiResponse(responseCode = "1000", description = "取消成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3204", description = "所选竞赛不存在")
    @ApiResponse(responseCode = "3206", description = "所选竞赛已经开始，不允许操作")
    public Result<Void> cancelPublish(Long contestId) {
        return convertToResult(contestService.cancelPublish(contestId));
    }
}
