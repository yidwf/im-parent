package com.yesido.websocket.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.model.LoginUser;
import com.yesido.websocket.service.LoginService;

@RestController
@RequestMapping
public class HomeController extends BaseController {

    @Value("${im_restful_host:localhost}")
    private String imRestfulHost;
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(String account, String password,
            HttpServletRequest request, HttpServletResponse response) {
        LoginUser loginUser = loginService.login(account, password);
        if (loginUser == null) {
            jsonResult.fail(response, "登录失败");
        }
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);
        JSONObject data = new JSONObject();
        data.put("success", true);
        jsonResult.data(data, response);
    }

    @RequestMapping(value = "/current/user", method = RequestMethod.POST)
    public void currentUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        jsonResult.data(loginUser, response);
    }

    @RequestMapping(value = "/im_restful/host", method = RequestMethod.POST)
    public void imRestfulHost(HttpServletRequest request, HttpServletResponse response) {
        JSONObject data = new JSONObject();
        data.put("im_restful_host", imRestfulHost);
        jsonResult.data(data, response);
    }
}
