package com.yesido.im.server.startup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.yesido.im.server.listeners.CloseListener;
import com.yesido.lib.utils.BizUtil;

/**
 * 监听应用停止：使用kill PID命令
 * 
 * @author yesido
 * @date 2020年1月8日 下午5:59:20
 */
@Component
public class ShutdownHook implements ApplicationListener<ContextClosedEvent> {
    Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private Map<Thread, CloseListener> threadMap = new ConcurrentHashMap<Thread, CloseListener>();

    public void registerThread(Thread thread, CloseListener listener) {
        logger.warn("[服务关闭]注册线程：{}", thread.toString());
        threadMap.put(thread, listener);
    }

    public void unregisterThread(Thread thread) {
        threadMap.remove(thread);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.warn("[服务关闭]捕获到服务关闭事件");
        threadMap.forEach((t, l) -> {
            try {
                l.onClose();
            } catch (Exception ignore) {
            }
        });
        BizUtil.sleep(5000); // hold住5秒时间
        logger.warn("[服务关闭]所有任务已经close！！");
    }
}