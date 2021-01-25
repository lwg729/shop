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
import org.springframework.transaction.annotation.Transactional;

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
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return paramMapper.select(specParam);
    }

    /**
     * 修改规格
     * @param specGroup
     */
    @Transactional
    @Override
    public void updateGroup(SpecGroup specGroup) {
        groupMapper.updateByPrimaryKeySelective(specGroup);
    }

    /**
     * 添加规格
     * @param specGroup
     */
    @Transactional
    @Override
    public void insertGroup(SpecGroup specGroup) {
        groupMapper.insertSelective(specGroup);
    }

    /**
     * 删除规格
     * @param id
     */
    @Transactional
    @Override
    public void deleteGroupById(Long id) {
        groupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新参数
     * @param specParam
     */
    @Transactional
    @Override
    public void updateParam(SpecParam specParam) {
        paramMapper.updateByPrimaryKeySelective(specParam);
    }

    /**
     * 添加参数
     * @param specParam
     */
    @Transactional
    @Override
    public void insertParam(SpecParam specParam) {
        paramMapper.insertSelective(specParam);
    }

    /**
     * 删除参数
     * @param id
     */
    @Transactional
    @Override
    public void deleteParamById(Long id) {
        paramMapper.deleteByPrimaryKey(id);
    }

}
