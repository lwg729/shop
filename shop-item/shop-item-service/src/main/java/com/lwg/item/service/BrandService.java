package com.lwg.item.service;

import com.lwg.common.pojo.PageResult;
import com.lwg.pojo.Brand;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/9 22:52
 */
public interface BrandService {
    PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);
}
