package com.yesido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * restful启动类
 * 
 * @author yesido
 * @date 2019年8月23日 上午10:56:09
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class RestfulApp {

    public static void main(String[] args) {
        SpringApplication.run(RestfulApp.class, args);
    }
}
