package com.yesido.im.ptoto.netty;

/**
 * 登录消息
 * 
 * @author yesido
 * @date 2019年8月30日 上午10:45:26
 */
public final class LoginMessage extends NettyMessage {

    /** --发送者：请求登录-- **/
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
