package com.example.system.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.system.model.question.*;
import com.example.system.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/question")
@RestController
@Tag(name = "题目管理接口")
public class QuestionController extends DatabaseController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/list")
    @Operation(summary = "题目列表", description = "展示题目")
    @ApiResponse(responseCode = "1000", description = "展示成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(QuestionShowDTO questionShowDTO) {
        List<QuestionListVO> list = questionService.list(questionShowDTO);
        return convertToTableResult(list);
    }

    @PostMapping("/add")
    @Operation(summary = "添加题目", description = "新增题目")
    @ApiResponse(responseCode = "1000", description = "新增成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3101", description = "资源已存在")
    public Result<Void> add(@Validated @RequestBody QuestionAddDTO questionAddDTO) {
        return convertToResult(questionService.add(questionAddDTO));
    }

    @GetMapping("/detail")
    @Operation(summary = "查询题目", description = "题目详情")
    @ApiResponse(responseCode = "1000", description = "查询成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    public Result<QuestionDetailVO> detail(Long questionId) {
        return Result.success(questionService.detail(questionId));
    }

    @PutMapping("/edit")
    @Operation(summary = "编辑题目", description = "修改题目")
    @ApiResponse(responseCode = "1000", description = "编辑成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    public Result<Void> edit(@Validated @RequestBody QuestionEditDTO questionEditDTO) {
        return convertToResult(questionService.edit(questionEditDTO));
    }

    @PutMapping("/delete")
    @Operation(summary = "删除题目", description = "删除题目")
    @ApiResponse(responseCode = "1000", description = "删除成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    public Result<Void> deleteToBin(Long questionId) {
        return convertToResult(questionService.deleteToBin(questionId));
    }

    @GetMapping("/deleted")
    @Operation(summary = "已删除题目列表", description = "展示已删除题目")
    @ApiResponse(responseCode = "1000", description = "展示成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult deletedList(QuestionDeletedDTO questionDeletedDTO) {
        List<QuestionDeletedVO> list = questionService.deletedList(questionDeletedDTO);
        return convertToTableResult(list);
    }

    @PutMapping("/remove")
    @Operation(summary = "彻底删除题目", description = "删除回收站题目")
    @ApiResponse(responseCode = "1000", description = "删除成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    public Result<Void> removeQuestion(Long questionId) {
        return convertToResult(questionService.deleteBin(questionId));
    }


    @PutMapping("/recover")
    @Operation(summary = "恢复题目", description = "恢复回收站题目")
    @ApiResponse(responseCode = "1000", description = "恢复成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3102", description = "资源不存在")
    public Result<Void> recoverQuestion(Long questionId) {
        return convertToResult(questionService.recoverBin(questionId));
    }
}
