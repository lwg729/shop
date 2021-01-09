package com.lwg.common.pojo;

import lombok.Data;

import java.util.List;

/**
 * 功能描述：分页响应结果实体类
 *
 * @Author: lwg
 * @Date: 2021/1/9 21:48
 */
@Data
public class PageResult<T> {

    private Long total; //总条数
    private String totalPage;  //总页数
    private List<T> items;  //当前页数据
}
