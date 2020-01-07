package com.leyou.api;


import com.leyou.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据PID 查询分类
     * @param pid
     * @return
     */
    @GetMapping("/list")
    List<Category> quaryByParentId(@RequestParam(value = "pid", defaultValue = "0") Long pid);


    /**
     * 根据ids查询商品分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
   List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
}
