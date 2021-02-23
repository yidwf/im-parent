package com.yesido.im.server.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.model.LoginUser;
import com.yesido.im.server.spring.SpringContextUtil;
import com.yesido.im.server.utils.BizUtil;
import com.yesido.redis.service.RedisService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 管理连接
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class ConnectionHolder {
    Logger logger = LoggerFactory.getLogger(ConnectionHolder.class);
    public final static AttributeKey<Long> ATTR_CONN_ID = AttributeKey.valueOf("connection_id"); // sessionid
    public final static AttributeKey<String> ATTR_CONN_OWNER = AttributeKey.valueOf("connection_owner"); // account
    public final static AttributeKey<AtomicInteger> ATTR_CONN_TIMEOUT_COUNT = AttributeKey.valueOf("connection_timeout_count"); // 超时次数
    public final static AttributeKey<AtomicInteger> ATTR_CONN_HEARTBEAT_COUNT = AttributeKey.valueOf("connection_heartbeat_count"); // 心跳次数

    private final AtomicLong atomicLong;
    private final Map<Long, Connection> connections;
    private final Map<String, Long> owners;

    /** -----------单例----------- **/
    private static ConnectionHolder instance = new ConnectionHolder();

    private ConnectionHolder() {
        atomicLong = new AtomicLong(0);
        connections = new ConcurrentHashMap<>();
        owners = new ConcurrentHashMap<>();
    }

    public static ConnectionHolder instance() {
        if (instance == null) {
            instance = new ConnectionHolder();
        }
        return instance;
    }

    public Connection create(ChannelHandlerContext ctx) {
        long sessionId = atomicLong.incrementAndGet();
        ctx.channel().attr(ATTR_CONN_ID).set(sessionId);
        Connection connection = new Connection(sessionId, ctx);
        connections.put(sessionId, connection);
        ctx.channel().attr(ConnectionHolder.ATTR_CONN_HEARTBEAT_COUNT).set(new AtomicInteger(2));
        ctx.channel().attr(ConnectionHolder.ATTR_CONN_TIMEOUT_COUNT).set(new AtomicInteger(0));
        return connection;
    }

    public void loginSuccess(ChannelHandlerContext ctx, String owner, int socketType) {
        Connection conn = get(ctx);
        conn.setOwner(owner);
        ctx.channel().attr(ATTR_CONN_OWNER).set(owner);
        owners.put(owner, ctx.channel().attr(ATTR_CONN_ID).get());
        conn.setSocketType(socketType);
    }

    public String getOwner(ChannelHandlerContext ctx) {
        String owner = ctx.channel().attr(ATTR_CONN_OWNER).get();
        return owner;
    }

    public Connection get(Long sessionId) {
        if (sessionId == null) {
            return null;
        }
        return connections.get(sessionId);
    }

    public Connection get(String owner) {
        Long sessionId = owners.get(owner);
        return get(sessionId);
    }

    public Connection get(ChannelHandlerContext ctx) {
        Long sessionId = ctx.channel().attr(ATTR_CONN_ID).get();
        return get(sessionId);
    }

    public void remove(Long sessionId) {
        if (sessionId == null) {
            return;
        }
        Connection connection = connections.remove(sessionId);
        if (connection != null) {
            logger.info("清除连接...sessionid=" + connection.getSessionId());
            String owner = connection.getOwner();
            if (owner != null && owners.containsKey(owner)) {
                owners.remove(owner);
            }
            connection.close();
            connection = null;

            clearRedisInfo(owner);
        }
    }

    /**
     * 清楚redis缓存信息
     */
    private void clearRedisInfo(String account) {
        if (account == null) {
            return;
        }
        RedisService redisService = SpringContextUtil.getBean(RedisService.class);
        String allUserKey = BizUtil.key(RedisKey.HK_IM_SERVER_LOGIN_USERS, Parameter.getServerIp());
        redisService.hdel(allUserKey, account);

        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        LoginUser user = redisService.get(userKey, LoginUser.class);
        if (user != null) {
            if (user.getLoginIp().equals(Parameter.getServerIp())) {
                redisService.delete(userKey);
            }
        }
    }

    public void remove(String owner) {
        Long sessionId = owners.get(owner);
        remove(sessionId);
    }

    public void remove(ChannelHandlerContext ctx) {
        Long sessionId = ctx.channel().attr(ATTR_CONN_ID).get();
        remove(sessionId);
    }

    public void remove(Connection connection) {
        Long sessionId = connection.getSessionId();
        remove(sessionId);
    }

    public AtomicLong getAtomicLong() {
        return atomicLong;
    }

    public Map<Long, Connection> getConnections() {
        return connections;
    }

    public Map<String, Long> getOwners() {
        return owners;
    }

    public boolean isActive(Long sessionId) {
        return get(sessionId) == null;
    }

    public boolean isActive(String owner) {
        return get(owner) == null;
    }

    public boolean isActive(ChannelHandlerContext ctx) {
        return get(ctx) == null;
    }
}
