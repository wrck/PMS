package com.dp.plat.core.dao;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.pojo.Company;

public interface CompanyMapper extends AbstractBaseMapper<Company> {
	/**
	 * 判断是否为母公司
	 * @param compId
	 * @return
	 */
	boolean isParent(@Param("compId")Integer compId);
	
}
