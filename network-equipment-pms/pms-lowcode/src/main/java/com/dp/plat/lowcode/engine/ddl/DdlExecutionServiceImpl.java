package com.dp.plat.lowcode.engine.ddl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.engine.DdlGenerator;
import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeField;
import com.dp.plat.lowcode.entity.LowCodeRelation;
import com.dp.plat.lowcode.mapper.LowCodeEntityMapper;
import com.dp.plat.lowcode.mapper.LowCodeFieldMapper;
import com.dp.plat.lowcode.mapper.LowCodeRelationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * DDL 执行服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DdlExecutionServiceImpl implements DdlExecutionService {

    private static final Pattern FORBIDDEN_PATTERN = Pattern.compile(
            "\\b(DROP\\s+TABLE|TRUNCATE|DROP\\s+DATABASE|DROP\\s+SCHEMA)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private final JdbcTemplate jdbcTemplate;
    private final DdlGenerator ddlGenerator;
    private final LowCodeEntityMapper entityMapper;
    private final LowCodeFieldMapper fieldMapper;
    private final LowCodeRelationMapper relationMapper;
    private final DdlExecutionLogMapper executionLogMapper;
    private final DdlBackupMapper backupMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> executeCreate(Long entityId, boolean confirmDrop) {
        LowCodeEntity entity = entityMapper.selectById(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在: " + entityId);
        }
        List<LowCodeField> fields = fieldMapper.selectList(
                new LambdaQueryWrapper<LowCodeField>()
                        .eq(LowCodeField::getEntityId, entityId)
                        .orderByAsc(LowCodeField::getSortOrder));
        List<LowCodeRelation> relations = relationMapper.selectList(
                new LambdaQueryWrapper<LowCodeRelation>()
                        .eq(LowCodeRelation::getFromEntityId, entityId));

        List<String> executedSqls = new ArrayList<>();

        // 1. 主表 CREATE TABLE
        if (!tableExists(entity.getTableName())) {
            String createSql = ddlGenerator.generateCreateTable(entity, fields, relations);
            validateBeforeExecution(createSql);
            executeAndLog(entity, createSql, "CREATE");
            executedSqls.add(createSql);
        } else {
            log.info("表 {} 已存在，跳过 CREATE", entity.getTableName());
        }

        // 2. 多对多中间表
        for (LowCodeRelation rel : relations) {
            if ("MANY_TO_MANY".equals(rel.getRelationType()) && rel.getJunctionTable() != null) {
                if (!tableExists(rel.getJunctionTable())) {
                    String junctionSql = ddlGenerator.generateJunctionTable(
                            rel.getJunctionTable(),
                            entity.getTableName(),
                            entity.getTableName(),
                            rel.getFromFieldName(),
                            rel.getToFieldName() != null ? rel.getToFieldName() : "to_id",
                            rel.getOnDelete());
                    validateBeforeExecution(junctionSql);
                    executeAndLog(entity, junctionSql, "CREATE");
                    executedSqls.add(junctionSql);
                }
            }
        }

        return executedSqls;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> executeAlter(Long entityId, boolean confirmDrop) {
        LowCodeEntity entity = entityMapper.selectById(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在: " + entityId);
        }
        String tableName = entity.getTableName();
        if (!tableExists(tableName)) {
            // 表不存在则走 CREATE 流程
            return executeCreate(entityId, confirmDrop);
        }

        // 备份当前表结构
        backupTableStructure(entityId, tableName, "ALTER");

        List<LowCodeField> fields = fieldMapper.selectList(
                new LambdaQueryWrapper<LowCodeField>()
                        .eq(LowCodeField::getEntityId, entityId)
                        .orderByAsc(LowCodeField::getSortOrder));

        List<String> executedSqls = new ArrayList<>();

        // 查询当前表已有列
        List<Map<String, Object>> existingColumns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                tableName);
        List<String> existingColumnNames = new ArrayList<>();
        for (Map<String, Object> col : existingColumns) {
            existingColumnNames.add((String) col.get("COLUMN_NAME"));
        }

        // 对比字段差异
        List<String> designColumnNames = new ArrayList<>();
        for (LowCodeField field : fields) {
            designColumnNames.add(field.getName());
            if (!existingColumnNames.contains(field.getName())) {
                // 新增列
                String addSql = ddlGenerator.generateAddColumn(tableName, field);
                validateBeforeExecution(addSql);
                executeAndLog(entity, addSql, "ALTER");
                executedSqls.add(addSql);
            } else {
                // 修改列（类型/长度变化）
                String alterSql = ddlGenerator.generateAlterColumn(tableName, field);
                validateBeforeExecution(alterSql);
                executeAndLog(entity, alterSql, "ALTER");
                executedSqls.add(alterSql);
            }
        }

        // 删除多余列（需 confirmDrop）
        if (confirmDrop) {
            for (String existingCol : existingColumnNames) {
                if (!"id".equals(existingCol) && !designColumnNames.contains(existingCol)
                        && !"create_time".equals(existingCol) && !"update_time".equals(existingCol)
                        && !"create_by".equals(existingCol) && !"update_by".equals(existingCol)
                        && !"deleted".equals(existingCol)) {
                    // 备份列数据
                    backupColumnData(entityId, tableName, existingCol);
                    String dropSql = ddlGenerator.generateDropColumn(tableName, existingCol);
                    validateBeforeExecution(dropSql);
                    executeAndLog(entity, dropSql, "DROP_COLUMN");
                    executedSqls.add(dropSql);
                }
            }
        } else if (existingColumnNames.stream().anyMatch(c ->
                !"id".equals(c) && !"create_time".equals(c) && !"update_time".equals(c)
                && !"create_by".equals(c) && !"update_by".equals(c) && !"deleted".equals(c)
                && !designColumnNames.contains(c))) {
            throw new DdlSecurityException("检测到需删除的列，请确认 confirmDrop=true 后重试");
        }

        return executedSqls;
    }

    @Override
    public void validateBeforeExecution(String ddlSql) {
        if (ddlSql == null || ddlSql.isBlank()) {
            throw new DdlSecurityException("DDL SQL 不能为空");
        }
        if (FORBIDDEN_PATTERN.matcher(ddlSql).find()) {
            throw new DdlSecurityException("DDL 包含禁止语句（DROP TABLE/TRUNCATE/DROP DATABASE）: " + ddlSql);
        }
    }

    @Override
    public void backupTableStructure(Long entityId, String tableName, String backupType) {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW CREATE TABLE `" + tableName + "`");
            if (!result.isEmpty()) {
                String createSql = (String) result.get(0).get("Create Table");
                DdlBackup backup = DdlBackup.builder()
                        .entityId(entityId)
                        .entityCode(getEntityCode(entityId))
                        .tableName(tableName)
                        .backupType(backupType)
                        .backupSql(createSql)
                        .build();
                backupMapper.insert(backup);
                log.info("已备份表 {} 结构", tableName);
            }
        } catch (Exception e) {
            log.error("备份表 {} 结构失败", tableName, e);
            throw new RuntimeException("备份表结构失败: " + tableName, e);
        }
    }

    @Override
    public boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * 备份列数据（DROP COLUMN 前调用）
     */
    private void backupColumnData(Long entityId, String tableName, String columnName) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT `id`, `" + columnName + "` FROM `" + tableName + "` WHERE `" + columnName + "` IS NOT NULL");
            String jsonData = objectMapper.writeValueAsString(rows);
            DdlBackup backup = DdlBackup.builder()
                    .entityId(entityId)
                    .entityCode(getEntityCode(entityId))
                    .tableName(tableName)
                    .backupType("DROP_COLUMN")
                    .backupData(jsonData)
                    .build();
            backupMapper.insert(backup);
            log.info("已备份表 {} 列 {} 数据（{} 行）", tableName, columnName, rows.size());
        } catch (Exception e) {
            log.error("备份列数据失败: {}.{}", tableName, columnName, e);
            throw new RuntimeException("备份列数据失败: " + tableName + "." + columnName, e);
        }
    }

    /**
     * 执行 DDL 并记录日志
     */
    private void executeAndLog(LowCodeEntity entity, String ddlSql, String executionType) {
        DdlExecutionLog logEntry = DdlExecutionLog.builder()
                .entityId(entity.getId())
                .entityCode(entity.getCode())
                .tableName(entity.getTableName())
                .executionType(executionType)
                .ddlSql(ddlSql)
                .build();
        try {
            jdbcTemplate.execute(ddlSql);
            logEntry.setStatus("SUCCESS");
            executionLogMapper.insert(logEntry);
            log.info("DDL 执行成功: {}", ddlSql);
        } catch (Exception e) {
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
            executionLogMapper.insert(logEntry);
            log.error("DDL 执行失败: {}", ddlSql, e);
            throw new RuntimeException("DDL 执行失败: " + ddlSql, e);
        }
    }

    private String getEntityCode(Long entityId) {
        LowCodeEntity entity = entityMapper.selectById(entityId);
        return entity != null ? entity.getCode() : "UNKNOWN";
    }
}
