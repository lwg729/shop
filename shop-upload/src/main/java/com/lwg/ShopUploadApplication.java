package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/10 23:56
 */

@SpringBootApplication
@EnableDiscoveryClient
public class ShopUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopUploadApplication.class,args);
    }
}
