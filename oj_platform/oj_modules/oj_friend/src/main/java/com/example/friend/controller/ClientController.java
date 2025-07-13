package com.example.friend.controller;

import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.LoginUserVO;
import com.example.common.entity.model.Result;
import com.example.friend.aspect.CheckStatus;
import com.example.friend.model.client.*;
import com.example.friend.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController extends DatabaseController {

    @Autowired
    private ClientService clientService;


    @PostMapping("/sendCode")
    @Operation(summary = "发送验证码", description = "验证码")
    @ApiResponse(responseCode = "1000", description = "用户登录成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3107", description = "用户手机号不存在")
    @ApiResponse(responseCode = "3108", description = "操作频繁，请稍后重试")
    @ApiResponse(responseCode = "3109", description = "操作频繁，已超出当天请求次数限制，请明日再试")
    @ApiResponse(responseCode = "3112", description = "验证码发送失败")
    public Result<Void> sendCode(@RequestBody ClientDTO clientDTO) {
        return convertToResult(clientService.sendCode(clientDTO));
    }

    @PostMapping("/code/login")
    @Operation(summary = "用户登录", description = "用户登录")
    @ApiResponse(responseCode = "1000", description = "用户登录成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3110", description = "验证码已过期")
    @ApiResponse(responseCode = "3111", description = "验证码输入有误")
    public Result<String> login(@RequestBody ClientDTO clientDTO) {
        return Result.success(clientService.codeLogin(clientDTO.getTelephone(),clientDTO.getCode()));
    }

    @PostMapping("/account/login")
    @Operation(summary = "用户登录", description = "用户登录")
    @ApiResponse(responseCode = "1000", description = "用户登录成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3114", description = "该用户名不存在")
    @ApiResponse(responseCode = "3115", description = "密码错误，请重试")
    @ApiResponse(responseCode = "3116", description = "该用户尚未设置密码，请使用验证码登录")
    public Result<String> accountLogin(@RequestBody ClientLoginDTO clientDTO) {
        return clientService.accountLogin(clientDTO.getUserName(),clientDTO.getUserPassword());
    }



    @DeleteMapping("/logout")
    @Operation(summary = "用户退出", description = "用户退出登录")
    @ApiResponse(responseCode = "1000", description = "退出成功")
    @ApiResponse(responseCode = "3000", description = "操作失败")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public Result<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return convertToResult(clientService.logout(token));
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取简要信息")
    @ApiResponse(responseCode = "1000", description = "获取信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    public Result<LoginUserVO> getInfo(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return clientService.info(token);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取用户信息", description = "获取详细信息")
    @ApiResponse(responseCode = "1000", description = "获取信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    public Result<ClientDetailVO> getDetail() {
        // token存储再threadLocal中
        return Result.success(clientService.detail());
    }

    @CheckStatus
    @PutMapping("/edit")
    @Operation(summary = "编辑用户信息", description = "编辑个人信息")
    @ApiResponse(responseCode = "1000", description = "修改信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    @ApiResponse(responseCode = "3107", description = "该手机号不存在")
    @ApiResponse(responseCode = "3113", description = "该用户名已被占用")
    public Result<Void> edit(@Validated @RequestBody ClientUpdateDTO clientUpdateDTO) {
        return convertToResult(clientService.edit(clientUpdateDTO));
    }

    @PutMapping("/password")
    @Operation(summary = "设置用户密码", description = "设置登录密码")
    @ApiResponse(responseCode = "1000", description = "设置密码成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3110", description = "验证码已过期")
    @ApiResponse(responseCode = "3111", description = "验证码输入有误")
    @ApiResponse(responseCode = "3300", description = "密码不能为空")
    @ApiResponse(responseCode = "3301", description = "密码长度必须在6到18位之间")
    @ApiResponse(responseCode = "3302", description = "两次输入的密码不一致")
    public Result<Void> password(@RequestBody ClientPasswordDTO clientPasswordDTO) {
        return convertToResult(clientService.editPassword(clientPasswordDTO));
    }

    @CheckStatus
    @PutMapping("/headImage/update")
    @Operation(summary = "编辑用户头像信息", description = "上传个人头像")
    @ApiResponse(responseCode = "1000", description = "修改信息成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    public Result<Void> headImageUpdate(@RequestBody ClientUpdateDTO clientUpdateDTO) {
        return convertToResult(clientService.headImageUpdate(clientUpdateDTO.getHeadImage()));
    }
}