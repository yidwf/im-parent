package com.yesido.im.server.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring bean 管理器
 * 
 * @author yesido
 * @date 2017年5月17日 下午3:43:59
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clz) {
        return getContext().getBean(clz);
    }

    public static <T> T getBean(Class<T> clz, String name) {
        return getContext().getBean(name, clz);
    }

    public static Object getBean(String name) {
        return getContext().getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        SpringContextUtil.context = context;
    }

}
