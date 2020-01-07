package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.mapper.CategoryMapper;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 通过ParentId查询分类
     * @param pid
     * @return
     */
    public List<Category> queryByParentId(Long pid){
        /*
            SELECT id,name,parent_id,is_parent,sort FROM tb_category WHERE parent_id = ?
         */

        //非主键查询，使用对象
        Category category=new Category();
        category.setParentId(pid);
        List<Category> categories = categoryMapper.select(category);

        //判断异常
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 通过分类idList 查询分类
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids){
        /*
            ids

        *   SELECT * FROM tb_category WHERE id in (?,?,? ......);
        * */
        //查询
        List<Category> categories = categoryMapper.selectByIdList(ids);
        //判断异常
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }


}
