package com.dp.plat.pms.extend.d365.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.pms.extend.d365.dao.PurchaseMapper;
import com.dp.plat.pms.extend.d365.entity.Purchase;
import com.dp.plat.pms.extend.d365.service.IPurchaseService;

/**
 *
 * @author w02611
 * @time   2022-07-01 18:10:45
 */
@Service("d365PurchaseService")
public class PurchaseService extends AbstractBaseService<PurchaseMapper, Purchase> implements IPurchaseService {
}
