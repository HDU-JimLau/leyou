package com.leyou.api;



import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Brand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
public interface BrandsApi {

    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @GetMapping("page")
    PageResult<Brand> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "25") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    );

    /**
     * 根据分类ID新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    Void addBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    List<Brand> queryBrandByCid(@PathVariable("cid") Long cid);


    /**
     * 根据bid查询品牌
     * @param bid
     * @return
     */
    @GetMapping("{id}")
    Brand queryBrandByid(@PathVariable("id") Long bid);


    /**
     * 根据ids 查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
