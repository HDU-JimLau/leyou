package com.leyou.pojo;

import java.util.Map;


public class SearchRequest {

    private static final int DEFAULT_SIZE =20; //每页大小，不从页面接收，而是固定的
    private static final int DEFAULT_PAGE =1; //默认页

    private String key;// 搜索条件

    private Integer page;// 当前页
    private String sortBy;//以此字段排序
    private Boolean descending;//是否降序
    private Map<String,String> filter;//过滤条件

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null){
            return DEFAULT_PAGE;
        }
        //获取页码时做校验，不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
