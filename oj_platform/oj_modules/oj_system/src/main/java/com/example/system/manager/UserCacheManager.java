package com.example.system.manager;

import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.system.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserCacheManager {

    @Autowired
    private RedisService redisService;

    public void refreshUser(Long userId, Integer status) {
        String userKey = getUserKey(userId);
        User cacheObject = redisService.getCacheObject(userKey, User.class);
        if (cacheObject == null) {
            return;
        }
        cacheObject.setStatus(status);
        redisService.setCacheObject(userKey, cacheObject);
        redisService.expire(userKey, RedisConstants.CLIENT_INFO_EXPIRATION, TimeUnit.MINUTES);
    }

    private String getUserKey(Long userId) {
        return RedisConstants.CLIENT_DETAIL_INFO + userId;
    }
}
