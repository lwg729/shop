package com.lwg.controller;

import com.lwg.pojo.User;
import com.lwg.service.impl.UserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

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
     *
     * @param data 要校验的数据
     * @return
     */
    @GetMapping("check/{data}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data) {
        Boolean bool = userService.checkData(data);
        if (bool == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(bool);
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user) {
        Boolean bool = userService.register(user);
        if (bool == null || !bool) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        User user = userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
