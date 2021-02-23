package com.yesido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * 
 * @author yesido
 * @date 2019年8月30日 下午2:27:30
 */
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ServerApp {

    public static void main(String[] args) {
        // 不启动web服务，不然会占用一个端口（不配置server.port默认用8080）
        // new SpringApplicationBuilder(ServerApp.class).web(WebApplicationType.NONE).run(args);
        SpringApplication.run(ServerApp.class, args);
    }
}
