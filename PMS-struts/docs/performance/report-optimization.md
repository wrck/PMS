# PMS-struts 报表查询性能优化建议

## 当前问题分析

### 1. SQL 查询模式
- 使用了多次 LEFT JOIN 和子查询
- 查询结果集可能很大
- 没有使用分页

### 2. 主要查询
- `query_assigned_rate` - 查询项目指派率
- `query_trace_rate` - 查询跟踪率
- `query_reportline_assigned_info` - 查询指派率趋势
- `query_reportline_trace_info` - 查询跟踪率趋势

## 优化建议

### 1. 添加数据库索引

```sql
-- 项目表主要查询字段索引
CREATE INDEX idx_project_state ON pm_project_header(projectState);
CREATE INDEX idx_project_column001 ON pm_project_header(column001);
CREATE INDEX idx_project_effective ON pm_project_header(effectiveTo);
CREATE INDEX idx_project_contract ON pm_project_header(contractNo);

-- 项目成员表索引
CREATE INDEX idx_member_project ON pm_project_member(projectId);
CREATE INDEX idx_member_role ON pm_project_member(memberRole);
CREATE INDEX idx_member_effective ON pm_project_member(effectiveTo);
CREATE INDEX idx_member_code ON pm_project_member(memberCode);

-- 项目状态表索引
CREATE INDEX idx_state_project ON pm_project_state(projectId);
CREATE INDEX idx_state_plan ON pm_project_state(projectPlanState);
```

### 2. SQL 优化

#### 优化查询指派率
```sql
-- 优化前：两次查询
SELECT COUNT(*) AS num, column001 AS officeCode 
FROM pm_project_header 
WHERE projectState IN (30,31,32) AND column001 IS NOT NULL AND effectiveTo IS NULL 
GROUP BY column001

-- 优化后：合并为一次查询
SELECT 
    COUNT(*) AS totalNum,
    SUM(CASE WHEN t2.memberCode IS NOT NULL THEN 1 ELSE 0 END) AS assignedNum,
    t1.column001 AS officeCode
FROM pm_project_header t1
LEFT JOIN pm_project_member t2 ON t1.projectId = t2.projectId 
    AND t2.effectiveTo IS NULL AND t2.memberRole = '30' AND t2.fromFlag = 1
WHERE t1.projectState IN (30,31,32) 
    AND t1.column001 IS NOT NULL 
    AND t1.effectiveTo IS NULL
GROUP BY t1.column001
```

### 3. 缓存策略

在 ReportServiceImpl 中添加缓存：

```java
// 使用 ConcurrentHashMap 缓存报表数据
private ConcurrentHashMap<String, Object> reportCache = new ConcurrentHashMap<>();
private long cacheExpireTime = 5 * 60 * 1000; // 5分钟

public Map<String, Double> queryAssignedRateWithCache(ReportQueryParam queryParam) {
    String cacheKey = "assigned_rate_" + queryParam.hashCode();
    Object cached = reportCache.get(cacheKey);
    if (cached != null) {
        return (Map<String, Double>) cached;
    }
    
    Map<String, Double> result = queryAssignedRate(queryParam);
    reportCache.put(cacheKey, result);
    
    // 设置缓存过期
    scheduledExecutor.schedule(() -> reportCache.remove(cacheKey), 
        cacheExpireTime, TimeUnit.MILLISECONDS);
    
    return result;
}
```

### 4. 分页查询

对于大数据量报表，建议添加分页：

```java
// 在 ReportQueryParam 中添加分页参数
private int pageNum = 1;
private int pageSize = 50;

// SQL 中添加 LIMIT
SELECT ... LIMIT #{offset}, #{pageSize}
```

### 5. 异步查询

对于耗时的报表查询，建议使用异步方式：

```java
// 使用 CompletableFuture 异步查询
public CompletableFuture<Map<String, Double>> queryAssignedRateAsync(ReportQueryParam queryParam) {
    return CompletableFuture.supplyAsync(() -> queryAssignedRate(queryParam));
}
```

## 实施优先级

1. **P0** - 添加数据库索引（立即执行，效果最明显）
2. **P1** - 优化 SQL 查询（合并重复查询）
3. **P2** - 添加缓存策略
4. **P3** - 分页查询和异步查询

## 验证方法

1. 执行 `EXPLAIN` 分析 SQL 执行计划
2. 监控查询执行时间
3. 比较优化前后的性能指标
