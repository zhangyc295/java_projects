package com.example.system.controller;

import com.example.common.entity.controller.DatabaseController;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.example.system.model.user.UserListVO;
import com.example.system.model.user.UserShowDTO;
import com.example.system.model.user.UserUpdateDTO;
import com.example.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends DatabaseController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @Operation(summary = "用户列表", description = "展示注册用户")
    @ApiResponse(responseCode = "1000", description = "展示成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    public TableResult list(UserShowDTO userShowDTO) {
        List<UserListVO> list = userService.list(userShowDTO);
        return convertToTableResult(list);
    }

    @PutMapping("/updateStatus")
    @Operation(summary = "用户列表", description = "展示注册用户")
    @ApiResponse(responseCode = "1000", description = "展示成功")
    @ApiResponse(responseCode = "2000", description = "服务器异常，请稍后重试")
    @ApiResponse(responseCode = "3104", description = "用户不存在")
    public Result<Void> updateStatus(@RequestBody UserUpdateDTO userUpdateDTO){
        return convertToResult(userService.updateStatus(userUpdateDTO));
    }
}
