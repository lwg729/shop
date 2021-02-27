package com.lwg.service.impl;

import com.lwg.client.BrandClient;
import com.lwg.client.CategoryClient;
import com.lwg.client.GoodsClient;
import com.lwg.client.SpecificationClient;
import com.lwg.pojo.*;
import com.lwg.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/27 18:20
 */

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    //封装商品详情页要查询的数据

    /**
     * 要查询的数据
     * - 1. SPU
     * - 2. SpuDetail
     * - 3. SKU集合
     * - 4. 商品分类
     * - 这里值需要分类的id和name就够了，因此我们查询到以后自己需要封装数据-----面包屑
     * - 5. 品牌对象
     * - 6. 规格组
     * - 查询规格组的时候，把规格组下所有的参数也一并查出，上面提供的接口中已经实现该功能，我们直接调
     * - 7. sku的特有规格参数
     * 有了规格组，为什么这里还要查询？因为在SpuDetail中的SpecialSpec中，是以id作为规格参数id作为key
     * 但是，在页面渲染时，需要知道参数的名称 我们就需要把id和name一一对应起来，因此需要额外查询sku的特有规格参数，然后变成一个id:name的键值对格式。也就是一个Map，方便将来根据id查找！
     *
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> loadData(Long spuId) {

        Map<String, Object> model = new HashMap<>();

        // 根据spuId查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        // 查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);

        // 查询分类：Map<String, Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        // 初始化一个分类的map
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // skus
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);

        // 查询规格参数组
        List<SpecGroup> groups = this.specificationClient.queryGroupsWithParam(spu.getCid3());

        // 查询特殊的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        // 初始化特殊规格参数的map
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("skus", skus);
        model.put("groups", groups);
        model.put("paramMap", paramMap);

        return model;
    }
}