package com.yesido.websocket.interceptor;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yesido.auth.ApiAuth;
import com.yesido.auth.jwt.JwtAuth;

/**
 * 拦截器配置
 * 
 * @author yesido
 * @date 2018年9月2日
 */
@Configuration
public class InterceptorWebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(InterceptorWebConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
        logger.info("拦截器静态资源处理...");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(loginInterceptor());
        String[] pathPatterns = new String[]{"/**"};
        registration.addPathPatterns(pathPatterns); // 拦截所有请求
        logger.info("配置拦截规则：" + Arrays.toString(pathPatterns));
        // 不拦截请求
        String[] excludePathPatterns = new String[]{
                "/login", "/error", "/404", "/401", "/**/*.html", "/js/**", "/css/**", "/tools/**", "/images/**"
        };
        logger.info("配置拦截例外规则：" + Arrays.toString(excludePathPatterns));
        registration.excludePathPatterns(excludePathPatterns);
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        logger.info("初始化登录拦截器...");
        return new LoginInterceptor();
    }

    @Bean
    public ApiAuth apiAuth() {
        return new JwtAuth();
    }

}
