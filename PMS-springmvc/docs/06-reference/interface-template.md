# PMS-springmvc 接口模板与规范

> 本文档提供 PMS-springmvc 模块 Controller 接口的编写模板与规范，涵盖 CRUD、分页查询、文件导入导出等场景。

---

## 一、Controller 类模板

### 1.1 标准 CRUD Controller

```java
package com.dp.plat.pms.springmvc.controller;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.YourEntity;
import com.dp.plat.pms.springmvc.service.IYourService;
import com.dp.plat.pms.springmvc.vo.YourVO;

/**
 * 业务模块 Controller
 * URL 前缀：/pm/your-module/
 */
@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "/your-module")
public class YourController extends AbstractController<IYourService, YourEntity, YourVO> {

    @PostConstruct
    public void init() {
        this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
        this.setViewModel("yourModule");  // 视图名称
        this.setUseTemplate(true);        // 使用动态模板
    }

    @Override
    public String home(Model model) {
        String view = super.home(model);
        return getRealViewNameSpace() + "list";
    }

    /**
     * 列表查询
     * URL: GET /pm/your-module/list
     */
    @Override
    @RequestMapping("/list")
    public String list(PageParam<Object> pageParam, YourVO v, Model model) {
        // 1. 权限检查
        if (!checkPermission(v, model, getDataName() + ":list")) {
            model.addAttribute("data", Collections.emptyList());
            return Consts.VIEW_UNAUTHORIZED;
        }
        
        // 2. 设置查询条件
        v.setDisabled(false);
        
        // 3. 数据权限过滤
        if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
            // 非管理员添加权限过滤
            v.setProjectTypes(user.getUserInfo().getCustom4());
            v.setOfficeCodes(user.getUserInfo().getCustom5());
        }
        
        // 4. 分页查询
        PageParam<Object> tempParam = new PageParam<>();
        YourVO temp = new YourVO();
        temp.setDisabled(false);
        tempParam.setModel(temp);
        pageParam.setModel(v);
        
        pageParam.setTotal(service.countBySelectivePageable(tempParam));
        pageParam.setFiltered(service.countBySelectivePageable(pageParam));
        List<Object> list = service.selectBySelectivePageable(pageParam);
        
        // 5. 返回结果
        model.addAttribute("data", list);
        List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
        pageParam.setColumns(columns);
        
        return getRealViewNameSpace() + "list";
    }

    /**
     * 详情查询
     * URL: GET /pm/your-module/{id}
     */
    @Override
    @RequestMapping(value = { "/{id}", "/modals/{id}" })
    public String findOne(@PathVariable("id") Integer id, Model model) {
        if (!checkPermission(null, model, getDataName() + ":detail")) {
            model.addAttribute("status", false);
            model.addAttribute("message", "没有权限进行该操作！");
            return Consts.VIEW_UNAUTHORIZED;
        }
        
        YourEntity entity = service.selectByPrimaryKey(id);
        if (entity != null) {
            model.addAttribute("targetValue", entity);
            List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
            model.addAttribute("fieldList", fieldList);
        }
        
        return getRealViewNameSpace() + "detail";
    }

    /**
     * 新增
     * URL: POST /pm/your-module/detail
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @SystemControllerLog(description = "新增[$v.name$]")
    public String create(YourVO v, Model model) {
        if (!checkPermission(v, model, getDataName() + ":add")) {
            model.addAttribute("status", false);
            model.addAttribute("message", "没有权限进行该操作！");
            return Consts.VIEW_UNAUTHORIZED;
        }
        
        Boolean status = true;
        String message = null;
        try {
            service.insertSelective(v);
            model.addAttribute("targetName", "yourVO");
        } catch (Exception e) {
            status = false;
            message = e.getMessage();
        }
        model.addAttribute("status", status);
        model.addAttribute("message", message);
        return getRealViewNameSpace() + "detail";
    }

    /**
     * 更新
     * URL: PUT /pm/your-module/{id}
     */
    @Override
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @SystemControllerLog(description = "修改[$v.name$]")
    public String update(@PathVariable("id") Integer id, YourVO v, Model model) {
        if (!checkPermission(v, model, getDataName() + ":edit")) {
            return Consts.VIEW_UNAUTHORIZED;
        }
        return super.update(id, v, model);
    }

    /**
     * 删除（逻辑删除）
     * URL: DELETE /pm/your-module/{id}
     */
    @Override
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @SystemControllerLog(description = "删除")
    public void delete(@PathVariable("id") Integer id, Model model) {
        if (!checkPermission(null, model, getDataName() + ":delete")) {
            return;
        }
        Boolean status = true;
        String message = null;
        try {
            YourVO v = new YourVO();
            v.setId(id);
            v.setDisabled(true);
            service.updateByPrimaryKeySelective(v);
        } catch (Exception e) {
            status = false;
            message = e.getMessage();
        }
        model.addAttribute("status", status);
        model.addAttribute("message", message);
    }
}
```

---

## 二、Service 接口模板

### 2.1 Service 接口

```java
package com.dp.plat.pms.springmvc.service;

import com.dp.plat.core.service.IBaseService;
import com.dp.plat.pms.springmvc.entity.YourEntity;
import com.dp.plat.pms.springmvc.vo.YourVO;

public interface IYourService extends IBaseService<YourEntity, YourVO> {
    
    // 扩展业务方法
    void customBusinessMethod(Integer id);
}
```

### 2.2 Service 实现

```java
package com.dp.plat.pms.springmvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.service.impl.BaseService;
import com.dp.plat.pms.springmvc.dao.YourMapper;
import com.dp.plat.pms.springmvc.entity.YourEntity;
import com.dp.plat.pms.springmvc.service.IYourService;
import com.dp.plat.pms.springmvc.vo.YourVO;

@Service
public class YourService extends BaseService<YourMapper, YourEntity, YourVO> 
    implements IYourService {

    @Autowired
    private YourMapper yourMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customBusinessMethod(Integer id) {
        // 业务逻辑
    }
}
```

---

## 三、Mapper 接口模板

### 3.1 标准 Mapper

```java
package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.YourEntity;

public interface YourMapper extends AbstractBaseMapper<YourEntity> {
    
    // 扩展查询方法（可选）
    // List<YourVO> selectCustomList(PageParam<Object> pageParam);
}
```

### 3.2 Mapper XML 模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.dp.plat.pms.springmvc.dao.YourMapper">
  
  <resultMap id="BaseResultMap" type="com.dp.plat.pms.springmvc.entity.YourEntity">
    <id column="id" jdbcType="INTEGER" property="id" />
    <result column="name" jdbcType="VARCHAR" property="name" />
    <result column="status" jdbcType="VARCHAR" property="status" />
    <result column="disabled" jdbcType="BIT" property="disabled" />
    <result column="customInfo" jdbcType="JSON" property="customInfo" />
    <result column="createTime" jdbcType="TIMESTAMP" property="createTime" />
    <result column="createBy" jdbcType="VARCHAR" property="createBy" />
    <result column="updateTime" jdbcType="TIMESTAMP" property="updateTime" />
    <result column="updateBy" jdbcType="VARCHAR" property="updateBy" />
  </resultMap>
  
  <sql id="Base_Column_List">
    id, name, status, disabled, customInfo, createTime, createBy, updateTime, updateBy
  </sql>
  
  <!-- 按主键查询 -->
  <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
    select <include refid="Base_Column_List" />
    from your_table
    where id = #{id,jdbcType=INTEGER}
  </select>
  
  <!-- 选择性插入 -->
  <insert id="insertSelective" parameterType="com.dp.plat.pms.springmvc.entity.YourEntity" 
          keyProperty="id" useGeneratedKeys="true">
    <selectKey keyProperty="id" order="AFTER" resultType="java.lang.Integer">
      SELECT LAST_INSERT_ID()
    </selectKey>
    insert into your_table
    <trim prefix="(" suffix=")" suffixOverrides=",">
      <if test="name != null">name,</if>
      <if test="status != null">status,</if>
      <if test="disabled != null">disabled,</if>
      <if test="customInfo != null">customInfo,</if>
      <if test="createTime != null">createTime,</if>
      <if test="createBy != null">createBy,</if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
      <if test="name != null">#{name,jdbcType=VARCHAR},</if>
      <if test="status != null">#{status,jdbcType=VARCHAR},</if>
      <if test="disabled != null">#{disabled,jdbcType=BIT},</if>
      <if test="customInfo != null">#{customInfo,jdbcType=JSON},</if>
      <if test="createTime != null">#{createTime,jdbcType=TIMESTAMP},</if>
      <if test="createBy != null">#{createBy,jdbcType=VARCHAR},</if>
    </trim>
  </insert>
  
  <!-- 选择性更新 -->
  <update id="updateByPrimaryKeySelective" parameterType="com.dp.plat.pms.springmvc.entity.YourEntity">
    update your_table
    <set>
      <if test="name != null">name = #{name,jdbcType=VARCHAR},</if>
      <if test="status != null">status = #{status,jdbcType=VARCHAR},</if>
      <if test="disabled != null">disabled = #{disabled,jdbcType=BIT},</if>
      <if test="customInfo != null">customInfo = #{customInfo,jdbcType=JSON},</if>
      <if test="updateTime != null">updateTime = #{updateTime,jdbcType=TIMESTAMP},</if>
      <if test="updateBy != null">updateBy = #{updateBy,jdbcType=VARCHAR},</if>
    </set>
    where id = #{id,jdbcType=INTEGER}
  </update>
  
</mapper>
```

---

## 四、Entity 实体类模板

```java
package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;
import com.dp.plat.core.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.dp.plat.core.serializer.JsonSerializer;

public class YourEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String status;
    
    // 通用字段（继承自 BaseEntity）
    // private Boolean disabled;
    // private Map<String, Object> customInfo;
    // private Date createTime;
    // private String createBy;
    // private Date updateTime;
    // private String updateBy;
    // private Date effectiveFrom;
    // private Date effectiveTo;

    // Getter / Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

---

## 五、VO 视图对象模板

```java
package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.pms.springmvc.entity.YourEntity;

public class YourVO extends YourEntity {

    private static final long serialVersionUID = 1L;

    // 展示用字段（不映射到数据库）
    private String statusName;      // 状态名称
    private String createName;      // 创建人姓名
    private String officeName;      // 办事处名称
    
    // 权限控制字段
    private String projectTypes;    // 允许访问的项目类型
    private String officeCodes;     // 允许访问的办事处
    private String userPower;       // 用户名权限
    private Integer userIdPower;    // 用户ID权限
    
    // 查询条件字段
    private Boolean checkProject;   // 是否检查项目权限
    private String memberCode;      // 成员账号

    // Getter / Setter
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    
    // ... 其他 Getter / Setter
}
```

---

## 六、接口返回格式

### 6.1 列表查询返回

```json
{
    "draw": 1,
    "recordsTotal": 100,
    "recordsFiltered": 50,
    "data": [
        {
            "id": 1,
            "name": "示例数据",
            "status": "1",
            "statusName": "已审批",
            "createBy": "zhangsan",
            "createName": "张三",
            "createTime": "2026-01-01 10:00:00"
        }
    ],
    "columns": [
        {
            "title": "名称",
            "data": "name"
        },
        {
            "title": "状态",
            "data": "status",
            "render": "stateName"
        }
    ]
}
```

### 6.2 操作结果返回

```json
{
    "status": true,
    "message": "操作成功",
    "targetValue": {
        "id": 1,
        "name": "示例数据"
    }
}
```

### 6.3 错误返回

```json
{
    "status": false,
    "message": "没有权限进行该操作！"
}
```

---

## 七、接口规范

### 7.1 URL 命名规范

| 操作 | HTTP 方法 | URL 格式 | 说明 |
|------|----------|---------|------|
| 首页 | GET | `/pm/your-module/` | 返回列表页面 |
| 列表查询 | GET | `/pm/your-module/list` | 分页查询数据 |
| 详情查询 | GET | `/pm/your-module/{id}` | 查询单条详情 |
| 新增表单 | GET | `/pm/your-module/detail` | 返回新增表单 |
| 新增提交 | POST | `/pm/your-module/detail` | 提交新增数据 |
| 更新 | PUT | `/pm/your-module/{id}` | 更新数据 |
| 删除 | DELETE | `/pm/your-module/{id}` | 逻辑删除 |

### 7.2 参数规范

- **分页参数**：使用 `PageParam` 对象，包含 `pageNo`（页码，从 1 开始）、`pageSize`（每页条数，默认 20）。
- **查询条件**：使用 VO 对象，非空字段作为查询条件。
- **路径参数**：使用 `@PathVariable` 注解，如 `/{id}`。
- **请求体**：使用 `@ModelAttribute` 或表单提交，不使用 `@RequestBody`（兼容 JSP 表单）。

### 7.3 权限检查规范

所有 Controller 方法必须调用 `checkPermission` 方法进行权限检查：

```java
// 列表查询权限
if (!checkPermission(v, model, getDataName() + ":list")) {
    model.addAttribute("data", Collections.emptyList());
    return Consts.VIEW_UNAUTHORIZED;
}

// 详情查看权限
if (!checkPermission(v, model, getDataName() + ":detail")) {
    return Consts.VIEW_UNAUTHORIZED;
}

// 新增权限
if (!checkPermission(v, model, getDataName() + ":add")) {
    return Consts.VIEW_UNAUTHORIZED;
}

// 编辑权限
if (!checkPermission(v, model, getDataName() + ":edit")) {
    return Consts.VIEW_UNAUTHORIZED;
}

// 删除权限
if (!checkPermission(null, model, getDataName() + ":delete")) {
    return;
}
```

### 7.4 日志记录规范

写操作（新增、修改、删除）必须添加 `@SystemControllerLog` 注解：

```java
@RequestMapping(value = "/detail", method = RequestMethod.POST)
@SystemControllerLog(description = "新增[$v.name$]")
public String create(YourVO v, Model model) { ... }

@RequestMapping(value = "{id}", method = RequestMethod.PUT)
@SystemControllerLog(description = "修改[$v.name$]")
public String update(@PathVariable("id") Integer id, YourVO v, Model model) { ... }

@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
@SystemControllerLog(description = "删除")
public void delete(@PathVariable("id") Integer id, Model model) { ... }
```

> **说明**：`$v.name$` 是 SpEL 表达式，从方法参数 `v` 中获取 `name` 属性值，用于日志描述。
