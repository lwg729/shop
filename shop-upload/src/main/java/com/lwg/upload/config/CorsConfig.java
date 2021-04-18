package com.lwg.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 功能描述：跨域配置
 *
 * @Author: lwg
 * @Date: 2021/1/9 0:18
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        //配置cors的配置信息
        CorsConfiguration configuration = new CorsConfiguration();

        //添加允许跨域的请求,客户端向服务端请求 不要用* 否则cookie无法使用
        configuration.addAllowedOrigin("http://manage.lware.com");
        configuration.addAllowedOrigin("http://api.lware.com");
        //是否发送cookie信息
        configuration.setAllowCredentials(true);

        //允许的请求方式
        configuration.addAllowedMethod("*");

        //允许的请求头信息
        configuration.addAllowedHeader("*");

        //添加映射路径路径资源 ,拦截请求
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(corsConfigurationSource);
    }

}
