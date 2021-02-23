package com.yesido.im.client.request;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.ContentType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.enums.SocketType;
import com.yesido.im.ptoto.netty.ChatMessage;
import com.yesido.im.ptoto.netty.LoginMessage;
import com.yesido.im.ptoto.netty.NettyMessage;

/**
 * 构造请求信息
 * 
 * @author yesido
 * @date 2019年8月9日 上午10:39:53
 */
public class RequestManager {

    public static ChatMessage buildSimpleChatText(String content, String from, String to) {
        return buildSimpleChatRequest(content, from, to, ContentType.TEXT);
    }

    public static ChatMessage buildSimpleChatImage(String content, String from, String to) {
        return buildSimpleChatRequest(content, from, to, ContentType.IMAGE);
    }

    public static ChatMessage buildSimpleChatVedio(String content, String from, String to) {
        return buildSimpleChatRequest(content, from, to, ContentType.VEDIO);
    }

    public static ChatMessage buildSimpleChatFile(String content, String from, String to) {
        return buildSimpleChatRequest(content, from, to, ContentType.FILE);
    }

    /**
     * 构造单聊请求
     * 
     * @param content 消息内容
     * @param from 发送者
     * @param to 接收者
     * @param contentType 内容类型
     * @return
     */
    public static ChatMessage buildSimpleChatRequest(String content, String from, String to, ContentType contentType) {
        ChatMessage message = new ChatMessage();
        message.setCode(HeaderCode.CHAT_SIMPLE.getCode());
        message.setFrom(from);
        message.setTo(to);
        message.setContentType(contentType.getType());
        message.setContent(content);
        return message;
    }

    /**
     * 构造登录请求
     * 
     * @param from
     * @param password
     * @param socketType
     * @return
     */
    public static LoginMessage buildLoginRequest(String from, String password, SocketType socketType) {
        LoginMessage message = new LoginMessage();
        message.setCode(HeaderCode.LOGIN.getCode());
        message.setFrom(from);
        message.setPrivateCode(password);
        message.setSocketType(socketType.getType());
        return message;
    }

    /**
     * 构造心跳请求
     * 
     * @return
     */
    public static NettyMessage buildHeartBeatRequest() {
        NettyMessage message = new NettyMessage();
        message.setCode(HeaderCode.HEARTBEAT.getCode());
        return message;
    }

    /**
     * 构造消息响应请求
     * 
     * @param answerIds
     * @return
     */
    public static NettyMessage buildAnswerRequest(List<String> answerIds) {
        NettyMessage message = new NettyMessage();
        message.setCode(HeaderCode.PUSH_MSG_ANSWER.getCode());
        message.setContent(JSONObject.toJSONString(answerIds));
        return message;
    }
}
