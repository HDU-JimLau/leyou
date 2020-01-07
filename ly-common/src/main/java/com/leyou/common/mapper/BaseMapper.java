package com.leyou.common.mapper;




import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.base.insert.InsertMapper;

//为了让通用mapper识别,添加该注解
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long>,
        InsertMapper<T>, InsertListMapper<T> {
}
