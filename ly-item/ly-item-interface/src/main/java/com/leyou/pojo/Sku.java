package com.leyou.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_sku")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private String ownSpec; //商品特殊规格的键值对
    private String indexes; //商品特殊规格的键值对
    private Boolean enable; //是否有效，创建逻辑删除
    private Date createTime; //创建时间
    private Date lastUpdateTime; //最后修改时间
    @Transient
    private Integer stock; //库存


}
