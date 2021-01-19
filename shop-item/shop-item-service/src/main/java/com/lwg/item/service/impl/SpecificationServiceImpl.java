package com.lwg.item.service.impl;

/**
 * 功能描述：商品规格参数
 *
 * @Author: lwg
 * @Date: 2021/1/20 0:23
 */

import com.lwg.item.mapper.SpecificationGroupMapper;
import com.lwg.item.mapper.SpecificationParamMapper;
import com.lwg.item.service.SpecificationService;
import com.lwg.pojo.SpecGroup;
import com.lwg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationGroupMapper groupMapper;

    @Autowired
    private SpecificationParamMapper paramMapper;

    /**
     * 查找规格
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groups = groupMapper.select(specGroup);
        return groups;
    }

    /**
     * 查找参数
     * @param gid
     * @return
     */
    @Override
    public List<SpecParam> queryParamsBygid(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        return paramMapper.select(specParam);
    }
}
