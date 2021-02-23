package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.netty.response.ResponseManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 拒接对外提供服务
 * 
 * <pre>
 * 当我们服务需要更新重启时，我们暂停对外提供服务
 * 1、关闭所有连接
 * 2、不处理新的消息
 * 3、正在处理的消息继续处理，服务器hold住一段时间后重启
 * </pre>
 * 
 * @author yesido
 * @date 2019年10月16日 下午4:13:37
 */
public class ServerHealthHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());
    private ConnectionHolder holder = ConnectionHolder.instance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (Parameter.SERVER_STATUS != 1) {
            logger.warn("[服务状态]当前服务状态停止：{}，拒绝处理消息：{}", Parameter.SERVER_STATUS, message.toJsonString());
            holder.get(ctx).writeAndFlush(ResponseManager.serverUnavailable());
            return;
        }
        ctx.fireChannelRead(message);
    }

}
