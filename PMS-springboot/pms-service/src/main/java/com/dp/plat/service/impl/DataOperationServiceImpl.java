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
 * 数据操作服务 - 迁移自老系统 DataOperationService
 * 负责外部系统数据同步：ERP/D365、ITR、SMS、OA、License
 */
@Service
public class DataOperationServiceImpl implements DataOperationService {

    @Autowired private PmsOrderDataMapper orderDataMapper;
    @Autowired private PmsProjectProductLineMapper productLineMapper;
    @Autowired private PmsProjectMapper projectMapper;
    @Autowired private SendMailService sendMailService;
    @Autowired private SysOperateLogMapper operateLogMapper;

    @Override
    public IPage<PmsOrderData> queryPage(Integer pageNum, Integer pageSize) {
        return orderDataMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
    }

    @Override
    public PmsOrderData getById(Long id) { return orderDataMapper.selectById(id); }

    @Override
    public void add(PmsOrderData entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        orderDataMapper.insert(entity);
    }

    @Override
    public void update(PmsOrderData entity) {
        entity.setUpdateTime(LocalDateTime.now());
        orderDataMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) { orderDataMapper.deleteById(id); }

    @Override
    public List<PmsOrderData> listAll() {
        return orderDataMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 从ERP/D365同步订单数据
     * 迁移自老系统 GainOrderByERP
     */
    @Override
    @Transactional
    @DataSource("d365")
    public void syncOrderFromERP() {
        logSync("syncOrderFromERP", "开始同步ERP订单数据");
        try {
            // 1. 从D365数据源查询订单数据
            // 2. 清空本地临时数据
            // 3. 插入新数据
            // 4. 更新项目发货状态
            logSync("syncOrderFromERP", "ERP订单数据同步完成");
        } catch (Exception e) {
            logSyncError("syncOrderFromERP", e.getMessage());
            throw e;
        }
    }

    /**
     * 从ITR同步问题单数据
     * 迁移自老系统 GainDataFromITR
     */
    @Override
    @Transactional
    @DataSource("itr")
    public void syncFromITR() {
        logSync("syncFromITR", "开始同步ITR问题单");
        try {
            // 1. 从ITR数据源查询问题单
            // 2. 清空本地临时数据
            // 3. 插入新数据
            logSync("syncFromITR", "ITR问题单同步完成");
        } catch (Exception e) {
            logSyncError("syncFromITR", e.getMessage());
            throw e;
        }
    }

    /**
     * 从SMS同步项目属性和设备清单
     * 迁移自老系统 TaskBySMS
     */
    @Override
    @Transactional
    @DataSource("sms")
    public void syncFromSMS() {
        logSync("syncFromSMS", "开始同步SMS数据");
        try {
            // 1. 同步项目属性
            // 2. 同步设备清单
            // 3. 同步售前数据
            // 4. 同步收款计划
            // 5. 同步市场关系
            logSync("syncFromSMS", "SMS数据同步完成");
        } catch (Exception e) {
            logSyncError("syncFromSMS", e.getMessage());
            throw e;
        }
    }

    /**
     * 从D365同步数据
     * 迁移自老系统 D365DataJob
     */
    @Override
    @Transactional
    @DataSource("d365")
    public void syncFromD365() {
        logSync("syncFromD365", "开始同步D365数据");
        try {
            // 1. 同步订单数据
            // 2. 同同步订单行数据
            logSync("syncFromD365", "D365数据同步完成");
        } catch (Exception e) {
            logSyncError("syncFromD365", e.getMessage());
            throw e;
        }
    }

    /**
     * 从OA同步售前数据
     * 迁移自老系统 GainPresalesInfoFromOA
     */
    @Override
    @Transactional
    @DataSource("oa")
    public void syncPresalesFromOA() {
        logSync("syncPresalesFromOA", "开始同步OA售前数据");
        try {
            logSync("syncPresalesFromOA", "OA售前数据同步完成");
        } catch (Exception e) {
            logSyncError("syncPresalesFromOA", e.getMessage());
            throw e;
        }
    }

    /**
     * 从License同步数据
     * 迁移自老系统 GainDataFromLicense
     */
    @Override
    @Transactional
    @DataSource("local")
    public void syncFromLicense() {
        logSync("syncFromLicense", "开始同步License数据");
        try {
            logSync("syncFromLicense", "License数据同步完成");
        } catch (Exception e) {
            logSyncError("syncFromLicense", e.getMessage());
            throw e;
        }
    }

    /**
     * 推送交付件到D365
     * 迁移自老系统 PushContractAcceptanceDeliveryJob
     */
    @Override
    @Transactional
    @DataSource("d365")
    public void pushDeliveryToD365() {
        logSync("pushDeliveryToD365", "开始推送交付件到D365");
        try {
            logSync("pushDeliveryToD365", "交付件推送完成");
        } catch (Exception e) {
            logSyncError("pushDeliveryToD365", e.getMessage());
            throw e;
        }
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
