package com.yesido.im.server.consts;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesido.im.server.netty.config.NettyConfig;
import com.yesido.im.server.spring.SpringContextUtil;

/**
 * 平台默认参数
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public class Parameter {
    private static Logger logger = LoggerFactory.getLogger(Parameter.class);

    /** --日期默认格式化格式：yyyy-MM-dd HH:mm:ss-- **/
    public final static SimpleDateFormat DETAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** --服务器启动时间-- **/
    public final static Date SERVER_LAUNCH_DATE = new Date();
    public final static Long SERVER_LAUNCH_TIME = System.currentTimeMillis();
    /** --本机ip-- **/
    public final static String LOCAL_IP = getLocalIp();
    /** --服务器状态，0=停止（准备重启），1=正常-- **/
    public static volatile int SERVER_STATUS = 1;

    /**
     * 获取本机ip
     * 
     * @return ip
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            String localIp = ip.getHostAddress();
                            return localIp;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取本机id失败", e);
        }
        return null;
    }

    /**
     * 获取本机IP或者域名
     */
    public static String getServerIp() {
        NettyConfig nettyConfig = SpringContextUtil.getBean(NettyConfig.class);
        return nettyConfig.getHost();
        // return LOCAL_IP;
    }
}
