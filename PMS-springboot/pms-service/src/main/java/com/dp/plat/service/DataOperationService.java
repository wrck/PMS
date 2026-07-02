package com.dp.plat.service;

import com.dp.plat.model.entity.PmsOrderData;
import java.util.List;

/**
 * 数据操作服务 - 负责外部系统数据同步
 */
public interface DataOperationService extends BaseService<PmsOrderData> {
    /** 从ERP/D365同步订单数据 */
    void syncOrderFromERP();
    /** 从ITR同步问题单 */
    void syncFromITR();
    /** 从SMS同步项目属性和设备清单 */
    void syncFromSMS();
    /** 从D365同步数据 */
    void syncFromD365();
    /** 从OA同步售前数据 */
    void syncPresalesFromOA();
    /** 从License同步数据 */
    void syncFromLicense();
    /** 推送交付件到D365 */
    void pushDeliveryToD365();
}
