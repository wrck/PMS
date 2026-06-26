# PMS-springmvc 性能优化指南

> 本文档总结 PMS-springmvc 模块的性能优化策略与最佳实践，涵盖数据库、查询、缓存、并发等方面。

---

## 一、数据库性能优化

### 1.1 索引优化

#### 1.1.1 必须建立的索引

| 表 | 索引字段 | 类型 | 原因 |
|----|---------|------|------|
| pm_project | projectCode | 唯一索引 | 业务唯一标识，频繁查询 |
| pm_project_member | projectId | 普通索引 | 按项目查询成员 |
| pm_daily_report | projectId | 普通索引 | 按项目查询日报 |
| pm_dispatch_project_settlement | dispatchId | 普通索引 | 按转包项目查询结算 |
| pm_workflow | dataType, dataId | 组合索引 | 多态关联查询 |
| pm_workflow | procInstId | 普通索引 | 流程回调查询 |

详见 [index-analysis.md](../03-database/index-analysis.md)。

#### 1.1.2 组合索引优化

列表查询常按多字段过滤，建议建立组合索引：

```sql
-- 项目列表查询
CREATE INDEX idx_project_list ON pm_project(disabled, projectType, officeCode);

-- 日报列表查询
CREATE INDEX idx_daily_report_list ON pm_daily_report(disabled, projectType, officeCode);

-- 转包项目列表查询
CREATE INDEX idx_dispatch_list ON pm_dispatch_project_header(disabled, state, officeCode);

-- 工作流待办查询
CREATE INDEX idx_workflow_pending ON pm_workflow(dataType, dataId, status);
```

### 1.2 查询优化

#### 1.2.1 避免 FIND_IN_SET 查询

**问题**：`FIND_IN_SET` 无法使用索引，导致全表扫描。

```java
// ❌ 不推荐：FIND_IN_SET 无法使用索引
WHERE FIND_IN_SET(dr.projectType, '10,afss,afxx')

// ✅ 推荐：应用层解析后使用 IN 查询
String[] types = projectTypes.split(",");
// MyBatis: WHERE dr.projectType IN ('10', 'afss', 'afxx')
```

#### 1.2.2 JSON 字段查询优化

**问题**：`customInfo->"$.field" = ?` 无法使用普通索引。

```sql
-- ❌ 不推荐：JSON 查询无法使用索引
WHERE customInfo->"$.serviceManagerCode" = 'zhangsan'

-- ✅ 推荐：使用 MySQL 8.0 生成列 + 索引
ALTER TABLE pm_daily_report 
ADD COLUMN serviceManagerCode VARCHAR(50) 
GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(customInfo, '$.serviceManagerCode'))) STORED,
ADD INDEX idx_serviceManagerCode (serviceManagerCode);
```

#### 1.2.3 分页查询优化

**问题**：`LIMIT offset, pageSize` 在 offset 较大时性能差。

```java
// ❌ 不推荐：深分页性能差
SELECT * FROM pm_project LIMIT 100000, 20;

// ✅ 推荐：游标分页（基于主键）
SELECT * FROM pm_project WHERE id > #{lastId} ORDER BY id LIMIT 20;

// ✅ 推荐：限制最大页数
if (pageNo > 1000) {
    throw new BusinessException("超过最大页数限制");
}
```

#### 1.2.4 避免 SELECT *

```java
// ❌ 不推荐：查询所有字段
SELECT * FROM pm_project WHERE id = ?

// ✅ 推荐：只查询必要字段
SELECT id, projectCode, projectName, projectState 
FROM pm_project WHERE id = ?
```

### 1.3 批量操作优化

#### 1.3.1 批量插入

```java
// ❌ 不推荐：循环单条插入
for (ProjectMember member : members) {
    projectMemberMapper.insertSelective(member);
}

// ✅ 推荐：批量插入
projectMemberMapper.batchInsert(members);
```

#### 1.3.2 批量更新

```java
// ❌ 不推荐：循环单条更新
for (DispatchSettlement settlement : settlements) {
    dispatchSettlementMapper.updateByPrimaryKeySelective(settlement);
}

// ✅ 推荐：使用 CASE WHEN 批量更新
UPDATE pm_dispatch_project_settlement 
SET paymentTime = CASE id 
    WHEN 1 THEN '2026-01-01' 
    WHEN 2 THEN '2026-01-02' 
END 
WHERE id IN (1, 2);
```

---

## 二、缓存优化

### 2.1 字段配置缓存

`data_field_relation` 表的字段配置数据变化频率低，建议缓存：

```java
@Service
public class DataFieldRelationService {
    
    private static final String CACHE_KEY = "data_field_relation:";
    
    @Cacheable(value = "fieldConfig", key = "#dataName + ':' + #dataType")
    public List<Object> findFieldList(String dataName, String dataType) {
        return dataFieldRelationMapper.selectBySelective(...);
    }
    
    @CacheEvict(value = "fieldConfig", key = "#dataName + ':' + #dataType")
    public void clearCache(String dataName, String dataType) {
        // 清除缓存
    }
}
```

### 2.2 基础数据缓存

`fnd_basic_data`、`fnd_department` 等基础数据表建议缓存：

```java
@Cacheable(value = "basicData", key = "#dataTypeCode")
public List<BasicData> getBasicDataByType(String dataTypeCode) {
    return basicDataMapper.selectByDataTypeCode(dataTypeCode);
}
```

### 2.3 待办任务缓存

工作台待办查询涉及多表 JOIN，建议短期缓存：

```java
@Cacheable(value = "workbench", key = "#userId + ':todo'", unless = "#result == null")
public List<PmWorkFlow> getTodoList(Integer userId) {
    // 查询待办任务
}
```

---

## 三、并发优化

### 3.1 定时任务并发控制

PMS-springmvc 的定时任务通过 Quartz 的 `concurrent=false` 配置防止并发执行：

```xml
<!-- quartz-job.xml -->
<job-detail>
    <job-class>com.dp.plat.pms.springmvc.job.D365DataJob</job-class>
    <concurrent>false</concurrent>  <!-- 不允许并发执行 -->
</job-detail>
```

### 3.2 乐观锁控制

对于高并发更新场景，建议使用乐观锁：

```java
// 实体类添加版本号字段
public class DispatchSettlement {
    private Integer version;  // 乐观锁版本号
}

// MyBatis 更新时检查版本号
<update id="updateByPrimaryKeySelective">
    UPDATE pm_dispatch_project_settlement 
    SET ... , version = version + 1
    WHERE id = #{id} AND version = #{version}
</update>
```

### 3.3 分布式锁

对于跨节点的定时任务，建议使用分布式锁：

```java
public void execute() {
    String lockKey = "D365DataJob:lock";
    try {
        // 尝试获取分布式锁
        if (redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 1, TimeUnit.HOURS)) {
            // 执行同步逻辑
            doSync();
        }
    } finally {
        redisTemplate.delete(lockKey);
    }
}
```

---

## 四、JVM 性能优化

### 4.1 JVM 参数建议

```bash
# 生产环境 JVM 参数
-Xms4g -Xmx4g                          # 堆内存初始和最大值一致，避免动态调整
-XX:NewRatio=2                          # 新生代:老年代 = 1:2
-XX:SurvivorRatio=8                     # Eden:Survivor = 8:1
-XX:+UseG1GC                            # 使用 G1 垃圾收集器
-XX:MaxGCPauseMillis=200                # 最大 GC 停顿时间 200ms
-XX:+HeapDumpOnOutOfMemoryError         # OOM 时生成堆转储
-XX:HeapDumpPath=/var/log/pms/heapdump  # 堆转储路径
```

### 4.2 连接池配置

Druid 连接池配置建议：

```properties
# 初始连接数
druid.initialSize=5
# 最小空闲连接数
druid.minIdle=10
# 最大活跃连接数
druid.maxActive=50
# 获取连接超时时间（毫秒）
druid.maxWait=60000
# 检测间隔（毫秒）
druid.timeBetweenEvictionRunsMillis=60000
# 连接最小空闲时间（毫秒）
druid.minEvictableIdleTimeMillis=300000
# 连接最大存活时间（毫秒）
druid.maxEvictableIdleTimeMillis=900000
# 检测连接是否有效的 SQL
druid.validationQuery=SELECT 1
# 申请连接时检测
druid.testWhileIdle=true
druid.testOnBorrow=false
druid.testOnReturn=false
```

---

## 五、前端性能优化

### 5.1 列表分页

- 列表查询必须使用分页，避免一次性加载大量数据。
- 默认每页 20 条，最大不超过 100 条。
- 使用 DataTables 的服务端分页模式。

### 5.2 懒加载

- 树形结构（如 EHR 部门树）使用懒加载，点击节点时加载子节点。
- 详情页面的关联数据使用 AJAX 异步加载。

### 5.3 静态资源缓存

- CSS、JS、图片等静态资源配置浏览器缓存。
- 使用版本号控制缓存更新：`/resources/js/app.js?v=1.0.0`。

---

## 六、性能监控

### 6.1 慢 SQL 监控

```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 2;  -- 超过 2 秒记录

-- 查看慢查询
SELECT * FROM mysql.slow_log 
WHERE db = 'dppms_d365' 
ORDER BY start_time DESC 
LIMIT 100;
```

### 6.2 Druid 监控

Druid 提供 Web 监控界面，可查看：
- SQL 执行时间统计
- 慢 SQL 列表
- 连接池状态
- SQL 防火墙

配置方式：
```xml
<servlet>
    <servlet-name>DruidStatView</servlet-name>
    <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>DruidStatView</servlet-name>
    <url-pattern>/druid/*</url-pattern>
</servlet-mapping>
```

### 6.3 关键指标监控

| 指标 | 告警阈值 | 说明 |
|------|---------|------|
| 接口响应时间 | > 3 秒 | 单个请求响应时间 |
| 慢 SQL 数量 | > 10/分钟 | 执行时间超过 2 秒的 SQL |
| 数据库连接数 | > 80% 最大连接数 | 连接池使用率 |
| JVM 堆内存使用率 | > 80% | 堆内存占用 |
| GC 频率 | Full GC > 1/小时 | 垃圾回收频率 |
| 定时任务执行时间 | D365DataJob > 30 分钟 | 同步任务耗时 |

---

## 七、性能优化检查清单

### 7.1 开发阶段

- [ ] 查询是否使用分页？
- [ ] 是否避免了 `SELECT *`？
- [ ] 是否避免了 `FIND_IN_SET` 查询？
- [ ] JSON 字段查询是否优化？
- [ ] 批量操作是否使用批量插入/更新？
- [ ] 外键关联字段是否建立索引？
- [ ] 列表查询的 WHERE 条件是否命中索引？

### 7.2 测试阶段

- [ ] 是否进行压力测试？
- [ ] 是否检查慢 SQL 日志？
- [ ] 是否验证连接池配置？
- [ ] 是否检查内存泄漏？

### 7.3 上线阶段

- [ ] 是否配置慢 SQL 监控？
- [ ] 是否配置 Druid 监控？
- [ ] 是否配置 JVM 监控？
- [ ] 是否配置告警通知？
