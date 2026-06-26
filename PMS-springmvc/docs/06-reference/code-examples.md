# PMS-springmvc 代码示例与参考

---

## 1. Controller 示例

### 1.1 项目管理 Controller

```java
@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {
    
    @Autowired
    private IProjectService projectService;
    
    // 查询项目列表
    @RequestMapping("/list")
    public String list(Model model, ProjectQuery query) {
        List<Project> projects = projectService.getProjectList(query);
        model.addAttribute("projects", projects);
        return "project/list";
    }
    
    // 查询项目详情
    @RequestMapping("/{id}")
    @ResponseBody
    public Result<?> getById(@PathVariable int id) {
        Project project = projectService.getProjectById(id);
        return success(project);
    }
    
    // 创建项目
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Result<?> create(@RequestBody Project project) {
        projectService.saveProject(project);
        return success(project);
    }
    
    // 更新项目
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Result<?> update(@PathVariable int id, @RequestBody Project project) {
        project.setId(id);
        projectService.saveProject(project);
        return success(project);
    }
    
    // 删除项目
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result<?> delete(@PathVariable int id) {
        projectService.deleteProject(id);
        return success();
    }
}
```

### 1.2 工作流 Controller

```java
@Controller
@RequestMapping("/workflow")
public class WorkFlowController extends BaseController {
    
    @Autowired
    private IPmWorkFlowService pmWorkFlowService;
    
    // 启动工作流
    @RequestMapping("/start")
    @ResponseBody
    public Result<?> start(@RequestParam int projectId) {
        UserDetail user = getCurrentUser();
        pmWorkFlowService.startWorkflow(projectId, user.getUsername());
        return success();
    }
    
    // 完成任务
    @RequestMapping("/complete")
    @ResponseBody
    public Result<?> complete(@RequestParam String taskId, 
                              @RequestParam String action) {
        UserDetail user = getCurrentUser();
        pmWorkFlowService.completeTask(taskId, action, user.getUsername());
        return success();
    }
    
    // 查询待办任务
    @RequestMapping("/todo")
    @ResponseBody
    public Result<?> todo() {
        UserDetail user = getCurrentUser();
        List<TaskVO> tasks = pmWorkFlowService.getTodoTasks(user.getUsername());
        return success(tasks);
    }
}
```

---

## 2. Service 示例

### 2.1 项目服务实现

```java
@Service
public class ProjectServiceImpl implements IProjectService {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private ProjectMemberMapper projectMemberMapper;
    
    @Override
    public Project getProjectById(int id) {
        return projectMapper.selectByPrimaryKey(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveProject(Project project) {
        log.info("保存项目: projectCode={}", project.getProjectCode());
        
        if (project.getId() == null) {
            // 新增
            project.setCreateTime(new Date());
            project.setCreateBy(getCurrentUser().getUsername());
            return projectMapper.insert(project);
        } else {
            // 更新
            project.setUpdateTime(new Date());
            project.setUpdateBy(getCurrentUser().getUsername());
            return projectMapper.updateByPrimaryKeySelective(project);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProject(int id) {
        log.info("删除项目: projectId={}", id);
        
        // 删除项目成员
        projectMemberMapper.deleteByProjectId(id);
        
        // 删除项目
        return projectMapper.deleteByPrimaryKey(id);
    }
}
```

### 2.2 工作流服务实现

```java
@Service
public class PmWorkFlowServiceImpl implements IPmWorkFlowService {
    
    private static final Logger log = LoggerFactory.getLogger(PmWorkFlowServiceImpl.class);
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Override
    public void startWorkflow(int projectId, String userId) {
        log.info("启动工作流: projectId={}, userId={}", projectId, userId);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("projectId", projectId);
        variables.put("userId", userId);
        
        runtimeService.startProcessInstanceByKey("projectWorkflow", variables);
    }
    
    @Override
    public void completeTask(String taskId, String action, String userId) {
        log.info("完成任务: taskId={}, action={}, userId={}", taskId, action, userId);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("action", action);
        variables.put("userId", userId);
        
        taskService.complete(taskId, variables);
    }
    
    @Override
    public List<TaskVO> getTodoTasks(String userId) {
        List<Task> tasks = taskService.createTaskQuery()
            .taskAssignee(userId)
            .list();
        
        return tasks.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }
}
```

---

## 3. DAO 示例

### 3.1 Mapper 接口

```java
public interface ProjectMapper {
    Project selectByPrimaryKey(Integer id);
    
    List<Project> selectByCondition(ProjectQuery query);
    
    int insert(Project record);
    
    int insertSelective(Project record);
    
    int updateByPrimaryKeySelective(Project record);
    
    int deleteByPrimaryKey(Integer id);
}
```

### 3.2 XML 映射

```xml
<mapper namespace="com.dp.plat.pms.springmvc.dao.ProjectMapper">
    <resultMap id="BaseResultMap" type="com.dp.plat.pms.springmvc.entity.Project">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="projectCode" jdbcType="VARCHAR" property="projectCode"/>
        <result column="projectName" jdbcType="VARCHAR" property="projectName"/>
        <result column="projectState" jdbcType="VARCHAR" property="projectState"/>
    </resultMap>
    
    <sql id="Base_Column_List">
        id, projectCode, projectName, projectState, customerName, createTime
    </sql>
    
    <select id="selectByPrimaryKey" resultMap="BaseResultMap">
        select <include refid="Base_Column_List"/>
        from pm_project
        where id = #{id,jdbcType=INTEGER}
    </select>
    
    <select id="selectByCondition" resultMap="BaseResultMap">
        select <include refid="Base_Column_List"/>
        from pm_project
        <where>
            <if test="projectName != null and projectName != ''">
                and projectName like concat('%', #{projectName}, '%')
            </if>
            <if test="projectState != null and projectState != ''">
                and projectState = #{projectState}
            </if>
        </where>
        order by createTime desc
    </select>
</mapper>
```

---

## 4. 实体类示例

```java
public class Project {
    private Integer id;
    private String projectCode;
    private String projectName;
    private String projectState;
    private String customerName;
    private String projectType;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private JSONObject customInfo;
    
    // getter/setter
}
```

---

## 5. VO 示例

```java
public class ProjectVO {
    private Integer id;
    private String projectCode;
    private String projectName;
    private String projectState;
    private String customerName;
    private String stateName;  // 状态名称
    private Date createTime;
    private String createByName;  // 创建人名称
    
    // getter/setter
}
```

---

## 6. 查询参数示例

```java
public class ProjectQuery {
    private String projectName;
    private String projectState;
    private String customerName;
    private Date createTimeStart;
    private Date createTimeEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    // getter/setter
}
```
