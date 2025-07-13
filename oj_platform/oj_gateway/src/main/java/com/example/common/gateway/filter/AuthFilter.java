package com.example.common.gateway.filter;

import com.example.common.entity.constants.FilterConstants;
import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.enums.UserIdentity;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.model.Result;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.redis.RedisService;
import com.example.common.entity.model.LoginUserRedis;
import com.example.common.entity.utils.JwtUtils;

import com.example.common.gateway.properties.IgnoreWhiteProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson2.JSON;
import cn.hutool.core.util.StrUtil;

import java.util.List;


@Slf4j
@Component
// GlobalFilter 全局过滤器
// Ordered 全局过滤器顺序
public class AuthFilter implements GlobalFilter, Ordered {

    // 排除过滤的 uri 白名单地址，在 nacos 中添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取 http 请求
        ServerHttpRequest request = exchange.getRequest();

        String url = request.getURI().getPath();

        // 跳过不需要验证的路径 (白名单)
        if (matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }

        //从 http 请求头中获取 token
        String token = getToken(request);

        // hutool包 字符串判空
        // return str == null || str.length() == 0;
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, FilterConstants.TOKEN_CANNOT_BE_EMPTY);
        }

        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);  //获取令牌中信息解析 payload 中信息
            if (claims == null) {
                // spring cloud gateway 基于webflux 与spring mvc不相容 即与全局异常处理SystemExceptionHandler不相容
                return unauthorizedResponse(exchange, FilterConstants.TOKEN_EXPIRED_OR_INVALID);
            }
        } catch (Exception e) {
            return unauthorizedResponse(exchange, FilterConstants.TOKEN_EXPIRED_OR_INVALID);
        }

        String userKey = JwtUtils.getAdminKey(claims);  //获取 jwt 中的 key
        boolean isLogin = redisService.hasKey(getTokenKey(userKey));
        if (!isLogin) {
            return unauthorizedResponse(exchange, FilterConstants.LOGIN_STATUS_EXPIRED);
        }
        String userId = JwtUtils.getAdminId(claims); //判断 jwt 中的信息是否完整
        if (StrUtil.isEmpty(userId)) {
            return unauthorizedResponse(exchange, FilterConstants.TOKEN_VALIDATION_FAILED);
        }

        // 通过redis验证 token 正确没有过期
        // 判断身份认证信息
        LoginUserRedis admin = redisService.getCacheObject(getTokenKey(userKey), LoginUserRedis.class);
        if (url.contains(HttpConstants.SYSTEM_URL_PREFIX) && !UserIdentity.ADMIN.getValue().equals(admin.getIdentity())) {
            return unauthorizedResponse(exchange, FilterConstants.TOKEN_VALIDATION_FAILED);
        }
        if (url.contains(HttpConstants.FRIEND_URL_PREFIX) && !UserIdentity.ORDINARY.getValue().equals(admin.getIdentity())) {
            return unauthorizedResponse(exchange, FilterConstants.TOKEN_VALIDATION_FAILED);
        }


        return chain.filter(exchange);
    }

    /**
     * 查找指定url是否匹配指定匹配规则链表中的任意一个字符串
     *
     * @param url         指定 url
     * @param patternList 需要检查的匹配规则链表
     * @return 是否匹配
     */
    private boolean matches(String url, List<String> patternList) {
        if (StrUtil.isEmpty(url) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }
        // 遍历白名单内容
        for (String pattern : patternList) {
            if (isMatch(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 url 是否与规则匹配
     * 匹配规则中：
     * ? 表示单个字符
     * * 表示一层路径内的任意字符串，不可跨层级
     * ** 表示任意层路径
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的 url
     * @return 是否匹配
     */
    private boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    //获取缓存key
    private String getTokenKey(String token) {
        return RedisConstants.LOGIN_TOKEN + token;
    }

    //从请求头中获取请求 token
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpConstants.AUTHENTICATION);

        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return webFluxResponseWriter(exchange.getResponse(), msg, ResultCode.FAILED_UNAUTHORIZED.getCode());
    }

    //拼装 webflux 模型响应
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, int code) {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Result<?> result = Result.fail(code, msg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    // 数值越小  越先执行
    @Override
    public int getOrder() {
        return -200;
    }

    public static void main(String[] args) {
        AuthFilter authFilter = new AuthFilter();
//        String pattern = "/sys/?bc";
//        System.out.println(authFilter.isMatch(pattern, "/sys/abc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/bbc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/abbc"));
//        System.out.println(authFilter.isMatch(pattern, "/sss/abc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/abcc"));

//        String pattern = "/sys/*/bc";
//        System.out.println(authFilter.isMatch(pattern, "/sys/a/bc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/abc/bc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/a/b/bc"));
//        System.out.println(authFilter.isMatch(pattern, "/a/b/bc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/a/b/"));

        String pattern = "/sys/**/bc";
        System.out.println(authFilter.isMatch(pattern, "/sys/a/bc"));
        System.out.println(authFilter.isMatch(pattern, "/sys/abc/bc"));
        System.out.println(authFilter.isMatch(pattern, "/sys/a/b/bc"));

        System.out.println(authFilter.isMatch(pattern, "/sys/a/b/c/123///bc"));
        System.out.println(authFilter.isMatch(pattern, "/a/b/c/123////bc"));
        System.out.println(authFilter.isMatch(pattern, "/sys/a/b/c/123/456////"));
    }
}