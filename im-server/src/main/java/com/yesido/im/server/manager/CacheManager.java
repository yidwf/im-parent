package com.yesido.im.server.manager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yesido.im.server.service.ICacheService;
import com.yesido.im.server.spring.SpringContextUtil;

/**
 * 缓存管理器
 * 
 * @author yesido
 * @date 2018年9月23日
 */
@Component
public class CacheManager {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron = "0 0/5 * * * ?")
    public void reloadLocal() {
        logger.debug("本地缓存重载开始");
        Long stime = System.currentTimeMillis();
        Map<String, ICacheService> cacheBeanMap = SpringContextUtil.getContext().getBeansOfType(ICacheService.class);
        cacheBeanMap.forEach((key, value) -> {
            value.reloadLocal();
        });
        logger.debug("本地缓存重载结束，耗时ms：{}", (System.currentTimeMillis() - stime));
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void reloadRedis() {
        logger.debug("redis缓存重载开始");
        Long stime = System.currentTimeMillis();
        Map<String, ICacheService> cacheBeanMap = SpringContextUtil.getContext().getBeansOfType(ICacheService.class);
        cacheBeanMap.forEach((key, value) -> {
            value.reloadRedis();
        });
        logger.debug("redis缓存重载结束，耗时ms：{}", (System.currentTimeMillis() - stime));
    }
}
