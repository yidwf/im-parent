package com.yesido.im.restful.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yesido.auth.ApiAuth;
import com.yesido.auth.MD5;
import com.yesido.im.ptoto.api.HttpResult;
import com.yesido.im.ptoto.model.LoginUser;
import com.yesido.im.restful.consts.Constant;
import com.yesido.lib.utils.BizUtil;
import com.yesido.redis.service.RedisService;
import com.yesido.rpc.service.im.server.RPCServerService;

@RestController
@RequestMapping(Constant.HTTP_URI_PREFIX + "/test")
public class TestController {
    private final static String login_user = "login_user#t:{1}";
    private final static int login_cd = 3600 * 24 * 7;
    @Autowired
    private RPCServerService rpcServerService;
    @Autowired
    private ApiAuth apiAuth;
    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/service/start", method = RequestMethod.GET)
    public HttpResult startService() {
        rpcServerService.startSrever();
        return HttpResult.ok();
    }

    @RequestMapping(value = "/service/stop", method = RequestMethod.GET)
    public HttpResult stopService() {
        rpcServerService.stopSrever();
        return HttpResult.ok();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public HttpResult login(String account, String password) {
        LoginUser loginUser = new LoginUser();
        loginUser.setAccount(account);
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
        return HttpResult.ok(loginUser);
    }
}
