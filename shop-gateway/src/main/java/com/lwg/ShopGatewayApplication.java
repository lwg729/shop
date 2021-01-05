package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/5 20:32
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class ShopGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopGatewayApplication.class, args);
    }
}