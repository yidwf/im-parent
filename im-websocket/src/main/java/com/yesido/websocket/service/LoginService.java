package com.yesido.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.auth.ApiAuth;
import com.yesido.auth.MD5;
import com.yesido.im.ptoto.model.LoginUser;
import com.yesido.lib.utils.BizUtil;
import com.yesido.lib.utils.ServletRequestUtil;
import com.yesido.redis.service.RedisService;

/**
 * 登录服务
 * 
 * @author yesido
 * @date 2019年8月29日 上午10:36:20
 */
@Service
public class LoginService {

    private final static String login_user = "login_user#t:{1}";
    private final static int login_cd = 3600 * 24 * 7;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ApiAuth apiAuth;

    /**
     * 登录
     */
    public LoginUser login(String account, String password) {
        LoginUser loginUser = new LoginUser();
        loginUser.setAccount(account);
        // loginUser.setPassword(password);
        loginUser.setName(account);
        if ("admin".equals(account)) {
            loginUser.setIcon("/images/chat/1.jpg");
        } else {
            loginUser.setIcon("/images/chat/2.png");
        }
        String jwtToken = apiAuth.createToken(account);
        String token = MD5.encoded(jwtToken);
        String key = BizUtil.key(login_user, token);
        loginUser.setToken(token);
        loginUser.setJwtToken(jwtToken);
        redisService.set(key, loginUser, login_cd);

        ServletRequestUtil.writeCookie("jwtToken", jwtToken);
        return loginUser;
    }
}
