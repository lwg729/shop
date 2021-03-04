package com.lwg.interceptor;

import com.lwg.common.utils.CookieUtils;
import com.lwg.config.JwtProperties;
import com.lwg.entity.UserInfo;
import com.lwg.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：mvc拦截器 进行统一登录校验
 *
 * @Author: lwg
 * @Date: 2021/3/4 22:44
 */
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    //定义线程域,存放登录用户  `ThreadLocal`来存储查询到的用户信息，线程内共享，
    // 因此请求到达`Controller`后可以共享User
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

   @Autowired
   private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //查询token
        String token = CookieUtils.getCookieValue(request, "LW_TOKEN");

        if (StringUtils.isBlank(token)) {
            //未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        try {
            //有token 查询用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            THREAD_LOCAL.set(userInfo);
            return true;
        }catch (Exception e){
            // 抛出异常，证明未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的局部变量.因为使用的是tomcat线程池,线程不会结束,所以不会释放线程的局部变量
        THREAD_LOCAL.remove();
    }

    //对外提供静态方法,用来获取登录user的信息  因为ThreadLocal为私有的
    public static UserInfo getLoginUser(){
        return THREAD_LOCAL.get();
    }
}
