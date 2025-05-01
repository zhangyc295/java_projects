package com.example.system.service.impl;

import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.model.Result;
import com.example.common.security.exception.ServiceException;
import com.example.system.manager.UserCacheManager;
import com.example.system.mapper.UserMapper;
import com.example.system.model.user.User;
import com.example.system.model.user.UserListVO;
import com.example.system.model.user.UserShowDTO;
import com.example.system.model.user.UserUpdateDTO;
import com.example.system.service.UserService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager userCacheManager;

    @Override
    public List<UserListVO> list(UserShowDTO userShowDTO) {
        PageHelper.startPage(userShowDTO.getPageNumber(), userShowDTO.getPageSize());
        return userMapper.selectUserList(userShowDTO);
    }

    @Override
    public int updateStatus(UserUpdateDTO userUpdateDTO) {
//        System.out.println(userUpdateDTO.getUserId());
        User user = userMapper.selectById(userUpdateDTO.getUserId());
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        Integer status = userUpdateDTO.getStatus();
        user.setStatus(status);
        // 修改缓存的status值
        userCacheManager.refreshUser(user.getUserId(), status);
        return userMapper.updateById(user);
    }
}
