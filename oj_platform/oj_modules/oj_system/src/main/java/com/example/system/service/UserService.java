package com.example.system.service;

import com.example.common.entity.model.Result;
import com.example.system.model.user.UserListVO;
import com.example.system.model.user.UserShowDTO;
import com.example.system.model.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    List<UserListVO> list(UserShowDTO userShowDTO);

    int updateStatus(UserUpdateDTO userUpdateDTO);
}
