package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.dao.CommonRelatedDataMapper;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("commonRelatedDataService")
public class CommonRelatedDataService extends AbstractBaseService<CommonRelatedDataMapper, CommonRelatedData> implements ICommonRelatedDataService {
}
