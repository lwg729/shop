package com.lwg.item.controller;

import com.lwg.item.service.impl.CateServiceImpl;
import com.lwg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/8 0:24
 */

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CateServiceImpl cateService;


    /**
     * 根据父id查询子节点
     *
     * @param pid
     * @return
     */
    @RequestMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        try {
            if (pid == null || pid < 0) {
                //相应400  参数不合法
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories = cateService.queryCategoryByPid(pid);
            if (CollectionUtils.isEmpty(categories)) {
                //相应404
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = cateService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(names);
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     *
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCids(@RequestParam("id") Long id) {
        List<Category> idList = cateService.queryAllByCid3(id);
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(idList);

    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryBid(@PathVariable("bid") Long bid) {
        List<Category> categories = this.cateService.queryCategoryBid(bid);
        if (categories == null || categories.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categories);
    }
}
