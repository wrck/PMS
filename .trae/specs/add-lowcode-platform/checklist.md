# Checklist

## 构建修复
- [x] 根 pom.xml 模块路径大小写与磁盘目录一致（PMS-struts/PMS-activiti/PMS-springmvc/PMS-ext-d365/PMS-security 已修正）
- [x] pms-rules 模块实际存在，pms-ext-fp 对其依赖正确保留（先前"缺失"判断系 LS 输出截断误判）
- [x] `mvn validate -o` 退出码 0，全部 8 个模块均能解析
- [x] pom modules 列表与磁盘目录一一对应

## 低代码后端
- [ ] DataFieldRelation 支持 draft/published/disabled 状态与版本字段
- [ ] IDataFieldRelationService 提供草稿保存、发布、停用、查询已发布配置方法
- [ ] 状态转换正确：发布覆盖旧版本（旧版本置 disabled），草稿不覆盖已发布
- [ ] DataFieldRelationController 提供 CRUD + 预览 + 发布/停用接口
- [ ] 配置校验：字段 type 合法、必填项齐全、同 dataName+dataType 发布态唯一
- [ ] 低代码管理端受 Shiro 权限保护（lowcode:config:*）
- [ ] 组件注册表与渲染器解耦：新增组件类型只需注册，不改设计器核心
- [ ] 导出接口生成稳定结构 JSON，含完整字段元数据与版本信息
- [ ] 导入接口校验 JSON 结构与字段类型，失败返回明细错误，成功以草稿写入

## 低代码前端
- [ ] 设计器三栏布局（组件区/画布区/属性区），支持页面类型切换
- [ ] 表单设计器支持全部字段类型与属性配置（含 select 绑字典、urlSelector 绑远程源）
- [ ] 列表设计器支持列选择/排序/可见性/可排序/可搜索/远程数据源绑定
- [ ] 标签页设计器支持多子页签配置与排序；关联页支持关联键映射
- [ ] 设计器保存/发布/预览按钮与后端联调通过
- [ ] 预览使用运行时渲染逻辑，与发布效果一致
- [ ] 导入导出 UI 可用，导出下载 JSON、导入上传并展示校验结果
- [ ] 设计器 UI 现代化（卡片化、留白、一致组件库、响应式断点）
- [ ] 设计器含首启引导提示与字段配置内联帮助

## 关键缺陷修复
- [ ] WithdrawTaskCmd 并行多实例节点回退已实现（TODO 清除）
- [ ] 回退后流程历史明细完整（FIXME 清除）
- [ ] 项目编码生成在并发场景下不再产生重复组编码（FIXME 清除）

## 部署自动化
- [ ] Dockerfile 多阶段构建产出可运行 Tomcat 镜像，含正确 profile 配置
- [ ] docker-compose.yml 可一键拉起 MySQL + Tomcat，应用可访问
- [ ] scripts/deploy.sh 提供 build/up/down/logs 子命令并可用
- [ ] docs/deployment.md 覆盖环境要求、配置项、构建命令、排错

## 用户引导与反馈
- [ ] docs/lowcode-guide.md 涵盖新手教程、组件配置、模板复用、最佳实践
- [ ] FeedbackController + t_feedback 表 + Mapper + Service 实现完整
- [ ] 反馈表单页与管理端列表页可用，反馈入口全局可见
- [ ] 管理端可查看反馈详情并标记处理状态

## 测试与验证
- [ ] 低代码配置 CRUD/发布/导入导出测试用例通过
- [ ] 工作流并行多实例回退测试用例通过
- [ ] 项目编码并发测试用例通过（无重复编码）
- [ ] docker-compose up -d 冒烟测试通过（应用可访问）
