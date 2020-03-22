package com.dp.plat.core.service;

import com.dp.plat.core.service.IAbstractBaseService;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.pojo.Company;

/**
 *
 * Created by CodeGenerator
 */
public interface ICompanyService extends IAbstractBaseService<Company>{

    /**
     * 判断是否为母公司
     * @param compId
     * @return
     */
    boolean isParent(@Param("compId") Integer compId);
}