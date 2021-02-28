package com.lwg.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwg.common.pojo.PageResult;
import com.lwg.item.mapper.*;
import com.lwg.item.service.GoodsService;
import com.lwg.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private AmqpTemplate amqpTemplate;

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
     * 封装发送到mq的方法
     * @param id
     * @param type
     */
    private void sendMessage(Long id,String type){
        //发送消息
        try {
            amqpTemplate.convertAndSend("item."+type,id);
        }catch (AmqpException e){
            e.printStackTrace();
        }
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
        //新增sku和库存信息
        saveSkuAndStock(spuBo, stockList);
        sendMessage(spuBo.getId(),"insert");
    }

    private void saveSkuAndStock(SpuBo spuBo, ArrayList<Stock> stockList) {
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
     * 修改商品
     * @param spuBo
     */
    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {
        //根据spuId查询要删除的sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(sku1->{
            //删除库存 根据sku的id
            stockMapper.deleteByPrimaryKey(sku1.getId());
        });

        //删除sku
        skuMapper.delete(sku);

        //新增库存stock和sku
        ArrayList<Stock> stockList = new ArrayList<>();
        saveSkuAndStock(spuBo, stockList);

        //更新spu和spu_detail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        /*spuBo.setValid(null);
        spuBo.setSaleable(null);*/
        spuMapper.updateByPrimaryKey(spuBo);

        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMessage(spuBo.getId(),"update");
    }


    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @Override
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 根据spuId查询sku
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }



    /**
     * 根据spu_id 查询spu_detail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }


}
