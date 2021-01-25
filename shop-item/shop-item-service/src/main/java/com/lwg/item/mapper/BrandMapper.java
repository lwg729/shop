package com.lwg.item.mapper;

import com.lwg.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/1/9 21:56
 */
public interface BrandMapper extends Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand(category_id,brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Update("UPDATE tb_category_brand set category_id = #{cid} where brand_id = #{bid}")
    void updateCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id =#{bid}")
    void deleteCatrgoryBrandByBid(@Param("bid") Long bid);

    @Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandsByCid(@Param("cid") Long cid);
}

