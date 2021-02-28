package com.lwg.controller;

import com.lwg.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/28 22:32
 */
@Controller
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 校验数据是否可用
     * @param data  要校验的数据
     * @param type  要校验的数据类型 1.用户名 2.手机
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = userService.checkData(data, type);
        if (bool==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }
}
