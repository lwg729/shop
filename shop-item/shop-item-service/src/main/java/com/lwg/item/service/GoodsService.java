package com.lwg.item.service;

import com.lwg.common.pojo.PageResult;
import com.lwg.pojo.Sku;
import com.lwg.pojo.Spu;
import com.lwg.pojo.SpuBo;
import com.lwg.pojo.SpuDetail;

import java.util.List;
import java.util.Map;


/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/24 21:20
 */
public interface GoodsService {
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(SpuBo spuBo);

    SpuDetail querySpuDetailBySpuId(Long spuId);

    List<Sku> querySkuBySpuId(Long spuId);

    void updateGoods(SpuBo spuBo);

    Spu querySpuById(Long id);

}
