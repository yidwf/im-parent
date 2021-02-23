package com.yesido.im.server.consts;

/**
 * redis常量定义
 * 
 * @author yesido
 *
 */
public class RedisConst {

    /** 订阅通道：通知服务器退出用户登录 **/
    public static String REDIS_CHANNEL_NOTIFY_SERVER_LOGOUT = "channel.im.server.notify.user.logout";
}
