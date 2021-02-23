package com.yesido.im.restful.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesido.im.ptoto.api.HttpResult;

/**
 * 全局错误控制器
 * 
 * @author yesido
 * @date 2019年8月26日 下午6:19:21
 */
@RestController
public class BaseController implements ErrorController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public HttpResult errorPage() {
        return HttpResult.serverError("网络繁忙，请稍后！");
    }

    @RequestMapping("/404")
    public HttpResult pageNotFound() {
        return HttpResult.notFound();
    }

    @RequestMapping("/401")
    public HttpResult unauthorized() {
        return HttpResult.unauthorized();
    }

}
