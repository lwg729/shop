package com.lwg.item.service.impl;

import com.lwg.item.mapper.CategoryMapper;
import com.lwg.item.service.CateService;
import com.lwg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        ArrayList<String> list = new ArrayList<>();
        for (Category category : categories) {
            list.add(category.getName());
        }
        return list;
    }

    /**
     * 根据cid3查询 cid2,cid1
     * @param id
     * @return
     */
    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
