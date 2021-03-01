package com.lwg.service.impl;

import com.lwg.mapper.UserMapper;
import com.lwg.pojo.User;
import com.lwg.service.UserService;
import com.lwg.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public Boolean checkData(String data) {
        User record = new User();
        record.setUsername(data);
        //等于0说明没有此条账户
        return this.userMapper.selectCount(record) == 0;
    }

    @Override
    public Boolean register(User user) {
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));


        //强制设置不能指定的参数为空
        user.setId(null);
        user.setCreated(new Date());

        //添加到数据库
        boolean b = userMapper.insertSelective(user) == 1;
        return b;
    }

    @Override
    public User queryUser(String username, String password) {
        //查询
        User record = new User();
        record.setUsername(username);

        User user = userMapper.selectOne(record);
        if (user==null){
            return null;
        }

        //校验密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password,user.getSalt()))){
            return null;
        }

        //用户密码都正确
        return user;
    }
}
