package com.dp.plat.pms.extend.d365.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.pms.extend.d365.dao.PurchaseLineMapper;
import com.dp.plat.pms.extend.d365.model.PurchaseLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseLineService;

/**
 *
 * @author w02611
 * @time   2022-07-01 18:10:45
 */
@Service("d365PurchaseLineService")
public class PurchaseLineService extends AbstractBaseService<PurchaseLineMapper, PurchaseLine> implements IPurchaseLineService {
}
