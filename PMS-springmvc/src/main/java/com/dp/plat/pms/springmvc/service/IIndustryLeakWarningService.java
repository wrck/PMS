package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 *
 * Created by CodeGenerator
 */
public interface IIndustryLeakWarningService extends IAbstractBaseService<IndustryLeakWarning> {

	List<Object> selectWarningAssetBySelectivePageable(PageParam<Object> pageParam);

	long countWarningAssetBySelectivePageable(PageParam<Object> pageParam);
}
