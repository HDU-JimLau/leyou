package com.leyou.pojo;

import com.leyou.common.vo.PageResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {

    private List<Category> categoryList; //分类待选项

    private List<Brand> brands;  //品牌待选项

    private List<Map<String,Object>> specs; //规格参数过滤条件

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categoryList, List<Brand> brands,List<Map<String,Object>> specs) {
        super(total, totalPage, items);
        this.categoryList = categoryList;
        this.brands = brands;
        this.specs =specs;
    }


    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
