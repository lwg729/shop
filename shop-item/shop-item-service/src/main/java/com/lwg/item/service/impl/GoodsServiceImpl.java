package com.lwg.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwg.common.pojo.PageResult;
import com.lwg.item.mapper.*;
import com.lwg.item.service.GoodsService;
import com.lwg.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SkuMapper skuMapper;

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
            spuBo.setBname(StringUtils.join(names, "/"));

            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setCname(brand.getName());
            spuBos.add(spuBo);

        });
        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }


    /**
     * 新增商品
     *
     * @param spuBo
     */
    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {
        //新增spu
        //设置默认字段
        spuBo.setId(null);
        spuBo.setSaleable(true);  //是否上架
        spuBo.setValid(false);   //是否删除
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());
        spuMapper.insertSelective(spuBo);  //将新增的spu插入数据库


        //新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());   //得到某spu的id从而得到改spu的详情信息
        spuDetailMapper.insertSelective(spuDetail);


        ArrayList<Stock> stockList = new ArrayList<>();
        //新增sku
        spuBo.getSkus().forEach(sku -> {
            //填充
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        });
        stockMapper.insertList(stockList);
    }


    /**
     * 根据spu_id 查询spu_detail
     * @param id
     * @return
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        return spuDetail;
    }
    /**
     * 根据spuId查询sku
     *
     * @param id
     * @return
     */
    @Override
    public List<Sku> querySkusBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        //查询库存
        skus.forEach(s -> {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }



}
