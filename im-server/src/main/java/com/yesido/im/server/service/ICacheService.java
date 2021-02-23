package com.yesido.im.server.service;

/**
 * 缓存服务
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public interface ICacheService {

    /**
     * 重载本地缓存
     */
    public void reloadLocal();

    /**
     * 重载redis缓存
     */
    public void reloadRedis();
}
