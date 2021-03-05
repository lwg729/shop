package com.lwg.Service;

import com.lwg.pojo.Cart;

import java.util.List;

/**
 * 功能描述：购物车
 *
 * @Author: lwg
 * @Date: 2021/3/4 23:54
 */
public interface CartService {

    //添加购物车
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCarts(Cart cart);

    void deleteCart(String skuId);
}
