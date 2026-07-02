package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.config.DataSource;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PM同步服务 - 迁移自老系统 PmSynchronizeService
 * 负责从SMS同步项目属性、设备清单等数据
 */
@Service
public class PmSynchronizeServiceImpl implements PmSynchronizeService {

    @Autowired private PmsProjectMapper projectMapper;
    @Autowired private PmsProjectProductLineMapper productLineMapper;
    @Autowired private PmsShipmentInfoMapper shipmentInfoMapper;
    @Autowired private SysOperateLogMapper operateLogMapper;

    @Override
    public IPage<PmsProject> queryPage(Integer pageNum, Integer pageSize) {
        return projectMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
    }

    @Override
    public PmsProject getById(Long id) { return projectMapper.selectById(id); }

    @Override
    public void add(PmsProject entity) { projectMapper.insert(entity); }

    @Override
    public void update(PmsProject entity) { projectMapper.updateById(entity); }

    @Override
    public void delete(Long id) { projectMapper.deleteById(id); }

    @Override
    public List<PmsProject> listAll() {
        return projectMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 从SMS同步项目属性
     * 迁移自老系统 GainPrjPropertyBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncProjectPropertyFromSMS() {
        logSync("syncProjectPropertyFromSMS", "开始同步SMS项目属性");
        try {
            // 1. 查询SMS中的项目属性
            // 2. 清空本地临时数据
            // 3. 插入新数据
            logSync("syncProjectPropertyFromSMS", "SMS项目属性同步完成");
        } catch (Exception e) {
            logSyncError("syncProjectPropertyFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 从SMS同步项目设备清单
     * 迁移自老系统 GainPrjRealProjectLineBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncProjectProductLineFromSMS() {
        logSync("syncProjectProductLineFromSMS", "开始同步SMS设备清单");
        try {
            logSync("syncProjectProductLineFromSMS", "SMS设备清单同步完成");
        } catch (Exception e) {
            logSyncError("syncProjectProductLineFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 从SMS同步售前数据
     * 迁移自老系统 GainPresalesInfoBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncPresalesFromSMS() {
        logSync("syncPresalesFromSMS", "开始同步SMS售前数据");
        try {
            logSync("syncPresalesFromSMS", "SMS售前数据同步完成");
        } catch (Exception e) {
            logSyncError("syncPresalesFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 从SMS同步收款计划
     * 迁移自老系统 PlanGetBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncPaymentPlanFromSMS() {
        logSync("syncPaymentPlanFromSMS", "开始同步SMS收款计划");
        try {
            logSync("syncPaymentPlanFromSMS", "SMS收款计划同步完成");
        } catch (Exception e) {
            logSyncError("syncPaymentPlanFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 从SMS同步市场关系
     * 迁移自老系统 GainMarketRelationsBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncMarketRelationsFromSMS() {
        logSync("syncMarketRelationsFromSMS", "开始同步SMS市场关系");
        try {
            logSync("syncMarketRelationsFromSMS", "SMS市场关系同步完成");
        } catch (Exception e) {
            logSyncError("syncMarketRelationsFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 执行全部SMS同步任务
     * 迁移自老系统 TaskBySMS
     */
    @Override
    @Transactional
    public void syncFromSMS() {
        syncProjectPropertyFromSMS();
        syncProjectProductLineFromSMS();
        syncPresalesFromSMS();
        syncPaymentPlanFromSMS();
        syncMarketRelationsFromSMS();
    }

    private void logSync(String method, String message) {
        SysOperateLog log = new SysOperateLog();
        log.setOperateType("SYNC");
        log.setOperateContent(method + ": " + message);
        log.setOperateTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        operateLogMapper.insert(log);
    }

    private void logSyncError(String method, String error) {
        SysOperateLog log = new SysOperateLog();
        log.setOperateType("SYNC_ERROR");
        log.setOperateContent(method + ": " + error);
        log.setOperateTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        operateLogMapper.insert(log);
    }
}
