package com.yesido.im.server.netty;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yesido.im.ptoto.decoder.ProtostuffDecode;
import com.yesido.im.ptoto.encoder.ProtostuffEncoder;
import com.yesido.im.server.netty.config.NettyConfig;
import com.yesido.im.server.netty.handler.AnswerHandler;
import com.yesido.im.server.netty.handler.ChatHandler;
import com.yesido.im.server.netty.handler.HandshakeHandler;
import com.yesido.im.server.netty.handler.HeartbeatHandler;
import com.yesido.im.server.netty.handler.LoginHandler;
import com.yesido.im.server.netty.handler.ServerHealthHandler;
import com.yesido.im.server.netty.handler.WebSocketServerHandler;
import com.yesido.im.server.startup.ShutdownHook;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty 服务类
 * 
 * @author yesido
 * @date 2018年9月1日
 */
@Component
public class NettyServer {
    Logger logger = LoggerFactory.getLogger(getClass());

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    @Autowired
    private NettyConfig nettyConfig;
    @Autowired
    private ShutdownHook shutdownHook;

    @PostConstruct
    public void init() {
        logger.info("初始化服务器：{}", nettyConfig.toJsonString());
    }

    public synchronized void start() {
        if (nettyConfig.isWebsocketConn()) {
            new Thread(() -> {
                this.bindWebSocket();
            }).start();
        }
        if (nettyConfig.isTcpConn()) {
            new Thread(() -> {
                this.bind();
            }).start();
        }
    }

    public void bind() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            logger.info("server is started...");
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast(new ProtostuffDecode());
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast(new ProtostuffEncoder());

                            // 超时
                            int timeout = nettyConfig.getTimeout();
                            pipeline.addLast("timeout", new IdleStateHandler(timeout, timeout, timeout, TimeUnit.SECONDS));

                            pipeline.addLast("ServerHealthHandler", new ServerHealthHandler());
                            pipeline.addLast("LoginHandler", new LoginHandler());
                            pipeline.addLast("HeartbeatHandler", new HeartbeatHandler());
                            pipeline.addLast("ChatHandler", new ChatHandler());
                            pipeline.addLast("AnswerHandler", new AnswerHandler());

                        }
                    });
            ChannelFuture f = bootstrap.bind(nettyConfig.getHost(), nettyConfig.getPort()).sync();
            logger.info("TCP服务启动：TCP://" + nettyConfig.getHost() + ":" + nettyConfig.getPort());
            Thread t = Thread.currentThread();
            shutdownHook.registerThread(t, () -> {
                logger.info("[服务关闭]触发TCP服务停止：TCP://" + nettyConfig.getHost() + ":" + nettyConfig.getPort());
                f.channel().close();
            });
            f.channel().closeFuture().sync(); // 阻塞
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void bindWebSocket() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelInitializer<SocketChannel> childHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    int timeout = nettyConfig.getTimeout();
                    // 超时
                    pipeline.addLast("timeout", new IdleStateHandler(timeout, timeout, timeout, TimeUnit.SECONDS));
                    pipeline.addLast("http-codec", new HttpServerCodec());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                    pipeline.addLast("http-chunked", new ChunkedWriteHandler());

                    pipeline.addLast("HandshakeHandler", new HandshakeHandler());
                    pipeline.addLast("WebSocketServerHandler", new WebSocketServerHandler());
                    pipeline.addLast("ServerHealthHandler", new ServerHealthHandler());
                    pipeline.addLast("HeartbeatHandler", new HeartbeatHandler());
                    pipeline.addLast("ChatHandler", new ChatHandler());
                    pipeline.addLast("AnswerHandler", new AnswerHandler());
                }
            };
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(childHandler);
            int websocket_port = nettyConfig.getWebsocket_port();
            ChannelFuture f = bootstrap.bind(websocket_port).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        logger.info("websocket服务启动成功：ws://{}:{}/websocket", nettyConfig.getHost(), websocket_port);
                    } else {
                        logger.info("websocket服务启动失败：{}", f.cause().getLocalizedMessage());
                    }
                }
            });
            Thread t = Thread.currentThread();
            shutdownHook.registerThread(t, () -> {
                logger.info("[服务关闭]触发websocket服务停止：ws://{}:{}/websocket", nettyConfig.getHost(), websocket_port);
                f.channel().close();
            });

            f.channel().closeFuture().sync(); // 阻塞
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
