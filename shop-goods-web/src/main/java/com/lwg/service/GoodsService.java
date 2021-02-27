package com.lwg.service;

import java.util.Map;

/**
 * 功能描述：封装商品详情页要查询的数据
 *
 * @Author: lwg
 * @Date: 2021/2/27 18:19
 */
public interface GoodsService {

    //加载商品详情页的数据
    Map<String, Object> loadData(Long spuId);
}
