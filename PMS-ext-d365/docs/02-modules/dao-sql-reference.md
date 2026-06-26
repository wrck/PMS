# DAO/SQL 参考

> 本文档基于实际 Mapper XML 源码编写，说明各 Mapper 的 SQL 映射、动态条件、特殊设计。
> 注意：实际表名带 `dp_erp_` 前缀，早期文档中的 `purchase_order` 等表名有误。

---

## 1. AbstractBaseMapper 通用接口

- **全限定名**：`com.dp.plat.pms.extend.d365.dao.AbstractBaseMapper<T>`
- **注解**：`@Qualifier("d365AbstractBaseMapper")`、`@Repository("d365AbstractBaseMapper")`
- **类型**：`abstract interface`（泛型）

### 1.1 方法清单

| 方法签名 | 说明 |
|----------|------|
| `int deleteByPrimaryKey(Object pk)` | 按主键删除 |
| `int insert(T t)` | 全字段插入 |
| `int insertSelective(T t)` | 选择性插入（仅非空字段） |
| `T selectByPrimaryKey(Object pk)` | 按主键查询 |
| `int updateByPrimaryKeySelective(T t)` | 选择性更新（仅非空字段） |
| `int updateByPrimaryKey(T t)` | 全字段更新 |
| `long countBySelective(T t)` | 按条件计数 |
| `List<T> selectBySelective(T t)` | 按条件查询 |

---

## 2. PurchaseMapper（采购订单头）

- **XML namespace**：`com.dp.plat.pms.extend.d365.dao.PurchaseMapper`
- **对应表**：`dp_erp_purchase_order_header`
- **表别名**：`poh`（purchase order header）
- **resultMap 类型**：`com.dp.plat.pms.extend.d365.entity.Purchase`

### 2.1 SQL 片段

#### Base_Column_List

```sql
`id`, `sourceType`, `sourceId`, `purchPoolId`, `purchId`, `vendAccount`, `purchName`,
`purContract`, `salesContract`, `contractAmount`, `workerPurchPlacer`, `applicant`,
`inventLocationId`, `deliveryDate`, `dlvMode`, `dlvTerm`, `payment`, `paymMode`,
`remark`, `otherSysNum`, `projectName`, `projectProgress`, `subcontractType`, `subcontStartDate`,
`subcontEndDate`, `dataAreaId`, `customInfo`, `createBy`, `createTime`, `updateBy`, `updateTime`
```

#### sql_where_selective（动态条件，表别名 poh）

按实体非空字段生成 WHERE 条件，字符串字段额外判断 `'' != xxx`：

```xml
<if test="purchId != null and '' != purchId">
    poh.`purchId` = #{purchId, jdbcType=VARCHAR} AND
</if>
```

特殊处理：
- `id`、`sourceId`、`deliveryDate`、`subcontStartDate`、`subcontEndDate`、`customInfo`、`createTime`、`updateTime` 仅判断 `!= null`（不判断空字符串）
- 其余 String 字段判断 `!= null and '' != xxx`

#### sql_model_where_selective（带 model 前缀 + 模糊查询）

支持 `model.xxx` 前缀的条件（用于分页查询包装对象），并支持模糊查询：

```xml
<if test="fuzzySearch == true and fuzzy != ''">
    (
        poh.`sourceType` like CONCAT("%", #{fuzzy}, "%") or
        poh.`purchId` like CONCAT("%", #{fuzzy}, "%") or
        ... 22 个字段的 OR 模糊匹配
    )
</if>
```

> ⚠️ 模糊查询字段列表**不包含** `id`、`sourceId`、`deliveryDate`、`subcontStartDate`、`subcontEndDate`、`customInfo`、`createTime`、`updateTime`（数值/日期/JSON 类型不参与模糊）。

#### sql_pageable_limit（分页，已注释）

```xml
<if test="orderBy != null">order by ${orderBy}</if>
<if test="orderBy == null">order by id desc</if>
<if test="start != null and pageSize != -1">limit #{start}, #{pageSize}</if>
```

> ⚠️ `selectBySelectivePageable` 和 `countBySelectivePageable` 在 XML 中**被注释**，未启用。

### 2.2 SQL 语句

| 语句 ID | SQL | 说明 |
|---------|-----|------|
| `selectByPrimaryKey` | `select <Base_Column_List> from dp_erp_purchase_order_header where id = #{id}` | 按主键查询 |
| `deleteByPrimaryKey` | `delete from dp_erp_purchase_order_header where id = #{id}` | 按主键删除 |
| `insert` | 全字段 INSERT + `SELECT LAST_INSERT_ID()` 回填主键 | 全字段插入 |
| `insertSelective` | 动态字段 INSERT（trim + if） + `SELECT LAST_INSERT_ID()` | 选择性插入 |
| `updateByPrimaryKeySelective` | 动态字段 UPDATE（set + if） | 选择性更新 |
| `updateByPrimaryKey` | 全字段 UPDATE | 全字段更新 |
| `selectBySelective` | `select * from dp_erp_purchase_order_header AS poh <where>` | 条件查询 |
| `countBySelective` | `select count(1) from dp_erp_purchase_order_header AS poh <where>` | 条件计数 |

### 2.3 主键回填

所有 INSERT 语句含：

```xml
<selectKey keyProperty="id" order="AFTER" resultType="java.lang.Integer">
    SELECT LAST_INSERT_ID()
</selectKey>
```

`order="AFTER"` 表示 INSERT 后执行，将自增主键回填到实体的 `id` 属性。

---

## 3. PurchaseLineMapper（采购订单行）

- **XML namespace**：`com.dp.plat.pms.extend.d365.dao.PurchaseLineMapper`
- **对应表**：`dp_erp_purchase_order_line`
- **resultMap 类型**：`com.dp.plat.pms.extend.d365.entity.PurchaseLine`

> ⚠️ Mapper 接口泛型为 `model.PurchaseLine`，但 XML resultMap 类型为 `entity.PurchaseLine`。由于 model 版继承 entity 版，MyBatis 可正常映射。

### 3.1 字段列表

```sql
`id`, `headerId`, `purchId`, `lineNum`, `itemId`, `purchQty`, `purchPrice`, `taxItemGroup`,
`inventSerialId`, `inventSiteId`, `inventLocationId`, `wmsLocationId`, `inventTransId`,
`officeCode`, `deliveryDate`, `remark`, `multiDimID`, `investmentProject`, `dimBankAccount`,
`dimCustomer`, `dimVendor`, `dimEmployee`, `dimContract`, `dimDepartment`, `dimBU`,
`dimProductLine`, `dimTerritory`, `dimIndustry`, `dimMultiDimID`, `dataAreaId`, `customInfo`,
`createBy`, `createTime`, `updateBy`, `updateTime`
```

### 3.2 jdbcType 说明

| 字段 | jdbcType | Java 类型 |
|------|----------|-----------|
| id, headerId | INTEGER | Integer |
| purchQty, purchPrice | DECIMAL | BigDecimal |
| deliveryDate | DATE | String（实体） |
| customInfo | JSON | `Map<String, Object>` |
| createTime, updateTime | TIMESTAMP | Date |
| 其余 String 字段 | VARCHAR | String |

### 3.3 SQL 语句

与 PurchaseMapper 结构一致（selectByPrimaryKey、insert、insertSelective、updateByPrimaryKeySelective、updateByPrimaryKey、selectBySelective、countBySelective），表名为 `dp_erp_purchase_order_line`。

---

## 4. PurchaseReceiptMapper（采购收货头）

- **XML namespace**：`com.dp.plat.pms.extend.d365.dao.PurchaseReceiptMapper`
- **对应表**：`dp_erp_purchase_receipt_header`
- **resultMap 类型**：`com.dp.plat.pms.extend.d365.entity.PurchaseReceipt`

### 4.1 字段列表

```sql
`id`, `sourceOrderType`, `sourceOrderId`, `sourceReceiptType`, `sourceReceiptId`,
`purchId`, `deliveryDate`, `documentDate`, `packingSlipId`, `packingSlipRemark`,
`projectProgress`, `dataAreaId`, `customInfo`, `createBy`, `createTime`, `updateBy`, `updateTime`
```

### 4.2 SQL 语句

与 PurchaseMapper 结构一致，表名为 `dp_erp_purchase_receipt_header`。

---

## 5. PurchaseReceiptLineMapper（采购收货行）

- **XML namespace**：`com.dp.plat.pms.extend.d365.dao.PurchaseReceiptLineMapper`
- **对应表**：`dp_erp_purchase_receipt_line`
- **resultMap 类型**：`com.dp.plat.pms.extend.d365.entity.PurchaseReceiptLine`

### 5.1 字段列表

```sql
`id`, `receiptId`, `purchId`, `inventSiteId`, `inventLocationId`, `wmsLocationId`,
`inventTransId`, `lineNum`, `qty`, `price`, `amount`, `dataAreaId`, `customInfo`,
`createBy`, `createTime`, `updateBy`, `updateTime`
```

### 5.2 SQL 语句

与 PurchaseMapper 结构一致，表名为 `dp_erp_purchase_receipt_line`。

---

## 6. SQL 设计特点

### 6.1 动态条件模式

所有 Mapper 采用统一的动态条件模式：

```xml
<sql id="sql_where">
    <where>
        <trim prefixOverrides=" and | or " suffixOverrides=" and | or ">
            <include refid="sql_where_selective" />
        </trim>
    </where>
</sql>
```

- `<where>` 自动处理无条件的 SQL（去掉 WHERE）
- `<trim prefixOverrides=" and | or ">` 去除首个 AND/OR
- `<trim suffixOverrides=" and | or ">` 去除末尾 AND/OR

### 6.2 表别名

仅 `selectBySelective` 和 `countBySelective` 使用表别名 `poh`（purchase order header）。其他语句直接使用表名（无别名）。

> ⚠️ PurchaseLineMapper、PurchaseReceiptMapper、PurchaseReceiptLineMapper 的 selective 条件中也使用 `poh` 别名（复用同一段 SQL 片段命名），但实际表不是 header。这是 MyBatisGenerator 生成时的命名遗留，不影响功能。

### 6.3 字段引用

所有字段名使用反引号 `` ` `` 包裹（如 `` `purchId` ``），避免与 MySQL 保留字冲突。

### 6.4 无 JOIN 查询

> ⚠️ 所有 Mapper 均为**单表 CRUD**，无 JOIN 查询。关联查询（如订单头+行）需在 Service 层多次调用组装。

### 6.5 无批量操作

> ⚠️ 无 `<foreach>` 批量 INSERT/UPDATE。批量插入需循环调用 `insertSelective`。

---

## 7. 索引使用建议

基于 SQL 查询模式，建议的索引（实际索引见 [索引分析](../03-database/index-analysis.md)）：

| 表 | 查询字段 | 建议索引 |
|----|----------|----------|
| dp_erp_purchase_order_header | purchId | 唯一索引（业务唯一键） |
| dp_erp_purchase_order_header | sourceType, sourceId | 联合索引（按源数据查询） |
| dp_erp_purchase_order_line | headerId | 普通索引（关联头表） |
| dp_erp_purchase_order_line | purchId | 普通索引（按订单号查询） |
| dp_erp_purchase_receipt_header | packingSlipId | 唯一索引（业务唯一键） |
| dp_erp_purchase_receipt_header | purchId | 普通索引（按订单号查询） |
| dp_erp_purchase_receipt_line | receiptId | 普通索引（关联头表） |
| dp_erp_purchase_receipt_line | purchId | 普通索引（按订单号查询） |

---

## 8. 相关文档

- [采购订单模块](purchase-order.md)
- [采购收货模块](purchase-receipt.md)
- [数据映射与转换](data-mapping.md)
- [ER 图](../03-database/er-diagram.md)
- [索引分析](../03-database/index-analysis.md)
- [完整数据字典](../03-database/complete-data-dictionary.md)
