package com.lwg.item.service;

import com.lwg.pojo.SpecGroup;
import com.lwg.pojo.SpecParam;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/20 0:23
 */
public interface SpecificationService {
    List<SpecGroup> queryGroupsByCid(Long cid);

    List<SpecParam> queryParamsBygid(Long gid);
}
