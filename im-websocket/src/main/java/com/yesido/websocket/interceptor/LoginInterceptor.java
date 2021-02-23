package com.yesido.websocket.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;

/**
 * 登录拦截器
 * 
 * @author Administrator
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    /**
     * 请求前置：controller 执行之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("请求前置处理:" + request.getRequestURL() + (request.getQueryString() == null ? "" : ("?" + request.getQueryString())));
        /*HttpSession session = request.getSession();
        Object object = session.getAttribute("loginUser");
        if(object == null) {
        	loginFalse(request, response);
        	return false;
        }
        JSONObject authorized = authorized(request, response);
        if(!authorized.getBooleanValue("status")) {
        	return false;
        }
        request.setAttribute("request_startTime", System.currentTimeMillis());*/
        return true;
    }

    public JSONObject authorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject object = new JSONObject();
        object.put("status", true);
        if (request.getRequestURI().indexOf("/admin/login") != -1) {
            object.put("status", false);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "您无权限访问该页面！");
        }
        return object;
    }

    /**
     * 请求后置：controller 执行之后，且页面渲染之前调用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        Object obj = request.getAttribute("request_startTime");
        if (obj != null) {
            long endTime = System.currentTimeMillis();
            long startTime = Long.parseLong(obj.toString());
            long requestTime = endTime - startTime;
            if (requestTime > 1000 * 3) {
                logger.warn(request.getRequestURL() + (request.getQueryString() == null ? "" : ("?" + request.getQueryString())) + "请求完成，总用时："
                        + requestTime + "毫秒");
            }
            logger.info("请求完成：" + request.getRequestURL() + "，总用时：" + requestTime + "毫秒");
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 页面渲染之后调用，一般用于资源清理操作
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void loginFalse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        StringBuilder builder = new StringBuilder();
        if (isAjaxRequest(request)) {
            JSONObject result = new JSONObject();
            result.put("status", 500);
            result.put("msg", "登录超时");
            builder.append(result.toJSONString());
        } else {

            builder.append("<script type=\"text/javascript\">");
            builder.append("window.top.location.href='" + request.getContextPath() + "/signin.html';");
            builder.append("</script>");
        }
        out.print(builder.toString());
        out.flush();
        out.close();
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        if (header != null && "XMLHttpRequest".equals(header)) {
            return true;
        } else {
            return false;
        }
    }
}
