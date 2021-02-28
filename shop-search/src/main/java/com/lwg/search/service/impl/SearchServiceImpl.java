package com.lwg.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lwg.common.pojo.PageResult;
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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Autowired
    private ElasticsearchTemplate template;


    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

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

        //添加查询条件
        /* MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);*/
        BoolQueryBuilder basicQuery = buildBooleanQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);

        //2. 分页
        //准备分页参数
        Integer page = request.getPage();  //默认页
        Integer size = request.getSize();  //每页大小
        queryBuilder.withPageable(PageRequest.of(page - 1, size));


        //通过sourceFilter设置返回的结果字段,我们只需要id，skus subtitle   过滤字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        //4. 排序
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            //如果不为空,则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }

        //5. 聚合商品分类,品牌
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //5.1 执行搜索,获取搜索的结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //5.2 获取局和结果并解析聚合结果
        List<Map<String, Object>> categories =
                getCategoryAggResult(goodsPage.getAggregation(categoryAggName));

        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //6. 封装结果并返回
        //总条数
        Long total = goodsPage.getTotalElements();
        //总页数
        int totalPage = (total.intValue() + size - 1) / size;

        //7. 判断分类聚合的结果集大小,等于1则聚合 等于一说明已选择该分类
        List<Map<String, Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            specs = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }


        return new SearchResult(total, totalPage, goodsPage.getContent(), categories, brands, specs);
    }

    /**
     * 构建bool查询构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {

            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }
    /**
     * 根据查询条件聚合规格参数
     *
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {
            //创建自定义查询构建器
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            //基于基本的查询条件,聚合规格参数
            queryBuilder.withQuery(basicQuery);

            //查询要聚合的规格参数
            List<SpecParam> params = specificationClient.queryParams(null, id, null, true);
            //对规格参数进行聚合
            params.forEach(param -> {
                queryBuilder.addAggregation(AggregationBuilders.terms(param.getName())
                        .field("specs." + param.getName() + ".keyword"));
            });

            // 添加结果集过滤
            queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

            // 执行聚合查询，获取聚合结果集
            AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

            List<Map<String,Object>> specs = new ArrayList<>();
            // 解析聚合结果集， key-聚合名称（规格参数名） value-聚合对象
            Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
            for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
                // 初始化一个map {k:规格参宿名 options：聚合的规格参数值}
                Map<String,Object> map = new HashMap<>();
                map.put("k", entry.getKey());
                // 初始化一个options集合，收集桶中的key
                List<String> options = new ArrayList<>();
                // 获取聚合
                StringTerms terms = (StringTerms)entry.getValue();
                // 获取桶集合
                terms.getBuckets().forEach(bucket -> {
                    options.add(bucket.getKeyAsString());
                });
                map.put("options", options);
                specs.add(map);
            }
            return specs;

    }

    /**
     * 解析品牌的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //1. 处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        //获取所有的品牌id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 获取聚合中的桶
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());

    }

    // 解析商品分类聚合结果
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        // 获取桶的集合，转化成List<Map<String, Object>>
        return terms.getBuckets().stream().map(bucket -> {
            // 初始化一个map
            Map<String,Object> map = new HashMap<>();
            // 获取桶中的分类id（key）
            Long id = bucket.getKeyAsNumber().longValue();
            // 根据分类id查询分类名称
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
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

    //编写创建和删除索引方法 ,方便rabbitmq调用进行同步
    public void createIndex(Long id)throws IOException{
        Spu spu = goodsClient.querySpuById(id);
        Goods goods = buildGoods(spu);
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long id){
        goodsRepository.deleteById(id);
    }
}
