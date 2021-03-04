package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/3 0:08
 */

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ShopCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopCartApplication.class, args);
    }
}
