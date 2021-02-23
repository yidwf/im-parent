package com.yesido.im.server.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 服务器节点信息
 * 
 * @author yesido
 * @date 2019年8月9日 上午11:28:45
 */
public class ServiceNode {

    private String serverIp; // 服务器IP
    private Long launchTime; // 服务器启动时间
    private Long heartbeatTime; // 服务器心跳时间
    private int connCount; // 服务器连接数

    public ServiceNode() {
        super();
    }

    public ServiceNode(String serverIp, Long launchTime) {
        super();
        this.serverIp = serverIp;
        this.launchTime = launchTime;
    }

    public ServiceNode(String serverIp, Long launchTime, Long heartbeatTime) {
        super();
        this.serverIp = serverIp;
        this.launchTime = launchTime;
        this.heartbeatTime = heartbeatTime;
    }

    public ServiceNode(String serverIp, Long launchTime, int connCount) {
        super();
        this.serverIp = serverIp;
        this.launchTime = launchTime;
        this.connCount = connCount;
    }

    public ServiceNode(String serverIp, Long launchTime, Long heartbeatTime, int connCount) {
        super();
        this.serverIp = serverIp;
        this.launchTime = launchTime;
        this.heartbeatTime = heartbeatTime;
        this.connCount = connCount;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Long getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Long launchTime) {
        this.launchTime = launchTime;
    }

    public Long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public int getConnCount() {
        return connCount;
    }

    public void setConnCount(int connCount) {
        this.connCount = connCount;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
