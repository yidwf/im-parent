package com.yesido.im.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.client.request.RequestManager;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理推送
 * 
 * @author yesido
 * @date 2019年8月8日 下午2:31:02
 */
public class PushHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.PUSH_MSG.getCode()) {
            logger.info("[推送消息]收到消息：{}", JSONObject.toJSONString(message));
            Map<String, String> attachment = message.getAttachment();
            List<String> answerIds = new ArrayList<String>();
            for (Entry<String, String> entry : attachment.entrySet()) {
                answerIds.add(entry.getKey());
            }
            NettyMessage msg = RequestManager.buildAnswerRequest(answerIds);
            ctx.writeAndFlush(msg);
        } else {
            ctx.fireChannelRead(message);
        }
    }

}
