package com.yesido.im.restful.config;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yesido.im.restful.filter.RequestWrapperFilter;

/**
 * 过滤器配置
 * 
 * @author yesido
 * @date 2019年8月28日 上午11:52:47
 */
@Configuration
public class FilterConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RequestWrapperFilter wrapperFilter;

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setFilter(wrapperFilter);
        registration.addUrlPatterns("/*");
        registration.setName("RequestWrapperFilter");
        registration.setOrder(1);
        return registration;
    }
}
