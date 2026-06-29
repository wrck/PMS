package com.dp.plat.core.service.impl;

import org.springframework.stereotype.Service;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.service.ICompanyService;

import com.dp.plat.core.pojo.Company;
import com.dp.plat.core.dao.CompanyMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("companyService")
public class CompanyService extends AbstractBaseService<CompanyMapper, Company> implements ICompanyService {

    @Override
    public boolean isParent(Integer compId) {
        return dao.isParent(compId);
    }
}