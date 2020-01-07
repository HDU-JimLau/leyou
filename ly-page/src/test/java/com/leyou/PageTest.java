package com.leyou;


import com.leyou.service.GoodsService;
import com.leyou.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageTest {

    @Autowired
    private PageService pageService;

    @Autowired
    private GoodsService goodsService;

    @Test
    public void Test(){
        goodsService.createHtml(141L);
    }
}
