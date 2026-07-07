package com.dp.plat.lowcode.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.dto.EntityDesignDTO;
import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeField;
import com.dp.plat.lowcode.service.LowCodeEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 动态实体数据访问服务。
 *
 * <p>基于 JdbcTemplate 直接操作动态生成的物理表，
 * 提供 CRUD 接口。运行时根据实体定义动态构建 SQL。</p>
 */
@Service
@RequiredArgsConstructor
public class DynamicEntityDataService {

    private final LowCodeEntityService entityService;
    private final JdbcTemplate jdbcTemplate;

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

        // 过滤合法字段
        Set<String> validFields = design.getFields().stream()
                .map(LowCodeField::getName)
                .collect(Collectors.toSet());
        Map<String, Object> filtered = data.entrySet().stream()
                .filter(e -> validFields.contains(e.getKey()))
                .collect(Collectors.toLinkedHashMap(Map.Entry::getKey, Map.Entry::getValue));

        String columns = filtered.keySet().stream()
                .map(f -> "`" + f + "`")
                .collect(Collectors.joining(", "));
        String placeholders = filtered.values().stream()
                .map(v -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";
        jdbcTemplate.update(sql, filtered.values().toArray());

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    /**
     * 更新记录。
     */
    public void update(String entityCode, Long id, Map<String, Object> data) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String tableName = design.getEntity().getTableName();

        Set<String> validFields = design.getFields().stream()
                .map(LowCodeField::getName)
                .filter(f -> !"id".equals(f))
                .collect(Collectors.toSet());
        Map<String, Object> filtered = data.entrySet().stream()
                .filter(e -> validFields.contains(e.getKey()))
                .collect(Collectors.toLinkedHashMap(Map.Entry::getKey, Map.Entry::getValue));

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
    }

    /**
     * 删除记录。
     */
    public void delete(String entityCode, Long id) {
        EntityDesignDTO design = getDesignByCode(entityCode);
        String sql = "DELETE FROM `" + design.getEntity().getTableName() + "` WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private EntityDesignDTO getDesignByCode(String entityCode) {
        LowCodeEntity entity = entityService.getOne(new LambdaQueryWrapper<LowCodeEntity>()
                .eq(LowCodeEntity::getCode, entityCode));
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在: " + entityCode);
        }
        return entityService.getDesign(entity.getId());
    }
}
