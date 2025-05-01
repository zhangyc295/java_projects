package com.example.friend.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.redis.RedisService;
import com.example.friend.mapper.ClientMapper;
import com.example.friend.model.client.Client;
import com.example.friend.model.client.ClientDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClientCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ClientMapper clientMapper;

    public ClientDetailVO getClientById(Long userId) {
        String userKey = getUserKey(userId);
        ClientDetailVO clientDetailVO = redisService.getCacheObject(userKey, ClientDetailVO.class);
        if (clientDetailVO != null) {
            redisService.expire(userKey, RedisConstants.CLIENT_INFO_EXPIRATION, TimeUnit.MINUTES);
            return clientDetailVO;
        }
        Client client = clientMapper.selectOne(new LambdaQueryWrapper<Client>()
                .select(Client::getUserId, Client::getNickName, Client::getUserName,
                        Client::getHeadImage, Client::getGender, Client::getTelephone,
                        Client::getEmail, Client::getSchool, Client::getMajor,
                        Client::getIntroduction, Client::getStatus).eq(Client::getUserId, userId));
        if (client == null) {
            return null;
        }
        refreshClient(client);
        clientDetailVO = new ClientDetailVO();
        BeanUtils.copyProperties(client, clientDetailVO);
        return clientDetailVO;
    }

    public void refreshClient(Client client) {
        String userKey = getUserKey(client.getUserId());
        redisService.setCacheObject(userKey, client);
        redisService.expire(userKey, RedisConstants.CLIENT_INFO_EXPIRATION, TimeUnit.MINUTES);
    }

    private String getUserKey(Long userId) {
        return RedisConstants.CLIENT_DETAIL_INFO + userId;
    }
}
