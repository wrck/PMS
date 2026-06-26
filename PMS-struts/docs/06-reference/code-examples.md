# PMS 典型代码示例文档

本文档从 PMS 项目源码中提取典型代码模式，每个示例附带实现逻辑说明与应用场景描述。

---

## 1. BaseAction 模板方法使用示例

### 1.1 继承 BaseAction

`BaseAction` 是所有 Action 的基类，继承自 Struts2 的 `ActionSupport`，并实现了 `ServletContextAware`、`ServletRequestAware`、`ServletResponseAware` 接口，自动注入 Servlet 相关对象。

```java
// BaseAction 提供的核心能力：
// 1. 自动注入 HttpServletRequest / HttpServletResponse / ServletContext
// 2. 统一错误消息处理（addFieldError / addActionError）
// 3. 默认 start() 方法返回 INPUT 视图
public class BaseAction extends ActionSupport
        implements ServletContextAware, ServletRequestAware, ServletResponseAware {

    // 默认显示方法，返回 INPUT 视图
    public String start() {
        return INPUT;
    }

    // 将 Service 层的错误/警告消息同步到 Action 的 FieldError 中
    protected void setErrmsg(BaseService service) {
        for (String msg : service.getErrmsg()) {
            this.addFieldError("errmsg", msg);
        }
        for (String warnMsg : service.getWarnmsg()) {
            this.addFieldError("warnmsg", warnMsg);
        }
        service.clearErrMsg();
    }
}
```

### 1.2 典型 Action 实现——ProjectAction

```java
// ProjectAction 继承 BaseAction，同时实现 Preparable 接口
// Preparable 的 prepareExecute() 方法在 execute() 之前执行，用于初始化下拉列表等基础数据
public class ProjectAction extends BaseAction implements Preparable {

    private ProjectService projectService;       // Spring 注入
    private BasicDataService basicDataService;   // Spring 注入
    private User user;                           // 当前登录用户
    private Project project;                     // 页面绑定对象

    // Preparable 回调：在 execute() 前初始化基础数据
    public void prepareExecute() {
        departmentList = departmentManageService.queryDepartments();
        projectTypeList = basicDataService.queryBasicDataBeans("02");
        deliverStateList = basicDataService.queryBasicDataBeans("20");
    }

    // 获取当前用户
    public String execute() throws Exception {
        user = UserContext.getUserContext().getUser();  // 从 Session 作用域的 UserContext 获取

        // 根据角色判断查询范围
        if (user.isHasAnyRole(ROLE_ENGINEEMANAGER, ROLE_ADMIN)) {
            projectlist = projectService.queryProjectList(project, displayParam);
        } else {
            projectlist = projectService.queryProjectListByPower(project, displayParam);
        }
        return SUCCESS;
    }

    // 创建项目
    public String insertProject() {
        user = UserContext.getUserContext().getUser();
        if (checkProjectNull(project)) {
            // 首次进入页面，展示表单
            return INPUT;
        } else {
            // 提交表单，执行保存
            projectService.insertProject(project);
            return SUCCESS;
        }
    }
}
```

### 1.3 分页参数处理

```java
// DisplayParam 封装分页参数，由前端传入当前页码和每页条数
// 在 Action 中初始化并传递给 Service/DAO 层
private DisplayParam displayParam;

private void initProject() {
    if (displayParam == null) {
        displayParam = new DisplayParam();
    }
    // 初始化排序字段映射
    Map<String, String> colMap = new HashMap<String, String>();
    colMap.put("7", "orderCreateTime");
    displayParam.setColmap(colMap);
    displayParam.getParam();  // 解码前端传入的查询参数
}
```

### 1.4 结果映射

```xml
<!-- struts.xml 中的结果映射配置 -->
<action name="ProjectManage" class="projectAction" method="execute">
    <result name="success">/jsp/project/projectList.jsp</result>
    <result name="input">/jsp/project/projectCreate.jsp</result>
    <result name="error">/jsp/error.jsp</result>
</action>
```

**应用场景**：所有业务 Action 均需继承 `BaseAction`，通过 `UserContext.getUserContext().getUser()` 获取当前登录用户，利用 `Preparable` 接口的 `prepareXxx()` 方法预加载页面所需的下拉列表数据。

---

## 2. Service 事务代理配置示例

### 2.1 双 Bean 配置模式

PMS 采用 `*Service` + `*ServiceAgent` 双 Bean 配置模式实现声明式事务管理：

```xml
<!-- applicationContext-service.xml -->

<!-- 事务代理基类，定义事务传播属性 -->
<bean id="transactionBaseService" abstract="true"
    class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager">
        <ref bean="transactionManager"/>
    </property>
    <property name="transactionAttributes">
        <props>
            <prop key="insert*">PROPAGATION_REQUIRED</prop>
            <prop key="update*">PROPAGATION_REQUIRED</prop>
            <prop key="delete*">PROPAGATION_REQUIRED</prop>
            <prop key="add*">PROPAGATION_REQUIRED</prop>
            <prop key="save*">PROPAGATION_REQUIRED</prop>
            <prop key="do*">PROPAGATION_REQUIRED</prop>
            <prop key="keep*">PROPAGATION_REQUIRED</prop>
            <prop key="start*">PROPAGATION_REQUIRED</prop>
            <prop key="submit*">PROPAGATION_REQUIRED</prop>
            <prop key="parse*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>

<!-- 项目管理 Service 配置 -->
<!-- 1. 实际业务 Bean：projectService -->
<bean id="projectService" class="com.dp.plat.service.ProjectServiceImpl"
    lazy-init="false" parent="baseServce">
    <property name="projectDao" ref="projectDao"></property>
    <property name="callBackService" ref="callBackService"></property>
</bean>

<!-- 2. 事务代理 Bean：projectServiceAgent -->
<bean id="projectServiceAgent" parent="transactionBaseService">
    <property name="target" ref="projectService" />
</bean>
```

### 2.2 Action 层引用代理 Bean

```xml
<!-- applicationContext-action.xml -->
<!-- Action 中注入的是 *ServiceAgent（事务代理），而非 *Service（原始 Bean） -->
<bean id="projectAction" class="com.dp.plat.action.ProjectAction" scope="prototype">
    <property name="projectService" ref="projectServiceAgent"></property>
    <property name="basicDataService" ref="basicDataServiceAgent"></property>
    <property name="userManageService" ref="userManageServiceAgent"></property>
</bean>
```

**实现逻辑**：
- `*Service` Bean 是实际的业务实现类，不含事务逻辑
- `*ServiceAgent` Bean 通过 `TransactionProxyFactoryBean` 对 `*Service` 进行 AOP 代理，自动为匹配 `insert*`/`update*`/`delete*` 等方法名的方法添加事务
- Action 层注入 `*ServiceAgent`，确保所有写操作都在事务中执行
- Service 内部互调时注入 `*Service`（原始 Bean），避免事务嵌套问题

**应用场景**：所有需要事务管理的 Service 均采用此双 Bean 模式，是 PMS 项目最核心的配置模式之一。

---

## 3. iBatis SQL 映射示例

### 3.1 resultMap 定义

```xml
<!-- 定义 HashMap 类型的 resultMap，用于动态列查询 -->
<resultMap class="java.util.HashMap" id="tableInfoMap">
    <result property="tableColumnName" column="tableColumnName" javaType="java.lang.String"/>
    <result property="tableColumnType" column="tableColumnType" javaType="java.lang.String"/>
</resultMap>
```

### 3.2 动态 SQL——isNotEmpty / isEqual

```xml
<!-- 项目查询中的动态条件拼接 -->
<sql id="sql_query_project_by_power_table">
    <isNotEqual compareValue="10" property="projectState">
        <!-- projectState 不等于 10 时，查询完整字段列表 -->
        SELECT ph.projectId, ph.projectCode, ph.projectName, ...
    </isNotEqual>

    <!-- 维保信息条件查询：仅在 checkWarranty=true 时关联维保表 -->
    <isEqual property="checkWarranty" compareValue="true">
        wcs.warrantyStatus, wcs.warrantyStatusName,
        wcs.warrantyGrade, wcs.warrantyGradeName,
        wcs.wafService, wcs.wafServiceName
    </isEqual>
    <isNotEqual property="checkWarranty" compareValue="true">
        NULL AS warrantyStatus, NULL AS warrantyStatusName,
        NULL AS warrantyGrade, NULL AS warrantyGradeName,
        NULL AS wafService, NULL AS wafServiceName
    </isNotEqual>

    FROM pm_project_header ph
        LEFT JOIN pm_project_group_relationship pr
            ON ph.projectCode = pr.projectCode

    <!-- 产品型号过滤：仅在 itemModel 非空时关联临时表 -->
    <isNotEmpty property="itemModel">
        INNER JOIN temp_tb_projectId_filter_itemModel
            ON filteredProjectId = ph.projectId
    </isNotEmpty>

    <!-- 序列号过滤：仅在 barCode 非空时关联发货条码表 -->
    <isNotEmpty property="barCode">
        INNER JOIN (
            SELECT c.contract_code AS contractNo, profitCenter
            FROM fb_shipment_barcode sb
            LEFT JOIN fb_shipment s ON ...
        ) bc ON bc.contractNo = pc.contractNo
    </isNotEmpty>
</sql>
```

### 3.3 iterate 集合遍历

```xml
<!-- 遍历项目状态列表，构建 IN 条件 -->
<iterate property="projectStateList" open="AND ph.projectState IN ("
    close=")" conjunction=",">
    #projectStateList[]#
</iterate>

<!-- 遍历办事处编码列表 -->
<iterate property="officeCodeList" open="AND ph.column001 IN ("
    close=")" conjunction=",">
    #officeCodeList[]#
</iterate>
```

### 3.4 分页查询

```xml
<!-- 分页查询：利用 MySQL 的 LIMIT 子句 -->
<select id="query_project_bypower" parameterClass="project"
    resultMap="projectResultMap">
    SELECT * FROM (
        SELECT tmp.*, @rownum := @rownum + 1 AS rownum
        FROM (SELECT ... FROM pm_project_header ph ...) tmp,
             (SELECT @rownum := 0) r
    ) ranked
    WHERE rownum BETWEEN #displayParam.startRow#
                     AND #displayParam.endRow#
</select>

<!-- 总数查询 -->
<select id="query_project_bypower_count" parameterClass="project"
    resultClass="int">
    SELECT COUNT(*) FROM pm_project_header ph ...
</select>
```

### 3.5 关联查询

```xml
<!-- 多表关联查询项目列表：关联合同表、成员表、状态表等 -->
SELECT
    ph.projectId, ph.projectCode, ph.projectName,
    GROUP_CONCAT(DISTINCT pc.contractNo SEPARATOR ',') AS contractNo,
    pm2.memberCode AS serviceManager, pm2.memberName AS serviceManagerName,
    pm3.memberCode AS projectManager, pm3.memberName AS projectManagerName,
    shipmentState.basicDataName AS shipmentStateName,
    planState.basicDataName AS planStateName
FROM pm_project_header ph
    LEFT JOIN pm_project_group_relationship pr ON ph.projectCode = pr.projectCode
    LEFT JOIN pm_project_contract pc ON pr.projectGroupCode = pc.projectGroupCode
    LEFT JOIN pm_project_member pm2 ON ph.projectId = pm2.projectId
        AND pm2.memberRole = '20' AND pm2.effectiveTo IS NULL
    LEFT JOIN pm_project_member pm3 ON ph.projectId = pm3.projectId
        AND pm3.memberRole = '30' AND pm3.effectiveTo IS NULL
    LEFT JOIN pm_project_state state ON ph.projectId = state.projectId
```

**应用场景**：iBatis SQL 映射文件位于 `config-ibaits/` 目录下，所有 DAO 层通过 `SqlMapClientTemplate` 调用映射语句。

---

## 4. Activiti 流程启动示例

### 4.1 流程引擎配置

```xml
<!-- activiti-context.xml -->
<bean id="processEngineConfiguration"
    class="org.activiti.spring.SpringProcessEngineConfiguration">
    <property name="dataSource" ref="dataSource" />
    <property name="transactionManager" ref="transactionManager" />
    <property name="databaseSchemaUpdate" value="true" />
</bean>

<bean id="processEngine" class="org.activiti.spring.ProcessEngineFactoryBean">
    <property name="processEngineConfiguration" ref="processEngineConfiguration"/>
</bean>

<!-- 从流程引擎获取各 Service -->
<bean id="runtimeService" factory-bean="processEngine"
    factory-method="getRuntimeService"/>
<bean id="taskService" factory-bean="processEngine"
    factory-method="getTaskService"/>
```

### 4.2 启动流程实例

```java
// WorkFlowServiceImpl.startProcess()
@Override
public ProcessInstance startProcess(String processDefinitionKey,
        String businessKey, Map<String, Object> vars) {
    // 设置流程发起人
    String username = UserContext.getUserContext().getUser().getUsername();
    Authentication.setAuthenticatedUserId(username);

    // 通过 RuntimeService 启动流程实例
    ProcessInstance pi = runtimeService.startProcessInstanceByKey(
        processDefinitionKey, businessKey, vars);

    Authentication.setAuthenticatedUserId(null);
    return pi;
}
```

### 4.3 流程变量设置与任务完成

```java
// CallBackServiceImpl.startCallBackFlow() —— 回访流程启动
@Override
public void startCallBackFlow(CallBack callBack) {
    // 1. 保存申请内容
    int callBackId = callBackDao.insertCallBack(callBack);

    // 2. 构建流程变量
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("programManager", getLoginName());
    vars.put("callbackManager", "callbackRole");
    vars.put("projectId", callBack.getProjectId());

    // 3. 拼接 businessKey（格式：类名.主键ID.项目ID）
    String key = callBack.getClass().getSimpleName();
    String businessKey = key + "." + callBackId + "." + callBack.getProjectId();

    // 4. 启动流程
    ProcessInstance process = workFlowService.startProcess(key, businessKey, vars);
    String instId = process.getId();

    // 5. 回写流程实例 ID 到业务表
    callBackDao.updateCallBackInstId(callBackId, instId);

    // 6. 办理当前任务（自动完成发起申请节点）
    Task task = workFlowService.queryTaskByBussinessKeyUser(businessKey, getLoginName());
    vars.clear();
    workFlowService.doSelfTask(task, instId, "发起申请", vars);

    // 7. 添加审批意见
    workFlowService.addSelfActComment(callBackId, key,
        task.getId(), instId, ActivityMessage.COMMENT_APPLY, null);
}
```

### 4.4 任务提交与审批

```java
// WorkFlowServiceImpl.submitTask()
@Override
public void submitTask(WorkflowCommonParam param) {
    String taskId = param.getTaskId();
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("outcome", param.getOutcome());       // 审批结果
    vars.put("issamecustomer", param.getIssamecustomer());
    vars.put("needleader", param.getNeedleader());

    // 添加审批意见并完成任务
    taskService.addComment(taskId, null, param.getComment());
    taskService.setVariablesLocal(taskId, vars);
    taskService.complete(taskId, vars);

    // 同一办理人自动流转处理
    List<Task> nextTaskList = this.getTaskByInstId(pi.getId());
    for (Task t : nextTaskList) {
        if (username.equals(t.getAssignee())) {
            // 与上环节办理人相同，系统默认办理
            taskId = t.getId();
            vars.put("outcome", "1");
            taskService.addComment(taskId, null, "与上环节办理人相同，系统默认办理");
            taskService.complete(taskId, vars);
        }
    }
}
```

**应用场景**：项目闭环流程、回访流程、售前测试流程、项目转包审批流程等均通过 Activiti 工作流引擎驱动。

---

## 5. 定时任务配置示例

### 5.1 Quartz JobBean 配置

```xml
<!-- beans-quartz.xml -->

<!-- 步骤1：配置任务实例（JobDetail） -->
<bean id="GainOrderBySAP" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    <property name="jobClass" value="com.dp.plat.job.GainOrderBySAP" />
    <property name="jobDataAsMap">
        <map>
            <entry key="timeout"><value>work</value></entry>
        </map>
    </property>
</bean>

<!-- 步骤2：配置触发器（Cron 表达式） -->
<bean id="GainOrderBySAPTrigger"
    class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
    <property name="jobDetail">
        <ref bean="GainOrderBySAP" />
    </property>
    <!-- 秒 分 时 月内日期 月 周内日期 年（可选） -->
    <property name="cronExpression">
        <value>0 50 23 * * ?</value>  <!-- 每天 23:50 执行 -->
    </property>
</bean>

<!-- 步骤3：注册到调度器 -->
<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <list>
            <ref bean="GainOrderBySAPTrigger" />
            <ref bean="MailTrigger" />
            <ref bean="TaskBySmsTrigger" />
            <!-- ... 其他触发器 ... -->
        </list>
    </property>
</bean>
```

### 5.2 常用 Cron 表达式

| Cron 表达式 | 含义 |
|---|---|
| `0 0/5 * * * ?` | 每 5 分钟执行一次 |
| `0 30 23 * * ?` | 每天 23:30 执行 |
| `0 50 23 * * ?` | 每天 23:50 执行 |
| `0 25 3,13 * * ?` | 每天 3:25 和 13:25 执行 |
| `0 30 8,13 * * ?` | 每天 8:30 和 13:30 执行 |
| `59 10 23 L * ?` | 每月最后一日 23:10:59 执行 |
| `00 20 08 ? * MON` | 每周一 08:20 执行 |
| `0 0 14 ? * SUN` | 每周日 14:00 执行 |
| `0 0 8 ? * MON-SAT` | 每周一至周六 08:00 执行 |
| `00 00 06 1 2/3 ?` | 每季度中间月初 06:00 执行 |

### 5.3 Job 实现类

```java
// GainOrderBySAP.java —— SAP 订单数据同步任务
@Deprecated  // 已废弃，合并至 GainOrderByERP
public class GainOrderBySAP implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            this.work();                          // 执行 SAP 数据同步
            UpdateShipmentState.work();           // 更新项目发货状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void work() throws IOException, SQLException {
        // 1. 获取 Spring 上下文
        ApplicationContext ctx = SpringContext.getApplicationContext();

        // 2. 构建 SAP 数据源的 SqlMapClient
        Reader readerSap = Resources.getResourceAsReader("sqlMapConfigSAP.xml");
        SqlMapClient sqlMapSap = SqlMapClientBuilder.buildSqlMapClient(readerSap);

        // 3. 构建本地数据源的 SqlMapClient
        Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
        SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);

        // 4. 记录同步开始日志
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("refreshTaskName", GainOrderBySAP.class.toString());
        paramMap.put("dataFrom", "ERP");
        paramMap.put("refreshFrom", new Date());
        Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);

        // 5. 分批同步数据（每批 2000 条）
        List<OrderBean> orderBeans = sqlMapSap.queryForList("query_DP_V_SO_ORDER_4_PMS");
        sqlMap.startTransaction();
        sqlMap.delete("delete_pm_order_data");
        List<OrderBean> list = new ArrayList<OrderBean>();
        int i = 0;
        for (OrderBean orderBean : orderBeans) {
            if (i < 2000) {
                i++;
                list.add(orderBean);
            } else {
                paramMap.put("list", list);
                sqlMap.insert("insert_pm_order_data", paramMap);
                i = 0;
                list = new ArrayList<OrderBean>();
                list.add(orderBean);
            }
        }
        // 提交剩余数据
        paramMap.put("list", list);
        sqlMap.insert("insert_pm_order_data", paramMap);
        sqlMap.commitTransaction();

        // 6. 更新成功日志
        paramMap.put("refreshState", 1);
        sqlMap.update("update_fnd_data_refresh_log_success", paramMap);
    }
}
```

**应用场景**：邮件发送、SAP/D365 订单同步、OA/EHR 人员同步、报表统计、项目状态自动更新等定时任务。

---

## 6. 自定义标签开发示例

### 6.1 PermissionTag 权限标签

```java
// PermissionTag.java —— 页面级权限控制标签
// 在 JSP 中使用：<p:permission permissionId="1">受控内容</p:permission>
public class PermissionTag extends BodyTagSupport {
    private Integer permissionId;  // 权限 ID

    @Override
    public int doStartTag() throws JspException {
        int ret = TagSupport.EVAL_BODY_INCLUDE;  // 默认显示标签体
        User user = UserContext.getUserContext().getUser();
        if (user == null) {
            return TagSupport.SKIP_PAGE;  // 未登录则跳过整个页面
        }
        // 校验用户是否拥有指定权限
        // List<Permissions> permissions = user.getPermissions();
        // boolean find = permissions.stream()
        //     .anyMatch(p -> permissionId.equals(p.getId()));
        // if (!find) ret = TagSupport.SKIP_BODY;  // 无权限则跳过标签体
        return ret;
    }
}
```

**JSP 使用**：
```jsp
<%@ taglib uri="/pms-tags" prefix="p" %>
<p:permission permissionId="1">
    <button>仅管理员可见</button>
</p:permission>
```

### 6.2 LeftMenuTag 左菜单标签

```java
// LeftMenuTag.java —— 左侧菜单渲染与权限校验标签
// 继承 SiteMesh 的 AbstractTag，在装饰器页面中使用
public class LeftMenuTag extends AbstractTag {

    @Override
    public int doEndTag() throws JspException {
        // 1. 获取 SiteMesh 装饰器页面的 meta 信息
        Page sitemeshPage = (Page) this.pageContext.getAttribute("__sitemesh__page");
        if (sitemeshPage != null) {
            String group = sitemeshPage.getProperty("meta.group");
            String function = sitemeshPage.getProperty("meta.function");
            UserContext userContext = UserContext.getUserContext();

            // 2. 权限校验：检查用户是否拥有当前页面的访问权限
            if (!userContext.isHasPermission(group, function)) {
                // 3. 检查是否为例外 URL
                String paths = basicDataService.querySysArg("sys.menu.permission.exclude.urls");
                if (!isMatch(servletPath, paths)) {
                    response.sendRedirect(request.getContextPath() + "/404.action");
                    return TagSupport.SKIP_PAGE;
                }
            }
        }

        // 4. 渲染左侧菜单
        LeftMenu menu = (LeftMenu) SpringContext.getBean("SysLeftMenu");
        menu.drow(pageContext);
        return TagSupport.EVAL_PAGE;
    }
}
```

**JSP 使用**：
```jsp
<%@ taglib uri="/pms-tags" prefix="p" %>
<p:leftMenu />
```

### 6.3 BarPercentTag 进度条标签

```java
// BarPercentTag.java —— 百分比进度条渲染标签
// 在 JSP 中使用：<p:barPercent value="completionRate" />
public class BarPercentTag extends TagSupport {
    private String value;  // OGNL 表达式，指向 Action 中的 Double 属性

    @Override
    public int doEndTag() throws JspException {
        // 1. 从 ValueStack 中获取属性值
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ValueStack vs = (ValueStack) request.getAttribute("struts.valueStack");
        Double val = (Double) vs.findValue(value);

        // 2. 计算进度条宽度（最大宽度的 3/4）
        DecimalFormat df = new DecimalFormat("0.###");
        Long v = (long) (val * 3 / 4);

        // 3. 使用 Velocity 模板渲染 HTML
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("perwidth", v);
        map.put("percent", df.format(val));
        map.put("floor", val >= Double.MAX_VALUE);
        VContext.getVM(out, "com/dp/plat/vmpage/BarPercent.vm", map);

        return TagSupport.EVAL_PAGE;
    }
}
```

**JSP 使用**：
```jsp
<p:barPercent value="projectCompletionRate" />
```

**应用场景**：权限控制、菜单渲染、进度条展示等页面级通用组件，通过自定义标签封装，避免 JSP 中嵌入大量 Java 代码。

---

## 7. 多数据源查询示例

### 7.1 BaseDao 多数据源配置

```java
// BaseDao.java —— 支持多数据源的 DAO 基类
public class BaseDao {
    private SqlMapClientTemplate sqlMapClientTemplate;       // 主数据源（PMS）
    private SqlMapClientTemplate sqlMapClientTemplateSAP;    // SAP 数据源
    private SqlMapClientTemplate sqlMapClientTemplateERP;    // ERP/D365 数据源
    private SqlMapClientTemplate sqlMapClientTemplateSSE;    // SSE 数据源

    // 各数据源的 getter/setter ...
}
```

### 7.2 Spring 多数据源配置

```xml
<!-- applicationContext.xml -->

<!-- 主数据源 -->
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${main.database.driverClassName}"></property>
    <property name="url" value="${main.database.url}"></property>
    <property name="username" value="${main.database.username}"></property>
    <property name="password" value="${main.database.password}"></property>
</bean>

<!-- D365 数据源 -->
<bean id="dataSourceD365" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${d365.database.driverClassName}" />
    <property name="url" value="${d365.database.url}" />
    <property name="username" value="${d365.database.username}" />
    <property name="password" value="${d365.database.password}"/>
</bean>
<bean id="sqlMapClientD365" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation" value="classpath:sql-map-config.xml" />
    <property name="dataSource" ref="dataSourceD365"></property>
</bean>
<bean id="sqlMapClientTemplateD365" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient"><ref bean="sqlMapClientD365" /></property>
</bean>

<!-- SSE 数据源 -->
<bean id="dataSourceSSE" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${sse.database.driverClassName}" />
    <property name="url" value="${sse.database.url}" />
    <property name="username" value="${sse.database.username}" />
    <property name="password" value="${sse.database.password}"/>
</bean>
```

### 7.3 数据同步 Job 实现模式

```java
// GainOrderBySAP.java 中的数据同步模式
// 1. 从外部数据源（SAP）查询数据
List<OrderBean> orderBeans = sqlMapSap.queryForList("query_DP_V_SO_ORDER_4_PMS");

// 2. 在本地数据源中开启事务
sqlMap.startTransaction();

// 3. 清空本地旧数据
sqlMap.delete("delete_pm_order_data");

// 4. 分批插入新数据（每批 2000 条，避免内存溢出）
List<OrderBean> list = new ArrayList<OrderBean>();
int i = 0;
for (OrderBean orderBean : orderBeans) {
    if (i < 2000) {
        i++;
        list.add(orderBean);
    } else {
        paramMap.put("list", list);
        sqlMap.insert("insert_pm_order_data", paramMap);
        i = 0;
        list = new ArrayList<OrderBean>();
        list.add(orderBean);
    }
}

// 5. 提交事务
sqlMap.commitTransaction();
sqlMap.endTransaction();
```

**应用场景**：SAP 订单同步、D365 数据同步、CRM 数据同步、EHR 人员信息同步等跨系统数据交互场景。

---

## 8. 文件上传处理示例

### 8.1 UploadAction 通用文件上传

```java
// UploadAction.java —— 通用文件上传 Action
public class UploadAction extends BaseAction {

    private File[] upload;             // Struts2 自动封装的上传文件
    private String uploadFileName;     // 原始文件名
    private String uploadFileType;     // 文件 MIME 类型
    private BasicDataService basicDataService;

    public String UPLOAD_PATH = UploadFileUtil.UPLOAD_PATH + seq + "file";

    // 通用文件上传
    public String upload() {
        if (upload == null) return INPUT;
        try {
            // 1. 构建上传路径（含随机子目录，避免单目录文件过多）
            String path = UPLOAD_PATH + seq + Util.getRandNumber();

            // 2. 执行文件上传
            UploadFileUtil.upload(upload, path, uploadFileName);

            // 3. 将文件信息写入数据库，返回文件 ID
            String fileIds = basicDataService.insertFileInfo(
                seq + path + seq, uploadFileName, uploadFileType);
            this.fileIds = fileIds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    // 富文本编辑器图片上传
    public String uploadImage() {
        try {
            String path = UPLOAD_PATH + seq + "images";
            // MD5 去重上传
            String uploadMD5FileName = UploadFileUtil.uploadNoRepeat(
                upload, path, uploadFileName);
            String fileIds = basicDataService.insertFileInfo(
                seq + path + seq, uploadMD5FileName);
            // 返回图片访问 URL
            message = basePath + "/" + path + "/" + uploadMD5FileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
}
```

### 8.2 Struts2 fileUpload 拦截器配置

```xml
<!-- struts.xml 中配置文件上传拦截器 -->
<interceptor-ref name="fileUpload">
    <param name="maximumSize">10485760</param>          <!-- 最大 10MB -->
    <param name="allowedTypes">application/pdf,image/*</param>
</interceptor-ref>
<interceptor-ref name="defaultStack" />
```

### 8.3 ProjectAction 中的交付件上传

```java
// ProjectAction.UploadFile() —— 周报附件上传
public String UploadFile() {
    if (upload != null && !upload.equals("")) {
        // 1. 构建上传路径
        String separator = java.io.File.separator;
        String path = separator + UploadFileUtil.UPLOAD_PATH
            + separator + "weekly" + separator + new Date().getTime();
        Util.mkdir(path);

        // 2. 获取文件扩展名白名单
        String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");

        // 3. 获取服务器真实路径
        String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);

        // 4. 逐个处理上传文件
        String[] uploadFileNames = uploadFileName.split(",");
        for (int i = 0; i < uploadFileNames.length; i++) {
            String ufn = uploadFileNames[i];

            // 5. 检查文件扩展名是否在白名单中
            if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
                return ERROR;
            }

            // 6. 重命名文件（避免冲突）
            String newName = projectService.getUploadFileRename(ufn);
            File target = new File(targetDirectory, newName);
            FileUtils.copyFile(upload[i], target);
        }
    }
    return SUCCESS;
}
```

### 8.4 文件存储路径规范

| 上传类型 | 存储路径 | 说明 |
|---|---|---|
| 通用文件 | `/upload/file/{random}/` | 随机子目录分散存储 |
| 周报附件 | `/upload/weekly/{timestamp}/` | 按时间戳分目录 |
| 富文本图片 | `/upload/file/images/` | MD5 去重存储 |
| 交付件 | `/upload/deliver/` | 按项目 ID 组织 |

**应用场景**：项目交付件上传、周报附件上传、技术公告附件上传、富文本编辑器图片上传等。

---

## 附录：关键工具类速查

| 工具类 | 路径 | 用途 |
|---|---|---|
| `MessageUtil` | `com.dp.plat.util.MessageUtil` | 系统常量定义（状态码、角色 ID、通知模板编码等） |
| `UserContext` | `com.dp.plat.context.UserContext` | 当前登录用户上下文（Session 作用域） |
| `UploadFileUtil` | `com.dp.plat.util.UploadFileUtil` | 文件上传、MD5 去重、扩展名校验 |
| `Base64Util` | `com.dp.plat.util.Base64Util` | Base64 编解码（项目 ID 加密传输） |
| `Md5Util` | `com.dp.plat.util.Md5Util` | MD5 加密（安全标识校验） |
| `NotificationTemplateUtil` | `com.dp.plat.util.NotificationTemplateUtil` | 邮件通知模板渲染与发送 |
| `WorkflowUtil` | `com.dp.plat.util.WorkflowUtil` | 流程任务终止、流转工具方法 |
| `SoftVersionParserFactory` | `com.dp.plat.prob.version.SoftVersionParserFactory` | 软件版本号解析策略工厂 |
