# 索引分析

> 本文档基于 Mapper XML 的查询模式分析索引需求。
> 注意：实际表名带 `dp_erp_` 前缀。数据库实际索引以生产环境为准，本文档为基于查询模式的分析建议。

---

## 1. 现有索引（基于数据字典文档）

### 1.1 dp_erp_purchase_order_header

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| PRIMARY | id | 主键 | 主键 |
| uk_purch_id | purchId | 唯一索引 | 采购订单号唯一 |

### 1.2 dp_erp_purchase_order_line

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| PRIMARY | id | 主键 | 主键 |
| idx_header_id | headerId | 普通索引 | 关联订单头 |
| idx_purch_id | purchId | 普通索引 | 按订单号查询 |

### 1.3 dp_erp_purchase_receipt_header

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| PRIMARY | id | 主键 | 主键 |
| uk_packing_slip_id | packingSlipId | 唯一索引 | 收货单号唯一 |
| idx_purch_id | purchId | 普通索引 | 按订单号查询 |

### 1.4 dp_erp_purchase_receipt_line

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| PRIMARY | id | 主键 | 主键 |
| idx_receipt_id | receiptId | 普通索引 | 关联收货头 |
| idx_purch_id | purchId | 普通索引 | 按订单号查询 |

---

## 2. 查询模式分析

### 2.1 按主键查询（selectByPrimaryKey）

所有表均通过 `where id = #{id}` 查询，使用 PRIMARY 索引。

### 2.2 按条件查询（selectBySelective / countBySelective）

`sql_where_selective` 动态生成 WHERE，可能涉及任意非空字段组合。常见查询模式：

| 查询场景 | 表 | 查询字段 | 频率 |
|----------|-----|----------|------|
| 按采购订单号查询 | header | purchId | 高 |
| 按源数据查询 | header | sourceType + sourceId | 中 |
| 按外部系统编号查询 | header | otherSysNum | 中（幂等检查） |
| 按订单号查询行 | line | purchId | 高 |
| 按头ID查询行 | line | headerId | 高 |
| 按批次号查询行 | line | inventTransId | 中 |
| 按收货单号查询 | receipt_header | packingSlipId | 中 |
| 按订单号查询收货 | receipt_header | purchId | 中 |
| 按源收货数据查询 | receipt_header | sourceReceiptType + sourceReceiptId | 中 |
| 按收货头ID查询行 | receipt_line | receiptId | 高 |
| 按批次号查询收货行 | receipt_line | inventTransId | 中 |

### 2.3 模糊查询（fuzzySearch）

`sql_model_where_selective` 支持 22 个字段的 OR 模糊查询。此类查询**无法使用索引**，全表扫描。

---

## 3. 索引使用评估

### 3.1 高效索引（查询命中）

| 索引 | 命中查询 | 评估 |
|------|----------|------|
| uk_purch_id (header.purchId) | 按采购订单号查询 | ✅ 高效，唯一索引 |
| idx_header_id (line.headerId) | 按头ID查询行 | ✅ 高效 |
| idx_purch_id (line.purchId) | 按订单号查询行 | ✅ 高效 |
| uk_packing_slip_id (receipt_header.packingSlipId) | 按收货单号查询 | ✅ 高效，唯一索引 |
| idx_receipt_id (receipt_line.receiptId) | 按收货头ID查询行 | ✅ 高效 |
| idx_purch_id (receipt_line.purchId) | 按订单号查询收货行 | ✅ 高效 |

### 3.2 缺失索引（建议补充）

| 表 | 建议索引 | 查询场景 | 优先级 |
|----|----------|----------|--------|
| dp_erp_purchase_order_header | idx_source (sourceType, sourceId) | 按源数据查询（转包/外派单关联） | 高 |
| dp_erp_purchase_order_header | idx_other_sys_num (otherSysNum) | 幂等检查（外部系统编号） | 中 |
| dp_erp_purchase_order_line | idx_invent_trans_id (inventTransId) | 按批次号查询（收货匹配） | 中 |
| dp_erp_purchase_receipt_header | idx_source_receipt (sourceReceiptType, sourceReceiptId) | 按源收货数据查询 | 中 |
| dp_erp_purchase_receipt_line | idx_invent_trans_id (inventTransId) | 按批次号查询 | 中 |

### 3.3 索引 DDL 建议

```sql
-- dp_erp_purchase_order_header
ALTER TABLE dp_erp_purchase_order_header
    ADD INDEX idx_source (sourceType, sourceId);
ALTER TABLE dp_erp_purchase_order_header
    ADD INDEX idx_other_sys_num (otherSysNum);

-- dp_erp_purchase_order_line
ALTER TABLE dp_erp_purchase_order_line
    ADD INDEX idx_invent_trans_id (inventTransId);

-- dp_erp_purchase_receipt_header
ALTER TABLE dp_erp_purchase_receipt_header
    ADD INDEX idx_source_receipt (sourceReceiptType, sourceReceiptId);

-- dp_erp_purchase_receipt_line
ALTER TABLE dp_erp_purchase_receipt_line
    ADD INDEX idx_invent_trans_id (inventTransId);
```

---

## 4. 索引与写入权衡

### 4.1 写入模式

PMS-ext-d365 的写入以 `insertSelective` 为主（推送 D365 后持久化回执），更新较少。

| 表 | 写入频率 | 更新频率 | 索引影响 |
|----|----------|----------|----------|
| dp_erp_purchase_order_header | 中（每次推送） | 低 | 索引维护成本低 |
| dp_erp_purchase_order_line | 中（每次推送，行数多） | 低 | 索引维护成本中等 |
| dp_erp_purchase_receipt_header | 中 | 低 | 索引维护成本低 |
| dp_erp_purchase_receipt_line | 中 | 低 | 索引维护成本中等 |

### 4.2 建议

- 写入频率不高，可适当增加索引以优化查询；
- `customInfo`（JSON 字段）不建议建索引（MySQL JSON 索引支持有限）；
- 模糊查询字段无索引优化空间，建议限制模糊查询范围或改用全文索引。

---

## 5. 索引覆盖分析

### 5.1 selectBySelective 的索引覆盖

`selectBySelective` 使用 `select *`，无法被索引完全覆盖（需回表）。如需优化高频查询，可考虑：

```sql
-- 针对按 purchId 查询的高频场景，创建覆盖索引
ALTER TABLE dp_erp_purchase_order_header
    ADD INDEX idx_purch_id_cover (purchId, id, sourceType, sourceId, vendAccount, purchName);
```

> ⚠️ 覆盖索引字段过多会导致索引体积膨胀，需权衡。当前模块查询频率不高，暂无必要。

### 5.2 countBySelective 的优化

`countBySelective` 使用 `count(1)`，InnoDB 仍需扫描索引。对于高频计数场景，可考虑：
- 缓存计数结果；
- 使用近似计数（`SHOW TABLE STATUS`）。

---

## 6. 索引维护建议

1. **定期分析索引使用率**：通过 `SHOW INDEX FROM <table>` 和慢查询日志分析；
2. **监控慢查询**：开启 MySQL 慢查询日志，关注 `selectBySelective` 的动态条件组合；
3. **避免过度索引**：每个索引增加写入开销，建议单表索引不超过 5-6 个；
4. **JSON 字段查询**：如需查询 `customInfo` 内的 key，考虑生成列 + 索引：
   ```sql
   ALTER TABLE dp_erp_purchase_order_header
       ADD COLUMN purchId_in_customInfo VARCHAR(50)
       GENERATED ALWAYS (JSON_UNQUOTE(JSON_EXTRACT(customInfo, '$.purchId'))) STORED;
   ALTER TABLE dp_erp_purchase_order_header
       ADD INDEX idx_custom_purch_id (purchId_in_customInfo);
   ```

---

## 7. 相关文档

- [ER 图](er-diagram.md)
- [完整数据字典](complete-data-dictionary.md)
- [数据库概览](database-overview.md)
- [DAO/SQL 参考](../02-modules/dao-sql-reference.md)
- [性能优化](../05-standards/performance-optimization.md)
