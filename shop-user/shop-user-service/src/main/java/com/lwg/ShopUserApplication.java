package com.lwg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/28 21:42
 */

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lwg.mapper")
public class ShopUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopUserApplication.class,args);
    }
}
