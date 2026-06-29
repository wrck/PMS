package com.dp.plat.ehr.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.ehr.dao.EHRLoginAccountMapper;
import com.dp.plat.ehr.entity.EHRLoginAccount;
import com.dp.plat.ehr.service.IEHRLoginAccountService;

/**
 *
 * Created by CodeGenerator
 */
@Service("eHRLoginAccountService")
public class EHRLoginAccountService extends AbstractBaseService<EHRLoginAccountMapper, EHRLoginAccount> implements IEHRLoginAccountService {
}
