package com.example.system.controller;

import com.example.common.entity.model.Result;
import com.example.system.model.AdminSaveDTO;
import com.example.system.model.AdminVO;
import com.example.system.model.LoginDTO;
import com.example.system.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RestController
@Tag(name = "管理员接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员登录")
    @ApiResponse(responseCode = "1000", description = "用户登录成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")

    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return adminService.login(loginDTO.getAccount(), loginDTO.getPassword());
    }


    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "新增管理员")
    @ApiResponse(responseCode = "1000", description = "新增用户成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3103", description = "用户已存在")
    public Result<Void> add(@RequestBody AdminSaveDTO adminSaveDTO) {
        return null;
    }


    @DeleteMapping("/{adminId}")
    @Operation(summary = "删除管理员", description = "删除管理员")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "管理员ID")
    })
    @ApiResponse(responseCode = "1000", description = "删除用户成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    public Result<Void> delete(@PathVariable Long adminId) {
        return null;
    }


    @Operation(summary = "管理员详情", description = "查询管理员信息")

    @GetMapping("/detail")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.QUERY, description = "管理员ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "管理员性别")
    })
    @ApiResponse(responseCode = "1000", description = "获取管理员信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public Result<AdminVO> detail(@RequestParam Long adminId, @RequestParam(required = false) String sex) {
        return null;
    }
}

