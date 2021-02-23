package com.yesido.im.server.netty.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.server.consts.Parameter;

/**
 * netty配置
 * 
 * @author yesido
 * @date 2018年9月1日
 */
@Component
public class NettyConfig {

    @Value("${netty.port}")
    private int port;
    @Value("${netty.websocket_port}")
    private int websocket_port;
    @Value("${netty.timeout}")
    private int timeout;
    @Value("${netty.host:null}")
    private String host;
    @Value("${netty.tcp_conn:true}")
    private boolean tcpConn;
    @Value("${netty.websocket_conn:true}")
    private boolean websocketConn;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWebsocket_port() {
        return websocket_port;
    }

    public void setWebsocket_port(int websocket_port) {
        this.websocket_port = websocket_port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        if (StringUtils.isBlank(host) || "null".equals(host)) {
            host = Parameter.LOCAL_IP;
        }
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isTcpConn() {
        return tcpConn;
    }

    public void setTcpConn(boolean tcpConn) {
        this.tcpConn = tcpConn;
    }

    public boolean isWebsocketConn() {
        return websocketConn;
    }

    public void setWebsocketConn(boolean websocketConn) {
        this.websocketConn = websocketConn;
    }

    public String toJsonString() {
        return JSONObject.toJSONString(this);
    }
}
