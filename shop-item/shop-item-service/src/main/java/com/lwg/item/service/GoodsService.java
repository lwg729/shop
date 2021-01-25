package com.lwg.item.service;

import com.lwg.common.pojo.PageResult;
import com.lwg.pojo.Sku;
import com.lwg.pojo.SpuBo;
import com.lwg.pojo.SpuDetail;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/24 21:20
 */
public interface GoodsService {
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(SpuBo spuBo);

    List<Sku> querySkusBySpuId(Long id);

    SpuDetail querySpuDetailBySpuId(Long id);
}
