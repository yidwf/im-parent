package com.yesido.im.server.consts;

/**
 * netty常量
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class NettyConst {

    /** --客户端心跳请求频率-- **/
    public final static int HEART_BEAT_RATE = 60 * 10;
    /** --客户端没有收到服务端的心跳应答消息的最大次数-- **/
    public final static int MAX_UN_REC_PONG_TIMES = 3;
    /** --超过此时间服务器没有收到客户端的的信息，则认为客户端已经断线-- **/
    public final static int SERVER_READ_TIMEOUT = HEART_BEAT_RATE * MAX_UN_REC_PONG_TIMES;

}
