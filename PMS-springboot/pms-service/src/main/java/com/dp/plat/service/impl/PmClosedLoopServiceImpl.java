package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.PmClosedLoopMapper;
import com.dp.plat.model.entity.PmClosedLoop;
import com.dp.plat.model.vo.WorkflowTaskVO;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PmClosedLoopServiceImpl implements PmClosedLoopService {

    @Autowired
    private PmClosedLoopMapper closedLoopMapper;
    @Autowired
    private WorkflowService workflowService;

    @Override
    public IPage<PmClosedLoop> queryClosedLoopPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState) {
        Page<PmClosedLoop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmClosedLoop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, PmClosedLoop::getProjectId, projectId)
               .eq(applyState != null, PmClosedLoop::getApplyState, applyState)
               .orderByDesc(PmClosedLoop::getCreateTime);
        return closedLoopMapper.selectPage(page, wrapper);
    }

    @Override
    public PmClosedLoop getDetail(Long id) {
        PmClosedLoop cl = closedLoopMapper.selectById(id);
        if (cl == null) {
            throw new BusinessException("闭环记录不存在");
        }
        return cl;
    }

    @Override
    @Transactional
    public void apply(PmClosedLoop closedLoop) {
        // 检查是否已有进行中的闭环申请
        Long count = closedLoopMapper.selectCount(
                new LambdaQueryWrapper<PmClosedLoop>()
                        .eq(PmClosedLoop::getProjectId, closedLoop.getProjectId())
                        .eq(PmClosedLoop::getApplyState, 0));
        if (count > 0) {
            throw new BusinessException("该项目已有进行中的闭环申请");
        }

        closedLoop.setApplyState(0); // 审批中
        closedLoop.setApplyBy(SecurityUtil.getCurrentUsername());
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoop.setCreateBy(SecurityUtil.getCurrentUsername());
        closedLoopMapper.insert(closedLoop);

        // 启动Flowable闭环审批流程
        String businessKey = "PmClosedLoop." + closedLoop.getId() + "." + closedLoop.getProjectId();
        Map<String, Object> vars = new java.util.HashMap<>();
        vars.put("initiator", SecurityUtil.getCurrentUsername());
        vars.put("projectId", closedLoop.getProjectId());
        String instId = workflowService.startProcess("closedloop", businessKey, vars);

        // 回写流程实例ID
        closedLoop.setInstId(instId);
        closedLoopMapper.updateById(closedLoop);

        // 添加审批意见
        workflowService.addApprovalComment(closedLoop.getId(), "closedloop", null,
                instId, 0, "发起闭环申请");
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved, String role) {
        PmClosedLoop cl = closedLoopMapper.selectById(id);
        if (cl == null) {
            throw new BusinessException("闭环记录不存在");
        }
        if (cl.getApplyState() != 0) {
            throw new BusinessException("该记录不在审批中");
        }

        if (approved) {
            // 根据角色决定下一步
            if ("pm".equals(role)) {
                // PM审批通过，等待SM审批
                // 状态不变，继续审批中
            } else if ("sm".equals(role)) {
                // SM审批通过，等待CB审批
                // 状态不变，继续审批中
            } else if ("cb".equals(role) || "cl".equals(role)) {
                // CB/CL审批通过，闭环完成
                cl.setApplyState(1); // 已通过
                cl.setEndTime(LocalDateTime.now());
            }
        } else {
            // 驳回
            cl.setApplyState(2); // 已驳回
            cl.setEndTime(LocalDateTime.now());
        }

        cl.setUpdateBy(SecurityUtil.getCurrentUsername());
        cl.setUpdateTime(LocalDateTime.now());
        closedLoopMapper.updateById(cl);

        // 通过Flowable完成审批任务
        if (StringUtils.hasText(cl.getInstId())) {
            // BPMN中使用candidateGroups，任务未直接分配，需要先认领
            // 先尝试按assignee查询，再按候选组查询
            WorkflowTaskVO task = workflowService.getTaskByProcessInstanceAndAssignee(
                    cl.getInstId(), SecurityUtil.getCurrentUsername());
            if (task == null) {
                // 任务在候选组中，先认领再完成
                List<WorkflowTaskVO> tasks = workflowService.getTasksByProcessInstanceId(cl.getInstId());
                if (tasks != null && !tasks.isEmpty()) {
                    task = tasks.get(0);
                    workflowService.claimTask(task.getTaskId(), SecurityUtil.getCurrentUsername());
                }
            }
            if (task != null) {
                Map<String, Object> vars = new java.util.HashMap<>();
                vars.put("outcome", approved ? 1 : -1);
                workflowService.completeTask(task.getTaskId(), cl.getInstId(), comment, vars);
            }
        }

        // 添加审批意见
        workflowService.addApprovalComment(cl.getId(), "closedloop", null,
                cl.getInstId(), approved ? 1 : -1, comment);
    }

    /** 查询项目的闭环历史 */
    public List<PmClosedLoop> queryByProject(Long projectId) {
        return closedLoopMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoop>()
                        .eq(PmClosedLoop::getProjectId, projectId)
                        .orderByDesc(PmClosedLoop::getCreateTime));
    }

    /** 查询进行中的闭环申请 */
    public PmClosedLoop queryRunningByProject(Long projectId) {
        return closedLoopMapper.selectOne(
                new LambdaQueryWrapper<PmClosedLoop>()
                        .eq(PmClosedLoop::getProjectId, projectId)
                        .eq(PmClosedLoop::getApplyState, 0)
                        .last("LIMIT 1"));
    }

    @Override
    @Transactional
    public void pmApply(PmClosedLoop closedLoop) {
        // 迁移自: PmClosedLoopAction.addPmCLApply()
        closedLoop.setApplyType("PM");
        closedLoop.setApplyState(0);
        closedLoop.setApplyBy(SecurityUtil.getCurrentUsername());
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateBy(SecurityUtil.getCurrentUsername());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoopMapper.insert(closedLoop);
        // 迁移自: PmClosedLoopServiceImpl.addPmCLApply()
        // 启动工作流(依赖Activiti,暂不集成)
    }

    @Override
    @Transactional
    public void smApply(PmClosedLoop closedLoop) {
        // 迁移自: PmClosedLoopAction.addSmCLApply()
        closedLoop.setApplyType("SM");
        closedLoop.setApplyState(0);
        closedLoop.setApplyBy(SecurityUtil.getCurrentUsername());
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateBy(SecurityUtil.getCurrentUsername());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoopMapper.insert(closedLoop);
    }

    @Override
    @Transactional
    public void cbApply(PmClosedLoop closedLoop) {
        // 迁移自: PmClosedLoopAction.addCbCLApply()
        closedLoop.setApplyType("CB");
        closedLoop.setApplyState(0);
        closedLoop.setApplyBy(SecurityUtil.getCurrentUsername());
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateBy(SecurityUtil.getCurrentUsername());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoopMapper.insert(closedLoop);
    }

    @Override
    @Transactional
    public void cantClose(Long id, String reason) {
        // 迁移自: PmClosedLoopAction.cantCB()
        PmClosedLoop cl = closedLoopMapper.selectById(id);
        if (cl == null) throw new BusinessException("闭环记录不存在");
        cl.setApplyState(3); // 无法闭环
        cl.setCloseReason(reason);
        cl.setEndTime(LocalDateTime.now());
        cl.setUpdateBy(SecurityUtil.getCurrentUsername());
        cl.setUpdateTime(LocalDateTime.now());
        closedLoopMapper.updateById(cl);
    }

    @Override
    @Transactional
    public void clApply(PmClosedLoop closedLoop) {
        // 迁移自: PmClosedLoopAction.addClCLApply()
        closedLoop.setApplyType("CL");
        closedLoop.setApplyState(0);
        closedLoop.setApplyBy(SecurityUtil.getCurrentUsername());
        closedLoop.setApplyTime(LocalDateTime.now());
        closedLoop.setCreateBy(SecurityUtil.getCurrentUsername());
        closedLoop.setCreateTime(LocalDateTime.now());
        closedLoopMapper.insert(closedLoop);
    }
}
