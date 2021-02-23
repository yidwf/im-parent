package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.server.service.NettyService;
import com.yesido.im.server.spring.SpringContextUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 消息应答通道
 * 
 * @author yesido
 * @date 2019年8月6日 上午11:44:15
 */
public class AnswerHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.PUSH_MSG_ANSWER.getCode()) {
            logger.info("[应答消息]收到消息：{}", JSONObject.toJSONString(message));
            NettyService service = SpringContextUtil.getBean(NettyService.class);
            service.msgAnswer(ctx, message);
        } else {
            ctx.fireChannelRead(message);
        }
    }
}
