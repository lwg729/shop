package com.lwg.api;

import com.lwg.common.pojo.PageResult;
import com.lwg.pojo.Sku;
import com.lwg.pojo.Spu;
import com.lwg.pojo.SpuBo;
import com.lwg.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 21:34
 */
public interface GoodsApi {

    /**
     * 查询商品列表
     * @param key
     * @param page
     * @param rows
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable);

    /**
     * 根据spu_id查询spu_detail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);

    /**
     * 根据spu的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);
}
