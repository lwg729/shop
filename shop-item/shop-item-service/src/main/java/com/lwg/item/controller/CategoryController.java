package com.lwg.item.controller;

import com.lwg.item.service.impl.CateServiceImpl;
import com.lwg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        if (pid==null||pid<0){
            //相应400  参数不合法
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = cateService.queryCategoryByPid(pid);
        if (CollectionUtils.isEmpty(categories)){
            //相应404
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
}
