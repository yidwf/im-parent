package com.yesido.im.server.listeners.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.service.NettyService;

/**
 * 监听服务器退出用户登录
 * 
 * @author yesido
 * @date 2019年7月18日 下午3:13:28
 */
@Component
public class NotifyServerLogoutListener {
    private static final Logger logger = LoggerFactory.getLogger(NotifyServerLogoutListener.class);

    @Autowired
    private NettyService nettyService;

    public void handleMessage(String message) {
        try {
            logger.info("[订阅消息]消息内容：{}", message);
            handle(message);
        } catch (Exception e) {
            logger.info("[订阅消息]处理消息异常：{}", message, e);
        }
    }

    private void handle(String message) {
        NettyMessage msg = JSONObject.parseObject(message, NettyMessage.class);
        HeaderCode code = HeaderCode.get(msg.getCode());
        if (code == null) {
            logger.info("[收到监听]无法识别的消息类型：{}", message);
            return;
        }
        switch (code) {
            case LOGIN_REPEAT:
                Map<String, String> attachment = msg.getAttachment();
                String account = attachment.get("account");
                String lastLoginIp = attachment.get("lastLoginIp");
                if (lastLoginIp.equals(Parameter.getServerIp())) {
                    logger.info("[收到监听]通知服务器关闭用户登录连接：{}", message);
                    nettyService.removeUserConnection(account, lastLoginIp);
                }
                break;
            default:
                logger.info("[收到监听]暂时不支持的消息类型：{}", message);
                break;
        }
    }
}
