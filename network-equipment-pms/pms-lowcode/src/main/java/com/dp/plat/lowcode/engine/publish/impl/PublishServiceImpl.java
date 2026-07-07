package com.dp.plat.lowcode.engine.publish.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.dto.EntityDesignDTO;
import com.dp.plat.lowcode.engine.publish.PublishService;
import com.dp.plat.lowcode.entity.LowCodeConnector;
import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeForm;
import com.dp.plat.lowcode.entity.LowCodeList;
import com.dp.plat.lowcode.entity.LowCodeMicroflow;
import com.dp.plat.lowcode.entity.LowCodePublishRecord;
import com.dp.plat.lowcode.entity.LowCodeRelatedPage;
import com.dp.plat.lowcode.entity.LowCodeRule;
import com.dp.plat.lowcode.entity.LowCodeTab;
import com.dp.plat.lowcode.mapper.LowCodePublishRecordMapper;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.service.LowCodeConnectorService;
import com.dp.plat.lowcode.service.LowCodeEntityService;
import com.dp.plat.lowcode.service.LowCodeFormService;
import com.dp.plat.lowcode.service.LowCodeListService;
import com.dp.plat.lowcode.service.LowCodeMicroflowService;
import com.dp.plat.lowcode.service.LowCodeRelatedPageService;
import com.dp.plat.lowcode.service.LowCodeRuleService;
import com.dp.plat.lowcode.service.LowCodeTabService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishServiceImpl implements PublishService {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^pms_lc_[a-z][a-z0-9_]*$");

    private final LowCodePublishRecordMapper publishRecordMapper;
    private final LowCodeConfigVersionService configVersionService;
    private final LowCodeEntityService entityService;
    private final LowCodeFormService formService;
    private final LowCodeListService listService;
    private final LowCodeMicroflowService microflowService;
    private final LowCodeConnectorService connectorService;
    private final LowCodeRuleService ruleService;
    private final LowCodeTabService tabService;
    private final LowCodeRelatedPageService relatedPageService;
    private final ObjectMapper objectMapper;

    @Override
    public LowCodePublishRecord submitForPublish(String configType, Long configId, String changeLog, Long applicantId, String applicant) {
        List<String> errors = validate(configType, configId);
        if (!errors.isEmpty()) {
            throw new RuntimeException("配置校验失败: " + String.join("; ", errors));
        }
        // 解析配置编码，便于后续版本快照定位
        String configCode = resolveConfigCode(configType, configId);
        LowCodePublishRecord record = LowCodePublishRecord.builder()
                .configType(configType).configId(configId)
                .configCode(configCode)
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
        if (configType == null || configType.isBlank()) {
            errors.add("configType 不能为空");
            return errors;
        }
        if (configId == null) {
            errors.add("configId 不能为空");
            return errors;
        }
        switch (configType) {
            case "ENTITY" -> validateEntity(configId, errors);
            case "FORM" -> validateForm(configId, errors);
            case "LIST" -> validateList(configId, errors);
            case "MICROFLOW" -> validateMicroflow(configId, errors);
            default -> {
                if (loadConfig(configType, configId) == null) {
                    errors.add(configType + " 配置不存在: id=" + configId);
                }
            }
        }
        return errors;
    }

    private void validateEntity(Long configId, List<String> errors) {
        LowCodeEntity entity = entityService.getById(configId);
        if (entity == null) {
            errors.add("ENTITY 配置不存在: id=" + configId);
            return;
        }
        if (entity.getTableName() == null || !TABLE_NAME_PATTERN.matcher(entity.getTableName()).matches()) {
            errors.add("ENTITY 物理表名非法（须以 pms_lc_ 开头）: " + entity.getTableName());
        }
        EntityDesignDTO design;
        try {
            design = entityService.getDesign(configId);
        } catch (Exception e) {
            errors.add("ENTITY 设计查询失败: " + e.getMessage());
            return;
        }
        if (design == null || design.getFields() == null || design.getFields().isEmpty()) {
            errors.add("ENTITY 缺少字段定义: " + entity.getCode());
        }
    }

    private void validateForm(Long configId, List<String> errors) {
        LowCodeForm form = formService.getById(configId);
        if (form == null) {
            errors.add("FORM 配置不存在: id=" + configId);
            return;
        }
        JsonNode config = parseJson(form.getFormConfig(), errors, "FORM", form.getCode());
        if (config != null) {
            JsonNode fields = config.get("fields");
            if (fields == null || !fields.isArray() || fields.isEmpty()) {
                errors.add("FORM 缺少 fields 定义: " + form.getCode());
            }
        }
    }

    private void validateList(Long configId, List<String> errors) {
        LowCodeList list = listService.getById(configId);
        if (list == null) {
            errors.add("LIST 配置不存在: id=" + configId);
            return;
        }
        JsonNode config = parseJson(list.getListConfig(), errors, "LIST", list.getCode());
        if (config != null) {
            JsonNode columns = config.get("columns");
            if (columns == null || !columns.isArray() || columns.isEmpty()) {
                errors.add("LIST 缺少 columns 定义: " + list.getCode());
            }
        }
    }

    private void validateMicroflow(Long configId, List<String> errors) {
        LowCodeMicroflow microflow = microflowService.getById(configId);
        if (microflow == null) {
            errors.add("MICROFLOW 配置不存在: id=" + configId);
            return;
        }
        JsonNode config = parseJson(microflow.getDefinition(), errors, "MICROFLOW", microflow.getCode());
        if (config != null) {
            JsonNode nodes = config.get("nodes");
            if (nodes == null || !nodes.isArray() || nodes.isEmpty()) {
                errors.add("MICROFLOW 缺少 nodes 定义: " + microflow.getCode());
            }
        }
    }

    private JsonNode parseJson(String json, List<String> errors, String type, String code) {
        if (json == null || json.isBlank()) {
            errors.add(type + " 配置内容为空: " + code);
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            errors.add(type + " 配置不是合法 JSON: " + code + " (" + e.getMessage() + ")");
            return null;
        }
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
        // 创建版本快照：按 configType 查询当前配置的完整 JSON（修复原先传 null 的 bug）
        try {
            ConfigSnapshot snapshot = resolveConfigSnapshot(record.getConfigType(), record.getConfigId());
            String configCode = snapshot != null && snapshot.code() != null
                    ? snapshot.code() : record.getConfigCode();
            LowCodeConfigVersionService.SnapshotContext ctx =
                    new LowCodeConfigVersionService.SnapshotContext(
                            record.getConfigType(), record.getConfigId(), configCode,
                            snapshot == null ? null : snapshot.snapshot(), record.getChangeLog());
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

    // ==================== 配置加载与快照序列化 ====================

    private record ConfigSnapshot(String code, String snapshot) {
    }

    /**
     * 加载配置对象（用于存在性校验）。ENTITY 返回 EntityDesignDTO，其余返回实体本身。
     */
    private Object loadConfig(String configType, Long configId) {
        return switch (configType) {
            case "ENTITY" -> {
                LowCodeEntity e = entityService.getById(configId);
                yield e == null ? null : entityService.getDesign(configId);
            }
            case "FORM" -> formService.getById(configId);
            case "LIST" -> listService.getById(configId);
            case "MICROFLOW" -> microflowService.getById(configId);
            case "CONNECTOR" -> connectorService.getById(configId);
            case "RULE" -> ruleService.getById(configId);
            case "TAB" -> tabService.getById(configId);
            case "RELATED_PAGE" -> relatedPageService.getById(configId);
            default -> null;
        };
    }

    private String resolveConfigCode(String configType, Long configId) {
        ConfigSnapshot snapshot = resolveConfigSnapshot(configType, configId);
        return snapshot == null ? null : snapshot.code();
    }

    /**
     * 按 configType 查询当前配置的完整 JSON，序列化为快照字符串（修复原先 approve 传 null 的 bug）。
     */
    private ConfigSnapshot resolveConfigSnapshot(String configType, Long configId) {
        try {
            return switch (configType) {
                case "ENTITY" -> {
                    LowCodeEntity e = entityService.getById(configId);
                    if (e == null) yield null;
                    yield new ConfigSnapshot(e.getCode(),
                            objectMapper.writeValueAsString(entityService.getDesign(configId)));
                }
                case "FORM" -> {
                    LowCodeForm f = formService.getById(configId);
                    yield f == null ? null : new ConfigSnapshot(f.getCode(), objectMapper.writeValueAsString(f));
                }
                case "LIST" -> {
                    LowCodeList l = listService.getById(configId);
                    yield l == null ? null : new ConfigSnapshot(l.getCode(), objectMapper.writeValueAsString(l));
                }
                case "MICROFLOW" -> {
                    LowCodeMicroflow m = microflowService.getById(configId);
                    yield m == null ? null : new ConfigSnapshot(m.getCode(), objectMapper.writeValueAsString(m));
                }
                case "CONNECTOR" -> {
                    LowCodeConnector c = connectorService.getById(configId);
                    yield c == null ? null : new ConfigSnapshot(c.getCode(), objectMapper.writeValueAsString(c));
                }
                case "RULE" -> {
                    LowCodeRule r = ruleService.getById(configId);
                    yield r == null ? null : new ConfigSnapshot(r.getCode(), objectMapper.writeValueAsString(r));
                }
                case "TAB" -> {
                    LowCodeTab t = tabService.getById(configId);
                    yield t == null ? null : new ConfigSnapshot(t.getCode(), objectMapper.writeValueAsString(t));
                }
                case "RELATED_PAGE" -> {
                    LowCodeRelatedPage rp = relatedPageService.getById(configId);
                    yield rp == null ? null : new ConfigSnapshot(rp.getCode(), objectMapper.writeValueAsString(rp));
                }
                default -> null;
            };
        } catch (Exception e) {
            log.warn("序列化配置快照失败: {}/{}", configType, configId, e);
            return null;
        }
    }
}
