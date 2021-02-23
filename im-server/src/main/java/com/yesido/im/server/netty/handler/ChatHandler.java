package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.server.service.NettyService;
import com.yesido.im.server.spring.SpringContextUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理聊天信息
 * 
 * @author yesido
 * @date 2019年7月31日 下午3:52:36
 */
public class ChatHandler extends SimpleChannelInboundHandler<ChatMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.CHAT_SIMPLE.getCode()) {
            logger.info("[单聊消息]收到消息：{}", JSONObject.toJSONString(message));
            NettyService service = SpringContextUtil.getBean(NettyService.class);
            service.handlerChatSimpleMsg(message);
        } else {
            ctx.fireChannelRead(message);
        }
    }

}
