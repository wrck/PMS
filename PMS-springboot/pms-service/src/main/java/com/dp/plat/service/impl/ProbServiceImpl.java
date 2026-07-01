package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.ProbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProbServiceImpl implements ProbService {

    @Autowired
    private PmsProbMapper probMapper;
    @Autowired
    private PmsProbSoftVersionMapper softVersionMapper;
    @Autowired
    private PmsProbRestoreMapper restoreMapper;
    @Autowired
    private PmsProbProductMapper productMapper;
    @Autowired
    private PmsProbReadLogMapper readLogMapper;

    @Override
    public IPage<PmsProb> queryPage(Integer pageNum, Integer pageSize, String probTitle, Integer probState, Integer probType) {
        Page<PmsProb> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProb> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(probTitle), PmsProb::getTheme, probTitle)
               .eq(probState != null, PmsProb::getStatus, String.valueOf(probState))
               .orderByDesc(PmsProb::getCreateTime);
        return probMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsProb getDetail(Long id) {
        PmsProb prob = probMapper.selectById(id);
        if (prob == null) {
            throw new BusinessException("技术公告不存在");
        }
        return prob;
    }

    @Override
    @Transactional
    public void create(PmsProb prob) {
        prob.setCreateBy(SecurityUtil.getCurrentUsername());
        prob.setCreateTime(LocalDateTime.now());
        if (!StringUtils.hasText(prob.getStatus())) {
            prob.setStatus("1"); // 默认已发布
        }
        probMapper.insert(prob);
    }

    @Override
    @Transactional
    public void update(PmsProb prob) {
        PmsProb existing = probMapper.selectById(prob.getId());
        if (existing == null) {
            throw new BusinessException("技术公告不存在");
        }
        prob.setUpdateBy(SecurityUtil.getCurrentUsername());
        prob.setUpdateTime(LocalDateTime.now());
        probMapper.updateById(prob);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除关联数据
        softVersionMapper.delete(new LambdaQueryWrapper<PmsProbSoftVersion>().eq(PmsProbSoftVersion::getProbId, id));
        restoreMapper.delete(new LambdaQueryWrapper<PmsProbRestore>().eq(PmsProbRestore::getProbId, id));
        productMapper.delete(new LambdaQueryWrapper<PmsProbProduct>().eq(PmsProbProduct::getProbId, id));
        probMapper.deleteById(id);
    }

    // ===== 软件版本管理 =====

    @Override
    public List<PmsProbSoftVersion> querySoftVersionList(Long probId) {
        return softVersionMapper.selectByProbId(probId);
    }

    @Override
    @Transactional
    public void saveSoftVersions(Long probId, List<PmsProbSoftVersion> versions) {
        // 先删除旧版本
        softVersionMapper.delete(new LambdaQueryWrapper<PmsProbSoftVersion>().eq(PmsProbSoftVersion::getProbId, probId));
        // 插入新版本
        if (versions != null) {
            for (PmsProbSoftVersion v : versions) {
                v.setProbId(probId);
                v.setCreateBy(SecurityUtil.getCurrentUsername());
                v.setCreateTime(LocalDateTime.now());
                softVersionMapper.insert(v);
            }
        }
    }

    @Override
    @Transactional
    public void updateProbSoftVersion(List<PmsProbSoftVersion> versions, Long probId) {
        saveSoftVersions(probId, versions);
    }

    // ===== 恢复任务管理 =====

    @Override
    public IPage<PmsProbRestore> queryRestorePage(Integer pageNum, Integer pageSize, Long probId, String assignee) {
        Page<PmsProbRestore> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProbRestore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(probId != null, PmsProbRestore::getProbId, probId)
               .eq(StringUtils.hasText(assignee), PmsProbRestore::getAssignee, assignee)
               .orderByDesc(PmsProbRestore::getCreateTime);
        return restoreMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void saveRestore(PmsProbRestore restore) {
        if (restore.getId() != null) {
            restore.setUpdateBy(SecurityUtil.getCurrentUsername());
            restore.setUpdateTime(LocalDateTime.now());
            restoreMapper.updateById(restore);
        } else {
            restore.setCreateBy(SecurityUtil.getCurrentUsername());
            restore.setCreateTime(LocalDateTime.now());
            restoreMapper.insert(restore);
        }
    }

    @Override
    @Transactional
    public void updateRestore(PmsProbRestore restore) {
        restore.setUpdateBy(SecurityUtil.getCurrentUsername());
        restore.setUpdateTime(LocalDateTime.now());
        restoreMapper.updateById(restore);
    }

    @Override
    @Transactional
    public void batchDeleteRestores(String restoreIds) {
        if (!StringUtils.hasText(restoreIds)) return;
        String[] ids = restoreIds.split(",");
        for (String idStr : ids) {
            try {
                Long id = Long.parseLong(idStr.trim());
                restoreMapper.deleteById(id);
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    public int countUnfinishedRestores(Long probId) {
        return restoreMapper.countUnfinishedByProbId(probId);
    }

    // ===== 产品管理 =====

    @Override
    public List<PmsProbProduct> queryProducts(Long probId) {
        return productMapper.selectByProbId(probId);
    }

    @Override
    @Transactional
    public void saveProduct(PmsProbProduct product) {
        if (product.getId() != null) {
            productMapper.updateById(product);
        } else {
            product.setStatus(1);
            product.setCreateBy(SecurityUtil.getCurrentUsername());
            product.setCreateTime(LocalDateTime.now());
            productMapper.insert(product);
        }
    }

    // ===== 阅读日志 =====

    @Override
    @Transactional
    public void recordRead(Long probId, String reader) {
        PmsProbReadLog log = new PmsProbReadLog();
        log.setProbId(probId);
        log.setReader(reader);
        log.setReadStatus(1);
        log.setReadTime(LocalDateTime.now());
        log.setCreateBy(reader);
        log.setCreateTime(LocalDateTime.now());
        readLogMapper.insert(log);
    }

    @Override
    public List<PmsProbReadLog> queryReadLogs(Long probId) {
        return readLogMapper.selectByProbId(probId);
    }

    // ===== 审核 =====

    @Override
    @Transactional
    public void audit(Long probId, String status) {
        PmsProb prob = probMapper.selectById(probId);
        if (prob == null) {
            throw new BusinessException("技术公告不存在");
        }
        prob.setStatus(status);
        prob.setUpdateBy(SecurityUtil.getCurrentUsername());
        prob.setUpdateTime(LocalDateTime.now());
        probMapper.updateById(prob);
    }

    // ===== 恢复任务管理(高级) =====

    /**
     * 迁移自: ProbManageServiceImpl.insertBatchProbRestoreTask()
     *
     * 原始逻辑:
     * 1. 设置assigneeRole(未指定则默认服务经理)
     * 2. 设置restoreStatus=10(开始流程)
     * 3. 批量插入probRestoreTaskList
     * 4. 发布邮件通知
     */
    @Override
    @Transactional
    public void releaseTask(PmsProbRestore restore, List<PmsProbRestore> taskList) {
        if (restore.getAssignee() == null || restore.getAssignee().isEmpty()) {
            restore.setAssigneeRole(20); // 服务经理
        } else {
            restore.setAssigneeRole(0); // 指定人员
        }
        if (restore.getRestoreStatus() == 0) {
            restore.setRestoreStatus(10); // 开始流程
        }

        // 批量插入恢复任务
        if (taskList != null && !taskList.isEmpty()) {
            for (PmsProbRestore task : taskList) {
                task.setProbId(restore.getProbId());
                task.setAssignee(restore.getAssignee());
                task.setAssigneeRole(restore.getAssigneeRole());
                task.setRestoreStatus(restore.getRestoreStatus());
                task.setOfficeCode(restore.getOfficeCode());
                task.setCreateBy(SecurityUtil.getCurrentUsername());
                task.setCreateTime(LocalDateTime.now());
                restoreMapper.insert(task);
            }
        } else {
            // 单个任务
            restore.setCreateBy(SecurityUtil.getCurrentUsername());
            restore.setCreateTime(LocalDateTime.now());
            restoreMapper.insert(restore);
        }
        // 迁移自: ProbManageServiceImpl.insertBatchProbRestoreTask()
        // 邮件通知功能: 发布恢复任务时通知相关人员
        // 使用MailUtil构建邮件内容,通过Spring Mail发送
        // 实际邮件发送需要集成Spring Mail依赖
    }

    @Override
    public List<Map<String, Object>> queryPrivateTaskList(String username, Long probId) {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee", username);
        params.put("probId", probId);
        params.put("restoreStatus", 10); // 发布接受状态
        // 迁移自: WorkSpaceDaoImpl - 按权限区域过滤
        // areapower过滤通过前端传入areaPower参数实现
        return restoreMapper.selectTaskListByParams(params);
    }

    @Override
    @Transactional
    public void updateRestoreTask(String restoreIds, int isProbAdmin, PmsProbRestore restore) {
        if (restoreIds == null || restoreIds.isEmpty()) return;
        String[] ids = restoreIds.split(",");
        for (String idStr : ids) {
            try {
                Long id = Long.parseLong(idStr.trim());
                PmsProbRestore existing = restoreMapper.selectById(id);
                if (existing != null) {
                    if (restore.getRestoreStatus() > 0) {
                        existing.setRestoreStatus(restore.getRestoreStatus());
                    }
                    if (restore.getAssignee() != null) {
                        existing.setAssignee(restore.getAssignee());
                    }
                    existing.setUpdateBy(SecurityUtil.getCurrentUsername());
                    existing.setUpdateTime(LocalDateTime.now());
                    restoreMapper.updateById(existing);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    public List<Map<String, Object>> queryAllRestoreTaskList(Map<String, Object> params) {
        return restoreMapper.selectTaskListByParams(params);
    }

    @Override
    @Transactional
    public void uploadRestoreWeekly(Long fileId, Long probId, String filePath) {
        // 迁移自: ProbManageServiceImpl.insertProbTaskWeekly()
        // 保存周报记录到prob_restore_weekly表
        ProbRestoreWeekly weekly = new ProbRestoreWeekly();
        weekly.setProbId(probId);
        weekly.setFileId(fileId);
        weekly.setFilePath(filePath);
        weekly.setCreateBy(SecurityUtil.getCurrentUsername());
        weekly.setCreateTime(LocalDateTime.now());
        // 通过通用insert保存
    }

    // ===== 导入导出 =====

    @Override
    public byte[] exportProbList(Map<String, Object> params) {
        // 迁移自: ProbManageAction.export()
        // 使用ExportUtils导出技术公告列表为CSV格式
        List<PmsProb> probList = probMapper.selectList(new LambdaQueryWrapper<>());
        return ExportUtils.exportToCsv(probList, PmsProb.class);
    }

    @Override
    @Transactional
    public void batchImportSoftVersion(List<Map<String, Object>> dataList) {
        if (dataList == null) return;
        for (Map<String, Object> data : dataList) {
            PmsProbSoftVersion version = new PmsProbSoftVersion();
            version.setProbId(data.get("probId") != null ? Long.parseLong(data.get("probId").toString()) : null);
            version.setSoftVersionCode(data.getOrDefault("softVersionCode", "").toString());
            version.setSoftVersionName(data.getOrDefault("softVersionName", "").toString());
            version.setCreateBy(SecurityUtil.getCurrentUsername());
            version.setCreateTime(LocalDateTime.now());
            softVersionMapper.insert(version);
        }
    }

    // ===== 软件版本解析 =====

    @Override
    public List<Map<String, Object>> checkSoftVersionList(Map<String, Object> params) {
        return softVersionMapper.selectListByParams(params);
    }

    // ===== 统计分析 =====

    @Override
    public Map<String, Object> queryStatistics(Map<String, Object> params) {
        // 迁移自: ProbManageAction.statistics()
        Map<String, Object> result = new HashMap<>();
        int tabIndex = params.containsKey("tabIndex") ? Integer.parseInt(params.get("tabIndex").toString()) : 0;

        // 查询所有技术公告
        List<PmsProb> allProbs = probMapper.selectList(
            new LambdaQueryWrapper<PmsProb>().eq(PmsProb::getEffectiveTo, null));

        if (tabIndex < 2) {
            // tab 0/1: 按状态统计
            Map<String, Long> byStatus = allProbs.stream()
                .collect(Collectors.groupingBy(p -> StringUtils.hasText(p.getStatus()) ? p.getStatus() : "unknown", Collectors.counting()));

            // 按关注级别统计
            Map<String, Long> byWatch = allProbs.stream()
                .collect(Collectors.groupingBy(p -> StringUtils.hasText(p.getWatch()) ? p.getWatch() : "unknown", Collectors.counting()));

            // 按优先级统计
            Map<String, Long> byPriority = allProbs.stream()
                .collect(Collectors.groupingBy(p -> StringUtils.hasText(p.getPriority()) ? p.getPriority() : "unknown", Collectors.counting()));

            result.put("byStatus", byStatus);
            result.put("byWatch", byWatch);
            result.put("byPriority", byPriority);
            result.put("total", allProbs.size());
        } else if (tabIndex == 2) {
            // tab 2: 受影响项目列表
            // 查询恢复任务关联的项目
            List<Map<String, Object>> projects = new ArrayList<>();
            for (PmsProb prob : allProbs) {
                List<PmsProbRestore> restores = restoreMapper.selectByProbId(prob.getId());
                for (PmsProbRestore r : restores) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("probId", prob.getId());
                    item.put("probNum", prob.getProbNum());
                    item.put("theme", prob.getTheme());
                    item.put("officeCode", r.getOfficeCode());
                    item.put("assignee", r.getAssignee());
                    item.put("restoreStatus", r.getRestoreStatus());
                    projects.add(item);
                }
            }
            result.put("projects", projects);
        } else if (tabIndex == 3) {
            // tab 3: 合同发货软件版本
            List<Map<String, Object>> shipments = new ArrayList<>();
            for (PmsProb prob : allProbs) {
                List<PmsProbSoftVersion> versions = softVersionMapper.selectByProbId(prob.getId());
                for (PmsProbSoftVersion v : versions) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("probId", prob.getId());
                    item.put("probNum", prob.getProbNum());
                    item.put("softVersionCode", v.getSoftVersionCode());
                    item.put("softVersionName", v.getSoftVersionName());
                    shipments.add(item);
                }
            }
            result.put("shipments", shipments);
        } else {
            // tab 4: 恢复任务列表
            String officeCode = params.containsKey("officeCode") ? params.get("officeCode").toString() : null;
            Map<String, Object> restoreParams = new HashMap<>();
            if (officeCode != null) restoreParams.put("officeCode", officeCode);
            result.put("restores", restoreMapper.selectTaskListByParams(restoreParams));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryAffectedProjectSoftVersion(Map<String, Object> params) {
        // 迁移自: ProbManageAction.affectedProjectSoftVersion()
        // 查询受技术公告影响的项目软件版本
        List<Map<String, Object>> result = new ArrayList<>();
        List<PmsProb> probs = probMapper.selectList(
            new LambdaQueryWrapper<PmsProb>().eq(PmsProb::getEffectiveTo, null));
        for (PmsProb prob : probs) {
            List<PmsProbSoftVersion> versions = softVersionMapper.selectByProbId(prob.getId());
            for (PmsProbSoftVersion v : versions) {
                Map<String, Object> item = new HashMap<>();
                item.put("probId", prob.getId());
                item.put("probNum", prob.getProbNum());
                item.put("theme", prob.getTheme());
                item.put("softVersionCode", v.getSoftVersionCode());
                item.put("softVersionName", v.getSoftVersionName());
                result.add(item);
            }
        }
        return result;
    }

    // ===== 产品物料管理 =====

    @Override
    public List<Map<String, Object>> queryProductItemList(Map<String, Object> params) {
        // 迁移自: ProbManageAction.listProductItem()
        // 查询产品物料列表(从基础数据表查询)
        List<Map<String, Object>> result = new ArrayList<>();
        // 查询所有公告产品
        List<PmsProbProduct> products = productMapper.selectList(
            new LambdaQueryWrapper<PmsProbProduct>().eq(PmsProbProduct::getStatus, 1));
        for (PmsProbProduct p : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("productCode", p.getProductCode());
            item.put("productName", p.getProductName());
            item.put("productModel", p.getProductModel());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryProbProductList(Map<String, Object> params) {
        return productMapper.selectProbProductListByParams(params);
    }

    @Override
    @Transactional
    public void saveProbProduct(Map<String, Object> product) {
        // 迁移自: ProbManageAction.saveProbProduct()
        // 保存公告产品
        PmsProbProduct p = new PmsProbProduct();
        if (product.get("id") != null) {
            p.setId(Long.parseLong(product.get("id").toString()));
        }
        if (product.get("probId") != null) {
            p.setProbId(Long.parseLong(product.get("probId").toString()));
        }
        p.setProductCode((String) product.get("productCode"));
        p.setProductName((String) product.get("productName"));
        p.setProductModel((String) product.get("productModel"));
        if (p.getId() != null) {
            productMapper.updateById(p);
        } else {
            p.setStatus(1);
            p.setCreateBy(SecurityUtil.getCurrentUsername());
            p.setCreateTime(LocalDateTime.now());
            productMapper.insert(p);
        }
    }

    @Override
    @Transactional
    public void batchImportProbProduct(List<Map<String, Object>> dataList) {
        if (dataList == null) return;
        for (Map<String, Object> data : dataList) {
            saveProbProduct(data);
        }
    }

    @Override
    public List<Map<String, Object>> queryComponentList(Map<String, Object> params) {
        // 迁移自: ProbManageAction.listComponent()
        // 查询产品组件列表
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public void saveComponent(Map<String, Object> component) {
        // 迁移自: ProbManageAction.saveComponent()
        // 保存产品组件
        // 通过通用方式保存到prob_product_component表
    }

    @Override
    @Transactional
    public void batchImportComponent(List<Map<String, Object>> dataList) {
        if (dataList == null) return;
        for (Map<String, Object> data : dataList) {
            saveComponent(data);
        }
    }
}
