package com.example.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;
    // 从bean对象的容器中获取secret

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
//            throw new ServiceException(ResultCode.ERROR_TOKEN);
            return true;
        }

        Long userId = tokenService.getUserId(token, secret);
        String userKey = tokenService.getUserKey(token, secret);
        // 存入ThreadLocal中

        ThreadLocalUtils.set(JwtConstants.LOGIN_USER_ID, userId);
        ThreadLocalUtils.set(JwtConstants.LOGIN_USER_KEY, userKey);
        tokenService.extendTokenTime(token, secret);

        return true;
    }

    // 释放内存
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.remove();
    }

    //从请求头中获取请求 token
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);

        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }
}
