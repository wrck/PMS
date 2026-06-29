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

    // ===== 统计 =====

    @Override
    public Map<String, Object> statistics(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        // 按状态统计
        List<PmsProb> allProbs = probMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> byStatus = allProbs.stream()
                .collect(Collectors.groupingBy(p -> StringUtils.hasText(p.getStatus()) ? p.getStatus() : "unknown", Collectors.counting()));
        result.put("byStatus", byStatus);
        result.put("total", allProbs.size());
        return result;
    }
}
