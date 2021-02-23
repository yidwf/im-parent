package com.yesido.im.ptoto.netty;

/**
 * 聊天信息结构体
 * 
 * @author yesido
 * @date 2019年8月30日 上午10:34:56
 */
public final class ChatMessage extends NettyMessage {

    /** --发送者-- **/
    private String from;

    /** --接收者：用户账号或者群id-- **/
    private String to;

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
