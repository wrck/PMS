package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.PmClosedLoopMapper;
import com.dp.plat.model.entity.PmClosedLoop;
import com.dp.plat.service.PmClosedLoopService;
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

        // TODO: 启动闭环审批流程(workflow)
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

        // TODO: 完成审批任务(workflow)
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
}
