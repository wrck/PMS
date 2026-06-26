# 性能优化技巧文档

本文档汇总 PMS 系统各层面的性能优化策略，包括 SQL 优化、连接池调优、iBatis 缓存、DisplayTag 分页、前端性能和 JVM 调优。

---

## 1. SQL 优化策略

### 1.1 项目列表查询 10+ 表 JOIN 优化

**问题**：项目列表查询涉及 `pm_project_header`、`pm_project_group_relationship`、`pm_project_group`、`pm_project_contract`、`pm_project_member`、`pm_presales_project_header`、`fnd_user_info`、`fnd_basic_data`（多次）、`fnd_department` 等 10+ 张表的 LEFT JOIN，查询耗时严重。

**优化方案**：

**方案一：分步查询替代大 JOIN**

将一次大查询拆分为多次小查询，先查主表获取 ID 列表，再批量查关联数据：

```sql
-- 第一步：查主表（带分页）
SELECT projectId, projectCode, projectName
FROM pm_project_header
WHERE effectiveTo IS NULL
AND projectState IN (...)
ORDER BY createTime DESC
LIMIT 0, 15;

-- 第二步：根据 projectId 批量查关联数据
SELECT * FROM pm_project_member
WHERE projectId IN (1, 2, 3, ...)
AND effectiveTo IS NULL;
```

**方案二：使用临时表**

```sql
CREATE TEMPORARY TABLE temp_project_list AS
SELECT projectId, projectCode, projectName
FROM pm_project_header
WHERE effectiveTo IS NULL
AND projectState IN (...)
ORDER BY createTime DESC
LIMIT 0, 15;

SELECT t.*, m.memberName, bd.basicDataName
FROM temp_project_list t
LEFT JOIN pm_project_member m ON m.projectId = t.projectId AND m.effectiveTo IS NULL
LEFT JOIN fnd_basic_data bd ON bd.basicDataId = t.type AND bd.dataTypeCode = 'maintenanceType';
```

**方案三：冗余字段减少 JOIN**

在 `pm_project_header` 中冗余存储常用查询字段（如 `officeName`、`projectStateName`），避免每次查询都 JOIN 基础数据表。

### 1.2 FIND_IN_SET 替代方案

**问题**：`FIND_IN_SET(wcs.officeCode, #areaPower#)` 无法使用索引，导致全表扫描。

```xml
<isNotEmpty prepend="OR" property="areaPower">
    FIND_IN_SET(wcs.officeCode, #areaPower#)
</isNotEmpty>
```

**优化方案**：

**方案一：拆分为关联表**

将 `areapower` 字段从逗号分隔的字符串拆分为独立的关联表：

```sql
CREATE TABLE fnd_user_area_power (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    officeCode VARCHAR(50) NOT NULL,
    effectiveFrom DATETIME,
    effectiveTo DATETIME,
    INDEX idx_user_office (userId, officeCode)
);
```

查询改为 JOIN：

```sql
SELECT p.* FROM pm_project_header p
JOIN fnd_user_area_power uap ON p.officeCode = uap.officeCode
WHERE uap.userId = #userId# AND uap.effectiveTo IS NULL;
```

**方案二：应用层过滤**

先查询用户有权限的 officeCode 列表，再使用 IN 查询：

```java
String[] areaPowerArr = userContext.getUser().getAreapower().split(",");
// 使用 iBatis iterate 构建 IN 条件
```

```xml
<iterate property="areaPowerList" open="AND officeCode IN (" close=")" conjunction=",">
    #areaPowerList[]#
</iterate>
```

### 1.3 effectiveFrom / effectiveTo 索引利用

**问题**：`effectiveTo IS NULL` 条件在大量数据下查询缓慢。

**优化方案**：

1. **建立专用索引**：

```sql
ALTER TABLE pm_project_header ADD INDEX idx_effective_state (effectiveTo, projectState);
ALTER TABLE fnd_user_info ADD INDEX idx_user_effective (effectiveFrom, effectiveTo);
ALTER TABLE fnd_user_menus ADD INDEX idx_user_menu_effective (fnd_user_id, effectiveFrom, effectiveTo);
```

2. **统一使用 `effectiveTo IS NULL` 而非 `effectiveTo > NOW()`**：

```sql
-- 推荐（索引友好）
SELECT * FROM pm_project_header WHERE effectiveTo IS NULL;

-- 不推荐（索引利用率低）
SELECT * FROM pm_project_header WHERE effectiveTo > NOW() OR effectiveTo IS NULL;
```

3. **覆盖索引 + 延迟关联**：

```sql
-- 先通过覆盖索引获取 ID
SELECT projectId FROM pm_project_header
WHERE effectiveTo IS NULL AND projectState = 1
ORDER BY createTime DESC LIMIT 0, 15;

-- 再根据 ID 查完整数据
SELECT h.* FROM pm_project_header h
JOIN (SELECT projectId FROM pm_project_header
      WHERE effectiveTo IS NULL AND projectState = 1
      ORDER BY createTime DESC LIMIT 0, 15) t
ON h.projectId = t.projectId;
```

### 1.4 分页查询优化

**问题**：深分页（如 `LIMIT 10000, 15`）查询缓慢。

**优化方案**：

**方案一：游标分页（推荐）**

使用上一页最后一条记录的 ID 作为游标：

```sql
SELECT * FROM pm_project_header
WHERE effectiveTo IS NULL AND projectId > #lastId#
ORDER BY projectId ASC LIMIT 15;
```

**方案二：延迟关联**

```sql
SELECT h.* FROM pm_project_header h
INNER JOIN (
    SELECT projectId FROM pm_project_header
    WHERE effectiveTo IS NULL
    ORDER BY createTime DESC
    LIMIT 10000, 15
) t ON h.projectId = t.projectId;
```

**方案三：禁止深分页**

限制最大页码，超过阈值时提示用户缩小查询范围。

---

## 2. 连接池调优

### 2.1 DBCP 参数调整

**当前配置**：

| 参数 | 当前值 | 建议范围 | 说明 |
|------|--------|---------|------|
| `initialSize` | 2 | 5~10 | 启动时预创建连接，减少冷启动延迟 |
| `maxActive` | 300 | 100~500 | 最大活跃连接数，需小于数据库 `max_connections` |
| `maxIdle` | 50 | 50~100 | 最大空闲连接数，建议不超过 `maxActive` 的 1/3 |
| `minIdle` | 3 | 5~10 | 最小空闲连接数，保证突发请求有可用连接 |
| `maxWait` | 60000 | 30000~60000 | 获取连接超时时间（毫秒） |
| `removeAbandoned` | true(开发)/false(生产) | - | 生产环境建议关闭，避免误回收 |
| `removeAbandonedTimeout` | 180(开发)/1800(生产) | - | 连接超时回收时间（秒） |
| `logAbandoned` | true | true | 记录连接泄漏堆栈 |
| `testOnBorrow` | true | true | 获取连接时验证有效性 |
| `validationQuery` | select 1 | select 1 | 连接验证 SQL |

**调优建议**：

1. **开发环境**：`initialSize=5`, `maxActive=50`, `removeAbandoned=true`
2. **测试环境**：`initialSize=5`, `maxActive=100`, `removeAbandoned=true`
3. **生产环境**：`initialSize=10`, `maxActive=300`, `removeAbandoned=false`

```properties
main.database.initialSize=10
main.database.maxActive=300
main.database.maxIdle=100
main.database.minIdle=10
main.database.maxWait=60000
main.database.removeAbandoned=false
main.database.removeAbandonedTimeout=1800
main.database.logAbandoned=true
```

### 2.2 外部数据源连接策略

**当前问题**：外部数据源使用 `DriverManagerDataSource`（无连接池），每次请求创建新连接，频繁创建/销毁连接开销大。

**优化方案**：

对外部数据源引入 DBCP 连接池：

```xml
<bean id="dataSourceSAP" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${sap.database.driverClassName}" />
    <property name="url" value="${sap.database.url}" />
    <property name="username" value="${sap.database.username}" />
    <property name="password" value="${sap.database.password}" />
    <property name="initialSize" value="1" />
    <property name="maxActive" value="10" />
    <property name="maxIdle" value="5" />
    <property name="minIdle" value="1" />
    <property name="maxWait" value="30000" />
    <property name="testOnBorrow" value="true" />
    <property name="validationQuery" value="select 1" />
</bean>
```

> **注意**：外部数据源连接池参数应远小于主库，因为外部数据源仅用于定时同步任务。

---

## 3. iBatis 缓存配置

### 3.1 LRUCacheController（CopyLRU）

PMS 自定义了 `LRUCacheController`（别名 `CopyLRU`），继承 iBatis 的 `LruCacheController`，增加了深拷贝功能：

- **getObject**：从缓存取值时进行深拷贝，避免外部修改影响缓存
- **putObject**：存入缓存时进行深拷贝，避免原始对象修改影响缓存
- **clone 策略**：优先使用 `ObjectUtils.clone()`，失败时使用 Fastjson 序列化/反序列化

```xml
<typeAlias type="com.dp.plat.ibatis.cache.LRUCacheController" alias="CopyLRU" />
```

### 3.2 sqlMapClient 查询缓存

**缓存配置示例**：

```xml
<cacheModel type="CopyLRU" id="userCache" readOnly="true">
    <flushInterval hours="1" />
    <flushOnExecute statement="insert-user-object" />
    <flushOnExecute statement="update-user-object" />
    <flushOnExecute statement="update-user" />
    <flushOnExecute statement="update-pwd-byusername" />
    <flushOnExecute statement="refreshCacheData" />
    <property name="size" value="50" />
</cacheModel>

<select id="queryUserInfo" parameterClass="string"
    resultClass="com.dp.plat.data.bean.User" cacheModel="userCache">
    SELECT * FROM fnd_user_info WHERE username = #username#
</select>
```

**缓存使用原则**：

| 场景 | 是否使用缓存 | 说明 |
|------|------------|------|
| 用户信息查询 | 是 | 变更频率低，1 小时刷新 |
| 基础数据查询 | 是 | 变更频率低，建议增加缓存 |
| 项目列表查询 | 否 | 数据实时性要求高 |
| 统计报表查询 | 视情况 | 可设置短时间缓存（5~10 分钟） |
| 外部数据源查询 | 否 | 数据由同步任务更新 |

### 3.3 缓存刷新策略

**定时刷新**：

```xml
<flushInterval hours="1" />
```

**操作触发刷新**：

```xml
<flushOnExecute statement="update-user-object" />
```

**手动刷新**：

通过执行 `refreshCacheData` SQL 触发所有关联缓存刷新：

```sql
UPDATE fnd_sys_arg SET var = NOW() WHERE code = 'sys.cache.latest.refreshTime';
```

**iBatis 全局配置**：

```xml
<settings cacheModelsEnabled="true" enhancementEnabled="true"
    lazyLoadingEnabled="true" maxRequests="32"
    maxSessions="10" maxTransactions="5" />
```

| 参数 | 值 | 说明 |
|------|-----|------|
| `cacheModelsEnabled` | true | 启用缓存 |
| `enhancementEnabled` | true | 启用字节码增强（提升延迟加载性能） |
| `lazyLoadingEnabled` | true | 启用延迟加载 |
| `maxRequests` | 32 | 最大并发请求数 |
| `maxSessions` | 10 | 最大会话数 |
| `maxTransactions` | 5 | 最大事务数 |

---

## 4. DisplayTag 分页优化

### 4.1 外部分页 vs 内存分页

**内存分页（当前方式）**：

DisplayTag 默认将全部数据加载到内存，在内存中进行分页：

```jsp
<display:table name="projectList" id="project" pagesize="15">
```

**问题**：当数据量超过 1000 条时，每次请求都加载全量数据，内存和数据库压力巨大。

**外部分页（推荐方式）**：

由 DAO 层完成分页查询，仅返回当前页数据：

```java
public List<Project> findProjectList(ProjectQuery query) {
    int offset = (query.getPage() - 1) * query.getPageSize();
    return getSqlMapClientTemplate().queryForList("findProjectList", query, offset, query.getPageSize());
}
```

```jsp
<display:table name="projectList" id="project" pagesize="15"
    partialList="true" size="totalRows">
```

**关键属性**：

| 属性 | 说明 |
|------|------|
| `partialList="true"` | 启用外部分页模式 |
| `size="totalRows"` | 总记录数（由 Action 提供） |

**Action 层配合**：

```java
public String list() {
    int totalRows = projectService.findProjectCount(query);
    request.setAttribute("totalRows", totalRows);
    projectList = projectService.findProjectList(query);
    return SUCCESS;
}
```

### 4.2 ExportExcel 大数据量处理

**问题**：DisplayTag 的 Excel 导出功能将全量数据加载到内存，大数据量时导致 OOM。

**优化方案**：

**方案一：限制导出行数**

```jsp
<display:setProperty name="export.excel.maxRows" value="10000" />
```

**方案二：自定义导出使用流式写入**

绕过 DisplayTag 的导出机制，直接使用 Apache POI 的 SXSSFWorkbook（流式写入）：

```java
public void exportExcel(HttpServletResponse response) {
    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
    Sheet sheet = workbook.createSheet("项目列表");

    int page = 1;
    List<Project> data;
    while (!(data = projectService.findProjectList(query, page, 500)).isEmpty()) {
        for (Project project : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(project.getProjectCode());
            row.createCell(1).setCellValue(project.getProjectName());
        }
        page++;
    }

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=projects.xlsx");
    workbook.write(response.getOutputStream());
    workbook.dispose();
}
```

**方案三：异步导出**

大数据量导出改为异步任务，生成完成后通知用户下载：

1. 用户点击导出按钮，创建异步任务
2. 后台分批查询数据，生成 Excel 文件
3. 完成后发送邮件通知用户下载

---

## 5. 前端性能

### 5.1 ECharts 按需加载

**问题**：ECharts 完整包体积较大（~3MB），全部加载影响页面首屏时间。

**优化方案**：

使用 ECharts 按需引入，仅加载需要的图表类型：

```javascript
var echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/bar');
require('echarts/lib/chart/line');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
```

或使用自定义构建工具生成精简版 ECharts。

### 5.2 jQuery 选择器优化

**优化原则**：

1. **使用 ID 选择器**：`$("#myId")` 是最快的选择器
2. **避免通用选择器**：`$(".myClass")` 比 `$("div.myClass")` 慢
3. **缓存 jQuery 对象**：

```javascript
var $list = $("#projectList");
$list.find(".row").each(function() { ... });
```

4. **使用事件委托**：

```javascript
$("#projectList").on("click", ".delete-btn", function() {
    var id = $(this).data("id");
    deleteProject(id);
});
```

5. **减少 DOM 操作**：批量操作时使用文档片段

```javascript
var fragment = document.createDocumentFragment();
for (var i = 0; i < data.length; i++) {
    var row = $("<tr><td>" + data[i].name + "</td></tr>")[0];
    fragment.appendChild(row);
}
$("#projectTable tbody").append(fragment);
```

### 5.3 CSS/JS 压缩

**优化方案**：

1. **使用 `<dp:script>` 和 `<dp:link>` 标签**：支持 `integrity`（SRI）和 `nonce` 属性，便于实施安全策略

```jsp
<dp:script src="js/app.min.js" integrity="sha384-xxx" />
<dp:link href="css/app.min.css" rel="stylesheet" integrity="sha384-xxx" />
```

2. **构建时压缩**：使用构建工具对 CSS/JS 进行压缩合并
3. **启用 GZIP 压缩**：在 Tomcat 的 `server.xml` 中配置

```xml
<Connector port="8080" compression="on"
    compressableMimeType="text/html,text/xml,text/css,application/javascript"
    compressionMinSize="2048" />
```

4. **静态资源缓存**：配置长过期时间

```xml
<filter-mapping>
    <filter-name>expiresFilter</filter-name>
    <url-pattern>*.css</url-pattern>
    <url-pattern>*.js</url-pattern>
</filter-mapping>
```

---

## 6. JVM 调优建议

### 6.1 堆内存配置

**推荐配置**：

```bash
JAVA_OPTS="-Xms2g -Xmx2g -Xmn512m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
```

| 参数 | 推荐值 | 说明 |
|------|--------|------|
| `-Xms` | 2g | 初始堆内存，与 `-Xmx` 相同避免动态扩容 |
| `-Xmx` | 2g | 最大堆内存 |
| `-Xmn` | 512m | 年轻代大小 |
| `-XX:MetaspaceSize` | 256m | 元空间初始大小 |
| `-XX:MaxMetaspaceSize` | 512m | 元空间最大大小 |

**内存分配比例**：

```
堆内存 (2GB)
├── 年轻代 (512MB, 25%)
│   ├── Eden (384MB)
│   ├── Survivor 0 (64MB)
│   └── Survivor 1 (64MB)
└── 老年代 (1536MB, 75%)
```

### 6.2 GC 策略

**推荐 JDK 8 配置**：

```bash
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=70"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSClassUnloadingEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
```

| 参数 | 说明 |
|------|------|
| `-XX:+UseParNewGC` | 年轻代使用 ParNew 收集器 |
| `-XX:+UseConcMarkSweepGC` | 老年代使用 CMS 收集器（低延迟） |
| `-XX:CMSInitiatingOccupancyFraction=70` | 老年代使用率 70% 时触发 CMS |
| `-XX:+CMSParallelRemarkEnabled` | 并行 Remark 阶段 |
| `-XX:+CMSClassUnloadingEnabled` | CMS 支持类卸载 |

**推荐 JDK 11+ 配置**：

```bash
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
JAVA_OPTS="$JAVA_OPTS -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -XX:G1HeapRegionSize=8m"
```

| 参数 | 说明 |
|------|------|
| `-XX:+UseG1GC` | 使用 G1 收集器 |
| `-XX:MaxGCPauseMillis=200` | 目标最大 GC 停顿时间 200ms |
| `-XX:G1HeapRegionSize=8m` | G1 Region 大小 |

### 6.3 GC 日志与监控

**开启 GC 日志**：

```bash
# JDK 8
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="$JAVA_OPTS -Xloggc:/var/log/pms/gc.log"
JAVA_OPTS="$JAVA_OPTS -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M"

# JDK 11+
JAVA_OPTS="$JAVA_OPTS -Xlog:gc*:file=/var/log/pms/gc.log:time,uptime:filecount=5,filesize=20M"
```

**OOM 时自动 Dump**：

```bash
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=/var/log/pms/heapdump.hprof"
```

### 6.4 常见性能问题诊断

**问题一：频繁 Full GC**

- 检查是否有大对象直接进入老年代
- 检查 `MetaspaceSize` 是否过小，导致 Metaspace GC
- 检查是否有内存泄漏（老年代持续增长）

**问题二：GC 停顿过长**

- 切换到 CMS 或 G1 收集器
- 调整 `-XX:CMSInitiatingOccupancyFraction` 提前触发 GC
- 减小堆内存，缩短单次 GC 时间

**问题三：CPU 使用率过高**

- 使用 `jstack` 查看线程堆栈，定位热点代码
- 检查是否有死循环或频繁的正则表达式编译
- 检查 Fastjson 的深拷贝是否成为性能瓶颈
