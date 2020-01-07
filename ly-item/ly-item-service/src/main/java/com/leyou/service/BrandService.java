package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.mapper.BrandMapper;
import com.leyou.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;


    /**
     * 分页查询
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        /*
            SELECT id,name,image,letter
            FROM tb_brand
            WHERE ( name like ? or letter = ? )
            order by id ASC
            LIMIT ? offset ?;
         */
        //1.分页
        PageHelper.startPage(page,rows);

        //2.过滤
        //创建一个例子，并且把字节码文件传入，通过反射得到定义的表名字，等等数据
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            //过滤条件
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        //3.排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause=sortBy + (desc  ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //4.查询
        List<Brand> brands = brandMapper.selectByExample(example);

        //判断异常
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //解析出PageInfo
        PageInfo<Brand> info = new PageInfo<>(brands);


        //封装为PageResult
        return new PageResult<Brand>(info.getTotal(), (long) info.getPages(),brands);
    }


    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    public void addBrand(Brand brand, List<Long> cids) {

        /*
            INSERT INTO tb_brand ( id,name,image,letter ) VALUES( ?,?,?,? );
        */
        //新增品牌
        //主键自增
        brand.setId(null);
        int conut = brandMapper.insert(brand);
        if(conut !=1){
            throw new LyException(ExceptionEnum.BRAND_ADD_ERROR);
        }

        /*
        *   INSERT INTO tb_category_brand (category_id, brand_id) VALUES (?,?);
        * */
        //新增中间表
        for (Long cid : cids) {
            int count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if(conut!=1){
                throw new LyException(ExceptionEnum.BRAND_ADD_ERROR);
            }
        }
    }

    /**
     * 通过主键查询品牌
     * @param bid
     * @return
     */
    public Brand queryById(Long bid){
        /*
        *   SELECT * FROM tb_brand WHERE id = ? ;
        * */
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     * 通过 分类id  查询分类下的所有品牌
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {
        /*
        *   SELECT b.id,b.`name`,b.letter,b.image
        *   FROM
        *   tb_brand b LEFT JOIN tb_category_brand cb
        *   on b.id=cb.brand_id
        *   WHERE category_id = ? ;
        * */
        List<Brand> brands = brandMapper.queryByCategoryId(cid);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }


    /**
     * 根据ids 查询品牌
     * @param ids
     * @return
     */
    public List<Brand> queryByIds(List<Long> ids) {

        List<Brand> brands = brandMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }
}
