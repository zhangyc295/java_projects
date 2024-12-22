package org.example.gobang.controller;

import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.RoomManager;
import org.example.gobang.model.User;
import org.example.gobang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoomManager roomManager;

    @RequestMapping("/login")
    public Object login(HttpServletRequest request, String username, String password) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return new User();
        }
        User userInfo = userService.getUserInfo(username);
        if (userInfo == null) {
            return new User();
        }
        if (password.equals(userInfo.getPassword())) {
            //用户信息储存到session中
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userInfo);
            return userInfo;
        }
        return new User();
    }

    @RequestMapping("/register")
    public Object register(String username, String password) {
        try {
            User userInfo = new User();
            userInfo.setUsername(username);
            userInfo.setPassword(password);
            userMapper.insertUser(userInfo);
            return userInfo;
        } catch (DuplicateKeyException e) {//用户名重复
            return new User();  //new User()
        }
    }

    @RequestMapping("/getUserInfo")
    public Object getUserInfo(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            //返回最新数据
            User newUser = userService.getUserInfo(user.getUsername());
            return newUser;
        } catch (Exception e) {
            return new User();
        }
    }

    @RequestMapping("/getOpponentInfo")
    public Object getOpponentInfo(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            Integer userId = user.getUserId();
            // 调用RoomManager的方法获取对手用户ID
            Integer OpponentId = roomManager.getOpponentUserId(userId);
            User opponentUser = userService.getUserByUserId(OpponentId);
            return opponentUser;
        } catch (Exception e) {
            return new User();
        }
    }
}

