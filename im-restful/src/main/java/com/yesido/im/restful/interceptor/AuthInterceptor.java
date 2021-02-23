package com.yesido.im.restful.interceptor;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yesido.auth.ApiAuth;
import com.yesido.im.restful.exception.AuthException;
import com.yesido.im.restful.service.LoginService;
import com.yesido.im.restful.utils.ServletRequestUtil;

/**
 * 用户合法性拦截
 * 
 * @author yesido
 * @date 2019年8月28日 下午4:03:31
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private ApiAuth apiAuth;
    @Autowired
    private LoginService loginService;

    // 不需要认证的url
    private Set<String> ignoreAuthUrls = new HashSet<>();

    @Value("${auth.request.ignore_urls:null}")
    public void setIgnoreCosttimeLogUrls(String ignoreUrls) {
        if (StringUtils.isBlank(ignoreUrls) || "null".equals(ignoreUrls.trim())) {
            return;
        }
        String[] urls = ignoreUrls.replaceAll("，", ",").split(",");
        for (String url : urls) {
            ignoreAuthUrls.add(url);
        }
    }

    /**
     * 请求前置：controller执行之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        if (isIgnoreUrl(uri)) {
            return true;
        }
        String param = ServletRequestUtil.getParameter(request);
        String jwtToken = ServletRequestUtil.getJwfToken(request);
        if (StringUtils.isBlank(jwtToken)) {
            logger.warn("[拦截器]token不存在，请求路径：{}，请求参数：{}", uri, param);
            throw new AuthException("token不存在");
        }
        if (!apiAuth.verifyToken(jwtToken)) {
            logger.warn("[拦截器]token无法解析，请求路径：{}，请求参数：{}", uri, param);
            // 验证解析jwtToken
            throw new AuthException("token无法解析：" + jwtToken);
        }
        if (!loginService.checkLoginToken(jwtToken)) {
            logger.warn("[拦截器]token失效，请求路径：{}，请求参数：{}", uri, param);
            // 验证redis是否过期
            throw new AuthException("token失效：" + jwtToken);
        }
        return true;
    }

    private boolean isIgnoreUrl(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.startsWith("/rpc") || uri.startsWith("/im_restful/test")) {
            return true;
        }
        return ignoreAuthUrls.contains(uri);
    }
}
