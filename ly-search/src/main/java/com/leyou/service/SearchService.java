package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.pojo.*;
import com.leyou.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 将spu转化为Goods
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu){

        Goods goods=new Goods();

        //(1)查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //(1.1)取出分类名
        List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());

        //(2)查询品牌
        Brand brand = brandClient.queryBrandByid(spu.getBrandId());

        //(3)搜索字段
        String all = spu.getTitle() + StringUtils.join(categoryNames, " ") + brand.getName();


        //(4)查询sku
        List<Sku> skus = goodsClient.querySkuById(spu.getId());

        //(4.1) 取出sku的price
        // List<Long> prices = skus.stream().map(Sku::getPrice).collect(Collectors.toList());

        //(4.2) 处理skus,处理掉额外的属性，将价格取出
        List<Map<String,Object>> skuList= new ArrayList<>();
        List<Long> prices =new ArrayList<>();
        for (Sku sku : skus) {
            //添加价格
            prices.add(sku.getPrice());
            //添加所需属性
            Map<String,Object> skuMap =new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.substringBefore(sku.getImages(),","));
            skuList.add(skuMap);
        }

        //(5)查询规格参数
        List<SpecParam> specParams = specificationClient.queryParamList(null, spu.getCid3(), true);

        //(6)查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        //(6.1)查询通用规格参数
        Map<String, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), String.class, String.class);
        //(6.2)查询特有规格参数
        String specialSpec = spuDetail.getSpecialSpec();
        Map<String, List<String>> specialSpecMap = (Map<String, List<String>>) JsonUtils.nativeRead(specialSpec, new TypeReference<Map<String, List<String>>>() {
        });
        //(6.3) 组合规格参数
        Map<String,Object> specs =new HashMap<>();

        for (SpecParam param : specParams) {
            //规格名称
            String key = param.getName();
            Object value="";
            //判断是否是通用规格
            if(param.getGeneric()){
                 value = genericSpec.get(param.getId().toString());
                 //判断是否是数值类型
                //如果是数字类型,则把数据库中的数字范围解析成前端显示的段
                if(param.getNumeric()){
                    //处理成端
                    value = chooseSegment(value.toString(), param);
                }
            }else {
                value=specialSpecMap.get(param.getId().toString());
            }
            specs.put(key,value);
        }


        //设置属性
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);
        goods.setPrice(prices);
        goods.setSkus(JsonUtils.serialize(skuList)); //todo
        goods.setSpecs(specs);
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 查询商品
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {

        //ES 中默认查询的永远是第0页，于是我这样操作。
        int page =request.getPage()-1;
        int size =request.getSize();

        //创建查询构建器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //0 结果过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //1 分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page,size));
        //2 搜索条件
        // QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey());
        QueryBuilder basicQuery = buildBasicQuery(request);
        nativeSearchQueryBuilder.withQuery(basicQuery);

        //3 聚合分类和品牌
        //3.1 聚合分类
        String categoryAggName = "category_agg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //3.2 聚合品牌
        String brandAggName = "brand_agg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //4 查询
        // Page<Goods> result = goodsRepository.search(nativeSearchQueryBuilder.build());
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(),Goods.class);

        //5 解析结果
        // 5.1解析分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //5.2 解析聚合结果
        Aggregations aggregations = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));

        //6完成规格参数聚合
        List<Map<String,Object>> specs = null;
        if (categories !=null && categories.size()==1){
            //商品分类存在并且数量为1，可以聚合规格参数
            specs=buildSpecificationAgg(categories.get(0).getId(),basicQuery);
        }

        return new SearchResult(total,(long)totalPages,goodsList,categories,brands,specs);

        // return new PageResult(total, (long) totalPages,goodsList);

    }


    private QueryBuilder buildBasicQuery(SearchRequest request){
        // List<Map<String,Object>> specs =new ArrayList<>();
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件
        Map<String,String> map =request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key =entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key="specs." +key+ ".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {

        List<Map<String,Object>> specs =new ArrayList<>();
        //1.查询需要聚合的规格参数
        List<SpecParam> specParams = specificationClient.queryParamList(null, cid, true);
        //2.聚合
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //2.1带上查询条件
        queryBuilder.withQuery(basicQuery);
        //2.2聚合
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //3.获取结果
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //4.解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            StringTerms terms = aggs.get(name);
            List<String> options = terms.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());

            //准备map
            Map<String,Object> map =new HashMap<>();
            map.put("k",name);
            map.put("options",terms.getBuckets().stream().map(b ->b.getKeyAsString()).collect(Collectors.toList()));

            specs.add(map);
        }

        return specs;
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {

        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        }catch (Exception e){


            return null;

        }

    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

            List<Brand> brands = brandClient.queryBrandByIds(ids);

            return brands;
        }catch (Exception e){
            log.error("[搜索服务]查询品牌异常：",e);
            return null;
        }
    }


    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }





}
