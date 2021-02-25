package com.lwg.item.service;

import com.lwg.pojo.Category;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/8 0:31
 */
public interface CateService {
    List<Category> queryCategoryByPid(Long pid);

    List<String> queryNamesByIds(List<Long> asList);

    List<Category> queryAllByCid3(Long id);
}
