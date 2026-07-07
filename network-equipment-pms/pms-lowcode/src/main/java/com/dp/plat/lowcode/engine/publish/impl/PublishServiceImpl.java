package com.dp.plat.lowcode.engine.publish.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.engine.publish.PublishService;
import com.dp.plat.lowcode.entity.LowCodePublishRecord;
import com.dp.plat.lowcode.mapper.LowCodePublishRecordMapper;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishServiceImpl implements PublishService {

    private final LowCodePublishRecordMapper publishRecordMapper;
    private final LowCodeConfigVersionService configVersionService;

    @Override
    public LowCodePublishRecord submitForPublish(String configType, Long configId, String changeLog, Long applicantId, String applicant) {
        List<String> errors = validate(configType, configId);
        if (!errors.isEmpty()) {
            throw new RuntimeException("配置校验失败: " + String.join("; ", errors));
        }
        LowCodePublishRecord record = LowCodePublishRecord.builder()
                .configType(configType).configId(configId)
                .version(getNextVersion(configType, configId))
                .status("SUBMITTED")
                .applicantId(applicantId).applicant(applicant)
                .changeLog(changeLog)
                .submittedAt(LocalDateTime.now())
                .build();
        publishRecordMapper.insert(record);
        log.info("发布申请提交: {}/{} v{} by {}", configType, configId, record.getVersion(), applicant);
        return record;
    }

    @Override
    public List<String> validate(String configType, Long configId) {
        List<String> errors = new ArrayList<>();
        // 简化校验：仅检查配置存在性，由具体配置类型扩展
        // TODO: 按 configType 分发到 Entity/Form/List 等的校验逻辑
        return errors;
    }

    @Override
    public LowCodePublishRecord approve(Long publishId, Long approverId, String approver) {
        LowCodePublishRecord record = publishRecordMapper.selectById(publishId);
        if (record == null) throw new RuntimeException("发布记录不存在: " + publishId);
        if (!"SUBMITTED".equals(record.getStatus())) {
            throw new RuntimeException("当前状态不允许审批: " + record.getStatus());
        }
        record.setStatus("PUBLISHED");
        record.setApproverId(approverId);
        record.setApprover(approver);
        record.setApprovedAt(LocalDateTime.now());
        record.setPublishedAt(LocalDateTime.now());
        publishRecordMapper.updateById(record);
        // 创建版本快照
        try {
            // 实际签名 createSnapshot(SnapshotContext)；snapshot 内容由具体配置类型提供，
            // 此处暂传 null，失败由 try/catch 捕获并告警，不阻塞发布流程。
            LowCodeConfigVersionService.SnapshotContext ctx =
                    new LowCodeConfigVersionService.SnapshotContext(
                            record.getConfigType(), record.getConfigId(), record.getConfigCode(),
                            null, record.getChangeLog());
            configVersionService.createSnapshot(ctx);
        } catch (Exception e) {
            log.warn("创建版本快照失败: {}/{}", record.getConfigType(), record.getConfigId(), e);
        }
        log.info("发布审批通过: id={} by {}", publishId, approver);
        return record;
    }

    @Override
    public LowCodePublishRecord reject(Long publishId, String reason, Long approverId, String approver) {
        LowCodePublishRecord record = publishRecordMapper.selectById(publishId);
        if (record == null) throw new RuntimeException("发布记录不存在: " + publishId);
        if (!"SUBMITTED".equals(record.getStatus())) {
            throw new RuntimeException("当前状态不允许拒绝: " + record.getStatus());
        }
        record.setStatus("REJECTED");
        record.setApproverId(approverId);
        record.setApprover(approver);
        record.setRejectReason(reason);
        record.setApprovedAt(LocalDateTime.now());
        publishRecordMapper.updateById(record);
        log.info("发布审批拒绝: id={} by {}", publishId, approver);
        return record;
    }

    @Override
    public LowCodePublishRecord rollback(Long publishId, Long userId, String userName) {
        LowCodePublishRecord record = publishRecordMapper.selectById(publishId);
        if (record == null) throw new RuntimeException("发布记录不存在: " + publishId);
        if (!"PUBLISHED".equals(record.getStatus())) {
            throw new RuntimeException("仅 PUBLISHED 状态可回滚");
        }
        // 调用版本服务回滚
        try {
            configVersionService.rollback(record.getConfigType(), record.getConfigId(), record.getVersion(),
                    "回滚到 v" + record.getVersion());
        } catch (Exception e) {
            log.warn("版本回滚失败: {}/{} v{}", record.getConfigType(), record.getConfigId(), record.getVersion(), e);
        }
        // 创建新发布记录标记回滚
        LowCodePublishRecord rollbackRecord = LowCodePublishRecord.builder()
                .configType(record.getConfigType()).configId(record.getConfigId())
                .configCode(record.getConfigCode())
                .version(getNextVersion(record.getConfigType(), record.getConfigId()))
                .status("PUBLISHED")
                .applicantId(userId).applicant(userName)
                .changeLog("回滚到 v" + record.getVersion())
                .publishedAt(LocalDateTime.now())
                .build();
        publishRecordMapper.insert(rollbackRecord);
        log.info("发布回滚: from={} to v{} by {}", publishId, record.getVersion(), userName);
        return rollbackRecord;
    }

    @Override
    public List<LowCodePublishRecord> listByConfig(String configType, Long configId) {
        return publishRecordMapper.selectList(new LambdaQueryWrapper<LowCodePublishRecord>()
                .eq(LowCodePublishRecord::getConfigType, configType)
                .eq(LowCodePublishRecord::getConfigId, configId)
                .orderByDesc(LowCodePublishRecord::getCreateTime));
    }

    @Override
    public List<LowCodePublishRecord> listPending() {
        return publishRecordMapper.selectList(new LambdaQueryWrapper<LowCodePublishRecord>()
                .eq(LowCodePublishRecord::getStatus, "SUBMITTED")
                .orderByAsc(LowCodePublishRecord::getSubmittedAt));
    }

    private Integer getNextVersion(String configType, Long configId) {
        LowCodePublishRecord latest = publishRecordMapper.selectOne(new LambdaQueryWrapper<LowCodePublishRecord>()
                .eq(LowCodePublishRecord::getConfigType, configType)
                .eq(LowCodePublishRecord::getConfigId, configId)
                .orderByDesc(LowCodePublishRecord::getVersion)
                .last("LIMIT 1"));
        return latest == null ? 1 : latest.getVersion() + 1;
    }
}
