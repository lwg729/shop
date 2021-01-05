package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/5 20:42
 */

@SpringBootApplication
@EnableDiscoveryClient
public class ShopItemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopItemServiceApplication.class, args);
    }
}
