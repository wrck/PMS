package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.SynchronizeService;
import com.dp.plat.pms.springmvc.dao.PmSynchronizeMapper;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;

@Service("pmSynchronizeService")
public class PmSynchronizeService extends SynchronizeService implements IPmSynchronizeService  {

	@Resource
	private PmSynchronizeMapper perfSynchronizeMapper;
	
	public List<AfPrjProperty> selectAllAfPrjProperty() {
		return perfSynchronizeMapper.selectAllAfPrjProperty();
	}
	public void clearAllAfPrjProperty() {
		perfSynchronizeMapper.clearAllAfPrjProperty();
	}
	public void insertAfPrjProperty(List<AfPrjProperty> list) {
		perfSynchronizeMapper.insertAfPrjProperty(list);
	}
}
