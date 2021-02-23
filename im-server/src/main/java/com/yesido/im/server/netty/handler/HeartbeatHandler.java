package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.server.context.Connection;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.netty.response.ResponseManager;
import com.yesido.im.server.utils.HandlerUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 处理心跳
 * 
 * @author yesido
 * @date 2019年7月31日 上午10:43:37
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());
    private ConnectionHolder holder = ConnectionHolder.instance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.HEARTBEAT.getCode()) {
            // logger.info("[心跳消息]收到消息：{}", JSONObject.toJSONString(message));
            // 响应心跳
            holder.get(ctx).writeAndFlush(ResponseManager.buildHeartbeatResponse());

            int count = ctx.channel().attr(ConnectionHolder.ATTR_CONN_HEARTBEAT_COUNT).get().incrementAndGet();
            /*if (count % 10 == 0 || count % 15 == 0) {
                logger.info("[心跳消息]累积心跳次数：{}", count);
                // 适合10秒一次的心跳，心跳超时30秒
                logger.info("[心跳消息]重置心跳超时次数 && 重置用户登录过期时间");
                ctx.channel().attr(ConnectionHolder.ATTR_CONN_TIMEOUT_COUNT).get().set(0);
                HandlerUtil.updateUserExpireCD(holder.get(ctx).getOwner());
            }*/
            if (count % 4 == 0 || count % 6 == 0) {
                //logger.info("[心跳消息]用户({})累积心跳次数：{}", holder.getOwner(ctx), count);
                // 适合30秒一次的心跳，心跳超时30秒
                //logger.info("[心跳消息]用户({})重置心跳超时次数 && 重置用户登录过期时间", holder.getOwner(ctx));
                ctx.channel().attr(ConnectionHolder.ATTR_CONN_TIMEOUT_COUNT).get().set(0);
                HandlerUtil.updateUserExpireCD(holder.get(ctx).getOwner());
            }
        } else {
            ctx.fireChannelRead(message);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        // 心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                int limit = 5;
                Connection conn = holder.get(ctx);
                Integer count = ctx.channel().attr(ConnectionHolder.ATTR_CONN_TIMEOUT_COUNT).get().incrementAndGet();
                logger.info("[心跳超时]用户({})心跳超时，当前次数：{}", conn.getOwner(), count);
                if (count >= limit) {
                    logger.info("[心跳超时]用户({})心跳超时{}次，关闭链路", conn.getOwner(), limit);
                    holder.remove(conn);
                }
            }
        }
    }
}
