package com.lwg.Service;

import com.lwg.client.GoodsClient;
import com.lwg.common.utils.JsonUtils;
import com.lwg.entity.UserInfo;
import com.lwg.interceptor.LoginInterceptor;
import com.lwg.pojo.Cart;
import com.lwg.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/4 23:54
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    static final String KEY_PREFIX = "lw:cart:uid:";

    static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Override
    public void addCart(Cart cart) {
        //获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();

        //Redis的key
        String key = KEY_PREFIX + user.getId();

        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        //查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean bool = hashOps.hasKey(skuId.toString());

        if (bool) {
            //存在,获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);

            //修改购物车数量
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在,新增购物车数据
            cart.setUserId(user.getId());

            //其他商品信息,需要查询商品服务
            Sku sku = goodsClient.querySkuById(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ?
                    "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }
}
