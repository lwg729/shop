package com.lwg.common.mapper;


import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 功能描述：通用mapper
 *
 * @Author: lwg
 * @Date: 2021/1/25 23:27
 */
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long>,InsertListMapper<T> {
}
