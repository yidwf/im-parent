package com.yesido.im.restful.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.auth.MD5;
import com.yesido.im.ptoto.model.LoginUser;
import com.yesido.im.restful.utils.ServletRequestUtil;
import com.yesido.lib.utils.BizUtil;
import com.yesido.redis.service.RedisService;

/**
 * 登录服务
 * 
 * @author yesido
 * @date 2019年8月29日 上午11:36:13
 */
@Service
public class LoginService {
    private final static String login_user = "login_user#t:{1}";
    @Autowired
    private RedisService redisService;

    /**
     * 获取登录用户
     * 
     * @param jwtToken 原始jwtToken
     * @return LoginUser
     */
    public LoginUser getLoginUser(String jwtToken) {
        String token = MD5.encoded(jwtToken);
        String key = BizUtil.key(login_user, token);
        return redisService.get(key, LoginUser.class);
    }

    /**
     * 获取登录用户
     * 
     * @return LoginUser
     */
    public LoginUser getLoginUser() {
        String jwtToken = ServletRequestUtil.getJwtToken();
        return getLoginUser(jwtToken);
    }

    /**
     * 获取登录用户account
     */
    public String getLoginUserAccount() {
        LoginUser user = getLoginUser();
        if (user == null) {
            return null;
        }
        return user.getAccount();
    }

    /**
     * 检测token是否过期
     * 
     * @param jwtToken 原始jwtToken
     * @return boolean true=不过期，false=过期
     */
    public boolean checkLoginToken(String jwtToken) {
        String token = MD5.encoded(jwtToken);
        String key = BizUtil.key(login_user, token);
        return redisService.exist(key);
    }

    /**
     * 检测token是否过期
     * 
     * @return boolean true=不过期，false=过期
     */
    public boolean checkLoginToken() {
        String jwtToken = ServletRequestUtil.getJwtToken();
        return checkLoginToken(jwtToken);
    }
}
