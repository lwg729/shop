package com.lwg.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String cname;
    private String bname;
}
