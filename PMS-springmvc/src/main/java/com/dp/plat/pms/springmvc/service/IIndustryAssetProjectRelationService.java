package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import java.util.List;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.service.IAbstractBaseService;

/**
 *
 * Created by CodeGenerator
 */
public interface IIndustryAssetProjectRelationService extends IAbstractBaseService<IndustryAssetProjectRelation> {

    long countProjectAssetBySelectivePageable(PageParam<Object> tempParam);

    List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam);

    void insertProjectAssetSelective(IndustryAssetVO v);

    void invalidAssetProjectRelation(IndustryAssetProjectRelation t);

}
