# Tasks

## 阶段一：构建修复（前置，阻塞所有后续工作）
- [x] Task 1: 修复 Maven 构建失败问题（实际根因：pom 模块路径大小写不匹配，非 pms-rules 缺失）
  - [x] 1.1 核实 `/workspace/pom.xml` 与磁盘目录：pms-rules 目录**实际存在**（先前 LS 输出截断误判），pms-ext-fp 对 pms-rules 的依赖**正确保留**
  - [x] 1.2 定位真实失败原因：根 pom 声明小写 `pms-struts`/`pms-activiti`/`pms-springmvc`/`pms-ext-d365`/`pms-security`，磁盘目录为大写 `PMS-*`，Linux 区分大小写导致 `mvn validate` 报 5 个 Child module does not exist
  - [x] 1.3 修正 5 个模块路径大小写：`pms-struts→PMS-struts`、`pms-activiti→PMS-activiti`、`pms-springmvc→PMS-springmvc`、`pms-ext-d365→PMS-ext-d365`、`pms-security→PMS-security`（core/pms-ext-fp/pms-rules 保持不变）
  - [x] 1.4 验证：`mvn validate -o` 退出码 0，全部 8 个模块均能解析，pom modules 列表与磁盘一致

## 阶段二：低代码平台后端（管理端 + 引擎）
- [ ] Task 2: 扩展 DataFieldRelation 数据模型与状态机制
  - [ ] 2.1 读取现有 `PMS-springmvc/.../entity/DataFieldRelation.java` 与对应 Mapper XML，盘点现有字段
  - [ ] 2.2 新增/确认字段：`status`（draft/published/disabled）、`version`、`templateId`（模板分组）、`description`，支持草稿-发布-停用状态转换
  - [ ] 2.3 在 `IDataFieldRelationService` 增加草稿保存、发布、停用、按 dataName+dataType 查询已发布配置等方法
  - [ ] 2.4 编写状态转换逻辑：草稿→发布（覆盖同 dataName+dataType 的旧发布版本为 disabled）、发布→停用、停用→发布
- [ ] Task 3: 实现 DataFieldRelationController 配置管理后台
  - [ ] 3.1 新增 `DataFieldRelationController`（路径 `/lowcode/config`），提供：列表、详情、新建、编辑、删除（CRUD）
  - [ ] 3.2 增加"预览"接口：按 dataName+dataType 返回草稿配置给预览渲染器
  - [ ] 3.3 增加"发布/停用"接口
  - [ ] 3.4 增加配置校验：字段 type 合法性、必填项、dataName+dataType 唯一性（同发布态）
  - [ ] 3.5 复用 core Shiro 权限：为低代码管理端分配独立权限标识（如 `lowcode:config:*`）
- [ ] Task 4: 实现组件注册表与渲染器解耦（架构可扩展性）
  - [ ] 4.1 定义统一组件接口/抽象（如 `ComponentRenderer`），声明 type → 渲染逻辑映射
  - [ ] 4.2 将现有 form/table/tab 三类渲染逻辑收敛到注册表，新增 related-page 渲染器
  - [ ] 4.3 确保新增组件类型只需实现接口并注册，无需改动设计器核心
- [ ] Task 5: 实现配置导入导出
  - [ ] 5.1 实现"导出"接口：按 dataName 列表导出为 JSON（含完整字段元数据 + 版本信息）
  - [ ] 5.2 实现"导入"接口：上传 JSON，校验结构/字段类型合法性，以草稿状态写入，冲突时返回明细错误
  - [ ] 5.3 支持批量导出（多页面打包为 JSON 数组）

## 阶段三：低代码平台前端（可视化设计器）
- [ ] Task 6: 搭建设计器前端框架
  - [ ] 6.1 盘点 PMS-springmvc 现有 Vue 组件（`vue-form-component.js` 等）与 Vue 版本，统一使用一个版本
  - [ ] 6.2 新增设计器主页面 `lowcode-designer.jsp` + `lowcode-designer.js`，三栏布局：组件区 / 画布区 / 属性区
  - [ ] 6.3 实现页面类型选择（Form/List/Tab/Related Page）切换
- [ ] Task 7: 实现表单设计器
  - [ ] 7.1 组件区列出可用字段类型（text/textarea/date/datetime/daterange/select/urlSelector/autocomplete/inputs/hidden 等）
  - [ ] 7.2 画布区支持添加/删除/排序字段，属性区配置字段全部属性（alias/name/title/type/required/readonly/disabled/cssId/cssClass/cssStyle/sort/visible/searchable/orderable/extData 等）
  - [ ] 7.3 select 支持"绑定字典"配置，urlSelector 支持"远程数据源 URL"配置
  - [ ] 7.4 保存草稿 / 发布 / 预览按钮联调后端
- [ ] Task 8: 实现列表设计器
  - [ ] 8.1 列选择（字段列表）、列排序、列可见性、可排序/可搜索开关
  - [ ] 8.2 远程数据源 URL 绑定配置
  - [ ] 8.3 保存/发布/预览联调
- [ ] Task 9: 实现标签页与关联页设计器
  - [ ] 9.1 标签页：配置多个子页签（引用已有 dataName），支持排序与可见性
  - [ ] 9.2 关联页：将一页面配置为另一实体的关联页（关联键映射）
  - [ ] 9.3 保存/发布/预览联调
- [ ] Task 10: 实现配置预览与导入导出 UI
  - [ ] 10.1 预览：复用运行时 Vue 渲染组件，加载草稿配置渲染
  - [ ] 10.2 导出：列表页"导出"按钮 → 下载 JSON
  - [ ] 10.3 导入：列表页"导入"按钮 → 上传 JSON → 校验结果展示
- [ ] Task 11: 设计器 UI 现代化与响应式
  - [ ] 11.1 采用卡片化布局、合理留白、一致配色与组件库（与 PMS-springmvc 现有风格统一）
  - [ ] 11.2 响应式断点适配（桌面优先，平板可用）
  - [ ] 11.3 设计器内首启引导提示与字段配置内联帮助（tooltips）

## 阶段四：关键缺陷修复
- [ ] Task 12: 修复工作流并行多实例回退
  - [ ] 12.1 读取 `PMS-activiti/.../process/cmd/WithdrawTaskCmd.java`，定位并行多实例 TODO 与"流程明细消失"FIXME
  - [ ] 12.2 实现并行多实例节点的回退逻辑（参照顺序多实例实现，处理并行执行分支）
  - [ ] 12.3 修复回退后流程历史明细缺失问题，保证 ACT_HI_* 完整
- [ ] Task 13: 修复项目编码生成并发竞态
  - [ ] 13.1 读取 `ProjectService.java:156` 与 `ProjectHeaderService.java:334` 的 FIXME 处组编码生成逻辑
  - [ ] 13.2 采用数据库唯一约束 + 失败重试，或数据库序列/原子取号，消除竞态
  - [ ] 13.3 验证并发场景下不再产生重复组编码

## 阶段五：部署自动化
- [ ] Task 14: 容器化部署
  - [ ] 14.1 新增 `Dockerfile`（多阶段：Maven 构建 → Tomcat 运行镜像），支持 PMS-springmvc（pms2 profile）构建
  - [ ] 14.2 新增 `docker-compose.yml`（MySQL 8.0 + Tomcat，含数据卷与健康检查）
  - [ ] 14.3 新增 `scripts/deploy.sh`（build / up / down / logs 子命令）
  - [ ] 14.4 新增 `docs/deployment.md`（环境要求、profile 配置、构建命令、常见排错）

## 阶段六：用户引导与反馈
- [ ] Task 15: 用户引导与反馈机制
  - [ ] 15.1 新增 `docs/lowcode-guide.md` 低代码平台用户指南（新手教程、组件配置说明、模板复用、最佳实践）
  - [ ] 15.2 新增 core `FeedbackController` + `Feedback` 实体 + `t_feedback` 表 DDL + Mapper + Service（提交/列表/详情/状态更新）
  - [ ] 15.3 新增反馈表单页面与管理端列表页面（JSP），挂载到菜单
  - [ ] 15.4 反馈入口在系统内全局可见（如顶栏"反馈"按钮）

## 阶段七：测试与验证
- [ ] Task 16: 测试用例与验证
  - [ ] 16.1 低代码配置 CRUD/发布/导入导出 单元/集成测试（JUnit + Mockito）
  - [ ] 16.2 工作流并行多实例回退测试用例
  - [ ] 16.3 项目编码并发测试用例（模拟并发取号）
  - [ ] 16.4 部署脚本冒烟测试：`docker-compose up -d` 后应用可访问

# Task Dependencies
- Task 1（构建修复）为所有后续任务前置，须最先完成
- Task 2 → Task 3 → Task 4 → Task 5（后端按序，Task 4/5 可在 Task 3 后并行）
- Task 6 → Task 7/8/9（前端设计器，7/8/9 可并行）→ Task 10 → Task 11
- 前端 Task 6+ 依赖后端 Task 3 接口
- Task 12、Task 13、Task 14、Task 15 互相独立，可与阶段二/三并行
- Task 16 依赖其对应功能任务完成
