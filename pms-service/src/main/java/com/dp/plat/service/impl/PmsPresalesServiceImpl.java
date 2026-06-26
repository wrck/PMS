package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsPresalesMapper;
import com.dp.plat.model.entity.PmsPresales;
import com.dp.plat.service.PmsPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 售前管理服务 - 迁移自老系统 PresalesServiceImpl
 *
 * 核心业务逻辑：
 * 1. 售前项目 CRUD
 * 2. 流程状态管理：未申请(-1) → 审批中(0) → 通过(1)/驳回(2)
 * 3. 服务经理审批 → 项目经理审批 → 工程管理部审批
 * 4. 终止并关闭
 */
@Service
public class PmsPresalesServiceImpl implements PmsPresalesService {

    @Autowired
    private PmsPresalesMapper presalesMapper;

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
        presales.setApplyState(-1); // 未申请
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
        presalesMapper.deleteById(id);
    }

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
        presales.setApplyState(0); // 审批中
        presales.setApplyTime(LocalDateTime.now());
        presalesMapper.updateById(presales);
        // TODO: 启动 Activiti/Flowable 流程实例
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        PmsPresales presales = presalesMapper.selectById(id);
        if (presales == null) {
            throw new BusinessException("售前项目不存在");
        }
        if (presales.getApplyState() != 0) {
            throw new BusinessException("该项目不在审批中");
        }
        presales.setApplyState(approved ? 1 : 2);
        if (!approved) {
            presales.setEndTime(LocalDateTime.now());
        }
        presalesMapper.updateById(presales);
        // TODO: 完成 Activiti/Flowable 任务
        // TODO: 记录审批意见
    }
}
