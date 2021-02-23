package com.yesido.im.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.model.LoginUser;
import com.yesido.im.server.utils.BizUtil;
import com.yesido.redis.service.RedisService;

/**
 * 登录用户service
 * 
 * @author yesido
 * @date 2019年8月1日 上午11:21:48
 */
@Service
public class LoginUserService {
    Logger logger = LoggerFactory.getLogger(LoginUserService.class);

    @Autowired
    private RedisService redisService;

    /**
     * 获取登录用户信息
     */
    public LoginUser getLoginUser(String account) {
        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        LoginUser user = redisService.get(userKey, LoginUser.class);
        return user;
    }

    /**
     * 移除登录用户信息
     */
    public void removeLoginUser(String account) {
        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        redisService.delete(userKey);
    }

    /**
     * 获取用户登录IP
     */
    public String getUserLoginIp(String account) {
        LoginUser user = getLoginUser(account);
        if (user != null) {
            return user.getLoginIp();
        }
        return null;
    }

    /**
     * 判断用户是不是在本服务节点登录
     * 
     * @return
     */
    public boolean isLoginThisServer(String account) {
        LoginUser user = getLoginUser(account);
        if (user != null && user.getLoginIp().equals(Parameter.getServerIp())) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否登录
     */
    public boolean isLogin(String account) {
        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        return redisService.exist(userKey);
    }
}
