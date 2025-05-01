package com.example.friend.aspect;

import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.enums.ClientStatus;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.security.exception.ServiceException;
import com.example.friend.manager.ClientCacheManager;
import com.example.friend.model.client.ClientDetailVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
public class CheckStatusAspect {

    @Autowired
    private ClientCacheManager clientCacheManager;

    @Before(value = "@annotation(com.example.friend.aspect.CheckStatus)")
    public void before(JoinPoint point){
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        ClientDetailVO client = clientCacheManager.getClientById(userId);
        if (client == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        if (Objects.equals(client.getStatus(), ClientStatus.LIMIT.getValue())) {
            throw new ServiceException(ResultCode.USER_BANNED);
        }
    }
}
