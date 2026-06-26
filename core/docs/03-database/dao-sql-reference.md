# core 模块 — DAO/SQL 参考

> 本文档说明 core 模块关键 Mapper（UserMapper、RoleMapper、MenuMapper）的 SQL 映射，供开发者查阅。
> 源码基准：`com.dp.plat.core.dao`、`com/dp/plat/core/mapping/*.xml`

---

## 1. UserMapper SQL 映射

### 1.1 接口方法

| 方法 | SQL 类型 | 说明 |
|------|---------|------|
| `deleteByPrimaryKey(Integer)` | DELETE | 按主键删除 |
| `insert(User)` | INSERT | 全字段插入 |
| `insertSelective(User)` | INSERT | 选择性插入 |
| `selectByPrimaryKey(Integer)` | SELECT | 按主键查询 |
| `updateByPrimaryKey(User)` | UPDATE | 全字段更新 |
| `updateByPrimaryKeySelective(User)` | UPDATE | 选择性更新 |
| `selectByUserName(String)` | SELECT | 按用户名查询 |
| `selectAllUser()` | SELECT | 查询所有用户 |
| `queryUserMenuByUsername(String)` | SELECT | 查询用户菜单 |
| `updateLoginInfoByUserName(User)` | UPDATE | 更新登录信息 |
| `countBySelective(PageParam)` | SELECT COUNT | 分页计数 |
| `selectBySelective(PageParam)` | SELECT | 分页查询 |
| `updateByUsername(User)` | UPDATE | 按用户名更新 |
| `updateUserErrorCount(String)` | UPDATE | 错误次数+1 |
| `checkUniqueUserName(String)` | SELECT | 检查用户名唯一 |
| `insertOrUpdateSelective(User)` | INSERT/UPDATE | 插入或更新 |
| `queryMaxRoleHomePageByUserId(Integer)` | SELECT | 查询角色主页 |
| `queryUserMenuByUserIdAndCompId(UserInfo)` | SELECT | 按公司查询菜单 |
| `queryMaxRoleHomePageByUserIdAndCompId(UserInfo)` | SELECT | 按公司查询角色主页 |
| `findUserByParam(Map)` | SELECT | 按参数查询 |

### 1.2 关键 SQL 说明

**selectByUserName（登录认证用）**：

```sql
SELECT
    user_id, user_name, password, status, needChangePwd,
    loginErrorCount, isSysUser, userCustom1, userCustom2,
    userCustom3, userCustom4, userCustom5
FROM t_user
WHERE user_name = #{userName}
```

- 命中 `user_name` 唯一索引；
- 返回完整用户信息供 ShiroRealm 认证。

**updateUserErrorCount（登录错误累加）**：

```sql
UPDATE t_user
SET loginErrorCount = loginErrorCount + 1
WHERE user_name = #{userName}
```

- 原子操作，避免并发问题；
- 达阈值后由应用层置 `status=2`（锁定）。

**checkUniqueUserName（用户名唯一校验）**：

```sql
SELECT COUNT(1) FROM t_user WHERE user_name = #{userName}
```

- 返回 boolean，true=已存在，false=可用。

**queryUserMenuByUserIdAndCompId（按公司查询菜单）**：

```sql
SELECT m.*
FROM t_menu m
INNER JOIN t_role_menu rm ON m.id = rm.menu_id
INNER JOIN t_user_role ur ON rm.role_id = ur.role_id
WHERE ur.user_id = #{userId}
  AND ur.comp_id = #{compId}
  AND m.status = 1
ORDER BY m.sort
```

- 三表 JOIN：`t_menu` + `t_role_menu` + `t_user_role`；
- 按公司隔离，仅返回当前公司角色对应的菜单。

### 1.3 resultMap 映射

```xml
<resultMap id="BaseResultMap" type="com.dp.plat.core.pojo.User">
    <id column="user_id" property="userId" jdbcType="INTEGER"/>
    <result column="user_name" property="userName" jdbcType="VARCHAR"/>
    <result column="password" property="password" jdbcType="VARCHAR"/>
    <result column="status" property="status" jdbcType="SMALLINT"/>
    <result column="needChangePwd" property="needChangePwd" jdbcType="BIT"/>
    <result column="loginErrorCount" property="loginErrorCount" jdbcType="INTEGER"/>
    <result column="isSysUser" property="isSysUser" jdbcType="SMALLINT"/>
    <!-- ... 审计字段与自定义字段 -->
</resultMap>
```

> **注意**：列名混合命名（`user_id` 下划线 + `needChangePwd` 驼峰），需显式映射。

---

## 2. RoleMapper SQL 映射

### 2.1 接口方法

| 方法 | SQL 类型 | 说明 |
|------|---------|------|
| `deleteByPrimaryKey(Integer)` | DELETE | 按主键删除 |
| `insert(Role)` | INSERT | 全字段插入 |
| `insertSelective(Role)` | INSERT | 选择性插入 |
| `selectByPrimaryKey(Integer)` | SELECT | 按主键查询 |
| `updateByPrimaryKey(Role)` | UPDATE | 全字段更新 |
| `updateByPrimaryKeySelective(Role)` | UPDATE | 选择性更新 |
| `countBySelective(RoleParam)` | SELECT COUNT | 分页计数 |
| `selectAllRole()` | SELECT | 查询所有角色 |
| `selectBySelective(Role, RoleParam)` | SELECT | 条件查询 |
| `selectBySelective(RoleParam)` | SELECT | 分页查询 |
| `selectRolesByRoleNames(String)` | SELECT | 按角色名集合查询 |
| `selectRoleByRoleName(String)` | SELECT | 按角色名查单个 |

### 2.2 关键 SQL 说明

**selectRoleByRoleName（按角色名查询）**：

```sql
SELECT *
FROM t_role
WHERE role_name = #{roleName}
  AND status = 1
```

- 命中 `role_name` 索引；
- 用于 ShiroRealm 授权时获取 maxRole。

**selectRolesByRoleNames（按角色名集合查询）**：

```sql
SELECT *
FROM t_role
WHERE role_name IN (#{roleNames})
  AND status = 1
ORDER BY priority
```

- `roleNames` 为逗号分隔的角色名；
- 按 `priority` 排序，取最高优先级角色。

### 2.3 AbstractBaseMapper 继承

`RoleMapper` 继承 `AbstractBaseMapper<Role>`，自动获得标准 CRUD：

```xml
<!-- 基类提供的 SQL（通过 OGNL 动态条件） -->
<select id="selectBySelective" parameterType="Role" resultMap="BaseResultMap">
    SELECT * FROM t_role
    <where>
        <if test="roleName != null">AND role_name = #{roleName}</if>
        <if test="status != null">AND status = #{status}</if>
    </where>
</select>
```

---

## 3. MenuMapper SQL 映射

### 3.1 接口方法

| 方法 | SQL 类型 | 说明 |
|------|---------|------|
| `deleteByPrimaryKey(Integer)` | DELETE | 按主键删除 |
| `insert(Menu)` | INSERT | 全字段插入 |
| `insertSelective(Menu)` | INSERT | 选择性插入 |
| `selectByPrimaryKey(Integer)` | SELECT | 按主键查询 |
| `updateByPrimaryKey(Menu)` | UPDATE | 全字段更新 |
| `updateByPrimaryKeySelective(Menu)` | UPDATE | 选择性更新 |
| `selectAll()` | SELECT | 查询所有菜单 |
| `selectBySelective(Menu)` | SELECT | 条件查询 |

### 3.2 关键 SQL 说明

**selectAll（查询所有菜单）**：

```sql
SELECT * FROM t_menu WHERE status = 1 ORDER BY pid, sort
```

- 按 `pid` 和 `sort` 排序，便于构建树形结构；
- `MenuUtil.buildMenuTree` 将平铺结果构建为树。

**selectBySelective（条件查询）**：

```sql
SELECT * FROM t_menu
<where>
    <if test="pid != null">AND pid = #{pid}</if>
    <if test="name != null">AND name LIKE #{name}</if>
    <if test="status != null">AND status = #{status}</if>
</where>
ORDER BY sort
```

### 3.3 resultMap 映射

```xml
<resultMap id="BaseResultMap" type="com.dp.plat.core.pojo.Menu">
    <id column="id" property="id" jdbcType="INTEGER"/>
    <result column="pid" property="pid" jdbcType="INTEGER"/>
    <result column="name" property="name" jdbcType="VARCHAR"/>
    <result column="url" property="url" jdbcType="VARCHAR"/>
    <result column="icon" property="icon" jdbcType="VARCHAR"/>
    <result column="sort" property="sort" jdbcType="INTEGER"/>
    <result column="status" property="status" jdbcType="BIT"/>
    <result column="target" property="target" jdbcType="VARCHAR"/>
    <!-- 注意：crate_time 为历史拼写错误 -->
    <result column="crate_time" property="createTime" jdbcType="TIMESTAMP"/>
</resultMap>
```

> **避坑**：`t_menu` 表中 `crate_time` 为历史拼写错误（应为 `create_time`），已固化在数据库中，修改需同步更新 resultMap。

---

## 4. ShiroService 聚合查询 SQL

`IShiroService` 为认证授权提供聚合查询，涉及多表 JOIN：

### 4.1 queryUserByName

```sql
SELECT * FROM t_user WHERE user_name = #{userName}
```

### 4.2 queryUserRoleByNameAndCompId

```sql
SELECT DISTINCT r.role_name
FROM t_role r
INNER JOIN t_user_role ur ON r.role_id = ur.role_id
WHERE ur.user_id = #{userId}
  AND (ur.comp_id = #{compId} OR #{compId} = -1)
  AND r.status = 1
```

- `compId = -1` 时查全部公司角色（系统用户）；
- 返回角色名字符串集合。

### 4.3 queryPermissionByUsernameAndCompId

```sql
SELECT DISTINCT p.permission_name
FROM t_permission p
INNER JOIN t_role_permission rp ON p.permission_id = rp.permission_id
INNER JOIN t_user_role ur ON rp.role_id = ur.role_id
INNER JOIN t_user u ON ur.user_id = u.user_id
WHERE u.user_name = #{userName}
  AND (ur.comp_id = #{compId} OR #{compId} = -1)
```

- 四表 JOIN：`t_permission` + `t_role_permission` + `t_user_role` + `t_user`；
- 返回权限字符串集合。

### 4.4 queryUserMenuByUserIdAndCompId

```sql
SELECT DISTINCT m.*
FROM t_menu m
INNER JOIN t_role_menu rm ON m.id = rm.menu_id
INNER JOIN t_user_role ur ON rm.role_id = ur.role_id
WHERE ur.user_id = #{userId}
  AND (ur.comp_id = #{compId} OR #{compId} = -1)
  AND m.status = 1
ORDER BY m.pid, m.sort
```

---

## 5. SQL 编写规范

### 5.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| Mapper 方法 | 驼峰命名 | `selectByUserName` |
| SQL ID | 与方法名一致 | `selectByUserName` |
| 参数 | `#{paramName}` | `#{userName}` |
| resultMap | `BaseResultMap` | 标准命名 |

### 5.2 动态 SQL

```xml
<!-- 条件查询使用 <where> + <if> -->
<select id="selectBySelective" parameterType="User" resultMap="BaseResultMap">
    SELECT * FROM t_user
    <where>
        <if test="userName != null">AND user_name = #{userName}</if>
        <if test="status != null">AND status = #{status}</if>
    </where>
</select>

<!-- 选择性插入使用 <trim> + <if> -->
<insert id="insertSelective" parameterType="User">
    INSERT INTO t_user
    <trim prefix="(" suffix=")" suffixOverrides=",">
        <if test="userName != null">user_name,</if>
        <if test="password != null">password,</if>
    </trim>
    <trim prefix="VALUES (" suffix=")" suffixOverrides=",">
        <if test="userName != null">#{userName},</if>
        <if test="password != null">#{password},</if>
    </trim>
</insert>
```

### 5.3 分页 SQL

```xml
<select id="selectBySelectivePageable" resultMap="BaseResultMap">
    SELECT * FROM t_user
    <where>
        <if test="param.userName != null">AND user_name LIKE #{param.userName}</if>
    </where>
    LIMIT #{pageNum}, #{pageSize}
</select>
```

> **注意**：core 使用 MySQL `LIMIT` 分页，SQL Server 数据源需改用 `OFFSET ... FETCH`。

---

## 6. 相关文档

- [01-architecture MyBatis 配置](../01-architecture/mybatis-configuration.md) — Mapper 扫描
- [02-modules Service 方法参考](../02-modules/service-methods-reference.md) — Service 接口
- [03-database 数据字典](complete-data-dictionary.md) — 表字段详情
- [03-database ER 图](er-diagram.md) — 表关系
