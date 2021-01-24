package com.lwg.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwg.common.pojo.PageResult;
import com.lwg.item.mapper.BrandMapper;
import com.lwg.item.mapper.SpuMapper;
import com.lwg.item.service.GoodsService;
import com.lwg.pojo.Brand;
import com.lwg.pojo.Spu;
import com.lwg.pojo.SpuBo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/24 21:20
 */

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CateServiceImpl cateService;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询商品列表
     *
     * @param key
     * @param page
     * @param rows
     * @param saleable
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //搜索字段过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo<>(spus);
        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            //将vo属性copy到Bo属性中
            BeanUtils.copyProperties(spu, spuBo);
            //查询分类名称
            List<String> names = cateService.queryNamesByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setBname(StringUtils.join(names,"/"));

            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setCname(brand.getName());
            spuBos.add(spuBo);

        });
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }
}
