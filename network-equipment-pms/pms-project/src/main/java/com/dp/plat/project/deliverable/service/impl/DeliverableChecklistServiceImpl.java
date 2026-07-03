package com.dp.plat.project.deliverable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;
import com.dp.plat.project.deliverable.enums.DeliverableType;
import com.dp.plat.project.deliverable.mapper.DeliverableChecklistMapper;
import com.dp.plat.project.deliverable.service.IDeliverableChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IDeliverableChecklistService}.
 */
@Service
@RequiredArgsConstructor
public class DeliverableChecklistServiceImpl
        extends ServiceImpl<DeliverableChecklistMapper, DeliverableChecklist>
        implements IDeliverableChecklistService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<DeliverableChecklist> create(DeliverableChecklist checklist) {
        if (checklist == null) {
            throw new BusinessException("交付物清单信息不能为空");
        }
        if (checklist.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (checklist.getRequired() == null) {
            checklist.setRequired(true);
        }
        if (checklist.getUploaded() == null) {
            checklist.setUploaded(false);
        }
        checklist.setId(null);
        this.save(checklist);
        return Result.ok(checklist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(DeliverableChecklist checklist) {
        if (checklist == null || checklist.getId() == null) {
            throw new BusinessException("交付物清单信息或ID不能为空");
        }
        DeliverableChecklist existing = super.getById(checklist.getId());
        if (existing == null) {
            throw new BusinessException("交付物清单项不存在");
        }
        this.updateById(checklist);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long id) {
        DeliverableChecklist existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException("交付物清单项不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result<DeliverableChecklist> getById(Long id) {
        if (id == null) {
            throw new BusinessException("ID不能为空");
        }
        DeliverableChecklist checklist = super.getById(id);
        if (checklist == null) {
            throw new BusinessException("交付物清单项不存在");
        }
        return Result.ok(checklist);
    }

    @Override
    public Result<List<DeliverableChecklist>> listByProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        List<DeliverableChecklist> list = this.list(new LambdaQueryWrapper<DeliverableChecklist>()
                .eq(DeliverableChecklist::getProjectId, projectId));
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<DeliverableChecklist>> initChecklist(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        // Return existing records if the checklist has already been initialised.
        List<DeliverableChecklist> existing = this.list(new LambdaQueryWrapper<DeliverableChecklist>()
                .eq(DeliverableChecklist::getProjectId, projectId));
        if (!existing.isEmpty()) {
            return Result.ok(existing);
        }
        List<DeliverableChecklist> records = new ArrayList<>();
        for (DeliverableType type : DeliverableType.values()) {
            DeliverableChecklist record = DeliverableChecklist.builder()
                    .projectId(projectId)
                    .deliverableType(type.name())
                    .required(true)
                    .uploaded(false)
                    .build();
            records.add(record);
        }
        this.saveBatch(records);
        return Result.ok(records);
    }
}
