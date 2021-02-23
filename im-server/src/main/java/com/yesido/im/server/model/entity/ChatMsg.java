package com.yesido.im.server.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 聊天消息
 * 
 * @author yesido
 * @date 2019年9月4日 上午10:05:23
 */
@Document(collection = "chat_msg")
public class ChatMsg extends Message {

    @Id
    private String id; // id

    @Indexed
    private String from; // 发送者

    @Indexed
    private String to; // 接收者

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
