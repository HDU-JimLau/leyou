package com.leyou.api;

import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {

    /**
     * 根据cid 查询对应规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    List<SpecGroup> queryBroupById(@PathVariable("cid") Long cid);


    /**
     * 查询规格参数列表
     * gid 规格分组id
     * cid 分类id
     * searching 根据过滤条件
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("params")
    List<SpecParam> queryParamList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching
    );



    /**
     * 根据cid查询 规格组和规格参数
     * @param cid
     * @return
     */
    @GetMapping("group/{cid}")
    List<SpecGroup> queryGroupByCid(@PathVariable("cid") Long cid);
}
