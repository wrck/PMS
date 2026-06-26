# PMS-springmvc 编码规范文档

---

## 1. Controller 层规范

### 1.1 继承 BaseController

所有 Controller 类必须继承 `com.dp.plat.pms.springmvc.controller.BaseController`，该基类提供以下能力：

- 统一返回 JSON 结果
- 分页参数处理
- 用户上下文获取

```java
@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {
    
    @Autowired
    private IProjectService projectService;
    
    @RequestMapping("/list")
    public String list(Model model) {
        List<Project> projects = projectService.getProjectList();
        model.addAttribute("projects", projects);
        return "project/list";
    }
    
    @RequestMapping("/save")
    @ResponseBody
    public Result<?> save(Project project) {
        try {
            projectService.saveProject(project);
            return success(project);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
```

### 1.2 URL 映射规范

- 使用 `@RequestMapping` 注解
- URL 使用小写字母和连字符
- RESTful 风格：`/project/{id}`

```java
@RequestMapping("/project")
public class ProjectController {
    
    @RequestMapping("/{id}")
    @ResponseBody
    public Result<?> getById(@PathVariable int id) { ... }
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Result<?> create(@RequestBody Project project) { ... }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Result<?> update(@PathVariable int id, @RequestBody Project project) { ... }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result<?> delete(@PathVariable int id) { ... }
}
```

---

## 2. Service 层规范

### 2.1 接口与实现分离

```java
// 接口
public interface IProjectService {
    Project getProjectById(int id);
    int saveProject(Project project);
    int deleteProject(int id);
}

// 实现
@Service
public class ProjectServiceImpl implements IProjectService {
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Override
    public Project getProjectById(int id) {
        return projectMapper.selectByPrimaryKey(id);
    }
    
    @Override
    @Transactional
    public int saveProject(Project project) {
        if (project.getId() == null) {
            return projectMapper.insert(project);
        } else {
            return projectMapper.updateByPrimaryKeySelective(project);
        }
    }
}
```

### 2.2 事务管理

```java
@Service
public class ProjectServiceImpl implements IProjectService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processProject(int projectId) {
        // 业务逻辑
        // 如果发生异常，自动回滚
    }
}
```

---

## 3. DAO 层规范

### 3.1 Mapper 接口命名

- 接口名：`表名Mapper`（驼峰命名）
- 方法名：`selectByPrimaryKey`, `insert`, `updateByPrimaryKeySelective`, `deleteByPrimaryKey`

```java
public interface ProjectMapper {
    Project selectByPrimaryKey(Integer id);
    int insert(Project record);
    int insertSelective(Project record);
    int updateByPrimaryKeySelective(Project record);
    int deleteByPrimaryKey(Integer id);
}
```

### 3.2 XML 映射文件

- 文件名：`表名Mapper.xml`
- 命名空间：`com.dp.plat.pms.springmvc.dao.表名Mapper`

```xml
<mapper namespace="com.dp.plat.pms.springmvc.dao.ProjectMapper">
    <resultMap id="BaseResultMap" type="com.dp.plat.pms.springmvc.entity.Project">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="projectCode" jdbcType="VARCHAR" property="projectCode"/>
    </resultMap>
    
    <sql id="Base_Column_List">
        id, projectCode, projectName, projectState
    </sql>
    
    <select id="selectByPrimaryKey" resultMap="BaseResultMap">
        select <include refid="Base_Column_List"/>
        from pm_project
        where id = #{id,jdbcType=INTEGER}
    </select>
</mapper>
```

---

## 4. 常量定义规范

```java
public class ProjectConstant {
    // 项目状态
    public static final String PROJECT_STATE_INIT = "30";
    public static final String PROJECT_STATE_SM_ASSIGNED = "31";
    public static final String PROJECT_STATE_PM_ASSIGNED = "32";
    public static final String PROJECT_STATE_IN_PROGRESS = "40";
    public static final String PROJECT_STATE_CLOSED = "100";
    
    // 项目类型
    public static final String PROJECT_TYPE_AFTER_SALES = "10";
    public static final String PROJECT_TYPE_PRESALES = "20";
}
```

---

## 5. 异常处理规范

```java
// 自定义业务异常
public class BusinessException extends RuntimeException {
    private int code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}

// 全局异常处理器
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }
}
```

---

## 6. 日志规范

```java
// 使用 SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    
    public void processProject(int projectId) {
        log.info("开始处理项目: projectId={}", projectId);
        try {
            // 业务逻辑
            log.info("项目处理完成: projectId={}", projectId);
        } catch (Exception e) {
            log.error("项目处理失败: projectId={}", projectId, e);
            throw e;
        }
    }
}
```
