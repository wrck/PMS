package com.dp.plat.pms.springmvc.service;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;

/**
 *
 * Created by CodeGenerator
 */
public interface IIndustryAssetLeakRelationService extends IAbstractBaseService<IndustryAssetLeakRelation>{

    long countProjectAssetLeakBySelectivePageable(PageParam<Object> tempParam);

    List<Object> selectProjectAssetLeakBySelectivePageable(PageParam<Object> pageParam);
    
    void insertProjectAssetLeakSelective(IndustryLeakVO v);

    void invalidProjectAssetLeakRelation(IndustryAssetLeakRelation t);
}