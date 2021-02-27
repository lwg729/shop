package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 功能描述：商品详情页启动类
 *
 * @Author: lwg
 * @Date: 2021/2/27 16:22
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ShopGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopGoodsWebApplication.class,args);
    }
}
