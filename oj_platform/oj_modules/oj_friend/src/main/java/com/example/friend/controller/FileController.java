package com.example.friend.controller;

import com.example.common.entity.model.Result;
import com.example.common.files.OSSResult;
import com.example.friend.aspect.CheckStatus;
import com.example.friend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;


    @CheckStatus
    @PostMapping("/upload")
    @Operation(summary = "头像上传", description = "上传至OSS")
    @ApiResponse(responseCode = "1000", description = "上传成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3303", description = "当天修改头像次数已达上限")
    @ApiResponse(responseCode = "3305", description = "头像上传失败")
    @ApiResponse(responseCode = "3306", description = "头像大小不得超过1MB")
    public Result<OSSResult> upload(@RequestBody MultipartFile file) {
        return Result.success(fileService.upload(file));
    }
}
