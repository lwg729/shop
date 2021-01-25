package com.lwg.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/24 21:14
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpuBo extends Spu{

    private String cname;  //商品分类名称
    private String bname;  //品牌名称
    private SpuDetail spuDetail; //商品详情
    private List<Sku> skus;  //sku列表


}
