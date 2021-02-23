package com.yesido.im.server.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 消息推送
 * 
 * @author yesido
 * @date 2019年9月4日 上午10:35:04
 */
@Document(collection = "push_msg")
public class PushMsg extends Message {

    @Id
    private String id; // id

    @Indexed
    private String to; // 接收者

    private String targetId; // 目标id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

}
