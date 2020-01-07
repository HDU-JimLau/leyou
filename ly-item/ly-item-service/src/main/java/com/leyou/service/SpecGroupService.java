package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecGroupService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;


    /**
     * 根据cid 查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupById(Long cid) {
        /*
        *   SELECT id,cid,name FROM tb_spec_group WHERE cid = ? ;
         * */
        //查询
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        //判断异常
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return specGroups;
    }

    /**
     * 查询规格组参数，gid cid searching 这三个属性只能同时出现一个。
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    public List<SpecParam> queryParamList(Long gid,Long cid,Boolean searching) {

        /*
        *   SELECT id,cid,group_id,name,`numeric`,unit,generic,searching,segments FROM tb_spec_param WHERE group_id = ?
        *   SELECT id,cid,group_id,name,`numeric`,unit,generic,searching,segments FROM tb_spec_param WHERE cid = ?
        *   SELECT id,cid,group_id,name,`numeric`,unit,generic,searching,segments FROM tb_spec_param WHERE searching = ?
         * */

        //属性赋值，查询
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> specParams = specParamMapper.select(specParam);
        //异常判断
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }



    /**
     * 根据cid 查询规格组及规格组参数
     * @param cid
     * @return
     */
    /*public List<SpecGroup> queryGroupByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGroupById(cid);
        //查询组内参数
        List<SpecParam> specParams = queryParamList(null, cid, null);
        //先把规格参数变成map,map的key是规格组id，map的值是组下的所有参数
        Map<Long,List<SpecParam>> map=new HashMap<>();
        for (SpecParam specParam : specParams) {
            if(!map.containsKey(specParam.getGroupId())){
                map.put(specParam.getGroupId(),new ArrayList<>());
            }
            map.get(specParam.getGroupId()).add(specParam);
        }
        //填充specParam到specGroup
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }*/

    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = queryGroupById(cid);
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(queryParamList(g.getId(), null, null));
        });

        return groups;
    }
}
