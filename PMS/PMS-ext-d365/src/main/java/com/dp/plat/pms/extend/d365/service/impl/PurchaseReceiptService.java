package com.dp.plat.pms.extend.d365.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.pms.extend.d365.dao.PurchaseReceiptMapper;
import com.dp.plat.pms.extend.d365.entity.PurchaseReceipt;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService;

/**
 *
 * @author w02611
 * @time   2022-07-01 18:10:45
 */
@Service("d365PurchaseReceiptService")
public class PurchaseReceiptService extends AbstractBaseService<PurchaseReceiptMapper, PurchaseReceipt> implements IPurchaseReceiptService {
}