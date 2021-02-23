package com.yesido.im.restful.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yesido.auth.ApiAuth;
import com.yesido.auth.jwt.JwtAuth;
import com.yesido.im.restful.interceptor.AuthInterceptor;

/**
 * 拦截器配置
 * 
 * @author yesido
 * @date 2019年8月28日 下午4:16:55
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截请求
        String[] pathPatterns = new String[]{"/**"};
        // 不拦截请求
        String[] excludePathPatterns = new String[]{"/error", "/404", "/401"};
        logger.info("配置拦截规则：{}", Arrays.toString(pathPatterns));
        logger.info("配置拦截例外规则：{}", Arrays.toString(excludePathPatterns));

        InterceptorRegistration registration = registry.addInterceptor(authInterceptor);
        registration.addPathPatterns(pathPatterns);
        registration.excludePathPatterns(excludePathPatterns);
        // super.addInterceptors(registry);
    }

    @Bean
    public ApiAuth apiAuth() {
        return new JwtAuth();
    }

}
