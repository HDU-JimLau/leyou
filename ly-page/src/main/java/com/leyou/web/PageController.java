package com.leyou.web;

import com.leyou.service.GoodsService;
import com.leyou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    PageService pageService;
    @Autowired
    GoodsService goodsService;
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuid,Model model){

        //查询模型数据
        Map<String,Object> spuMap=null;
        // spuMap = pageService.loadModel(spuid);
        spuMap = goodsService.loadModel(spuid);
        //准备数据模型
        model.addAllAttributes(spuMap);
        return "item";
    }
}
