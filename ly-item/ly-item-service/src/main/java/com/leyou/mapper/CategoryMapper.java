package com.leyou.mapper;

import com.leyou.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 继承 IdListMapper可以实现对 传入 ids(列表id) 的操作
 */
public interface CategoryMapper extends Mapper<Category>,IdListMapper<Category,Long>{
}
