package com.lwg.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/6 17:12
 */

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ShopSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopSearchApplication.class,args);
    }
}
