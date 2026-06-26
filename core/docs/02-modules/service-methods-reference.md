# core 模块 — Service 方法参考

> 本文档列出 core 模块所有 Service 接口的方法签名与说明，供上层模块开发者查阅。
> 源码基准：`com.dp.plat.core.service` 包下所有接口。

---

## 1. Service 接口总览

| Service 接口 | 实现类 | 对应表 | 说明 |
|-------------|--------|--------|------|
| `IAbstractBaseService<T>` | `AbstractBaseService<T>` | - | 泛型 CRUD 基类 |
| `IUserService` | `UserService` | `t_user` | 用户管理 |
| `IUserInfoService` | `UserInfoService` | `t_user_info` | 用户信息 |
| `IUserRoleService` | `UserRoleService` | `t_user_role` | 用户-角色 |
| `IUserLoginRecordService` | `UserLoginRecordService` | `t_user_login_record` | 登录记录 |
| `IRoleService` | `RoleService` | `t_role` | 角色管理 |
| `IRoleMenuService` | `RoleMenuService` | `t_role_menu` | 角色-菜单 |
| `IMenuService` | `MenuService` | `t_menu` | 菜单管理 |
| `IResourceService` | `ResourceServicee` | `t_resource` | URL 资源 |
| `IDictionaryService` | `DictionaryService` | `t_dictionary` | 数据字典 |
| `IFileInfoService` | `FileInfoService` | `t_file` | 文件管理 |
| `IUploaderService` | `UploaderService` | `t_file` | 文件上传 |
| `IMailInfoService` | `MailInfoService` | `t_mails` | 邮件管理 |
| `INotifyTemplateService` | `NotifyTemplateService` | `t_notify_template` | 通知模板 |
| `ISysLogService` | `SysLogService` | `t_sys_log` | 系统日志 |
| `ISyncLogService` | `SyncLogService` | `t_sync_log` | 同步日志 |
| `ISynchronizeService` | `SynchronizeService` | `t_sync_log/t_sync_state` | 数据同步 |
| `ISystemVariableService` | `SystemVariableService` | `t_sys_variable` | 系统变量 |
| `IDepartmentService` | `DepartmentService` | `t_department` | 部门管理 |
| `ICompanyService` | `CompanyService` | `t_company` | 公司管理 |
| `IShiroService` | `ShiroService` | 聚合查询 | 认证授权查询 |
| `IDataOperationService` | `DataOperationService` | 通用 | 数据操作 |
| `IDataExportService` | `DataExportService` | 通用 | 数据导出 |

---

## 2. IAbstractBaseService<T>（泛型 CRUD 基类）

```java
public interface IAbstractBaseService<T> {
    int deleteByPrimaryKey(Object pk);
    int insert(T t);
    int insertSelective(T t);
    T selectByPrimaryKey(Object pk);
    int updateByPrimaryKey(T t);
    int updateByPrimaryKeySelective(T t);
    long countBySelective(T t);
    long countBySelectivePageable(PageParam pageParam);
    List<T> selectBySelective(T t);
    List<T> selectBySelectivePageable(PageParam pageParam);
}
```

| 方法 | 说明 |
|------|------|
| `deleteByPrimaryKey(Object pk)` | 按主键删除 |
| `insert(T t)` | 全字段插入 |
| `insertSelective(T t)` | 选择性插入（null 跳过） |
| `selectByPrimaryKey(Object pk)` | 按主键查询 |
| `updateByPrimaryKey(T t)` | 全字段更新 |
| `updateByPrimaryKeySelective(T t)` | 选择性更新 |
| `countBySelective(T t)` | 条件计数 |
| `countBySelectivePageable(PageParam)` | 分页计数 |
| `selectBySelective(T t)` | 条件查询 |
| `selectBySelectivePageable(PageParam)` | 分页查询 |

---

## 3. IUserService（用户管理）

```java
public interface IUserService {
    int deleteByPrimaryKey(Integer userId);
    int insert(User record);
    int insertSelective(User record);
    User selectByPrimaryKey(Integer userId);
    int updateByPrimaryKey(User record);
    void updateByPrimaryKeySelective(User user);
    List<User> selectAllUser();
    void updateLoginInfoByUserName(User user);
    long countBySelective(PageParam<UserDetail> pageParam);
    List<UserDetail> selectBySelective(PageParam<UserDetail> pageParam);
    void updateByUsername(User user);
    User selectByUserName(String username);
    void updateUserErrorCount(String username);
    boolean checkUniqueUserName(String userName);
    void insertOrUpdateSelective(User user);
    String queryMaxRoleHomePageByUserId(Integer userId);
    String queryMaxRoleHomePageByUserIdAndCompId(UserInfo userInfo);
    List<UserDetail> findUserByParam(Map<String, String[]> parameterMap);
}
```

| 方法 | 说明 |
|------|------|
| `selectAllUser()` | 查询所有可用用户 |
| `selectByUserName(String)` | 按用户名查询 |
| `updateLoginInfoByUserName(User)` | 更新登录信息 |
| `updateByUsername(User)` | 按用户名更新 |
| `updateUserErrorCount(String)` | 错误次数 +1 |
| `checkUniqueUserName(String)` | 检查用户名唯一 |
| `insertOrUpdateSelective(User)` | 插入或更新 |
| `queryMaxRoleHomePageByUserId(Integer)` | 查询角色主页 |
| `queryMaxRoleHomePageByUserIdAndCompId(UserInfo)` | 按公司查询角色主页 |
| `countBySelective(PageParam)` | 分页计数 |
| `selectBySelective(PageParam)` | 分页查询 |
| `findUserByParam(Map)` | 按参数查询 |

---

## 4. IUserInfoService（用户信息）

```java
public interface IUserInfoService extends IAbstractBaseService<UserInfo> {
    int deleteByPrimaryKey(Integer id);
    int insert(UserInfo record);
    int insertSelective(UserInfo record);
    UserInfo selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(UserInfo record);
    int updateByPrimaryKeySelective(UserInfo userInfo);
    UserInfo selectByUserId(Integer id);
    UserInfoVO selectOneByUserId(Integer id);
    List<UserInfoVO> selectVOsByUserId(Integer id);
    void updateByUserId(UserInfo userInfo);
    void deleteByUserId(Integer id);
    List<UserInfo> selectBySelective(UserInfo userInfo);
    UserInfoVO selectOneByUserIdAndCompId(UserInfo userInfo);
    UserInfoVO selectOneByUserNameAndCompId(String userName);
    UserInfoVO selectOneByUserNameAndCompId(String userName, Integer orgId);
}
```

| 方法 | 说明 |
|------|------|
| `selectByUserId(Integer)` | 按 userId 查询 |
| `selectOneByUserId(Integer)` | 查询单个 VO |
| `selectVOsByUserId(Integer)` | 查询 VO 列表（多公司） |
| `updateByUserId(UserInfo)` | 按 userId 更新 |
| `deleteByUserId(Integer)` | 按 userId 删除 |
| `selectOneByUserIdAndCompId(UserInfo)` | 按用户+公司查询 |
| `selectOneByUserNameAndCompId(String)` | 按用户名+公司查询 |
| `selectOneByUserNameAndCompId(String, Integer)` | 按用户名+orgId 查询 |

---

## 5. IRoleService（角色管理）

```java
public interface IRoleService {
    int deleteByPrimaryKey(Integer roleId);
    int insert(Role role);
    int insertSelective(Role role);
    Role selectByPrimaryKey(Integer roleId);
    int updateByPrimaryKeySelective(Role role);
    int updateByPrimaryKey(Role role);
    long countBySelective(RoleParam pageParam);
    List<Role> selectAllRole();
    List<Role> selectBySelective(Role role, RoleParam pageParam);
    List<Role> selectBySelective(RoleParam pageParam);
    List<Role> selectRolesByRoleNames(String roleNames);
    Role selectRoleByRoleName(String roleName);
}
```

| 方法 | 说明 |
|------|------|
| `selectAllRole()` | 查询所有角色 |
| `selectBySelective(Role, RoleParam)` | 条件查询 |
| `selectBySelective(RoleParam)` | 分页查询 |
| `selectRolesByRoleNames(String)` | 按角色名集合查询 |
| `selectRoleByRoleName(String)` | 按角色名查询单个 |

---

## 6. IMenuService（菜单管理）

```java
public interface IMenuService {
    int deleteByPrimaryKey(Integer id);
    int insert(Menu record);
    int insertSelective(Menu record);
    Menu selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Menu record);
    int updateByPrimaryKey(Menu record);
    List<Menu> selectAll();
    List<Menu> selectBySelective(Menu menu);
    List<TreeNode> getTreeData();
}
```

| 方法 | 说明 |
|------|------|
| `selectAll()` | 查询所有菜单 |
| `selectBySelective(Menu)` | 条件查询 |
| `getTreeData()` | 获取树形数据（`List<TreeNode>`） |

---

## 7. IShiroService（认证授权查询）

```java
public interface IShiroService {
    User queryUserByName(String username);
    Set<String> queryUserRoleByName(String principal);
    Set<String> queryPermissionByUsername(String principal);
    List<Menu> queryUserMenuByUsername(String username);
    Set<String> queryUserRoleByNameAndCompId(String userName, Integer compId);
    Set<String> queryPermissionByUsernameAndCompId(String userName, Integer compId);
    List<Menu> queryUserMenuByUserIdAndCompId(UserInfo userInfo);
}
```

| 方法 | 说明 |
|------|------|
| `queryUserByName(String)` | 按用户名查询用户 |
| `queryUserRoleByName(String)` | 查询用户角色（无公司隔离） |
| `queryPermissionByUsername(String)` | 查询用户权限（无公司隔离） |
| `queryUserMenuByUsername(String)` | 查询用户菜单 |
| `queryUserRoleByNameAndCompId(String, Integer)` | 按公司查询角色 |
| `queryPermissionByUsernameAndCompId(String, Integer)` | 按公司查询权限 |
| `queryUserMenuByUserIdAndCompId(UserInfo)` | 按公司查询菜单 |

---

## 8. IDictionaryService（数据字典）

```java
public interface IDictionaryService {
    int deleteByPrimaryKey(Integer id);
    int insert(Dictionary record);
    int insertSelective(Dictionary record);
    Dictionary selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Dictionary record);
    int updateByPrimaryKey(Dictionary record);
    List<Dictionary> selectBySelective(PageParam<Dictionary> pageParam);
    long countBySelective(PageParam<Dictionary> pageParam);
    List<Dictionary> selectByDicTypeId(int dicTypeId);
}
```

| 方法 | 说明 |
|------|------|
| `selectBySelective(PageParam)` | 分页查询 |
| `countBySelective(PageParam)` | 分页计数 |
| `selectByDicTypeId(int)` | 按字典类型 ID 查询 |

---

## 9. IFileInfoService（文件管理）

```java
public interface IFileInfoService extends IAbstractBaseService<FileInfo> {
    FileType selectFileTypeByCode(String typeCode);
    void insertFileInfo(FileInfo fileInfo, String userName);
    FileInfo selectFileInfoById(Integer fileId);
    List<FileInfo> selectFileInfoByIds(Collection<String> ids);
    List<FileInfo> selectFileInfoByIdsAndType(Collection<String> ids, Integer typeId);
    void insertdownlog(String fileIds, String remoteAddr);
    void insertdownlog(String fileIds, String remoteAddr, String user);
}
```

| 方法 | 说明 |
|------|------|
| `selectFileTypeByCode(String)` | 按编码查询文件类型 |
| `insertFileInfo(FileInfo, String)` | 插入文件信息 |
| `selectFileInfoById(Integer)` | 按文件 ID 查询 |
| `selectFileInfoByIds(Collection)` | 批量查询 |
| `selectFileInfoByIdsAndType(Collection, Integer)` | 按类型批量查询 |
| `insertdownlog(String, String)` | 记录下载日志 |
| `insertdownlog(String, String, String)` | 记录下载日志（含用户） |

---

## 10. ISysLogService（系统日志）

```java
public interface ISysLogService {
    int deleteByPrimaryKey(Integer id);
    int insert(SysLog record);
    int insertSelective(SysLog record);
    SysLog selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(SysLog record);
    int updateByPrimaryKey(SysLog record);
    List<SysLog> selectBySelective(PageParam<SysLog> pageParam);
    long countBySelective(PageParam<SysLog> pageParam);
}
```

| 方法 | 说明 |
|------|------|
| `selectBySelective(PageParam)` | 分页查询日志 |
| `countBySelective(PageParam)` | 分页计数 |

---

## 11. ISystemVariableService（系统变量）

```java
public interface ISystemVariableService {
    HashMap<String, String> querySystemVariables();
    // 其他 CRUD 方法
}
```

| 方法 | 说明 |
|------|------|
| `querySystemVariables()` | 查询所有系统变量（启动时加载到 `SystemConfig.systemVariables`） |

---

## 12. 其他 Service 接口

### 12.1 IRoleMenuService（角色-菜单）

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 标准 CRUD |
| `selectByRoleId(Integer)` | 按角色 ID 查询菜单 |
| `selectByMenuId(Integer)` | 按菜单 ID 查询角色 |

### 12.2 IDepartmentService（部门管理）

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 标准 CRUD |
| `selectAll()` | 查询所有部门 |
| `selectBySelective(Department)` | 条件查询 |

### 12.3 ICompanyService（公司管理）

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 标准 CRUD |
| `selectAll()` | 查询所有公司 |
| `selectBySelective(Company)` | 条件查询 |

### 12.4 ISyncLogService（同步日志）

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 标准 CRUD |
| `selectBySelective(SyncLog)` | 条件查询 |

### 12.5 ISynchronizeService（数据同步）

```java
public interface ISynchronizeService {
    Map<String, Object> selectSyncState(String tableObject);
    int insertSyncState(SyncState syncState);
    void insertSyncLog(SyncLog syncLog);
    void clearSyncState();
    void deleteSyncState(SyncState syncState);
}
```

| 方法 | 说明 |
|------|------|
| `selectSyncState(String)` | 查询上一次增量同步时的状态值 |
| `insertSyncState(SyncState)` | 插入/更新增量同步状态值 |
| `insertSyncLog(SyncLog)` | 插入同步日志 |
| `clearSyncState()` | 全量更新时清空同步状态表 |
| `deleteSyncState(SyncState)` | 清空指定的同步状态表 |

### 12.6 IMailInfoService（邮件管理）

> **注意**：`IMailInfoService` 位于 `com.dp.plat.support.mail.service` 包（非 `core.service`），但通过 Spring component-scan 注册为 Bean。

```java
public interface IMailInfoService {
    NotificationTemplate queryNotificationTemplate(String templateCode);
    List<MailSenderInfo> queryUnSendMails();
    List<MailSenderInfo> queryUnSendMails(Integer failedCount);
    void updateMailWhenSendSuccess(String mailIds);
    void updateOneMailInfoWhenSendSuccess(MailInfo successMail);
    void updateMailInfoWhenSendSuccess(List<MailInfo> successMails);
    void updateMailFailedCount(String failedMailIds);
    void updateMailInfoWhenSend(List<MailInfo> mails);
    // 标准 CRUD：selectByPrimaryKey/deleteByPrimaryKey/insert/insertSelective/
    //           updateByPrimaryKeySelective/updateByPrimaryKey/
    //           countBySelectivePageable/countBySelective/
    //           selectBySelectivePageable/selectBySelective
}
```

| 方法 | 说明 |
|------|------|
| `queryNotificationTemplate(String)` | 按模板编码查询通知模板 |
| `queryUnSendMails()` | 查询所有待发邮件（预期发送时间≤当前） |
| `queryUnSendMails(Integer)` | 查询待发邮件（失败次数 < failedCount） |
| `updateMailWhenSendSuccess(String)` | 按邮件 ID 串更新发送成功状态 |
| `updateOneMailInfoWhenSendSuccess(MailInfo)` | 更新单封邮件实际发送地址与状态 |
| `updateMailInfoWhenSendSuccess(List)` | 批量更新邮件实际发送地址与状态 |
| `updateMailFailedCount(String)` | 按邮件 ID 串更新失败次数 |
| `updateMailInfoWhenSend(List)` | 统一更新邮件发送情况（成功更新地址/状态，失败更新次数/错误） |
| CRUD 方法 | 标准 CRUD（基于 MailInfo） |

### 12.7 INotifyTemplateService（通知模板）

```java
public interface INotifyTemplateService extends IAbstractBaseService<NotifyTemplate> {
    NotifyTemplate selectByTemplateCode(String templateCode);
    void deleteByTemplateCode(String templateCode);
}
```

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 继承自 `IAbstractBaseService<NotifyTemplate>` |
| `selectByTemplateCode(String)` | 按模板编码查询 |
| `deleteByTemplateCode(String)` | 按模板编码删除 |

### 12.8 IDataOperationService（数据操作）

```java
public interface IDataOperationService extends IAbstractBaseService<DataOperation> {
    DataOperation selectByOperationName(String operationName);
    int checkOperationName(String operationName);
    Map<String, Object> queryExportColumns(String sql);
    List<Map<String, Object>> queryExportDataByMap(Map<String, Object> params);
    List<Map<String, Object>> queryExportData(PageParam<?> pageParam);
    long countExportData(PageParam<?> pageParam);
}
```

| 方法 | 说明 |
|------|------|
| CRUD 方法 | 继承自 `IAbstractBaseService<DataOperation>` |
| `selectByOperationName(String)` | 按操作名查询数据操作配置 |
| `checkOperationName(String)` | 校验操作名是否存在 |
| `queryExportColumns(String)` | 查询导出列定义 |
| `queryExportDataByMap(Map)` | 按参数查询导出数据 |
| `queryExportData(PageParam)` | 分页查询导出数据 |
| `countExportData(PageParam)` | 分页统计导出数据条数 |

### 12.9 IDataExportService（数据导出）

```java
public interface IDataExportService {
    List<UserDetail> exportUserDetail(PageParam<UserDetail> pageParam);
    Map<String, String> queryDynamicColumn(String objectName);
    String queryDynamicColumnSort(String objectName);
}
```

| 方法 | 说明 |
|------|------|
| `exportUserDetail(PageParam)` | 按分页参数导出用户详情列表 |
| `queryDynamicColumn(String)` | 按对象名查询动态列定义 |
| `queryDynamicColumnSort(String)` | 按对象名查询动态列排序 |

---

## 13. 相关文档

- [02-modules 公共组件](common-components.md) — Service/Mapper 清单
- [user-management 用户管理](user-management.md) — 用户 Service 详解
- [role-permission 角色权限](role-permission.md) — 角色 Service 详解
- [03-database DAO/SQL 参考](../03-database/dao-sql-reference.md) — Mapper SQL 映射
