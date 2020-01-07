package com.leyou.pojo;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
//对应数据库中表名称
@Table(name = "tb_category")
public class Category {

    //设置主键
    @Id
    //设置主键生成策略
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;

}
