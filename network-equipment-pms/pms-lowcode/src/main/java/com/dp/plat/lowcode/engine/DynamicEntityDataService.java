package com.dp.plat.lowcode.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.dto.EntityDesignDTO;
import com.dp.plat.lowcode.engine.trigger.CrudTriggerExecutor;
import com.dp.plat.lowcode.engine.trigger.LowCodeTrigger;
import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeField;
import com.dp.plat.lowcode.service.LowCodeEntityService;
import com.dp.plat.lowcode.service.LowCodeTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 动态实体数据访问服务。
 *
 * <p>基于 JdbcTemplate 直接操作动态生成的物理表，
 * 提供 CRUD 接口。运行时根据实体定义动态构建 SQL。</p>
 *
 * <p>CRUD 操作前后会触发类型为 CRUD 的低代码触发器（借鉴 Zoho/Budibase）：
 * BEFORE 触发器异常将阻断主操作，AFTER 触发器异常仅记录日志。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicEntityDataService {

    private final LowCodeEntityService entityService;
    private final JdbcTemplate jdbcTemplate;
    private final LowCodeTriggerService triggerService;
    private final CrudTriggerExecutor crudTriggerExecutor;

    /**
     * 查询列表（分页）。
     */
    public Map<String, Object> list(String entityCode, int page, int size,
                                     Map<String, Object> filters) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String tableName = design.getEntity().getTableName();

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (filters != null) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                where.append(" AND `").append(entry.getKey()).append("` = ?");
                params.add(entry.getValue());
            }
        }

        String countSql = "SELECT COUNT(*) FROM `" + tableName + "`" + where;
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());

        int offset = (page - 1) * size;
        String listSql = "SELECT * FROM `" + tableName + "`" + where
                + " LIMIT " + size + " OFFSET " + offset;
        List<Map<String, Object>> records = jdbcTemplate.queryForList(listSql, params.toArray());

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /**
     * 查询单条记录。
     */
    public Map<String, Object> getById(String entityCode, Long id) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String sql = "SELECT * FROM `" + design.getEntity().getTableName()
                + "` WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    /**
     * 新增记录。
     */
    public Long create(String entityCode, Map<String, Object> data) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String tableName = design.getEntity().getTableName();

        // BEFORE_CREATE 触发器（异常则阻断）
        fireBeforeCrudTriggers(entityCode, "CREATE",
                buildContext(entityCode, "CREATE", "BEFORE", null, data));

        // 过滤合法字段
        Set<String> validFields = design.getFields().stream()
                .map(LowCodeField::getName)
                .collect(Collectors.toSet());
        Map<String, Object> filtered = data.entrySet().stream()
                .filter(e -> validFields.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        String columns = filtered.keySet().stream()
                .map(f -> "`" + f + "`")
                .collect(Collectors.joining(", "));
        String placeholders = filtered.values().stream()
                .map(v -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";
        jdbcTemplate.update(sql, filtered.values().toArray());

        Long createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        // AFTER_CREATE 触发器（异常不阻断）
        fireAfterCrudTriggers(entityCode, "CREATE",
                buildContext(entityCode, "CREATE", "AFTER", createdId, data));

        return createdId;
    }

    /**
     * 更新记录。
     */
    public void update(String entityCode, Long id, Map<String, Object> data) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String tableName = design.getEntity().getTableName();

        // BEFORE_UPDATE 触发器（异常则阻断）
        fireBeforeCrudTriggers(entityCode, "UPDATE",
                buildContext(entityCode, "UPDATE", "BEFORE", id, data));

        Set<String> validFields = design.getFields().stream()
                .map(LowCodeField::getName)
                .filter(f -> !"id".equals(f))
                .collect(Collectors.toSet());
        Map<String, Object> filtered = data.entrySet().stream()
                .filter(e -> validFields.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        if (filtered.isEmpty()) {
            return;
        }

        String setClause = filtered.keySet().stream()
                .map(f -> "`" + f + "` = ?")
                .collect(Collectors.joining(", "));
        String sql = "UPDATE `" + tableName + "` SET " + setClause + " WHERE id = ?";

        List<Object> params = new ArrayList<>(filtered.values());
        params.add(id);
        jdbcTemplate.update(sql, params.toArray());

        // AFTER_UPDATE 触发器（异常不阻断）
        fireAfterCrudTriggers(entityCode, "UPDATE",
                buildContext(entityCode, "UPDATE", "AFTER", id, data));
    }

    /**
     * 删除记录。
     */
    public void delete(String entityCode, Long id) {
        EntityDesignDTO design = getDesignByCode(entityCode);

        // BEFORE_DELETE 触发器（异常则阻断）
        fireBeforeCrudTriggers(entityCode, "DELETE",
                buildContext(entityCode, "DELETE", "BEFORE", id, null));

        String sql = "DELETE FROM `" + design.getEntity().getTableName() + "` WHERE id = ?";
        jdbcTemplate.update(sql, id);

        // AFTER_DELETE 触发器（异常不阻断）
        fireAfterCrudTriggers(entityCode, "DELETE",
                buildContext(entityCode, "DELETE", "AFTER", id, null));
    }

    private EntityDesignDTO getDesignByCode(String entityCode) {
        LowCodeEntity entity = entityService.getOne(new LambdaQueryWrapper<LowCodeEntity>()
                .eq(LowCodeEntity::getCode, entityCode));
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在: " + entityCode);
        }
        return entityService.getDesign(entity.getId());
    }

    // ==================== CRUD 触发器钩子 ====================

    private Map<String, Object> buildContext(String entityCode, String operation, String timing,
                                             Long id, Map<String, Object> data) {
        Map<String, Object> context = new HashMap<>();
        context.put("entityCode", entityCode);
        context.put("operation", operation);
        context.put("timing", timing);
        context.put("eventType", timing + "_" + operation);
        if (id != null) {
            context.put("id", id);
        }
        if (data != null) {
            context.put("data", data);
        }
        return context;
    }

    /**
     * 触发 BEFORE CRUD 触发器：异常向上抛出以阻断主 CRUD 操作。
     */
    private void fireBeforeCrudTriggers(String entityCode, String operation, Map<String, Object> context) {
        for (LowCodeTrigger trigger : listMatchingCrudTriggers(entityCode, operation, "BEFORE")) {
            // BEFORE 触发器异常直接抛出，阻断主操作
            triggerService.executeTrigger(trigger.getCode(), context);
        }
    }

    /**
     * 触发 AFTER CRUD 触发器：异常仅记录日志，不阻断主 CRUD 操作。
     */
    private void fireAfterCrudTriggers(String entityCode, String operation, Map<String, Object> context) {
        for (LowCodeTrigger trigger : listMatchingCrudTriggers(entityCode, operation, "AFTER")) {
            try {
                triggerService.executeTrigger(trigger.getCode(), context);
            } catch (Exception e) {
                log.error("AFTER CRUD 触发器执行失败，已忽略: trigger={}, entityCode={}, operation={}",
                        trigger.getCode(), entityCode, operation, e);
            }
        }
    }

    /**
     * 查询匹配的 CRUD 触发器列表。触发器基础设施异常不阻断主 CRUD（返回空列表）。
     */
    private List<LowCodeTrigger> listMatchingCrudTriggers(String entityCode, String operation, String timing) {
        try {
            List<LowCodeTrigger> triggers = triggerService.list(new LambdaQueryWrapper<LowCodeTrigger>()
                    .eq(LowCodeTrigger::getType, "CRUD")
                    .eq(LowCodeTrigger::getStatus, "ACTIVE"));
            List<LowCodeTrigger> matched = new ArrayList<>();
            for (LowCodeTrigger trigger : triggers) {
                if (crudTriggerExecutor.matches(trigger, entityCode, operation, timing)) {
                    matched.add(trigger);
                }
            }
            return matched;
        } catch (Exception e) {
            log.error("查询 CRUD 触发器失败，已忽略: entityCode={}, operation={}", entityCode, operation, e);
            return List.of();
        }
    }
}
