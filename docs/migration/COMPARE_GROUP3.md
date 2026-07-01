# PMS迁移比对报告 - 第3组：闭环管理模块

> 比对日期: 2026-07-01
> 比对范围: PmClosedLoopAction, PmClosedLoopQuesnaireAction → 对应SpringBoot Controller/Service

---

## 一、PmClosedLoopAction → PmClosedLoopController / PmClosedLoopServiceImpl

### 方法: execute() (主入口 - 项目闭环信息页)
- **源逻辑摘要**:
  1. 校验project是否存在
  2. 权限校验(propm,prosm,cl,cb,manager 任一)
  3. 获取项目成员(最终客户)
  4. 获取申请历史集合(evaluationType=0)
  5. 获取私有/公有任务ID(getTaskId)
  6. 根据pmClosedLoopResultType获取流程状态与流程图(getProcessStatus)
  7. 非0时获取历史流程信息(getHisProcess)含问卷结果、模板、评分规则
  8. resultType=30时获取回访问卷(getCbForm)
  9. resultType=40时获取闭环建议问卷(getClForm)
  10. 设置closeApplyUser
- **目标实现**: Controller有`list()`(分页查询)和`detail()`(按ID查详情)，均为简单CRUD，无任何业务逻辑
- **状态**: ❌ 未迁移
- **差异说明**: execute()是一个复杂的页面加载方法，包含权限校验、任务管理、流程状态查询、问卷数据加载等10+步业务逻辑。目标仅有简单分页查询，完全缺失项目上下文加载、权限控制、工作流集成、问卷表单加载等核心逻辑。

### 方法: addPmCLApply() (项目经理提交闭环申请)
- **源逻辑摘要**:
  1. 校验project存在
  2. 权限校验(pm AND propm，两者都需满足)
  3. 校验服务经理是否有效
  4. 设置项目信息到evaluationHeader
  5. 调用pmClosedLoopService.addPmCLApply()启动Activiti工作流
  6. 更新项目最后刷新时间
  7. 重定向到项目修改页
- **目标实现**: Controller `pmApply()` → Service `pmApply()` 仅设置applyType/状态/时间后insert
- **状态**: ❌ 未迁移
- **差异说明**: 缺失①项目校验②权限校验(pm+propm)③服务经理有效性校验④evaluationHeader项目信息设置⑤Activiti工作流启动⑥项目刷新时间更新。Service层仅有最基础的insert操作。

### 方法: addSmCLApply() (服务经理审核)
- **源逻辑摘要**:
  1. 校验project和taskId
  2. 权限校验(sm AND prosm)
  3. 若驳回：校验项目经理(含B角)是否有效
  4. 设置项目信息、applyHeaderId
  5. 调用pmClosedLoopService.addSmCLApply()
  6. 重定向
- **目标实现**: Controller `smApply()` → Service `smApply()` 仅设置applyType后insert
- **状态**: ❌ 未迁移
- **差异说明**: 缺失①taskId校验②权限校验③驳回时PM有效性校验(含B角逻辑)④applyHeaderId获取⑤工作流集成。业务逻辑完全缺失。

### 方法: addCbCLApply() (回访人员操作 - 双分支)
- **源逻辑摘要**:
  - **分支1(pmClosedLoopResultType==1, 提交回访问卷)**:
    1. 权限校验(cb)
    2. 校验taskId、问卷模板、结果行
    3. 处理草稿状态
    4. 验证问卷选项(optMap)
    5. 计算评分(quesMark方法)
    6. 设置evaluationType=CB
    7. 调用addCbCLApplyQues()
    8. 成功后返回"seeScore"显示评分页
  - **分支2(pmClosedLoopResultType==2, 确认评分结果)**:
    1. 获取问卷结果头
    2. 若markResult==-1则设evaluationResult=-1
    3. 若驳回：校验PM有效性(含B角)
    4. 调用addCbCLApply()
- **目标实现**: Controller `cbApply()` → Service `cbApply()` 仅设置applyType后insert
- **状态**: ❌ 未迁移
- **差异说明**: 源码有两大分支、问卷评分计算、选项验证、草稿处理、评分结果展示等复杂逻辑。目标仅一行insert，完全缺失问卷交互流程和评分逻辑。

### 方法: cantCB() (无法闭环)
- **源逻辑摘要**:
  1. 校验project、taskId、evaluationComment
  2. 权限校验(cb)
  3. 校验服务经理是否有效
  4. 查询是否存在草稿CB评估，若有则递归删除(deletePmClEvaRecur)
  5. 设置evaluationResult=CANTCB
  6. 调用addCbCLApply()
- **目标实现**: Controller `cantClose(id, reason)` → Service `cantClose()` 设置applyState=3和closeReason
- **状态**: ⚠️ 部分迁移
- **差异说明**: 基本的"无法闭环"状态变更已迁移。缺失①权限校验②服务经理有效性校验③草稿CB评估的递归删除逻辑④工作流集成。

### 方法: addClCLApply() (工程人员操作 - 双分支)
- **源逻辑摘要**:
  - **分支1(pmClosedLoopResultType==1, 提交闭环建议问卷)**:
    1. 权限校验(cl)
    2. 校验taskId、问卷行、模板
    3. 验证选项、计算评分(quesMark)
    4. 设置evaluationType=CL
    5. 调用addCbCLApplyQues()
    6. 返回"seeScore"
  - **分支2(else, 确认评分结果)**:
    1. 获取问卷结果头
    2. 若驳回：校验PM有效性(含B角)
    3. 调用addClCLApply()
- **目标实现**: Controller `clApply()` → Service `clApply()` 仅设置applyType后insert
- **状态**: ❌ 未迁移
- **差异说明**: 与addCbCLApply类似，复杂的问卷提交/评分/确认流程完全缺失。

### 方法: pmSeeCbCl() (PM查看回访/闭环结果)
- **源逻辑摘要**:
  1. 获取evaluationHeader
  2. 获取问卷结果头和行
  3. 计算各类型评分(getQuesTypeScore)
  4. 获取问卷模板信息
  5. 获取评分规则说明(PmClosedLoopMarkFactory)
  6. 获取模板行和选项
- **目标实现**: 无对应端点
- **状态**: ❌ 未迁移
- **差异说明**: 查看闭环结果的功能完全缺失。

### 方法: getTaskId() (private - 获取任务ID)
- **源逻辑摘要**: 获取当前用户的私有任务ID，若无则获取公有任务ID
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: Activiti工作流任务管理未迁移。

### 方法: getProcessStatus() (private - 获取流程状态)
- **源逻辑摘要**: 获取流程任务ID、项目闭环状态、流程图部署ID
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 工作流状态查询未迁移。

### 方法: getHisProcess() (获取历史流程信息)
- **源逻辑摘要**: 遍历evaluationHeaderList，获取闭环建议/回访/申请头信息，查询问卷结果头/行，计算评分，获取问卷模板+选项+评分规则
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 历史流程信息加载是核心业务逻辑，完全缺失。

### 方法: getCbForm() / getClForm() (private - 获取回访/闭环表单)
- **源逻辑摘要**: 根据评估类型获取对应的问卷模板、结果、选项等表单数据
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移

### 方法: getQuesTypeScore() (private - 计算问题类型得分)
- **源逻辑摘要**: 按问题类型汇总分数，返回"类型名|类型ID\n分数"格式列表
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移

### 方法: getUserPower() (权限校验)
- **源逻辑摘要**: 基于角色(pm/sm/cb/cl/manager)和项目成员身份(propm/prosm)的复合权限校验，支持AND/OR组合
- **目标实现**: 无对应实现（注释称依赖Spring Security，但Controller中未见任何权限注解）
- **状态**: ❌ 未迁移
- **差异说明**: 业务级权限校验(项目成员身份判断)无法仅靠Spring Security角色实现，需要自定义逻辑。

### 方法: quesMark() (private - 问卷评分)
- **源逻辑摘要**:
  1. 遍历结果行，计算总分
  2. 拼接答案字符串(格式: 题型:题序-题号|选项,...;)
  3. 根据评分规则(PmClosedLoopMarkFactory)逐项评分
  4. 标记不合格题目(quesEvaResult=-1)
  5. 确定最终结果(pass/reject)
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 问卷评分是闭环模块的核心算法，完全缺失。

### 方法: getApplyHeaderId() (private - 获取申请头ID)
- **源逻辑摘要**: 查询项目对应的PM类型评估头ID
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移

---

## 二、PmClosedLoopQuesnaireAction → PmClosedLoopQuesnaireController

> ⚠️ **重要发现**: PmClosedLoopQuesnaireController 的所有方法均为空桩(stub)，仅返回 `R.ok()` 或空集合，无任何实际业务逻辑。无对应的Service实现类。

### 方法: execute() (问卷列表)
- **源逻辑摘要**: 初始化问卷对象，调用displayParam获取参数，查询问卷列表，设置分页信息
- **目标实现**: `list()` 返回空List
- **状态**: ❌ 未迁移
- **差异说明**: 空桩，无任何查询逻辑。

### 方法: addPCLQuesnaire() (新建问卷页面)
- **源逻辑摘要**: 获取问题类型列表(basicData)，获取所有评分规则说明
- **目标实现**: `create()` 空桩
- **状态**: ❌ 未迁移

### 方法: pmCLQuesEdit() (编辑问卷页面)
- **源逻辑摘要**:
  1. 校验问卷ID
  2. 获取评分规则说明
  3. 获取问题类型和行类型
  4. 获取问卷头信息
  5. 获取问卷行列表
  6. 获取问卷选项列表
- **目标实现**: `detail(id)` 返回空Map
- **状态**: ❌ 未迁移

### 方法: submitQues() (提交新问卷)
- **源逻辑摘要**: 校验问卷名称/类型，设置草稿状态，插入问卷头，返回"addQues"重定向
- **目标实现**: `submit(id, answers)` 空桩
- **状态**: ❌ 未迁移

### 方法: addLine() (添加问卷行)
- **源逻辑摘要**:
  - doType==2时调用editLine()
  - 否则：校验问卷存在，检查分数限制，计算下一题号
- **目标实现**: `addLine(id, line)` 空桩
- **状态**: ❌ 未迁移

### 方法: submitLine() (提交问卷行)
- **源逻辑摘要**:
  1. 校验行数据(头ID、题号、内容)
  2. 非AQ类型需校验选项
  3. 检查分数限制(checkScore)
  4. 编辑/新增分别调用不同方法
  5. 异常处理含堆栈信息
- **目标实现**: `submitLine(id, lineId)` 空桩
- **状态**: ❌ 未迁移

### 方法: updateQues() (更新问卷)
- **源逻辑摘要**: 校验所有字段，trim markIndexs，设置草稿状态，调用updateQuesHeader
- **目标实现**: `update(id, questionnaire)` 空桩
- **状态**: ❌ 未迁移

### 方法: deleteHeader() (删除问卷头)
- **源逻辑摘要**: 校验ID，调用deleteQuesHeader
- **目标实现**: `delete(id)` 空桩
- **状态**: ❌ 未迁移

### 方法: startEffective() (问卷生效)
- **源逻辑摘要**: 校验ID和quesType，设置SUBMIT状态和生效开始时间，调用updateEffecticeStart
- **目标实现**: `activate(id)` 空桩
- **状态**: ❌ 未迁移

### 方法: pmCLQuesSee() (查看问卷)
- **源逻辑摘要**: 获取问卷头、评分规则说明、行类型、行列表、选项列表
- **目标实现**: `view(id)` 返回空Map
- **状态**: ❌ 未迁移

### 方法: deleteLine() (删除问卷行)
- **源逻辑摘要**: 校验行ID，获取行信息，校验头ID和题号，删除行
- **目标实现**: `deleteLine(id, lineId)` 空桩
- **状态**: ❌ 未迁移

### 方法: editLine() (编辑问卷行)
- **源逻辑摘要**: 校验行ID，获取行信息，获取行类型，获取问卷头，检查分数限制，获取选项
- **目标实现**: `editLine(id, lineId)` 返回空Map
- **状态**: ❌ 未迁移

### 方法: endEffective() (问卷失效)
- **源逻辑摘要**: 校验ID，设置ENDEFFEC状态和生效结束时间，调用updateQuesStatus
- **目标实现**: `deactivate(id)` 空桩
- **状态**: ❌ 未迁移

### 辅助方法 (未迁移)
- `checkScore(int quesId, double newScore, int oldLineId)` - 按ID检查分数限制
- `checkScore(PmClosedLoopQuesnaire headerObj)` - 按头对象检查分数限制
- `checkScore(PmClosedLoopQuesnaire headerObj, List<PmClosedLoopQuesnaireLine> lineList)` - 按头+行列表检查分数限制
- **状态**: ❌ 未迁移

---

## 三、汇总表

### PmClosedLoopAction (16个方法)

| # | 方法 | 类型 | 状态 | 说明 |
|---|------|------|------|------|
| 1 | execute() | 主入口 | ❌ | 复杂页面加载逻辑完全缺失 |
| 2 | addPmCLApply() | 业务 | ❌ | 仅剩基础insert，缺失权限+校验+工作流 |
| 3 | addSmCLApply() | 业务 | ❌ | 仅剩基础insert，缺失权限+校验+工作流 |
| 4 | addCbCLApply() | 业务 | ❌ | 双分支问卷交互逻辑完全缺失 |
| 5 | cantCB() | 业务 | ⚠️ | 基本状态变更已迁移，缺失草稿清理+权限 |
| 6 | addClCLApply() | 业务 | ❌ | 双分支问卷交互逻辑完全缺失 |
| 7 | pmSeeCbCl() | 查询 | ❌ | 查看结果功能完全缺失 |
| 8 | getTaskId() | 辅助 | ❌ | 工作流任务管理未迁移 |
| 9 | getProcessStatus() | 辅助 | ❌ | 工作流状态未迁移 |
| 10 | getHisProcess() | 辅助 | ❌ | 历史流程信息加载未迁移 |
| 11 | getCbForm() | 辅助 | ❌ | 回访表单加载未迁移 |
| 12 | getClForm() | 辅助 | ❌ | 闭环表单加载未迁移 |
| 13 | getQuesTypeScore() | 辅助 | ❌ | 评分计算未迁移 |
| 14 | getUserPower() | 辅助 | ❌ | 业务级权限校验未迁移 |
| 15 | quesMark() | 辅助 | ❌ | 问卷评分核心算法未迁移 |
| 16 | getApplyHeaderId() | 辅助 | ❌ | 申请头ID获取未迁移 |

### PmClosedLoopQuesnaireAction (13个方法)

| # | 方法 | 类型 | 状态 | 说明 |
|---|------|------|------|------|
| 1 | execute() | 查询 | ❌ | 空桩返回空List |
| 2 | addPCLQuesnaire() | 页面 | ❌ | 空桩 |
| 3 | pmCLQuesEdit() | 页面 | ❌ | 空桩返回空Map |
| 4 | submitQues() | 业务 | ❌ | 空桩 |
| 5 | addLine() | 业务 | ❌ | 空桩 |
| 6 | submitLine() | 业务 | ❌ | 空桩 |
| 7 | updateQues() | 业务 | ❌ | 空桩 |
| 8 | deleteHeader() | 业务 | ❌ | 空桩 |
| 9 | startEffective() | 业务 | ❌ | 空桩 |
| 10 | pmCLQuesSee() | 查询 | ❌ | 空桩返回空Map |
| 11 | deleteLine() | 业务 | ❌ | 空桩 |
| 12 | editLine() | 业务 | ❌ | 空桩返回空Map |
| 13 | endEffective() | 业务 | ❌ | 空桩 |

### 总体统计

| 指标 | 数量 |
|------|------|
| 源方法总数 | 29 |
| ✅ 完全迁移 | 0 |
| ⚠️ 部分迁移 | 1 (cantCB) |
| ❌ 未迁移 | 28 |
| **迁移完成率** | **约 3.4%** |

---

## 四、关键缺失分析

### 1. 工作流引擎集成 (最严重)
原系统深度依赖Activiti工作流引擎，包括：
- 流程启动(addPmCLApply/addSmCLApply等)
- 任务管理(私有/公有任务ID)
- 流程状态查询
- 流程图查看
- 任务完成回调

新系统Service中多处注释"暂不集成工作流引擎"，这是最大的迁移缺口。

### 2. 问卷评分系统
原系统有完整的问卷管理生命周期：
- 问卷模板CRUD(含行、选项)
- 问卷填写与草稿保存
- 自动评分(quesMark方法，含评分规则工厂模式)
- 评分结果展示
- 按题型汇总评分

新系统PmClosedLoopQuesnaireController全部为空桩，无任何实现。

### 3. 业务权限校验
原系统的getUserPower()实现了基于角色+项目成员身份的复合权限校验，新系统Controller中无任何权限注解或校验逻辑。

### 4. 数据模型差异
原系统使用PmClEvaluationHeader/PmClQuesnaireResultHeader等精细数据模型，新系统使用简化的PmClosedLoop单一实体，数据模型的简化导致大量业务逻辑无法直接映射。

### 5. 问卷管理服务缺失
新系统没有PmClosedLoopQuesnaireService的实现类，Mapper文件虽存在但无Service层封装。

---

## 五、迁移建议

1. **优先级P0**: 设计并实现工作流集成方案(建议引入Flowable替代Activiti)
2. **优先级P0**: 实现问卷评分核心算法(quesMark + PmClosedLoopMarkFactory)
3. **优先级P1**: 实现问卷模板CRUD完整Service层
4. **优先级P1**: 实现业务级权限校验(Spring Security + 自定义权限判断)
5. **优先级P2**: 补充所有Controller空桩的实际业务逻辑
6. **优先级P2**: 评估是否需要保留原系统的精细数据模型(评估头/结果头/结果行三层结构)
