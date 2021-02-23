package com.yesido.im.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.client.handler.ChatHandler;
import com.yesido.im.client.handler.HeartbeatHandler;
import com.yesido.im.client.handler.PushHandler;
import com.yesido.im.client.request.RequestManager;
import com.yesido.im.ptoto.decoder.ProtostuffDecode;
import com.yesido.im.ptoto.encoder.ProtostuffEncoder;
import com.yesido.im.ptoto.enums.SocketType;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 客户端
 * 
 * @author yesido
 * @date 2019年8月7日 下午4:28:33
 */
public class NettyClent {
    Logger logger = LoggerFactory.getLogger(getClass());
    /** --发起重连线程-- **/
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    /** --两次重连间隔时间：单位秒-- **/
    private final static int ATTEMPT_INTERVEL_TIME = 5;
    /** --尝试重新连接次数-- **/
    private final static int ATTEMPT_CONN_TIMES = 10;
    /** --计数器-- **/
    private AtomicInteger attemptConnTimes = new AtomicInteger(0);
    public String host;
    public int port;
    /** --账号：用来登录-- **/
    private String account;
    /** --密码：用来登录-- **/
    private String password;

    private Channel channel;
    /** --断线后是否重新连接-- **/
    private boolean isReconnect = true;

    private NettyClent() {
        host = "192.168.8.142";
        port = 9090;
    }

    private static class NettyClientHolder {
        public static NettyClent client = new NettyClent();
    }

    public static NettyClent getClient() {
        return NettyClientHolder.client;
    }

    /**
     * 连接服务器
     */
    public void connect() {
        logger.info("准备连接服务器-->" + host + ":" + port);
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            logger.info("连接失败：账号或者密码为空！");
            return;
        }
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 编码解码
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast(new ProtostuffDecode());
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast(new ProtostuffEncoder());

                            // 业务handler
                            pipeline.addLast("HeartbeatHandler", new HeartbeatHandler());
                            pipeline.addLast("ChatHandler", new ChatHandler());
                            pipeline.addLast("PushHandler", new PushHandler());
                        }
                    });
            logger.info("准备连接服务器，host：{}，port：{}", host, port);
            this.channel = bootstrap.connect(host, port).sync().channel();
            // 登录请求
            NettyMessage message = RequestManager.buildLoginRequest(account, password, SocketType.SOCKET);
            this.writeAndFlush(message);
            this.channel.closeFuture().sync(); // 阻塞
        } catch (Exception e) {
            logger.error("连接异常：" + e.getMessage());
            reconnect(); // 异常重连，不要放在finally里面
        } finally {
            group.shutdownGracefully();
            group = null;
        }
    }

    /**
     * 断线重连
     */
    public void reconnect() {
        int accemptTimes = attemptConnTimes.incrementAndGet();
        if (isReconnect() && accemptTimes <= ATTEMPT_CONN_TIMES) {
            int nextAttemptTime = ATTEMPT_INTERVEL_TIME + (accemptTimes - 1) * ATTEMPT_INTERVEL_TIME;
            logger.info("第{}次发起重连，将在{}秒后重连...", accemptTimes, nextAttemptTime);
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(nextAttemptTime);
                        connect();
                    } catch (Exception e) {
                        logger.error("发起重连异常...", e);
                    }
                }
            });
        } else {
            logger.info("客户端退出重连，是否允许重连：{}，当前尝试重连次数：{}", isReconnect(), accemptTimes);
        }
    }

    public String getAccount() {
        return account;
    }

    public NettyClent setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public NettyClent setPassword(String password) {
        this.password = password;
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public NettyClent setReconnect(boolean isReconnect) {
        this.isReconnect = isReconnect;
        return this;
    }

    public NettyClent resetCounter() {
        attemptConnTimes.set(0);
        return this;
    }

    public boolean isConnect() {
        if (channel == null) {
            return false;
        }
        return channel.isActive() && channel.isOpen();
    }

    /**
     * 关闭连接
     */
    public NettyClent close() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        return this;
    }

    /**
     * 向服务器发送数据
     */
    public void writeAndFlush(Object message) {
        if (channel != null) {
            channel.writeAndFlush(message);
        }
    }
}
