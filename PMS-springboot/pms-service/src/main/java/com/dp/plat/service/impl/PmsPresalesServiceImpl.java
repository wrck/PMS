package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.PmsPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PmsPresalesServiceImpl implements PmsPresalesService {

    @Autowired
    private PmsPresalesMapper presalesMapper;
    @Autowired
    private PmsPresalesProductMapper productMapper;
    @Autowired
    private PmsPresalesTaskMapper taskMapper;
    @Autowired
    private PmsPresalesCommentMapper commentMapper;
    @Autowired
    private PmsProjectDeliverMapper deliverMapper;

    @Override
    public IPage<PmsPresales> queryPresalesPage(Integer pageNum, Integer pageSize,
                                                  String presalesCode, String projectName,
                                                  Integer applyState, String officeCode) {
        Page<PmsPresales> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsPresales> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(presalesCode), PmsPresales::getPresalesCode, presalesCode)
               .like(StringUtils.hasText(projectName), PmsPresales::getProjectName, projectName)
               .eq(applyState != null, PmsPresales::getApplyState, applyState)
               .eq(StringUtils.hasText(officeCode), PmsPresales::getOfficeCode, officeCode)
               .orderByDesc(PmsPresales::getCreateTime);
        return presalesMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsPresales getPresalesDetail(Long id) {
        PmsPresales presales = presalesMapper.selectById(id);
        if (presales == null) {
            throw new BusinessException("售前项目不存在");
        }
        return presales;
    }

    @Override
    @Transactional
    public void createPresales(PmsPresales presales) {
        presales.setApplyState(-1);
        presales.setCreateBy(SecurityUtil.getCurrentUsername());
        presales.setCreateTime(LocalDateTime.now());
        presalesMapper.insert(presales);
    }

    @Override
    @Transactional
    public void updatePresales(PmsPresales presales) {
        PmsPresales existing = presalesMapper.selectById(presales.getId());
        if (existing == null) {
            throw new BusinessException("售前项目不存在");
        }
        if (existing.getApplyState() == 0) {
            throw new BusinessException("审批中的项目不能修改");
        }
        presales.setUpdateBy(SecurityUtil.getCurrentUsername());
        presales.setUpdateTime(LocalDateTime.now());
        presalesMapper.updateById(presales);
    }

    @Override
    @Transactional
    public void deletePresales(Long id) {
        PmsPresales existing = presalesMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("售前项目不存在");
        }
        if (existing.getApplyState() == 0) {
            throw new BusinessException("审批中的项目不能删除");
        }
        // 删除关联数据
        productMapper.delete(new LambdaQueryWrapper<PmsPresalesProduct>().eq(PmsPresalesProduct::getPresalesId, id));
        taskMapper.delete(new LambdaQueryWrapper<PmsPresalesTask>().eq(PmsPresalesTask::getPresalesId, id));
        commentMapper.delete(new LambdaQueryWrapper<PmsPresalesComment>().eq(PmsPresalesComment::getPresalesId, id));
        presalesMapper.deleteById(id);
    }

    // ===== 流程 =====

    @Override
    @Transactional
    public void startFlow(Long id) {
        PmsPresales presales = presalesMapper.selectById(id);
        if (presales == null) {
            throw new BusinessException("售前项目不存在");
        }
        if (presales.getApplyState() != -1) {
            throw new BusinessException("该项目已提交申请");
        }
        presales.setApplyState(0);
        presales.setApplyBy(SecurityUtil.getCurrentUsername());
        presales.setApplyTime(LocalDateTime.now());
        presalesMapper.updateById(presales);
    }

    @Override
    @Transactional
    public void reApply(Long id, PmsPresales presales) {
        PmsPresales existing = presalesMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("售前项目不存在");
        }
        existing.setApplyState(0);
        existing.setApplyBy(SecurityUtil.getCurrentUsername());
        existing.setApplyTime(LocalDateTime.now());
        // 更新可修改字段
        if (StringUtils.hasText(presales.getProjectName())) existing.setProjectName(presales.getProjectName());
        if (StringUtils.hasText(presales.getProjectType())) existing.setProjectType(presales.getProjectType());
        presalesMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void smAudit(Long id, String comment, boolean approved) {
        doAudit(id, comment, approved, "SM");
    }

    @Override
    @Transactional
    public void pmAudit(Long id, String comment, boolean approved) {
        doAudit(id, comment, approved, "PM");
    }

    @Override
    @Transactional
    public void emAudit(Long id, String comment, boolean approved) {
        doAudit(id, comment, approved, "EM");
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        doAudit(id, comment, approved, "GENERAL");
    }

    private void doAudit(Long id, String comment, boolean approved, String auditType) {
        PmsPresales presales = presalesMapper.selectById(id);
        if (presales == null) {
            throw new BusinessException("售前项目不存在");
        }
        if (presales.getApplyState() != 0) {
            throw new BusinessException("该项目不在审批中");
        }

        // 记录审批意见
        PmsPresalesComment c = new PmsPresalesComment();
        c.setPresalesId(id);
        c.setCommentBy(SecurityUtil.getCurrentUsername());
        c.setComment(comment);
        c.setResult(approved ? 1 : 2);
        c.setCommentTime(LocalDateTime.now());
        c.setCreateBy(SecurityUtil.getCurrentUsername());
        c.setCreateTime(LocalDateTime.now());
        commentMapper.insert(c);

        if (!approved) {
            // 驳回
            presales.setApplyState(2);
            presales.setEndTime(LocalDateTime.now());
            presalesMapper.updateById(presales);
        } else {
            // 通过 - 根据审批类型决定下一步
            // SM通过 → 等待PM审批, PM通过 → 等待EM审批, EM通过 → 最终通过
            if ("EM".equals(auditType) || "GENERAL".equals(auditType)) {
                presales.setApplyState(1);
                presales.setEndTime(LocalDateTime.now());
                presalesMapper.updateById(presales);
            }
            // SM/PM通过后状态不变，等待下一级审批
        }
    }

    @Override
    @Transactional
    public void terminate2Close(Long id, String closeRemark) {
        PmsPresales presales = presalesMapper.selectById(id);
        if (presales == null) {
            throw new BusinessException("售前项目不存在");
        }
        presales.setCloseRemark(closeRemark);
        presales.setEndTime(LocalDateTime.now());
        presalesMapper.updateById(presales);
    }

    // ===== 产品 =====

    @Override
    public List<PmsPresalesProduct> queryProducts(Long presalesId) {
        return productMapper.selectByPresalesId(presalesId);
    }

    @Override
    @Transactional
    public void saveProduct(PmsPresalesProduct product) {
        if (product.getId() != null) {
            productMapper.updateById(product);
        } else {
            product.setCreateBy(SecurityUtil.getCurrentUsername());
            product.setCreateTime(LocalDateTime.now());
            productMapper.insert(product);
        }
    }

    // ===== 任务 =====

    @Override
    public List<PmsPresalesTask> queryTasks(Long presalesId) {
        return taskMapper.selectByPresalesId(presalesId);
    }

    @Override
    @Transactional
    public void updateTask(PmsPresalesTask task) {
        task.setUpdateBy(SecurityUtil.getCurrentUsername());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    // ===== 审批意见 =====

    @Override
    public List<PmsPresalesComment> queryComments(Long presalesId) {
        return commentMapper.selectByPresalesId(presalesId);
    }

    @Override
    @Transactional
    public void addComment(PmsPresalesComment comment) {
        comment.setCommentTime(LocalDateTime.now());
        comment.setCreateBy(SecurityUtil.getCurrentUsername());
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }

    // ===== 交付件 =====

    @Override
    @Transactional
    public void uploadDeliver(Long presalesId, Long taskId, String fileIds) {
        // 更新任务的交付件文件ID
        PmsPresalesTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setDeliverFileIds(fileIds);
            task.setUpdateBy(SecurityUtil.getCurrentUsername());
            task.setUpdateTime(LocalDateTime.now());
            taskMapper.updateById(task);
        }
    }

    @Override
    @Transactional
    public void deleteDeliver(Long deliverId) {
        deliverMapper.deleteById(deliverId);
    }

    @Override
    @Transactional
    public void updateConfirmFiles(Long presalesId, String fileIds) {
        PmsPresales presales = presalesMapper.selectById(presalesId);
        if (presales != null) {
            presales.setConfirmFileIds(fileIds);
            presales.setUpdateBy(SecurityUtil.getCurrentUsername());
            presales.setUpdateTime(LocalDateTime.now());
            presalesMapper.updateById(presales);
        }
    }

    // ===== 导出 =====

    @Override
    public List<PmsPresales> exportPresales(PmsPresales query) {
        LambdaQueryWrapper<PmsPresales> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getOfficeCode())) {
            wrapper.eq(PmsPresales::getOfficeCode, query.getOfficeCode());
        }
        if (query.getApplyState() != null) {
            wrapper.eq(PmsPresales::getApplyState, query.getApplyState());
        }
        return presalesMapper.selectList(wrapper);
    }
}
