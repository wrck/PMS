package com.dp.plat.pms.extend.d365.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.pms.extend.d365.dao.PurchaseReceiptLineMapper;
import com.dp.plat.pms.extend.d365.entity.PurchaseReceiptLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService;

/**
 *
 * @author w02611
 * @time   2022-07-01 18:10:45
 */
@Service("d365PurchaseReceiptLineService")
public class PurchaseReceiptLineService extends AbstractBaseService<PurchaseReceiptLineMapper, PurchaseReceiptLine> implements IPurchaseReceiptLineService {
}
