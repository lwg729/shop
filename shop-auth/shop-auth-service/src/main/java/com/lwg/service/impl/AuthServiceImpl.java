package com.lwg.service.impl;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/2 21:42
 */

import com.lwg.client.UserClient;
import com.lwg.config.JwtProperties;
import com.lwg.entity.UserInfo;
import com.lwg.pojo.User;
import com.lwg.service.AuthService;
import com.lwg.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String accredit(String username, String password) {
        try {
            //调用微服务,执行查询
            User user = userClient.queryUser(username, password);
            if (user==null){
                return null;
            }

            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
