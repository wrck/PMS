package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.service.IIndustryLeakWarningService;
import com.dp.plat.pms.springmvc.dao.IndustryLeakWarningMapper;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("industryLeakWarningService")
public class IndustryLeakWarningService extends AbstractBaseService<IndustryLeakWarningMapper, IndustryLeakWarning> implements IIndustryLeakWarningService {

	@Override
	public List<Object> selectWarningAssetBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectWarningAssetBySelectivePageable(pageParam);
	}

	@Override
	public long countWarningAssetBySelectivePageable(PageParam<Object> pageParam) {
		return dao.countWarningAssetBySelectivePageable(pageParam);
	}
}
