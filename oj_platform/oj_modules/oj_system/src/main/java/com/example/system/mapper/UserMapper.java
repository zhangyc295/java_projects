package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.model.user.User;
import com.example.system.model.user.UserListVO;
import com.example.system.model.user.UserShowDTO;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<UserListVO> selectUserList(UserShowDTO userShowDTO);
}
