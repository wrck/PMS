package com.dp.plat.pms.springmvc.service;

import java.util.List;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;

/**
 *
 * Created by CodeGenerator
 */
public interface IIndustryAssetService extends IExcelAnalysisService<IndustryAsset> {

	List<Object> selectProjectAssetBySelectivePageable(PageParam<Object> pageParam);
	
	long countProjectAssetBySelectivePageable(PageParam<Object> pageParam);

}
