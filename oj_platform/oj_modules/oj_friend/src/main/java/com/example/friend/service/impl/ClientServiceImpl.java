package com.example.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.constants.HttpConstants;
import com.example.common.entity.constants.JwtConstants;
import com.example.common.entity.constants.RedisConstants;
import com.example.common.entity.enums.ClientStatus;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.enums.UserIdentity;
import com.example.common.entity.model.LoginUserRedis;
import com.example.common.entity.model.LoginUserVO;
import com.example.common.entity.model.Result;
import com.example.common.entity.utils.ThreadLocalUtils;
import com.example.common.message.AliService;
import com.example.common.redis.RedisService;
import com.example.common.security.exception.ServiceException;
import com.example.common.security.service.TokenService;
import com.example.friend.manager.ClientCacheManager;
import com.example.friend.mapper.ClientContestMapper;
import com.example.friend.mapper.ClientMapper;
import com.example.friend.model.client.*;
import com.example.friend.service.ClientService;
import com.example.friend.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private AliService aliService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TokenService tokenService;

    @Value("${send-times:5}")
    private Integer sendTimes;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${file.oss.downLoadURL}")
    private String downLoadURL;

    // true生成随机验证码    false生成123456测试验证码
    @Value("${is-send:false}")
    private boolean isSend;
    @Autowired
    private ClientContestMapper clientContestMapper;

    @Autowired
    private ClientCacheManager clientCacheManager;

    @Override
    public boolean sendCode(ClientDTO clientDTO) {
        if (!checkTelephone(clientDTO.getTelephone())) {
            throw new ServiceException(ResultCode.USER_TELEPHONE_NOT_EXISTS);
        }
        String telephoneCodeKey = getTelephoneCodeKey(clientDTO.getTelephone());
        Long expire = redisService.getExpire(telephoneCodeKey, TimeUnit.SECONDS);
        // System.out.println(expire);
        if (expire != null && (RedisConstants.CODE_EXPIRATION * 60 - expire) < 60) {
            throw new ServiceException(ResultCode.FREQUENT_OPERATION);
        }
        // 每天限制请求50次
        String codeTimes = getTelephoneCodeTimesKey(clientDTO.getTelephone());
        Long timesOneDay = redisService.getCacheObject(codeTimes, Long.class);
        if (timesOneDay != null && timesOneDay >= sendTimes) {
            throw new ServiceException(ResultCode.FREQUENT_OPERATION_ONE_DAY);
        }

        String code = isSend ? RandomUtil.randomNumbers(6) : "123456";
        // 储存到redis中   key-value
        redisService.setCacheObject(telephoneCodeKey, code, RedisConstants.CODE_EXPIRATION, TimeUnit.MINUTES);

        if (isSend) {
            boolean ret = aliService.sendPhoneCode(clientDTO.getTelephone(), code);
            if (!ret) {
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        }
        redisService.increment(codeTimes);
        // 设置过期时间 当天
        if (timesOneDay == null) {
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.now().plusDays(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimes, seconds, TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public String codeLogin(String telephone, String code) {
        Client client = clientMapper.selectOne(new LambdaQueryWrapper<Client>().eq(Client::getTelephone, telephone));

        checkCode(telephone, code);
        // 验证码验证成功

        if (client == null) {  // 新用户  注册
            client = new Client();
            client.setTelephone(telephone);
            client.setStatus(ClientStatus.NORMAL.getValue());
            clientMapper.insert(client);
        }
        return tokenService.createToken(client.getUserId(), secret, UserIdentity.ORDINARY.getValue(), client.getNickName(), client.getHeadImage());

//        if (user != null) {
//            String telephoneCodeKey = getTelephoneCodeKey(telephone);
//            String cacheCode = redisService.getCacheObject(telephoneCodeKey, String.class);
//            if (StrUtil.isEmpty(cacheCode)) {
//                throw new ServiceException(ResultCode.TELEPHONE_CODE_INVALID);
//            }
//            if (!cacheCode.equals(code)) {
//                throw new ServiceException(ResultCode.TELEPHONE_CODE_ERROR);
//            }
//            // 验证码验证成功
//            redisService.deleteObject(telephoneCodeKey);
//            return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getValue(), user.getNickName());
//        }
    }

    @Override
    public Result<String> accountLogin(String userName, String userPassword) {
        Client client = clientMapper.selectOne(new LambdaQueryWrapper<Client>().eq(Client::getUserName, userName));
        if (client == null) {
            // 新用户  不允许密码登录注册
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        // 从数据库中获取存储的密码（加密后的密码）
        String oldPassword = client.getUserPassword();

        if (StrUtil.isEmpty(oldPassword)) {
            throw new ServiceException(ResultCode.PASSWORD_NOT_EXISTS);
        }

        // 使用BCrypt或其他加密工具来比较用户输入的密码与数据库中存储的密码是否匹配
        if (!BCryptUtils.matchesPassword(userPassword, oldPassword)) {
            throw new ServiceException(ResultCode.PASSWORD_ERROR); // 密码错误
        }

        String token = tokenService.createToken(client.getUserId(), secret, UserIdentity.ORDINARY.getValue(), client.getNickName(), client.getHeadImage());
        return Result.success(token);
    }


    @Override
    public boolean logout(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.delete(token, secret);
    }

    @Override
    public Result<LoginUserVO> info(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀 HttpConstants.PREFIX
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUserRedis loginUser = tokenService.getAdmin(token, secret);
        if (loginUser == null) {
            return Result.fail(ResultCode.USER_NOT_EXISTS);
        }
        LoginUserVO clientVO = new LoginUserVO();
        clientVO.setNickName(loginUser.getNickName());
        if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
            clientVO.setHeadImage(downLoadURL + loginUser.getHeadImage());
        }
        return Result.success(clientVO);
    }

    @Override
    public ClientDetailVO detail() {
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        ClientDetailVO clientDetailVO = clientCacheManager.getClientById(userId);
        if (clientDetailVO == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        if (StrUtil.isNotEmpty(clientDetailVO.getHeadImage())) {
            clientDetailVO.setHeadImage(downLoadURL + clientDetailVO.getHeadImage());
        }
        return clientDetailVO;
    }

    @Override
    public int edit(ClientUpdateDTO clientUpdateDTO) {
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
//        System.out.println(clientUpdateDTO.getTelephone());
//        System.out.println(clientUpdateDTO.getSchool());
        if (userId == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        Client client = clientMapper.selectById(userId);
        if (client == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        Client same = clientMapper.selectOne(new LambdaQueryWrapper<Client>()
                .eq(Client::getUserName, clientUpdateDTO.getUserName())
                .ne(Client::getUserId, userId));
        // 排除自己
        if (same != null) {
            throw new ServiceException(ResultCode.USERNAME_EXISTS);
        }
        if (!checkTelephone(clientUpdateDTO.getTelephone())) {
            throw new ServiceException(ResultCode.USER_TELEPHONE_NOT_EXISTS);
        }

        client.setNickName(clientUpdateDTO.getNickName());
        client.setUserName(clientUpdateDTO.getUserName());
        client.setGender(clientUpdateDTO.getGender());
        client.setTelephone(clientUpdateDTO.getTelephone());
        client.setEmail(clientUpdateDTO.getEmail());
        client.setSchool(clientUpdateDTO.getSchool());
        client.setMajor(clientUpdateDTO.getMajor());
        client.setIntroduction(clientUpdateDTO.getIntroduction());
        clientCacheManager.refreshClient(client);
        tokenService.refreshLoginUser(client.getNickName(), client.getHeadImage(),
                ThreadLocalUtils.get(JwtConstants.LOGIN_USER_KEY, String.class));
        return clientMapper.updateById(client);
    }

    @Override
    public int editPassword(ClientPasswordDTO clientPasswordDTO) {
        String password = clientPasswordDTO.getUserPassword();
        String confirmPassword = clientPasswordDTO.getConfirmPassword();
        if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            throw new ServiceException(ResultCode.PASSWORD_EMPTY);
        }
        if (password.length() < 6 || password.length() > 18) {
            throw new ServiceException(ResultCode.PASSWORD_LENGTH_INVALID);
        }
        if (!password.equals(confirmPassword)) {
            throw new ServiceException(ResultCode.PASSWORD_NOT_SAME);
        }
        checkCode(clientPasswordDTO.getTelephone(), clientPasswordDTO.getCode());

        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
        String encryptPassword = BCryptUtils.encryptPassword(clientPasswordDTO.getUserPassword());
        Client client = new Client();
        client.setUserId(userId);
        client.setUserPassword(encryptPassword);
        return clientMapper.updateById(client);
        // 根据 userId 更新密码
    }

    @Override
    public int headImageUpdate(String headImage) {
        Long userId = ThreadLocalUtils.get(JwtConstants.LOGIN_USER_ID, Long.class);
//        System.out.println(clientUpdateDTO.getTelephone());
//        System.out.println(clientUpdateDTO.getSchool());
        if (userId == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }
        Client client = clientMapper.selectById(userId);
        if (client == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXISTS);
        }

        client.setHeadImage(headImage);

        clientCacheManager.refreshClient(client);
        tokenService.refreshLoginUser(client.getNickName(), client.getHeadImage(),
                ThreadLocalUtils.get(JwtConstants.LOGIN_USER_KEY, String.class));
        return clientMapper.updateById(client);
    }

    private void checkCode(String telephone, String code) {
        String telephoneCodeKey = getTelephoneCodeKey(telephone);
        String cacheCode = redisService.getCacheObject(telephoneCodeKey, String.class);
        if (StrUtil.isEmpty(cacheCode)) {
            throw new ServiceException(ResultCode.TELEPHONE_CODE_INVALID);
        }
        if (!cacheCode.equals(code)) {
            throw new ServiceException(ResultCode.TELEPHONE_CODE_ERROR);
        }
    }

    private static String getTelephoneCodeKey(String telephone) {
        return RedisConstants.TELEPHONE_CODE + telephone;
    }

    private static String getTelephoneCodeTimesKey(String telephone) {
        return RedisConstants.GET_CODE_TIMES + telephone;
    }

    public static boolean checkTelephone(String telephone) {
//        Pattern p = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Pattern p = Pattern.compile("^1[3-9]\\d{9}$");
        Matcher m = p.matcher(telephone);
        return m.matches();
    }
}
