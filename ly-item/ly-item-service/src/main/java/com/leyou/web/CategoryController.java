package com.leyou.web;


import com.leyou.pojo.Category;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService service;


    /**
     * 根据PID 查询分类
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> quaryByParentId(@RequestParam(value = "pid",defaultValue="0") Long pid){
        List<Category> categories = service.queryByParentId(pid);
        //可以这样写，但是第二种方式更好
        // return ResponseEntity.status(HttpStatus.OK).body(categories);
        return ResponseEntity.ok(categories);
    }


    /**
     * 根据ids查询商品分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){

        return ResponseEntity.ok(service.queryByIds(ids));
    }

}
