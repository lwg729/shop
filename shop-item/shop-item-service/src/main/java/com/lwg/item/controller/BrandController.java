package com.lwg.item.controller;

import com.lwg.common.pojo.PageResult;
import com.lwg.item.service.impl.BrandServiceImpl;
import com.lwg.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/9 22:52
 */

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandServiceImpl brandService;

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
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc
    ) {
        PageResult<Brand> result = this.brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }


    /**
     * 新增品牌
     *
     * @param brand
     * @param cids  多个商品id
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrand(brand, cids);
        return ResponseEntity.ok(null);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteBrand(@RequestParam("bid") Long bid) {
        brandService.deleteBrand(bid);
        return ResponseEntity.ok(null);
    }

}
