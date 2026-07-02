package com.dp.plat.service;

import com.dp.plat.model.entity.PmsProject;
import java.util.List;

/**
 * PM同步服务 - 负责从SMS同步项目数据
 */
public interface PmSynchronizeService extends BaseService<PmsProject> {
    void syncProjectPropertyFromSMS();
    void syncProjectProductLineFromSMS();
    void syncPresalesFromSMS();
    void syncPaymentPlanFromSMS();
    void syncMarketRelationsFromSMS();
    void syncFromSMS();
}
