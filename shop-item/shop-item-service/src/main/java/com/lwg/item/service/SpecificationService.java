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

    void updateGroup(SpecGroup specGroup);

    void insertGroup(SpecGroup specGroup);

    void deleteGroupById(Long id);

    void updateParam(SpecParam specParam);

    void insertParam(SpecParam specParam);

    void deleteParamById(Long id);
}
