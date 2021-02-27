package com.lwg.api;

import com.lwg.pojo.SpecGroup;
import com.lwg.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 21:43
 */
@RequestMapping("spec")
public interface SpecificationApi {
    /**
     * 查找参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching);

    /**
     * 查询规格参数组,及组内参数
     * @return
     */
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid")Long cid);
}
