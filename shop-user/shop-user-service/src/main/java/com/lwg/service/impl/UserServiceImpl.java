package com.lwg.service.impl;

import com.lwg.mapper.UserMapper;
import com.lwg.pojo.User;
import com.lwg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/28 22:29
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        //等于0说明没有此条账户
        return this.userMapper.selectCount(record) == 0;
    }
}
