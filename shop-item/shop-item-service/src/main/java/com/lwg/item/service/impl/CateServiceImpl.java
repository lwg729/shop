package com.lwg.item.service.impl;

import com.lwg.item.mapper.CategoryMapper;
import com.lwg.item.service.CateService;
import com.lwg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：根据父节点0依次往下查
 *
 * @Author: lwg
 * @Date: 2021/1/8 0:32
 */
@Service
public class CateServiceImpl implements CateService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categories = categoryMapper.select(category);
        return categories;
    }
}
