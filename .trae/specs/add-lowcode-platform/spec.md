# 低代码可视化配置平台与交付补全 Spec

## Why

PMS 项目经全面盘点发现：业务功能模块（25 个）已较为完整，但在五个维度存在系统性缺口——① **完全缺失低代码/可视化配置能力**：PMS-springmvc 虽有 `data_field_relation` 元数据驱动的表单/列表/标签页渲染器（Vue 组件），但无可视化设计器、无配置管理后台，字段元数据只能 DBA 通过 SQL 维护，无法满足"业务人员可视化配置界面"的低代码诉求；② **构建即失败**：根 pom 声明 `pms-rules` 模块但磁盘无该目录，`mvn package` 直接报错；③ **关键功能未完成**：并行多实例工作流回退未实现、项目编码生成存在并发竞态；④ **无 CI/CD、无容器化、无部署脚本**，仅靠手动拷贝 WAR；⑤ **无终端用户引导/教程、无产品反馈通道**。

本 Spec 以"低代码可视化配置平台"为核心交付，向上承袭现有 `data_field_relation` 渲染基础设施，向下补齐构建修复、关键缺陷修复、部署自动化与用户引导，形成可独立交付的高质量增量。

## What Changes

### A. 低代码可视化配置平台（新增模块能力，核心交付）
- 新增 `DataFieldRelationController` 配置管理后台（CRUD + 预览 + 发布/停用），补齐现有渲染器缺失的管理端
- 新增可视化页面设计器 UI（基于 PMS-springmvc 现有 Vue 技术栈）：拖拽式/表单式配置表单（Form）、列表（List）、标签页（Tab）、关联页（Related Page）四类基础组件
- 设计器支持字段元数据全属性配置：类型（hidden/textarea/date/datetime/daterange/select/urlSelector/autocomplete/inputs 等）、校验（required/readonly/disabled）、样式（cssId/cssClass/cssStyle）、排序、可见性、字段继承（superData）、扩展属性（extData/extKey/extValue）
- 新增配置模板的导入/导出功能（JSON 格式），支持模板复用与跨环境迁移
- 新增"配置预览"能力：设计器内实时预览生成的界面，确保符合 UI/UX 标准
- 设计器生成的前端界面统一采用现代 UI 设计标准（卡片化布局、留白、一致组件库、响应式断点）

### B. 构建修复与关键缺陷修复
- **BREAKING（构建层面）**：移除根 pom 中磁盘不存在的 `pms-rules` 模块声明（或将 pms-ext-fp 对 pms-rules 的依赖调整为可直接内联的 AviatorUtils），使 `mvn clean package` 可成功执行
- 修复 PMS-activiti `WithdrawTaskCmd` 并行多实例节点回退未实现（TODO/FIXME）
- 修复 PMS-springmvc 项目编码生成并发竞态（`ProjectService`/`ProjectHeaderService` FIXME：并发获取相同组编码）

### C. 部署自动化
- 新增 `Dockerfile`（基于 Tomcat 基础镜像，多阶段构建产出可运行镜像）覆盖 PMS-springmvc 与 PMS-struts 两个 WAR
- 新增 `docker-compose.yml`（含 MySQL、Tomcat、可选 SQL Server 桥接）一键拉起本地/测试环境
- 新增 `scripts/deploy.sh` 部署脚本（构建 + 镜像 + 启停）
- 新增 `docs/deployment.md` 部署流程文档（环境要求、配置项、构建命令、排错）

### D. 用户引导与技术支持
- 新增低代码平台用户使用指南（`docs/lowcode-guide.md`）：新手教程、组件配置说明、模板复用、最佳实践
- 新增产品内"使用引导"机制：设计器首启引导提示、字段配置内联帮助
- 新增轻量用户反馈通道（`FeedbackController` + 反馈表单页面）：用户可提交问题/建议，写入 `t_feedback` 表并在管理端查看

### E. 架构与可扩展性
- 低代码引擎采用"组件注册表 + 渲染器"解耦设计：新增组件类型只需实现统一接口并注册，无需改动设计器核心
- 配置数据与渲染逻辑分离：`data_field_relation` 为唯一数据源，设计器/管理端/渲染器均通过 `IDataFieldRelationService` 访问
- 设计器配置（JSON）与运行时元数据（DB）双向同步规则明确，支持导入草稿 → 校验 → 发布

## Impact

- **Affected specs（现有能力）**：
  - PMS-springmvc 动态表单/列表/标签页渲染（`BaseController.findFieldList`、Vue 组件 `vue-form-component` 等）——管理端补齐后从"半自动"升级为"全自助"
  - core 通用导入导出框架（`DataOperationController`）——低代码配置导入导出复用其反射与校验范式
  - core 菜单/字典管理——低代码页面可作为菜单项挂载，复用 `t_menu` + Shiro 权限过滤
- **Affected code（关键文件/系统）**：
  - `/workspace/pom.xml`（移除 pms-rules 模块声明）
  - `/workspace/PMS-springmvc/src/main/java/com/dp/plat/pms/springmvc/`（新增 controller/service/dao/entity，扩展 BaseController）
  - `/workspace/PMS-springmvc/src/main/webapp/static/pm/js/vue-*.js`（设计器前端）
  - `/workspace/PMS-activiti/src/main/java/com/dp/plat/activiti/process/cmd/WithdrawTaskCmd.java`（并行多实例回退）
  - `/workspace/PMS-springmvc/.../service/impl/ProjectService.java`、`ProjectHeaderService.java`（并发修复）
  - `/workspace/`（新增 Dockerfile、docker-compose.yml、scripts/、docs/）
  - `/workspace/core/`（新增 FeedbackController、t_feedback 表/mapper）

## ADDED Requirements

### Requirement: 低代码可视化页面设计器
系统 SHALL 提供基于浏览器的可视化页面设计器，支持业务人员通过配置（拖拽或表单填写）生成表单、列表、标签页、关联页四类界面，无需编写代码。

#### Scenario: 配置一个表单页面
- **WHEN** 用户在设计器中新建"表单"类型页面，添加字段（如"项目名称"设为 text+required、"立项日期"设为 date、"项目类型"设为 select 绑定字典），保存并发布
- **THEN** 配置写入 `data_field_relation`，运行时访问该表单页面应渲染出对应字段，校验规则与样式生效，且字段顺序、可见性、只读态与配置一致

#### Scenario: 配置一个列表页面
- **WHEN** 用户配置"列表"类型页面，选择数据列、设置可排序/可搜索/可见性，绑定远程数据源 URL
- **THEN** 运行时列表按配置渲染表头与数据，支持分页、排序、搜索，列的显隐与排序遵循配置

#### Scenario: 配置标签页与关联页
- **WHEN** 用户配置"标签页"类型页面（多个子页签）或将一页面关联为另一实体的"关联页"
- **THEN** 运行时按配置渲染页签切换与关联跳转，页签顺序与可见性受配置控制

### Requirement: 配置导入导出与模板复用
系统 SHALL 支持将页面配置以 JSON 格式导出，并支持从 JSON 导入为草稿，实现模板复用与跨环境迁移。

#### Scenario: 导出配置为模板
- **WHEN** 用户在设计器中选择"导出"，选择一个或多个页面配置
- **THEN** 系统生成包含完整字段元数据的 JSON 文件供下载，JSON 结构稳定可版本管理

#### Scenario: 导入模板为草稿
- **WHEN** 用户上传配置 JSON 文件
- **THEN** 系统校验 JSON 结构与字段类型合法性，校验通过后以"草稿"状态导入，不覆盖已发布配置；校验失败返回明确错误定位

### Requirement: 配置预览
系统 SHALL 提供设计器内实时预览能力，让用户在发布前看到生成的真实界面。

#### Scenario: 预览表单
- **WHEN** 用户在设计器中点击"预览"
- **THEN** 系统以运行时渲染逻辑展示当前草稿配置对应的界面，与最终发布效果一致

### Requirement: 部署自动化
系统 SHALL 提供基于容器的自动化部署能力，支持一键构建镜像与拉起环境。

#### Scenario: 一键构建镜像
- **WHEN** 执行 `scripts/deploy.sh build`（或 `docker build`）
- **THEN** 系统多阶段构建产出包含可运行 WAR 的 Tomcat 镜像，镜像内含正确 profile 配置

#### Scenario: 一键启动环境
- **WHEN** 执行 `docker-compose up -d`
- **THEN** MySQL 与 Tomcat 容器启动，应用可访问，初始化脚本就绪

### Requirement: 用户反馈通道
系统 SHALL 提供产品内置的用户反馈提交与查看机制。

#### Scenario: 提交反馈
- **WHEN** 用户在系统内点击"反馈"，填写问题类型、描述、联系方式并提交
- **THEN** 反馈写入 `t_feedback` 表，用户收到提交成功提示

#### Scenario: 管理端查看反馈
- **WHEN** 管理员访问反馈管理页面
- **THEN** 可查看反馈列表、详情，并标记处理状态

## MODIFIED Requirements

### Requirement: Maven 构建
原构建因 `pms-rules` 模块声明但磁盘缺失而失败。修改为：移除该模块声明并调整 pms-ext-fp 依赖，使 `mvn clean package -P dev` 可在无 pms-rules 目录时成功完成，且 pms-ext-fp 的 Aviator 表达式能力保留（通过内联或 core 中的 AviatorUtils）。

### Requirement: 工作流任务回退
PMS-activiti `WithdrawTaskCmd` 原仅支持顺序多实例回退，并行多实例为 TODO。修改为：补全并行多实例节点的回退逻辑，并修复"回退后回退节点之前流程明细消失"的 FIXME，保证流程历史完整性。

### Requirement: 项目编码生成
PMS-springmvc 项目编码生成原存在并发竞态（FIXME：并发获取相同组编码）。修改为：采用数据库序列或乐观锁/唯一约束保证编码唯一性，消除竞态。

## REMOVED Requirements

### Requirement: pms-rules 独立模块
**Reason**: 根 pom 声明 `pms-rules` 模块但磁盘不存在该目录，导致完整构建失败；该模块原计划承载 LiteFlow/Groovy 规则引擎但从未落地（仅 AviatorUtils 一文），且 AviatorUtils 在 core/PMS-struts/pms-rules 三处重复实现。
**Migration**: 移除根 pom 的 `<module>pms-rules</module>` 声明；将 pms-ext-fp 对 pms-rules 的依赖改为依赖 core（core 已含 `AviatorUtils`）；统一以 core 中的 AviatorUtils 为唯一实现。后续如需 LiteFlow/Groovy，再以新模块重新引入。
