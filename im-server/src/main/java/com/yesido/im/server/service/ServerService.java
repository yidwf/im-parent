package com.yesido.im.server.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.model.ServiceNode;
import com.yesido.im.server.netty.config.NettyConfig;
import com.yesido.im.server.startup.ShutdownHook;
import com.yesido.im.server.utils.BizUtil;
import com.yesido.redis.service.RedisService;
import com.yesido.zookeeper.balance.BalanceService;
import com.yesido.zookeeper.model.ServerData;

/**
 * 服务器service
 * 
 * @author yesido
 * @date 2019年10月16日 下午5:41:29
 */
@Service
public class ServerService {
    Logger logger = LoggerFactory.getLogger(getClass());
    private static AtomicInteger heartbeatCount = new AtomicInteger(0);
    private static Long launchTime = Parameter.SERVER_LAUNCH_TIME;
    private ServerData server;

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private NettyConfig nettyConfig;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShutdownHook shutdownHook;

    @PostConstruct
    public void initConfig() {
        if (this.server != null) {
            return;
        }
        synchronized (this) {
            if (this.server != null) {
                return;
            }
            this.server = new ServerData();
            this.server.setHost(nettyConfig.getHost());
            this.server.setWebsocket_port(nettyConfig.getWebsocket_port());
            this.server.setPort(nettyConfig.getPort());
            this.server.setSid(UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }

    /**
     * 初始化服务器
     */
    public void initServer() {
        String serverIp = Parameter.getServerIp();
        // 在本机登录的所有账号
        String loginUserKey = BizUtil.key(RedisKey.HK_IM_SERVER_LOGIN_USERS, serverIp);
        if (redisService.exist(loginUserKey)) {
            redisService.delete(loginUserKey);
        }
        heartbeat(); // 服务器心跳
        register(); // 注册到zookeeper

        // 注册服务关闭钩子
        Thread t = Thread.currentThread();
        shutdownHook.registerThread(t, () -> {
            logger.info("[服务关闭]触发服务停止，线程：{}", JSONObject.toJSONString(t));
            stopSrever();
        });
    }

    /**
     * 服务器心跳
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void heartbeat() {
        Long timeout = 10L;
        int limit = 3;
        Long timeout_cd = timeout * (limit + 3);
        long currentTime = System.currentTimeMillis();
        String serverIp = Parameter.getServerIp();
        int count = heartbeatCount.incrementAndGet();
        if (count % limit == 0 || count == 1) {
            // 更新过期时间，更新心跳时间，保证服务器节点正常
            ServiceNode serverHeartbeat = new ServiceNode(serverIp, launchTime, currentTime);
            redisService.set(BizUtil.key(RedisKey.H_IM_SERVICE_IP_HEARTBEAT, serverIp), serverHeartbeat, timeout_cd);

            // 更新服务器节点信息
            Map<String, Long> owners = ConnectionHolder.instance().getOwners();
            ServiceNode currentServer = new ServiceNode(serverIp, launchTime, owners.size());
            redisService.hset(RedisKey.HK_IM_SERVER_RUN_IPS, serverIp, currentServer);
        }

        String lock = BizUtil.key("im_server_heartbeat_lock#t={1}", currentTime / 1000 / timeout);
        if (!redisService.setNxEx(lock, 1, timeout)) {
            return;
        }

        Map<String, ServiceNode> servers = redisService.hgetall(RedisKey.HK_IM_SERVER_RUN_IPS, ServiceNode.class);
        logger.debug("准备服务器节点心跳检测：{}", servers.values());
        Set<String> timeoutNodes = new HashSet<String>();
        for (Entry<String, ServiceNode> entry : servers.entrySet()) {
            String _serverIp = entry.getValue().getServerIp();
            String _key = BizUtil.key(RedisKey.H_IM_SERVICE_IP_HEARTBEAT, _serverIp);
            ServiceNode server = redisService.get(_key, ServiceNode.class);
            if (server == null) {
                logger.info("无法获取服务器节点[{}]心跳信息，移除信息", _serverIp);
                redisService.hdel(RedisKey.HK_IM_SERVER_RUN_IPS, _serverIp);
                timeoutNodes.add(_serverIp);
            }
        }
        if (timeoutNodes.isEmpty()) {
            logger.debug("服务器节点心跳检测完成...");
        } else {
            logger.warn("服务器节点心跳检测完成，超时节点：{}", timeoutNodes);
        }
    }

    /**
     * 注册到zookeeper
     */
    public void register() {
        balanceService.register("/services_balances/" + appName, server);
    }

    /**
     * 取消zookeeper服务节点注册
     */
    public void unRegister() {
        balanceService.unRegister("/services_balances/" + appName, server);
    }

    /**
     * 设置服务不可用
     */
    public void stopSrever() {
        Parameter.SERVER_STATUS = 0;
        unRegister();
    }

    /**
     * 设置服务启动
     */
    public void startSrever() {
        Parameter.SERVER_STATUS = 1;
        register();
    }
}
