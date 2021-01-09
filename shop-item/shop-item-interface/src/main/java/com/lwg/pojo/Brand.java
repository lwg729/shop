package com.lwg.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能描述：品牌实体类
 *
 * @Author: lwg
 * @Date: 2021/1/9 18:10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String image;  //品牌图片地址
    private Character letter;  //品牌的首字母
}
