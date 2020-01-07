package com.leyou.web;

import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import com.leyou.service.SpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecGroupController {

    @Autowired
    private SpecGroupService service;

    /**
     * 根据cid 查询对应规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryBroupById(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(service.queryGroupById(cid));
    }


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
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(service.queryParamList(gid,cid,searching));
    }



    /**
     * 根据cid查询规格组和规格组参数
     * @param cid
     * @return
     */
    @GetMapping("group/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(service.querySpecsByCid(cid));
    }
}
