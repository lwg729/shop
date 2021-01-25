package com.lwg.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwg.common.pojo.PageResult;
import com.lwg.item.mapper.BrandMapper;
import com.lwg.item.service.BrandService;
import com.lwg.pojo.Brand;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/9 22:52
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并查询品牌信息
     *
     * @param key    搜索条件
     * @param page   当前页
     * @param rows   每页大小
     * @param sortBy 排序字段
     * @param desc   是否降序
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        // 添加分页条件
        PageHelper.startPage(page, rows);

        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = this.brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids   商品分类id
     *  此方法我们不仅要新增品牌,还要操作品牌商品表
     */
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌属性
        brandMapper.insertSelective(brand);

        //在品牌商品表中插入信息
        cids.forEach(cid->{
            brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 品牌修改
     * @param brand
     * @param cids
     */
    @Override
    public void updateBrand(Brand brand, List<Long> cids) {
        //修改品牌
        brandMapper.updateByPrimaryKeySelective(brand);

        //维护中间表
        cids.forEach(cid->{
            brandMapper.updateCategoryBrand(cid,brand.getId());
        });
    }


    /**
     * 删除品牌
     * @param bid
     */
    @Override
    public void deleteBrand(Long bid) {
        //删除品牌表
        brandMapper.deleteByPrimaryKey(bid);

        //维护中间表
        brandMapper.deleteCatrgoryBrandByBid(bid);
    }

    /**
     * 根据商品id-cid查询品牌
     * @param cid
     */
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return brandMapper.queryBrandsByCid(cid);
    }


}
