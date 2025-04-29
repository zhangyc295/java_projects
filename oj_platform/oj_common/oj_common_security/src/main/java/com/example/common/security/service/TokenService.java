package com.example.common.security.service;

import cn.hutool.core.lang.UUID;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.common.entity.model.LoginUserRedis;
import com.example.common.entity.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private RedisService redisService;

    public String createToken(Long userId, String secret, Integer identity, String nickName, String headImage) {
        Map<String, Object> claim = new HashMap<>();
        String adminUUid = UUID.fastUUID().toString();

        claim.put(JwtConstants.LOGIN_USER_ID, userId);
        claim.put(JwtConstants.LOGIN_USER_KEY, adminUUid);

        String token = JwtUtils.createToken(claim, secret);
        // Jwt中存储了 Id 和 token 的信息

        String key = RedisConstants.LOGIN_TOKEN + adminUUid;   //全局唯一key
        LoginUserRedis loginUser = new LoginUserRedis();
        loginUser.setIdentity(identity);
        loginUser.setNickName(nickName);
        loginUser.setHeadImage(headImage);

        redisService.setCacheObject(key, loginUser, RedisConstants.TOKEN_EXPIRATION, TimeUnit.MINUTES);
        // 存入redis   唯一key  用户信息  token过期时间
        return token;
    }

    public void extendTokenTime(String token, String secret) {

        //log.error(token);
        //log.error(secret);
//        Claims claims;
//        try {
//            claims = JwtUtils.parseToken(token, secret);  //获取令牌中信息解析 payload 中信息
//            if (claims == null) {
//                log.error("解析token异常，{}", token);
//                return;
//            }
//        } catch (Exception e) {
//            log.error("解析token异常，{}", token, e);
//            return;
//        }
//
//        String adminKey = JwtUtils.getAdminKey(claims);
        String adminKey = getUserKey(token, secret);
        if (adminKey == null) {
            return;
        }
        String key = RedisConstants.LOGIN_TOKEN + adminKey;
        Long time = redisService.getExpire(key, TimeUnit.MINUTES);
        if (time != null && time < RedisConstants.REFRESH_TIME) {
            redisService.expire(key, RedisConstants.TOKEN_EXPIRATION, TimeUnit.MINUTES);
        }
    }

    public LoginUserRedis getAdmin(String token, String secret) {
        String adminKey = getUserKey(token, secret);
        if (adminKey == null) {
            return null;
        }
        return redisService.getCacheObject(RedisConstants.LOGIN_TOKEN + adminKey, LoginUserRedis.class);
    }

    public boolean delete(String token, String secret) {
        String adminKey = getUserKey(token, secret);
        if (adminKey == null) {
            return false;
        }
        // 删除 redis 中的 token
        return redisService.deleteObject(RedisConstants.LOGIN_TOKEN + adminKey);
    }

    public Long getUserId(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);  //获取令牌中信息解析 payload 中信息
            if (claims == null) {
                log.error("解析token异常，{}", token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token异常，{}", token, e);
            return null;
        }
        return Long.valueOf(JwtUtils.getAdminId(claims));
    }

    public String getUserKey(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);  //获取令牌中信息解析 payload 中信息
            if (claims == null) {
                log.error("解析token异常，{}", token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token异常，{}", token, e);
            return null;
        }
        return JwtUtils.getAdminKey(claims);
    }

    public void refreshLoginUser(String nickName, String headImage, String userKey) {
        LoginUserRedis loginUser = redisService.getCacheObject(RedisConstants.LOGIN_TOKEN + userKey, LoginUserRedis.class);
        loginUser.setHeadImage(headImage);
        loginUser.setNickName(nickName);
        redisService.setCacheObject(RedisConstants.LOGIN_TOKEN + userKey, loginUser);
    }
}

