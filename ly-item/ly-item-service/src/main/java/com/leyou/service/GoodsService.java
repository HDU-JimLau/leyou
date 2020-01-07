package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.mapper.*;
import com.leyou.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// import org.springframework.amqp.core.AmqpTemplate;

@Slf4j
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 分页查询
     *
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key, Boolean saleable) {

        /**
         * SQL 语句为：
         *
         *  SELECT * FROM tb_spu WHERE ( saleable = ? ) order by last_update_time DESC LIMIT ? offset ?;
         *
         *
         */
        //1.分页
        PageHelper.startPage(page, rows);
        //2.过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //3.默认排序,按照更新时间默认排序
        example.setOrderByClause("last_update_time DESC");
        //4.查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //解析分类和品牌的名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        return new PageResult<Spu>(pageInfo.getTotal(), (long) pageInfo.getPages(), spus);
    }


    /**
     * 解析分类和品牌的名称
     *
     * @param spus
     */
    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    /**
     * 根据spu_id来查询商品详细信息
     *
     * @param id
     * @return
     */
    public SpuDetail querySpuDetailById(Long id) {

        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 新增商品
     *
     * @param spu
     */
    public void saveGoods(Spu spu) {

        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int insert = spuMapper.insert(spu);
        if (insert != 1) {
            throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
        }

        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int insert2 = spuDetailMapper.insert(spuDetail);
        if (insert2 != 1) {
            throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
        }


        //新增 sku 和 stock
        saveSkuAndStock(spu);

        //发送消息
        sendMessage(spu.getId(),"insert");

        /*//新增sku
        List<Sku> skus = spu.getSkus();
        ArrayList<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int insert1 = skuMapper.insert(sku);
            if (insert1 != 1) {
                throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
            }
            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        //批量新增库存

        int count = stockMapper.insertList(stocks);
        if (count != stocks.size()) {
            throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
        }*/


    }

    /**
     * 根据id查询sku
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkuById(Long spuId) {

        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        //异常判断
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //设置库存
        for (Sku skus1 : skus) {
            Stock stock = stockMapper.selectByPrimaryKey(skus1.getId());
            if (stock == null) {
                throw new LyException(ExceptionEnum.GOODS_STOCK_FOUND_ERROR);
            }
            skus1.setStock(stock.getStock());
        }

        //使用流操作设置库存
/*        List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(stocks)){
            throw new LyException(ExceptionEnum.GOODS_STOCK_FOUND_ERROR);
        }
        //我们把stock编程一个map,其中key是:sku的id，值是库存值
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skus.forEach(sku1 -> sku1.setStock(stockMap.get(sku1.getId())));
*/


        return skus;
    }

    /**
     * 更新商品,更新商品业务还需要认真揣摩
     *
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) throws LyException {
        if(spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }

        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skus = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skus)) {
            //删除sku
            skuMapper.delete(sku);
            //删除库存
            List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if (i != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        int i1 = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (i1 != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        //更新 sku和stock
        saveSkuAndStock(spu);

        sendMessage(spu.getId(),"update");
    }

    /**
     * 提取出来的公共方法
     * @param spu
     */
    private void saveSkuAndStock(Spu spu){
        //新增sku
        List<Sku> skus = spu.getSkus();
        ArrayList<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int insert1 = skuMapper.insert(sku);
            if (insert1 != 1) {
                throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
            }
            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        //批量新增库存

        int count = stockMapper.insertList(stocks);
        if (count != stocks.size()) {
            throw new LyException(ExceptionEnum.GOODS_ADD_ERROE);
        }
    }

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {

        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new LyException(ExceptionEnum.GOODS_SPU_FOUND_ERROR);
        }
        //查询sku
        spu.setSkus(querySkuById(spu.getId()));
        //查询detail
        spu.setSpuDetail(querySpuDetailById(spu.getId()));

        return spu;
    }

    /**
     * 发送消息
     * @param id
     * @param type
     */
    private void sendMessage(Long id,String type){
        try {
            amqpTemplate.convertAndSend("item."+type,id);
        }catch (Exception e){
            log.error("{}商品消息发送异常，商品id：{}",type,id,e);
        }

    }
}
