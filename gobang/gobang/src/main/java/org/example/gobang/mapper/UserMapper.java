package org.example.gobang.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.gobang.model.User;

@Mapper
public interface UserMapper {
    @Insert("insert into user(userId, username, password, score, total_game, win_game)" +
            " values (null, #{username}, #{password}, 500, 0, 0)")
    void insertUser(User user);

    //注册
    @Select("select * from user where username = #{username}")
    User selectByName(String username);
    //登录

    @Select("select * from user where userId = #{userId}")
    User selectByUserId(Integer userId);

    @Update("update user set total_game = total_game + 1, win_game = win_game + 1, score = score + 10" +
            " where userId = #{userId}")
    Integer userWin(Integer userId);

    @Update("update user set total_game = total_game + 1, score = score - 5" +
            " where userId = #{userId}")
    Integer userLose(Integer userId);
}
