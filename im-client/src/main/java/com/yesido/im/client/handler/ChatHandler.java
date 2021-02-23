package com.yesido.im.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理聊天信息
 * 
 * @author yesido
 * @date 2019年8月8日 上午10:04:02
 */
public class ChatHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.CHAT_SIMPLE.getCode()) {
            logger.info("[单聊消息]收到消息：{}", JSONObject.toJSONString(message));
        } else {
            ctx.fireChannelRead(message);
        }
    }

}
