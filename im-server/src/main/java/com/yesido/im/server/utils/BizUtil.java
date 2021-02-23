package com.yesido.im.server.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

public class BizUtil extends com.yesido.lib.utils.BizUtil {

    public final static AttributeKey<String> http_ip_key = AttributeKey.valueOf("http_ip");

    /**
     * 提取IP
     * 
     * @param addr 地址信息
     * @return
     */
    public static String trimIp(String addr) {
        String regex = "\\d+\\.\\d+\\.\\d+\\.\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(addr);
        if (matcher.find()) {
            String ip = matcher.group(0);
            return ip;
        }
        return null;
    }

    /**
     * 获取已经保存在Channel的ip
     * 
     * @param channel
     * @return
     */
    public static String getIp(Channel channel) {
        String ip = channel.attr(http_ip_key).get();
        if (ip == null) {
            ip = getClientIp(channel);
        }
        return ip;
    }

    public static String getClientIp(Channel channel) {
        InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        return clientIP;
    }

    public static String getClientIp(FullHttpRequest request) {
        String ip = getHeader(request, "x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = getHeader(request, "Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = getHeader(request, "WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = getHeader(request, "HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = getHeader(request, "HTTP_X_FORWARDED_FOR");
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    private static String getHeader(FullHttpRequest request, String key) {
        return request.headers().get(key); // netty 4.0
        // return request.headers().getAndConvert(key); // netty 5.0
    }

    /**
     * 按照key排序
     */
    public static Map<String, String> sortMap(Map<String, String> oldMap) {
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> arg0,
                    Entry<String, String> arg1) {
                // 参数arg0在前正序排列
                return arg0.getKey().compareTo(arg1.getKey());
            }
        });
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        for (Entry<String, String> entry : list) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        return newMap;
    }
}
