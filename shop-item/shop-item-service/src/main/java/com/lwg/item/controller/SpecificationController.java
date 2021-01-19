package com.lwg.item.controller;

import com.lwg.item.service.impl.SpecificationServiceImpl;
import com.lwg.pojo.SpecGroup;
import com.lwg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/20 0:23
 */

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationServiceImpl specificationService;

    /**
     * 查找规格
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamsBygid(@RequestParam("gid") Long gid) {
        List<SpecParam> params = specificationService.queryParamsBygid(gid);
        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }
}
