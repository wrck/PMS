package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;

public interface IndustryLeakWarningMapper extends AbstractBaseMapper<IndustryLeakWarning> {

	List<Object> selectWarningAssetBySelectivePageable(PageParam<Object> pageParam);

	long countWarningAssetBySelectivePageable(PageParam<Object> pageParam);
}
