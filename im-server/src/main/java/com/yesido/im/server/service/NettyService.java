package com.yesido.im.server.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.BizType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.enums.SocketType;
import com.yesido.im.ptoto.model.entity.GroupMember;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.ptoto.netty.PushMessage;
import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.consts.RedisConst;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.context.Connection;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.model.LoginUser;
import com.yesido.im.server.model.ServiceNode;
import com.yesido.im.server.mq.decalre.QueueDeclare;
import com.yesido.im.server.netty.response.ResponseManager;
import com.yesido.im.server.utils.BizUtil;
import com.yesido.im.server.utils.HandlerUtil;
import com.yesido.mongodb.base.UpdateParam;
import com.yesido.mongodb.service.MongoDBService;
import com.yesido.rabbitmq.sender.RabbitSender;
import com.yesido.redis.service.RedisService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * netty服务类
 * 
 * @author yesido
 * @date 2018年9月1日
 */
@Service
public class NettyService {
    Logger logger = LoggerFactory.getLogger(NettyService.class);
    private ConnectionHolder holder = ConnectionHolder.instance();

    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private LoginUserService loginUserService;
    @Autowired
    private RabbitSender rabbitSender;

    /**
     * 验证账号合法性
     */
    public boolean verifyAccount(String account, String password) {
        if (StringUtils.isBlank(account) ||
                StringUtils.isBlank(password)) {
            return false;
        }
        return true;
    }

    /**
     * 黑名单验证
     */
    public boolean isInBlackList(String ip) {
        return false;
    }

    /**
     * 登录失败
     */
    public void loginFail(ChannelHandlerContext ctx, int socketType, String msg) {
        NettyMessage response = ResponseManager.buildLoginFailMsg(msg);
        if (socketType == SocketType.WEBSOCKET.getType()) {
            ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(response)));
        } else {
            ctx.writeAndFlush(response);
        }
        holder.remove(ctx);
    }

    /**
     * 获取服务器启动时间
     */
    public Long serverLanuchTime(String serverIp) {
        if (Parameter.getServerIp().equals(serverIp)) {
            return Parameter.SERVER_LAUNCH_TIME;
        }
        ServiceNode server = redisService.hget(RedisKey.HK_IM_SERVER_RUN_IPS, serverIp, ServiceNode.class);
        if (server != null) {
            return server.getLaunchTime();
        }
        return null;
    }

    /**
     * 通知服务器移除登录信息
     */
    public void notifyServerLogout(String loginIp, String account, String fromIp) {
        NettyMessage message = new NettyMessage();
        message.setCode(HeaderCode.LOGIN_REPEAT.getCode());
        message.addAttachment("account", account);
        message.addAttachment("fromIp", fromIp);
        message.addAttachment("lastLoginIp", loginIp);
        redisService.publish(RedisConst.REDIS_CHANNEL_NOTIFY_SERVER_LOGOUT, message);
    }

    /**
     * 通知服务器用户退出登录
     * 
     * @param account
     * @param lastLoginIp
     */
    public void removeUserConnection(String account, String lastLoginIp) {
        Connection oldConn = holder.get(account);
        if (oldConn != null) {
            logger.info("[登录提醒]用户[{}]重复登录了，关闭在服务器IP[{}]的连接，本服务器：{}", account, lastLoginIp, Parameter.getServerIp());
            NettyMessage response = ResponseManager.buildLoginRepeatMsg(lastLoginIp);
            oldConn.writeAndFlush(response);
            holder.remove(oldConn);
        } else {
            logger.info("[登录提醒]获取不到用户[{}]的登录连接，无法关闭在服务器IP[{}]的连接，本服务器：{}", account, lastLoginIp, Parameter.getServerIp());
        }
    }

    /**
     * 处理登录成功
     */
    public void loginSuccess(ChannelHandlerContext ctx, String account, int socketType) {
        repeatLogin(account); // 处理重复登录

        // 设置登录信息
        holder.loginSuccess(ctx, account, socketType);

        String serverIp = Parameter.getServerIp();
        LoginUser user = new LoginUser(account, serverIp, System.currentTimeMillis());
        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        redisService.set(userKey, user, HandlerUtil.getUserExpireCD());

        String allUserKey = BizUtil.key(RedisKey.HK_IM_SERVER_LOGIN_USERS, serverIp);
        redisService.hset(allUserKey, account, user);

        // 登录响应
        NettyMessage response = ResponseManager.buildSuccessResponse("登录成功，服务器IP：" + serverIp);
        holder.get(account).writeAndFlush(response);

        // 离线数据
        offlineMessage(account);
    }

    /**
     * 处理重复登录
     */
    private void repeatLogin(final String account) {
        String serviceIp = Parameter.getServerIp();

        String userKey = BizUtil.key(RedisKey.H_IM_LOGIN_USER, account);
        LoginUser user = redisService.get(userKey, LoginUser.class);

        if (user != null) {
            if (user.getLoginIp().equals(serviceIp)) {
                // 登录的IP与本服务器一致
                redisService.delete(userKey);
                if (user.getLogintime() >= Parameter.SERVER_LAUNCH_TIME) {
                    // 踢掉上次登录
                    removeUserConnection(account, user.getLoginIp());
                }
            } else {
                // 上次登录是在其他服务器，通知其他服务器退出
                logger.info("通知服务器节点[{}]剔除用户[{}]的连接", user.getLoginIp(), account);
                notifyServerLogout(user.getLoginIp(), account, serviceIp);
            }
        }
    }

    public void offlineMessage(String account) {
        if (StringUtils.isBlank(account)) {
            logger.info("[离线数据]用户账号为空！");
            return;
        }
        Connection conn = holder.get(account);
        if (conn == null) {
            logger.info("[离线数据]无法获取用户连接：{}", account);
            return;
        }
        Map<String, String> _msgs = redisService.hgetall(RedisKey.offlineMsgKey(account));
        if (CollectionUtils.isEmpty(_msgs)) {
            logger.info("[离线数据]用户离线数据为空：{}", account);
            return;
        }
        _msgs = BizUtil.sortMap(_msgs);
        Map<String, Object> msgs = new LinkedHashMap<>();
        for (Entry<String, String> entry : _msgs.entrySet()) {
            msgs.put(entry.getKey(), entry.getValue());
            if (msgs.size() >= 5) {
                NettyMessage message = ResponseManager.buildPushResponse("离线消息数据", msgs);
                conn.writeAndFlush(message);
                msgs.clear();
                BizUtil.sleep(200);
            }
        }
        if (msgs.size() > 0) {
            NettyMessage message = ResponseManager.buildPushResponse("离线消息数据", msgs);
            conn.writeAndFlush(message);
            msgs.clear();
        }
    }

    /**
     * 处理单聊信息
     */
    public void handlerChatSimpleMsg(ChatMessage message) {
        String to = message.getTo();
        Connection conn = holder.get(to);
        if (conn == null) {
            String loginIp = loginUserService.getUserLoginIp(to);
            if (loginIp != null) {
                // 用户在其他服务器登录：路由消息到其他服务器
                rabbitSender.sendMessage(QueueDeclare.EX_TOPIC_IM_SERVER, QueueDeclare.getMsgRouteRoutingKey(loginIp), message);
            } else {
                // 用户离线
                messageService.handlerChatMessgae(message, false);
            }
        } else {
            // 用户在当前服务器登录
            messageService.handlerChatMessgae(message, true);
        }

    }

    /**
     * 消息应答
     */
    public void msgAnswer(ChannelHandlerContext ctx, NettyMessage nettyMessage) {
        Connection conn = holder.get(ctx);
        String account = conn.getOwner();
        String content = nettyMessage.getContent();
        logger.info("用户({})消息应答：{}", account, content);
        List<String> fileds = JSONArray.parseArray(content, String.class);
        for (String msgId : fileds) {
            try {
                redisService.hdel(RedisKey.offlineMsgKey(account), msgId);
                UpdateParam<ChatMessage> updateParam = new UpdateParam<ChatMessage>(ChatMessage.class);
                updateParam.addFiledParam("isRead", true);
                updateParam.getQueryParam().addIsQueryParam("id", msgId);
                mongoDBService.update(updateParam);
            } catch (Exception e) {
                logger.info("用户({})消息应答出错：{}", account, msgId);
            }
        }
    }

    /**
     * 处理推送数据
     */
    public void handlerPushMsg(PushMessage message) {
        BizType bizType = BizType.get(message.getBizType());
        switch (bizType) {
            case GROUP_CREATE:
            case GROUP_REMOVE_MEMBER:
            case GROUP_DELETE:
            case GROUP_MEMBER_EXIT:
            case GROUP_MEMBER_INVITE:
                List<GroupMember> members = message.getMembers();
                for (GroupMember member : members) {
                    String account = member.getAccount();
                    if (loginUserService.isLoginThisServer(account)) {
                        messageService.handlerPushMessgae(account, message, true);
                    } else if (loginUserService.isLogin(account)) {
                        // 用户在其他服务器登录：路由消息到其他服务器
                        String loginIp = loginUserService.getUserLoginIp(account);
                        rabbitSender.sendMessage(QueueDeclare.EX_TOPIC_IM_SERVER, QueueDeclare.getMsgRouteRoutingKey(loginIp), message);
                    } else {
                        messageService.handlerPushMessgae(account, message, false);
                    }
                }
                break;
            default:
                break;
        }
    }

}
