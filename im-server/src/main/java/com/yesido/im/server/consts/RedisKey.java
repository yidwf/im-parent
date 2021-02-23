package com.yesido.im.server.consts;

import com.yesido.im.server.utils.BizUtil;

/**
 * 定义redis相关key
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public class RedisKey {

    /** --当前服务的IP：需要设置过期时间，定时任务心跳检测-- **/
    public final static String H_IM_SERVICE_IP_HEARTBEAT = "im_server_ip_heartbeat:{1}";

    /** --正在运行的所有服务IP-- **/
    public final static String HK_IM_SERVER_RUN_IPS = "im_server_run_ips";

    /** --本节点所有登录用户-- **/
    public final static String HK_IM_SERVER_LOGIN_USERS = "im_server_login_users#ip:{1}";

    /** --登录用户-- **/
    public final static String H_IM_LOGIN_USER = "im_login_user#a:{1}";

    /** --用户离线消息-- **/
    public final static String HK_IM_SERVER_USER_OFFLINE_MSG = "im_server_user_offline_msg#a:{1}";

    /**
     * 离线数据key
     */
    public static String offlineMsgKey(String account) {
        return BizUtil.key(HK_IM_SERVER_USER_OFFLINE_MSG, account);
    }
}
