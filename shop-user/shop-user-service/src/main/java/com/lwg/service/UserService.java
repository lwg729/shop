package com.lwg.service;

import com.lwg.pojo.User;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/28 22:29
 */
public interface UserService {
    //校验数据是否可用
    public Boolean checkData(String data);

    //注册功能
    Boolean register(User user);

    //查询 用户
    User queryUser(String username, String password);
}
