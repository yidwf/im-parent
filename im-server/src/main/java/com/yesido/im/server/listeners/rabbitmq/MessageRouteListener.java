package com.yesido.im.server.listeners.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.PushMessage;
import com.yesido.im.server.service.NettyService;

/**
 * 处理路由消息
 * 
 * @author yesido
 * @date 2019年7月25日 下午3:02:34
 */
@Component
public class MessageRouteListener implements ChannelAwareMessageListener {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NettyService service;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String _msg = new String(message.getBody());
            logger.info("收到消息：{}", _msg);
            JSONObject obj = JSONObject.parseObject(_msg);
            HeaderCode code = HeaderCode.get(obj.getIntValue("code"));
            switch (code) {
                case CHAT_SIMPLE:
                    ChatMessage chatMessage = JSONObject.parseObject(_msg, ChatMessage.class);
                    service.handlerChatSimpleMsg(chatMessage);
                    break;
                case PUSH_MSG:
                    PushMessage pushMessage = JSONObject.parseObject(_msg, PushMessage.class);
                    service.handlerPushMsg(pushMessage);
                    break;
                default:
                    logger.info("[路由消息]不支持的消息类型：{}-->{}", code, _msg);
                    break;
            }
        } catch (Exception e) {
            logger.error("[路由消息]消息处理失败：{}", message, e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
