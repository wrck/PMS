package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.IDataFieldRelationService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.dao.DataFieldRelationMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("dataFieldRelationService")
public class DataFieldRelationService extends AbstractBaseService<DataFieldRelationMapper, DataFieldRelation> implements IDataFieldRelationService {
}
