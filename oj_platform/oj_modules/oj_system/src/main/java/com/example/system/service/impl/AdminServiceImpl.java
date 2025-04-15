package com.example.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.enums.UserIdentity;
import com.example.common.entity.model.AdminRedis;
import com.example.common.entity.model.AdminVO;
import com.example.common.entity.model.Result;
import com.example.common.security.exception.ServiceException;
import com.example.common.security.service.TokenService;
import com.example.system.mapper.AdminMapper;
import com.example.system.model.admin.Admin;
import com.example.system.model.admin.AdminSaveDTO;
import com.example.system.service.AdminService;
import com.example.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Admin admin = adminMapper.selectOne(queryWrapper.select(Admin::getAdminId, Admin::getAdminPassword, Admin::getNickName).eq(Admin::getAdminAccount, account));
        if (admin == null) {
            // loginResult.setCode(ResultCode.USER_NOT_EXISTS.getCode());
            // setMsg(ResultCode.USER_NOT_EXISTS.getMsg());
            // return loginResult;
            return Result.fail(ResultCode.USER_NOT_EXISTS);
        }
        if (BCryptUtils.matchesPassword(password, admin.getAdminPassword())) {
            String token = tokenService.createToken(admin.getAdminId(), secret, UserIdentity.ADMIN.getValue(), admin.getNickName());
            return Result.success(token);
        }
        return Result.fail(ResultCode.FAILED_LOGIN);
    }

    @Override
    public boolean logout(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.delete(token, secret);
    }

    @Override
    public Result<AdminVO> info(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        AdminRedis admin = tokenService.getAdmin(token, secret);
        if (admin == null) {
            return Result.fail(ResultCode.USER_NOT_EXISTS);
        }
        AdminVO adminVO = new AdminVO();
        adminVO.setNickName(admin.getNickName());
        return Result.success(adminVO);
    }

    @Override
    public int add(AdminSaveDTO adminSaveDTO) {
        List<Admin> admins = adminMapper.selectList(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getAdminAccount, adminSaveDTO.getAccount()));
        // 注册时用户名已存在
        if (!admins.isEmpty()) {
            throw new ServiceException(ResultCode.USER_EXISTS);
        }
        Admin admin = new Admin();
        admin.setAdminAccount(adminSaveDTO.getAccount());
        admin.setAdminPassword(BCryptUtils.encryptPassword(adminSaveDTO.getPassword()));
        // 同一功能处理
        // admin.setCreatedBy(100L);
        // admin.setCreateTime(LocalDateTime.now());
        // admin.setUpdatedBy(100L);
        // admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.insert(admin);
    }
}
