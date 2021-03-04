package com.lwg.controller;

import com.lwg.Service.CartServiceImpl;
import com.lwg.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/4 23:51
 */
@Controller
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    /**
     * 添加购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }
}
