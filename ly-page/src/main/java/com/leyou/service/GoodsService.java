package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoodsService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;


    @Autowired
    TemplateEngine templateEngine;


    public Map<String, Object> loadModel(Long spuId){

        try {
            // 查询spu
            Spu spu = this.goodsClient.querySpuById(spuId);

            // 查询spu详情
            SpuDetail spuDetail = spu.getSpuDetail();

            // 查询sku
            List<Sku> skus = spu.getSkus();

            // 查询品牌
            // List<Brand> brands = this.brandClient.queryBrandByIds(Arrays.asList(spu.getBrandId()));
            Brand brand = brandClient.queryBrandByid(spu.getBrandId());

            // 查询分类
            // List<Category> categories = getCategories(spu);
            List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

            // 查询组内参数
            List<SpecGroup> specGroups = this.specificationClient.queryGroupByCid(spu.getCid3());

            // 查询所有特有规格参数
            List<SpecParam> specParams = this.specificationClient.queryParamList(null, spu.getCid3(), null);
            // 处理规格参数
            Map<Long, String> paramMap = new HashMap<>();
            specParams.forEach(param->{
                paramMap.put(param.getId(), param.getName());
            });

            Map<String, Object> map = new HashMap<>();
            map.put("spu", spu);
            map.put("spuDetail", spuDetail);
            map.put("skus", skus);
            // map.put("brand", brands.get(0));
            map.put("brand", brand);
            map.put("categories", categories);
            map.put("groups", specGroups);
            // map.put("params", paramMap);
            map.put("paramMap", paramMap);
            return map;
        } catch (Exception e) {
            log.error("加载商品数据出错,spuId:{}", spuId, e);
        }
        return null;
    }

    /**
     * 创建html页面
     *
     * @param spuId
     * @throws Exception
     */
    public void createHtml(Long spuId) {

        PrintWriter writer = null;
        try {
            // 获取页面数据
            Map<String, Object> spuMap = loadModel(spuId);

            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(spuMap);

            // 创建输出流
            File file = new File("d:\\" + spuId + ".html");
            // 如果文件存在 删除文件
            if(file.exists()){
                file.delete();
            }
            writer = new PrintWriter(file);

            // 执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("页面静态化出错：{}，"+ e, spuId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    /**
     * 删除页面
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\", id + ".html");
        if (file.exists()){
            file.deleteOnExit();
        }
    }
}
