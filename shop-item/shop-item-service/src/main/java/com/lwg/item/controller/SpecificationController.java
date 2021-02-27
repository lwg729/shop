package com.lwg.item.controller;

import com.lwg.item.service.impl.SpecificationServiceImpl;
import com.lwg.pojo.SpecGroup;
import com.lwg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * 根据分类id查找规格参数组
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

    /**
     * 根据条件查询规格参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching) {
        List<SpecParam> params = specificationService.queryParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 修改商品规格
     *
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup) {
        specificationService.updateGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 新增规格
     *
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> insertGroup(@RequestBody SpecGroup specGroup) {
        specificationService.insertGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格
     *
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("id") Long id) {
        specificationService.deleteGroupById(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新参数
     *
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParam specParam) {
        specificationService.updateParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 添加参数
     *
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> insertParam(@RequestBody SpecParam specParam) {
        specificationService.insertParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable("id") Long id) {
        specificationService.deleteParamById(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsWithParam(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}
