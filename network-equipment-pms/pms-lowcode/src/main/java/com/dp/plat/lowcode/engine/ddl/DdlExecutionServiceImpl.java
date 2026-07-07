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
            // 构建 toEntityId → 物理表名 映射，供外键目标表名推导使用
            Map<Long, String> entityIdToTableName = buildEntityIdToTableName(relations, entityId);
            String createSql = ddlGenerator.generateCreateTable(entity, fields, relations, entityIdToTableName);
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

        // 查询当前表已有列的完整元数据（用于真实字段 Diff，避免无条件 MODIFY COLUMN）
        List<Map<String, Object>> existingColumns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, IS_NULLABLE "
                        + "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                tableName);
        List<String> existingColumnNames = new ArrayList<>();
        Map<String, Map<String, Object>> existingColumnMeta = new HashMap<>();
        for (Map<String, Object> col : existingColumns) {
            String colName = (String) col.get("COLUMN_NAME");
            existingColumnNames.add(colName);
            existingColumnMeta.put(colName, col);
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
                // 修改列：仅当 fieldType/length/scale/nullable 实际变化时才生成 MODIFY COLUMN
                Map<String, Object> colMeta = existingColumnMeta.get(field.getName());
                if (colMeta != null && !fieldNeedsAlter(field, colMeta)) {
                    log.debug("字段 {} 类型/长度/精度/可空性无变化，跳过 MODIFY COLUMN", field.getName());
                    continue;
                }
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

    /**
     * 比较设计字段与数据库现有列元数据，判断是否需要执行 MODIFY COLUMN。
     * 仅比较 fieldType/length/scale/nullable 四个维度；任一不同即返回 true。
     */
    private boolean fieldNeedsAlter(LowCodeField field, Map<String, Object> colMeta) {
        String dbDataType = asString(colMeta.get("DATA_TYPE"));
        String fieldType = field.getFieldType();

        // 类型对比
        String expectedDataType = mapFieldTypeToDb(fieldType);
        if (expectedDataType != null && !expectedDataType.equalsIgnoreCase(dbDataType)) {
            return true;
        }

        // 长度对比（STRING 看 CHARACTER_MAXIMUM_LENGTH；DECIMAL 看 NUMERIC_PRECISION/NUMERIC_SCALE）
        if ("STRING".equals(fieldType)) {
            Integer expectedLen = field.getLength() != null ? field.getLength() : 255;
            Integer dbLen = asInteger(colMeta.get("CHARACTER_MAXIMUM_LENGTH"));
            if (!expectedLen.equals(dbLen)) {
                return true;
            }
        } else if ("DECIMAL".equals(fieldType)) {
            Integer expectedPrecision = field.getLength() != null ? field.getLength() : 10;
            Integer expectedScale = field.getScale() != null ? field.getScale() : 2;
            Integer dbPrecision = asInteger(colMeta.get("NUMERIC_PRECISION"));
            Integer dbScale = asInteger(colMeta.get("NUMERIC_SCALE"));
            if (!expectedPrecision.equals(dbPrecision) || !expectedScale.equals(dbScale)) {
                return true;
            }
        }

        // 可空性对比（IS_NULLABLE: YES/NO）
        Integer expectedNullable = field.getNullable() != null ? field.getNullable() : 1;
        String dbNullable = asString(colMeta.get("IS_NULLABLE"));
        boolean dbIsNullable = "YES".equalsIgnoreCase(dbNullable);
        if ((expectedNullable == 1) != dbIsNullable) {
            return true;
        }

        return false;
    }

    /**
     * 低代码字段类型 → MySQL INFORMATION_SCHEMA.DATA_TYPE 小写映射。
     */
    private String mapFieldTypeToDb(String fieldType) {
        if (fieldType == null) {
            return null;
        }
        return switch (fieldType) {
            case "STRING" -> "varchar";
            case "INTEGER" -> "int";
            case "LONG" -> "bigint";
            case "DECIMAL" -> "decimal";
            case "BOOLEAN" -> "tinyint";
            case "DATE" -> "date";
            case "DATETIME" -> "datetime";
            case "TEXT" -> "text";
            default -> null;
        };
    }

    private String asString(Object obj) {
        return obj == null ? null : String.valueOf(obj);
    }

    private Integer asInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 根据关联列表构建 toEntityId → 物理表名 映射，用于外键目标表名推导。
     * 自关联（fromEntityId == toEntityId）无需查表，跳过。
     */
    private Map<Long, String> buildEntityIdToTableName(List<LowCodeRelation> relations, Long selfEntityId) {
        Map<Long, String> result = new HashMap<>();
        if (relations == null || relations.isEmpty()) {
            return result;
        }
        List<Long> targetIds = new ArrayList<>();
        for (LowCodeRelation rel : relations) {
            if (rel.getToEntityId() == null) {
                continue;
            }
            if (rel.getFromEntityId() != null && rel.getFromEntityId().equals(rel.getToEntityId())) {
                // 自关联：由生成器用当前表名处理
                continue;
            }
            if (!targetIds.contains(rel.getToEntityId())) {
                targetIds.add(rel.getToEntityId());
            }
        }
        if (targetIds.isEmpty()) {
            return result;
        }
        List<LowCodeEntity> targets = entityMapper.selectBatchIds(targetIds);
        if (targets != null) {
            for (LowCodeEntity t : targets) {
                if (t.getTableName() != null) {
                    result.put(t.getId(), t.getTableName());
                }
            }
        }
        return result;
    }
}
