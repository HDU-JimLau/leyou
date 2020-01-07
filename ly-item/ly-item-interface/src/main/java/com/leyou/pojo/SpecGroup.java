package com.leyou.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Data
@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;
    private String name;

    @Transient
    private List<SpecParam> params;// 该组下的所有规格参数集合

}
