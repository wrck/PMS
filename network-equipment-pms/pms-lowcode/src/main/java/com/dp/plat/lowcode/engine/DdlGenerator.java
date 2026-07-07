package com.dp.plat.lowcode.engine;

import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeField;
import com.dp.plat.lowcode.entity.LowCodeRelation;
import java.util.List;

/**
 * DDL 生成器接口。
 *
 * <p>抽象 DDL 生成逻辑，支持不同数据库方言。
 * 本期仅实现 {@link MySQLDdlGenerator}，预留 PostgreSQL 扩展点。</p>
 */
public interface DdlGenerator {

    /**
     * 生成 CREATE TABLE 语句（含字段、主键、索引、外键约束）。
     *
     * @param entity    实体定义
     * @param fields    字段列表
     * @param relations 关联列表（可为空）
     * @return 完整 CREATE TABLE SQL
     */
    String generateCreateTable(LowCodeEntity entity, List<LowCodeField> fields, List<LowCodeRelation> relations);

    /**
     * 生成 ALTER TABLE ADD COLUMN 语句。
     *
     * @param tableName 物理表名
     * @param field     新增字段
     * @return ALTER TABLE SQL
     */
    String generateAddColumn(String tableName, LowCodeField field);

    /**
     * 生成 ALTER TABLE DROP COLUMN 语句。
     *
     * @param tableName  物理表名
     * @param columnName 列名
     * @return ALTER TABLE SQL
     */
    String generateDropColumn(String tableName, String columnName);

    /**
     * 生成 CREATE INDEX 语句。
     *
     * @param tableName 物理表名
     * @param indexName 索引名
     * @param columnNames 索引列名列表
     * @param isUnique   是否唯一索引
     * @return CREATE INDEX SQL
     */
    String generateCreateIndex(String tableName, String indexName, List<String> columnNames, boolean isUnique);

    /**
     * 生成多对多中间表 CREATE TABLE 语句。
     *
     * @param junctionTable 中间表名
     * @param fromTableName 源表名
     * @param toTableName   目标表名
     * @param fromFieldName 源外键字段名
     * @param toFieldName   目标外键字段名
     * @param onDelete      级联删除策略
     * @return CREATE TABLE SQL
     */
    String generateJunctionTable(String junctionTable, String fromTableName, String toTableName,
                                  String fromFieldName, String toFieldName, String onDelete);
}
