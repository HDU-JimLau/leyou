package com.leyou.repository;

import com.leyou.client.GoodsClient;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Goods;
import com.leyou.pojo.Spu;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Test
    public void Test(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void loadData(){
        int page =1;
        int row =100;
        int size = 0;
        do {
            //查询spu信息
            // key
            PageResult<Spu> spuPageResult = goodsClient.querySpuByPage(page, row, null, true);
            List<Spu> spuList = spuPageResult.getItems();
            //构成goods
            List<Goods> goods = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());
            //存入索引库
            goodsRepository.saveAll(goods);
            //翻页
            page++;
            //是否达到末尾
            size = spuList.size();
        }while (size==100);
    }



}