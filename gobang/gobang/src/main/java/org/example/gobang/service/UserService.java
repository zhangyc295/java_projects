package org.example.gobang.service;

import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserInfo(String username) {
        return userMapper.selectByName(username);
    }

    public String getUserNameByUserId(Integer userId) {
        User user = userMapper.selectByUserId(userId);
        return user.getUsername();
    }

    public void updateWinner(Integer userId) {
        userMapper.userWin(userId);
    }

    public void updateLoser(Integer userId) {
        userMapper.userLose(userId);
    }

    public User getUserByUserId(Integer userId) {
        return userMapper.selectByUserId(userId);
    }
}
