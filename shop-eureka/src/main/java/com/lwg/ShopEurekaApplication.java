package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/5 20:24
 */
@SpringBootApplication
@EnableEurekaServer
public class ShopEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopEurekaApplication.class, args);
    }
}