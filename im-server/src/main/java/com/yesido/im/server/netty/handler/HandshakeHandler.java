package com.yesido.im.server.netty.handler;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.ptoto.enums.SocketType;
import com.yesido.im.server.netty.config.NettyConfig;
import com.yesido.im.server.service.NettyService;
import com.yesido.im.server.spring.SpringContextUtil;
import com.yesido.im.server.utils.BizUtil;
import com.yesido.im.server.utils.HandlerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * websocket握手
 * 
 * @author yesido
 * @date 2019年7月31日 下午3:49:48
 */
public class HandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(getClass());

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        handlerHttpRequest(ctx, (FullHttpRequest) msg);
    }

    private void handlerHttpRequest(ChannelHandlerContext ctx,
            FullHttpRequest request) throws IOException {
        // 如果HTTP解码失败，返回HTTP异常
        if (!request.decoderResult().isSuccess()
                || !"websocket".equals(request.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String uri = request.uri();
        String http_ip = BizUtil.getClientIp(request);
        String channel_ip = BizUtil.getClientIp(ctx.channel());
        ctx.channel().attr(BizUtil.http_ip_key).set(http_ip);

        logger.info(request.headers().entries().toString());
        logger.info("[握手接入]http_ip：{}, channel_ip:{}, handshaker uri:{}", http_ip, channel_ip, uri);

        NettyService nettyService = SpringContextUtil.getBean(NettyService.class);
        NettyConfig nettyConfig = SpringContextUtil.getBean(NettyConfig.class);

        String ip = BizUtil.getIp(ctx.channel());
        Map<String, String> parameters = HandlerUtil.parameterMap(request);
        String account = parameters.get("account");
        String password = parameters.get("password");
        logger.info("[握手接入]登录ip：{}, account:{}", ip, account);
        if (nettyService.isInBlackList(ip)) {
            logger.info("[握手接入]登录失败：IP被列入黑名单 !");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        boolean success = nettyService.verifyAccount(account, password);
        if (!success) {
            logger.info("[握手接入]{}登录失败：账号与密码不匹配!", account);
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }
        int websocket_port = nettyConfig.getWebsocket_port();
        String webSocketURL = "ws://localhost:" + websocket_port + "/websocket";
        // 构造握手响应
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(webSocketURL, null, false);
        handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            // 握手成功
            handshaker.handshake(ctx.channel(), request);

            nettyService.loginSuccess(ctx, account, SocketType.WEBSOCKET.getType()); // 处理登录
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // 返回应答给客户端
        if (response.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes()); // netty 4.0
            // HttpHeaderUtil.setContentLength(response, response.content().readableBytes()); // netty 5.0
        }

        // 如果是非Keep-Alive，关闭连接
        // netty 4.0
        ChannelFuture f = ctx.channel().writeAndFlush(response);
        if (HttpUtil.isKeepAlive(response) || response.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

        // netty 5.0
        /*if (!HttpHeaderUtil.isKeepAlive(response) || response.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }*/
    }
}
