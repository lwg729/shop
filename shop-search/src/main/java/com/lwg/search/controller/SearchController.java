package com.lwg.search.controller;

import com.lwg.common.pojo.PageResult;
import com.lwg.search.pojo.Goods;
import com.lwg.search.pojo.SearchRequest;
import com.lwg.search.service.impl.SearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/17 22:32
 */

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    private SearchServiceImpl searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        PageResult<Goods> result = searchService.search(request);
        if (request==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}
