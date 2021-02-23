package com.yesido.im.server.context;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.SocketType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 封装连接
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class Connection {

    /** --会话ID：-- **/
    private long sessionId;
    /** --连接拥有者：-- **/
    private String owner;
    /** --连接：-- **/
    private ChannelHandlerContext context;
    /** --连接类型-- **/
    private int socketType;

    public int getSocketType() {
        return socketType;
    }

    public void setSocketType(int socketType) {
        this.socketType = socketType;
    }

    public Connection(long connId, ChannelHandlerContext context) {
        this.sessionId = connId;
        this.context = context;
    }

    public Connection(long sessionId, String owner,
            ChannelHandlerContext context) {
        this.sessionId = sessionId;
        this.owner = owner;
        this.context = context;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    /**
     * 写数据
     * 
     * @param message
     */
    public void writeAndFlush(Object message) {
        write(message);
        flush();
    }

    /**
     * 写数据
     * 
     * @param message
     */
    public void write(Object message) {
        if (socketType == SocketType.WEBSOCKET.getType()) {
            String msg = JSONObject.toJSONString(message);
            context.write(new TextWebSocketFrame(msg));
        } else {
            context.write(message);
        }
    }

    public void flush() {
        context.flush();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (context != null) {
            context.channel().flush();
            context.channel().close();
            context.close();
            context = null;
        }
    }
}
