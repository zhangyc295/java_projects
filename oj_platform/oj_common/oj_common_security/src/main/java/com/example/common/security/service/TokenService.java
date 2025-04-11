package com.example.common.security.service;

import cn.hutool.core.lang.UUID;
import com.example.common.entity.constants.FilterConstants;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.common.entity.model.AdminRedis;
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

    public String createToken(Long userId, String secret, Integer identity) {
        Map<String, Object> claim = new HashMap<>();
        String adminUUid = UUID.fastUUID().toString();

        claim.put(JwtConstants.ADMIN_ID, userId);
        claim.put(JwtConstants.ADMIN_KEY, adminUUid);

        String token = JwtUtils.createToken(claim, secret);
        // Jwt中存储了 Id 和 token 的信息

        String key = RedisConstants.LOGIN_TOKEN + adminUUid;   //全局唯一key
        AdminRedis adminRedis = new AdminRedis();
        adminRedis.setIdentity(identity);

        redisService.setCacheObject(key, adminRedis, RedisConstants.TOKEN_EXPIRATION, TimeUnit.MINUTES);
        // 存入redis   唯一key  用户信息  token过期时间
        return token;
    }

    public void extendTokenTime(String token, String secret) {

        log.error(token);
        log.error(secret);
        System.out.println(token);
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);  //获取令牌中信息解析 payload 中信息
            if (claims == null) {
                log.error("解析token异常，{}", token);
                return;
            }
        } catch (Exception e) {
            log.error("解析token异常，{}", token, e);
            return;
        }

        String adminKey = JwtUtils.getAdminKey(claims);
        String key = RedisConstants.LOGIN_TOKEN + adminKey;
        Long time = redisService.getExpire(key, TimeUnit.MINUTES);
        if (time != null && time < RedisConstants.REFRESH_TIME) {
            redisService.expire(key, RedisConstants.TOKEN_EXPIRATION, TimeUnit.MINUTES);
        }
    }
}
