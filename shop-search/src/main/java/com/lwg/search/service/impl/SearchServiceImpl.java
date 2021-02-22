package com.lwg.search.service.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lwg.common.pojo.PageResult;
import com.lwg.common.utils.JsonUtils;
import com.lwg.pojo.*;
import com.lwg.search.client.BrandClient;
import com.lwg.search.client.CategoryClient;
import com.lwg.search.client.GoodsClient;
import com.lwg.search.client.SpecificationClient;
import com.lwg.search.pojo.Goods;
import com.lwg.search.pojo.SearchRequest;
import com.lwg.search.pojo.SearchResult;
import com.lwg.search.reponsitory.GoodsRepository;
import com.lwg.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述：导入数据-将查询得到的spu转化为Goods存入elasticSearch
 *
 * @Author: lwg
 * @Date: 2021/2/10 22:40
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;


    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询分类名称
        List<String> names = categoryClient.queryNamesByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询spu下的所有sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(
                    sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });


        //查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParams(
                null, spu.getCid3(), null, true);

        //查询spuDetail。获取规格参数值
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());

        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(),
                new TypeReference<Map<Long, Object>>() {
                });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<Object>>>() {
                });

        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()) {
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        //设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));  // 搜索字段包含标题,分类，品牌，规格等
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuList));  // 所有sku的集合的json格式
        goods.setSpecs(paramMap);  // 所有的可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());

        return goods;
    }

    //实现基本搜索
    @Override
    public PageResult<Goods> search(SearchRequest request) {
        String key = request.getKey();
        //1. 判断是否有搜索条件,如果没有,直接返回Null,不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2. 对key进行全文检索查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        //通过sourceFilter设置返回的结果字段,我们只需要id，skus subtitle   过滤字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        //3. 分页
        //准备分页参数
        Integer page = request.getPage();  //默认页
        Integer size = request.getSize();  //每页大小
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //4. 排序
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            //如果不为空,则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending? SortOrder.DESC:SortOrder.ASC));
        }

        //5. 聚合商品分类,品牌
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //5.1 执行搜索,获取搜索的结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        //5.2 解析聚合结果
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //3. 封装结果并返回
        //总条数
        Long total = goodsPage.getTotalElements();
        //总页数
        int totalPage = (total.intValue() + size - 1) / size;
        return new SearchResult(total,totalPage,goodsPage.getContent(),categories,brands);
    }

    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //1. 处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        //获取所有的品牌id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //解析聚合结果中所有的桶,把桶的集合转化成id的集合
        List<Long> brandIds = terms.getBuckets().stream().map(bucket ->
                bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据ids查询品牌
        return brandIds.stream().map(id->brandClient.queryBrandById(id)).collect(Collectors.toList());

    }

    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        //获取所有分支的id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();

        //定义一个品牌集合,搜集所有的品牌形象
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        //解析所有的id桶,查询品牌
        buckets.forEach(bucket->{
            cids.add(bucket.getKeyAsNumber().longValue());
        });
        List<String> names = categoryClient.queryNamesByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
