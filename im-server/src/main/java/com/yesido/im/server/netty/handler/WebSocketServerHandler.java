package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.server.context.ConnectionHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * websocket消息解析
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    Logger logger = LoggerFactory.getLogger(getClass());

    private ConnectionHolder holder = ConnectionHolder.instance();
    private WebSocketServerHandshaker handshaker;

    // netty 4.0
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            logger.info("链路关闭消息");
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            logger.info("收到Ping消息");
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        // 业务消息开始处理
        String text = ((TextWebSocketFrame) frame).text();
        JSONObject obj = JSONObject.parseObject(text);
        HeaderCode code = HeaderCode.get(obj.getIntValue("code"));
        NettyMessage message = null;
        switch (code) {
            case CHAT_SIMPLE:
                message = JSONObject.parseObject(text, ChatMessage.class);
                break;
            default:
                message = JSONObject.parseObject(text, NettyMessage.class);
                break;
        }
        ctx.fireChannelRead(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        holder.create(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        holder.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("捕获异常：{}", ctx.channel().remoteAddress().toString(), cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
