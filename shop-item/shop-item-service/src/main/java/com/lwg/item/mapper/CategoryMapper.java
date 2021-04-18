package com.lwg.item.mapper;

import com.lwg.pojo.Category;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jboss.logging.Param;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/8 0:35
 */

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {

    @Select("select c.name from tb_category c inner join tb_category_brand tcb on tcb.category_id=c.id inner join tb_brand b on tcb.brand_id=b.id where brand_id=#{bid}")
    List<Category> queryCategoryBid(Long bid);
}
