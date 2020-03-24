package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.SynchronizeService;
import com.dp.plat.pms.springmvc.dao.PmSynchronizeMapper;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;

@Service("pmSynchronizeService")
public class PmSynchronizeService extends SynchronizeService implements IPmSynchronizeService {

	@Resource
	private PmSynchronizeMapper perfSynchronizeMapper;

	@Override
	public List<AfPrjProperty> selectAllAfPrjProperty() {
		return perfSynchronizeMapper.selectAllAfPrjProperty();
	}

	@Override
	public void clearAllAfPrjProperty() {
		perfSynchronizeMapper.clearAllAfPrjProperty();
	}

	@Override
	public void insertAfPrjProperty(List<AfPrjProperty> list) {
		perfSynchronizeMapper.insertAfPrjProperty(list);
	}

	@Override
	public List<ProjectProduct> selectAllProjectProduct() {
		return perfSynchronizeMapper.selectAllProjectProduct();
	}

	@Override
	public void clearAllProjectProduct() {
		perfSynchronizeMapper.clearAllProjectProduct();
	}

	@Override
	public void insertProjectProduct(List<ProjectProduct> list) {
		perfSynchronizeMapper.insertProjectProduct(list);
	}

	@Override
	public void splitAfProjectByProductCode(String productCode) {
		perfSynchronizeMapper.splitAfProjectByProductCode(productCode);
	}
}
