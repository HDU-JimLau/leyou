package com.leyou.api;

// import com.leyou.common.vo.PageResult;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
public interface GoodsApi {

    /**
     * 分页查询spu
     *
     * @param page 当前页数
     * @param rows 每页数量
     * @param key  过滤关键字
     * @param saleable 商品是否上架
     * @return
     */
    @GetMapping("spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable
    );

    /**
     * 根据id来查询商品详细信息
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);
    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    Void saveGoods(@RequestBody Spu spu);

    /**
     * 根据 spuId 查询sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuById(@RequestParam("id") Long spuId);

    /**
     * 更新商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    Void updateGoods(@RequestBody Spu spu);

    /**
     * 根据spu id  查询 spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);
}
