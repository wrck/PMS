package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.service.IFacilitatorService;
import com.dp.plat.pms.springmvc.dao.FacilitatorMapper;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("facilitatorService")
public class FacilitatorService extends AbstractBaseService<FacilitatorMapper, Facilitator> implements IFacilitatorService {
}
