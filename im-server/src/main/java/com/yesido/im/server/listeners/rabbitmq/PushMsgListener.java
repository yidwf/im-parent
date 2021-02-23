package com.yesido.im.server.listeners.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.PushMessage;
import com.yesido.im.server.mq.decalre.QueueDeclare;
import com.yesido.im.server.service.NettyService;

/**
 * 监听消息推送
 * 
 * @author yesido
 * @date 2019年8月29日 下午3:46:55
 */
@Component
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = QueueDeclare.Q_IM_SERVER_MSG_PUSH, durable = "true", autoDelete = "false"),
        exchange = @Exchange(value = QueueDeclare.EX_TOPIC_IM_SERVER, type = ExchangeTypes.TOPIC, durable = "true"),
        key = QueueDeclare.RK_IM_SERVER_MSG_PUSH))
public class PushMsgListener {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NettyService nettyService;

    @RabbitHandler
    public void onMessage(String msg, Message message, Channel channel) throws Exception {
        try {
            logger.info("收到消息：{}", msg);
            PushMessage nettyMessage = JSONObject.parseObject(msg, PushMessage.class);
            HeaderCode code = HeaderCode.get(nettyMessage.getCode());
            switch (code) {
                case PUSH_MSG:
                    nettyService.handlerPushMsg(nettyMessage);
                    break;
                default:
                    logger.info("[消息推送]不支持的消息类型：{}-->{}", code, msg);
                    break;
            }
        } catch (Exception e) {
            logger.error("[消息推送]消息处理失败：{}", message, e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
