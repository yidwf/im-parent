package com.yesido.im.restful.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.restful.handler.RequestWrapper;

/**
 * ServletRequest 工具类
 * 
 * @author yesido
 * @date 2019年8月28日 下午3:31:50
 */
public class ServletRequestUtil extends com.yesido.lib.utils.ServletRequestUtil {

    public static String getParameter(HttpServletRequest request) {
        String reqParam = null;
        if (request instanceof RequestWrapper) {
            RequestWrapper wrapper = (RequestWrapper) request;
            if (wrapper.isBodyEmpty()) {
                Map<String, String[]> map = wrapper.getParameterMap();
                reqParam = JSONObject.toJSONString(map);
            } else {
                reqParam = wrapper.getBody();
            }
        } else {
            Map<String, String[]> map = request.getParameterMap();
            reqParam = JSONObject.toJSONString(map);
        }
        return reqParam;
    }
}
