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
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

            //购物车中的数据为json类型的 反序列化为cart对象
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

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<Cart> queryCarts() {

        //获取登录用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        //存入Redis的key
        String key = KEY_PREFIX + userInfo.getId();

        //判断是否存在该购物车
        if (!redisTemplate.hasKey(key)){
            return null;
        }

        //购物车数据
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        List<Object> carts = hashOps.values();
        //判断购物车中是否存在该商品记录
        if (CollectionUtils.isEmpty(carts)){
            return null;
        }
        //返回购物车数据给前端
        return carts.stream().map(o->JsonUtils.parse(o.toString(),Cart.class)).collect(Collectors.toList());
    }
}
