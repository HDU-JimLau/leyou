package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT b.id,b.`name`,b.letter,b.image\n" +
            "FROM\n" +
            "tb_brand b \n" +
            "INNER JOIN\n" +
            "tb_category_brand cb \n" +
            "on b.id=cb.brand_id\n" +
            "WHERE cb.category_id=#{cid};")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
}
