package com.lwg.search.service;

import com.lwg.pojo.Spu;
import com.lwg.search.pojo.Goods;

import java.io.IOException;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/10 22:39
 */
public interface SearchService {
    public Goods buildGoods(Spu spu) throws IOException;
}
