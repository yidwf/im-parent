package com.yesido.im.server.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.LoginMessage;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.service.NettyService;
import com.yesido.im.server.spring.SpringContextUtil;
import com.yesido.im.server.utils.BizUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理登录请求
 * 
 * @author yesido
 * @date 2019年8月7日 下午5:06:17
 */
public class LoginHandler extends SimpleChannelInboundHandler<LoginMessage> {
    Logger logger = LoggerFactory.getLogger(getClass());
    private ConnectionHolder holder = ConnectionHolder.instance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) throws Exception {
        if (message != null && message.getCode() == HeaderCode.LOGIN.getCode()) {
            logger.info("[登录处理]收到消息：{}", JSONObject.toJSONString(message));
            NettyService nettyService = SpringContextUtil.getBean(NettyService.class);

            String ip = BizUtil.getClientIp(ctx.channel());
            String account = message.getFrom();
            logger.info("[登录处理]登录ip：{}, account:{}", ip, account);
            if (nettyService.isInBlackList(ip)) {
                logger.info("[登录处理]登录失败：IP被列入黑名单 !");
                nettyService.loginFail(ctx, message.getSocketType(), "IP被列入黑名单!");
                return;
            }

            boolean success = nettyService.verifyAccount(account, message.getPrivateCode());
            if (!success) {
                logger.info("[登录处理]{}登录失败：账号与密码不匹配!", account);
                nettyService.loginFail(ctx, message.getSocketType(), "账号与密码不匹配!");
                return;
            }
            nettyService.loginSuccess(ctx, account, message.getSocketType()); // 处理登录
        } else {
            ctx.fireChannelRead(message);
        }
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
