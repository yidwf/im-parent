package com.yesido.im.client.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.client.NettyClent;
import com.yesido.im.client.request.RequestManager;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端心跳请求
 * 
 * @author yesido
 * @date 2017年5月18日 下午2:44:16
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<NettyMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());
    // 客户端
    private NettyClent client = NettyClent.getClient();
    // 心跳定时任务器
    private volatile ScheduledFuture<?> heartBeat;
    // 心跳计数器
    private volatile AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        if (message.getCode() == HeaderCode.LOGIN.getCode()) {
            logger.info("登录消息：{}", message.getContent());
            client.resetCounter().setReconnect(true); // 重置重连计数器 && 允许重连
            logger.info("开始心跳服务请求...");
            startHeartBeat(ctx);
        } else if (message.getCode() == HeaderCode.LOGOUT.getCode()) {
            logger.info("登出消息：{}", message.getContent());
            client.close();
        } else if (message.getCode() == HeaderCode.LOGIN_REPEAT.getCode()) {
            logger.info("重复登录：{}", message.getContent());
            client.setReconnect(false).close();
        } else if (message.getCode() == HeaderCode.HEARTBEAT.getCode()) {
            logger.info("心跳响应：{}", message.getContent());
            counter.set(0);
        } else {
            ctx.fireChannelRead(message);
        }
    }

    // 开始心跳
    private void startHeartBeat(ChannelHandlerContext ctx) {
        closeCurrentHeartBeat();
        heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 30, TimeUnit.SECONDS);
    }

    // 清除心跳
    private void closeCurrentHeartBeat() {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
    }

    /**
     * 定时发送心跳请求
     * 
     * @author yesido
     * @date 2019年8月7日 下午6:00:18
     */
    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext context;

        public HeartBeatTask(ChannelHandlerContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            int times = counter.incrementAndGet();
            if (times > 3) {
                logger.info("心跳异常超时，停止心跳，清除连接...");
                context.close();
                closeCurrentHeartBeat();
            } else {
                NettyMessage message = RequestManager.buildHeartBeatRequest();
                context.writeAndFlush(message);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("捕获异常：{}", ctx.channel().remoteAddress().toString(), cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接建立成功：{}", client.getAccount());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive连接关闭：{}", client.getAccount());
        client.reconnect(); // 尝试重连
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
