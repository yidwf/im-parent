package com.yesido.im.server.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.model.LoginUser;
import com.yesido.im.server.spring.SpringContextUtil;
import com.yesido.lib.utils.NumberUtil;
import com.yesido.redis.service.RedisService;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

/**
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class HandlerUtil {
    Logger logger = LoggerFactory.getLogger(HandlerUtil.class);

    /**
     * 获取请求参数
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static Map<String, String> parameterMap(FullHttpRequest request) throws IOException {
        Map<String, String> parameterMap = new HashMap<>();
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
        for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
            for (String attrVal : attr.getValue()) {
                parameterMap.put(attr.getKey(), attrVal);
            }
        }
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.offer(request);
        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData interfaceHttpData : parmList) {
            Attribute data = (Attribute) interfaceHttpData;
            parameterMap.put(data.getName(), data.getValue());
        }
        return parameterMap;
    }

    public static int getUserExpireCD() {
        return NumberUtil.range(120, 180);
    }

    /**
     * 刷新用户过期时间
     */
    public static void updateUserExpireCD(String account) {
        RedisService redisService = SpringContextUtil.getBean(RedisService.class);
        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        LoginUser user = redisService.get(userKey, LoginUser.class);
        if (user != null) {
            if (user.getLoginIp().equals(Parameter.getServerIp())) {
                redisService.expire(userKey, getUserExpireCD());
            }
        }
    }

}
