package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.enums.UserIdentity;
import com.example.common.entity.model.Result;
import com.example.common.security.service.TokenService;
import com.example.system.mapper.AdminMapper;
import com.example.system.model.Admin;
import com.example.system.service.AdminService;
import com.example.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
// @RefreshScope 无需重启服务变更即可生效
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Result<String> login(String account, String password) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        //select password from oj_admin where admin_account = account;
        Admin admin = adminMapper.selectOne(queryWrapper.select(Admin::getAdminId, Admin::getAdminPassword).eq(Admin::getAdminAccount, account));
        if (admin == null) {
            // loginResult.setCode(ResultCode.USER_NOT_EXISTS.getCode());
            // setMsg(ResultCode.USER_NOT_EXISTS.getMsg());
            // return loginResult;
            return Result.fail(ResultCode.USER_NOT_EXISTS);
        }
        if (BCryptUtils.matchesPassword(password, admin.getAdminPassword())) {
            String token = tokenService.createToken(admin.getAdminId(), secret, UserIdentity.ADMIN.getValue());
            return Result.success(token);
        }
        return Result.fail(ResultCode.FAILED_LOGIN);
    }
}
