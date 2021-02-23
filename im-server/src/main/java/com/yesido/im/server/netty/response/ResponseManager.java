package com.yesido.im.server.netty.response;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.ptoto.enums.ContentType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.NettyMessage;

/**
 * 构造响应消息
 * 
 * @author yesido
 * @date 2018年9月2日
 */
public class ResponseManager {
    Logger logger = LoggerFactory.getLogger(ResponseManager.class);

    private static NettyMessage newTextNettyMessage(HeaderCode code, String content) {
        return newNettyMessage(code, ContentType.TEXT, content);
    }

    private static NettyMessage newNettyMessage(HeaderCode code, ContentType type, String content) {
        NettyMessage msg = new NettyMessage();
        msg.setCode(code.getCode());
        msg.setContent(content);
        msg.setContentType(type.getType());
        return msg;
    }

    private static ChatMessage newChatMessage(HeaderCode code, ContentType type, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setCode(code.getCode());
        msg.setContent(content);
        msg.setContentType(type.getType());
        return msg;
    }

    /**
     * 构造单聊响应消息
     */
    public static ChatMessage buildSimpleChatMsg(ChatMessage message) {
        ChatMessage msg = newChatMessage(HeaderCode.CHAT_SIMPLE, ContentType.TEXT, message.getContent());
        msg.setFrom(message.getFrom());
        msg.setTo(message.getTo());
        msg.setCreatetime(message.getCreatetime());
        msg.setAttachment(message.getAttachment());
        return msg;
    }

    /**
     * 构造单聊响应消息
     */
    public static ChatMessage buildSimpleChatMsg(String content, String from, String to, Long createtime, Map<String, String> attachment) {
        ChatMessage msg = newChatMessage(HeaderCode.CHAT_SIMPLE, ContentType.TEXT, content);
        msg.setFrom(from);
        msg.setCreatetime(createtime);
        msg.setAttachment(attachment);
        msg.setTo(to);
        return msg;
    }

    /**
     * 构造登录成功响应消息
     */
    public static NettyMessage buildSuccessResponse(String msg) {
        return newTextNettyMessage(HeaderCode.LOGIN, msg);
    }

    /**
     * 构造登出响应消息
     */
    public static NettyMessage buildLoginFailMsg(String msg) {
        return newTextNettyMessage(HeaderCode.LOGOUT, msg);
    }

    /**
     * 重复登录响应消息
     */
    public static NettyMessage buildLoginRepeatMsg(String loginIp) {
        return newTextNettyMessage(HeaderCode.LOGIN_REPEAT, loginIp);
    }

    /**
     * 心跳响应
     */
    public static NettyMessage buildHeartbeatResponse() {
        return newTextNettyMessage(HeaderCode.HEARTBEAT, "心跳响应");
    }

    /**
     * 服务不可用
     */
    public static NettyMessage serverUnavailable() {
        return newTextNettyMessage(HeaderCode.SERVER_UNAVAILABLE, "当前服务不可用");
    }

    /**
     * 构造推送数据
     * 
     * @param content
     * @param attachment
     * @return
     */
    public static NettyMessage buildPushResponse(String content, Map<String, Object> attachment) {
        NettyMessage message = new NettyMessage();
        if (attachment != null) {
            Set<Entry<String, Object>> entries = attachment.entrySet();
            for (Entry<String, Object> entry : entries) {
                message.addAttachment(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(content);
        message.setCode(HeaderCode.PUSH_MSG.getCode());
        return message;
    }

}
