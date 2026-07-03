package com.dp.plat.governance.change.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.governance.change.entity.BaselineHistory;
import com.dp.plat.governance.change.mapper.BaselineHistoryMapper;
import com.dp.plat.governance.change.service.IBaselineHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IBaselineHistoryService}.
 */
@Service
public class BaselineHistoryServiceImpl
        extends ServiceImpl<BaselineHistoryMapper, BaselineHistory>
        implements IBaselineHistoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaselineHistory recordBaselineChange(Long projectId, Long changeRequestId, String crNo,
                                                String changeType, String fieldName,
                                                String oldValue, String newValue, String changedBy) {
        String oldVal = StringUtils.hasText(oldValue) ? oldValue : "空";
        String newVal = StringUtils.hasText(newValue) ? newValue : "空";
        BaselineHistory history = BaselineHistory.builder()
                .projectId(projectId)
                .changeRequestId(changeRequestId)
                .crNo(crNo)
                .changeType(changeType)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .description(fieldName + " 由 " + oldVal + " 变更为 " + newVal)
                .changedAt(LocalDateTime.now())
                .changedBy(changedBy)
                .build();
        this.save(history);
        return history;
    }

    @Override
    public List<BaselineHistory> listByProject(Long projectId) {
        if (projectId == null) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<BaselineHistory>()
                .eq(BaselineHistory::getProjectId, projectId)
                .orderByDesc(BaselineHistory::getChangedAt));
    }
}
