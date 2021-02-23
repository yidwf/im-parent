package com.yesido.im.server.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.ptoto.netty.PushMessage;
import com.yesido.im.server.consts.RedisKey;
import com.yesido.im.server.context.Connection;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.model.entity.ChatMsg;
import com.yesido.im.server.model.entity.PushMsg;
import com.yesido.im.server.netty.response.ResponseManager;
import com.yesido.lib.utils.IDUtil;
import com.yesido.mongodb.service.MongoDBService;
import com.yesido.redis.service.RedisService;

/**
 * 消息service
 * 
 * @author yesido
 * @date 2019年8月1日 上午11:50:07
 */
@Service
public class MessageService {
    Logger logger = LoggerFactory.getLogger(MessageService.class);
    ConnectionHolder holder = ConnectionHolder.instance();

    @Autowired
    private RedisService redisService;
    @Autowired
    private MongoDBService mongoDBService;

    /**
     * 构造ChatMsg
     */
    private ChatMsg toChatMsg(ChatMessage model, boolean isRead) {
        ChatMsg result = new ChatMsg();
        result.setId(StringUtils.isNotBlank(model.getId()) ? model.getId() : IDUtil.nextId());
        result.setCode(model.getCode());
        result.setFrom(model.getFrom());
        result.setTo(model.getTo());
        result.setContentType(model.getContentType());
        result.setContent(model.getContent());
        result.setRead(isRead);
        result.setCreatetime(model.getCreatetime() == null ? System.currentTimeMillis() : model.getCreatetime());
        result.setAttachment(model.getAttachment());
        result.setBizType(model.getBizType());
        return result;
    }

    /**
     * 处理单聊聊天消息
     * 
     * @param message 聊天信息
     * @param online 是否在线，true=在线，false=离线
     */
    public void handlerChatMessgae(ChatMessage message, boolean online) {
        String id = IDUtil.nextId();
        message.setId(id);
        ChatMsg chatMsg = toChatMsg(message, online);
        mongoDBService.save(chatMsg);
        logger.info("[聊天消息]消息持久化完成：{}", id);

        String to = chatMsg.getTo();
        if (online) {
            logger.info("[聊天消息]用户({})在线，发送即时消息：{}", to, id);
            Connection toConn = holder.get(to);
            toConn.writeAndFlush(chatMsg);
        } else {
            logger.info("[聊天消息]用户({})不在线，缓存到redis，做为离线消息：{}", to, id);
            String key = RedisKey.offlineMsgKey(to);
            redisService.hset(key, id, chatMsg.jsonString());
        }
    }

    /**
     * 处理推送消息
     * 
     * @param account 推送用户
     * @param message 消息
     * @param online 是否在线，true=在线，false=离线
     */
    public void handlerPushMessgae(String account, PushMessage message, boolean online) {
        String id = IDUtil.nextId();
        message.setId(id);
        PushMsg pushMsg = toPushMsg(account, message, online);
        mongoDBService.save(pushMsg);
        logger.info("[推送消息]消息持久化完成：{}", id);

        if (online) {
            logger.info("[推送消息]用户({})在线，发送即时消息：{}", account, id);
            Map<String, Object> attachment = new HashMap<String, Object>();
            attachment.put(id, pushMsg);
            NettyMessage nettyMessage = ResponseManager.buildPushResponse(pushMsg.getContent(), attachment);
            Connection toConn = holder.get(account);
            toConn.writeAndFlush(nettyMessage);
        } else {
            logger.info("[推送消息]用户({})不在线，缓存到redis，做为离线消息：{}", account, id);
            String key = RedisKey.offlineMsgKey(account);
            redisService.hset(key, id, pushMsg.jsonString());
        }
    }

    private PushMsg toPushMsg(String to, PushMessage model, boolean isRead) {
        PushMsg result = new PushMsg();
        result.setId(StringUtils.isNotBlank(model.getId()) ? model.getId() : IDUtil.nextId());
        result.setCode(model.getCode());
        result.setTo(to);
        result.setTargetId(model.getTargetId());
        result.setContentType(model.getContentType());
        result.setContent(model.getContent());
        result.setRead(isRead);
        result.setCreatetime(model.getCreatetime() == null ? System.currentTimeMillis() : model.getCreatetime());
        result.setAttachment(model.getAttachment());
        result.setBizType(model.getBizType());
        return result;
    }
}
