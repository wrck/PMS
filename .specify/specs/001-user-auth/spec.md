# Feature Specification: 001-user-auth(用户与权限)

**Feature Branch**: `001-user-auth`
**Created**: 2026-07-09
**Status**: Draft
**Source**: 逆向反推自 PMS-struts + core 代码

> 域职责:用户认证、授权、组织架构、登录会话、密码管理、CAS 单点登录。
> 逆向来源基线(仅作溯源凭证,不约束新系统技术栈):老架构(Struts)与新架构(过渡,REST 前缀 `/system`)两套端点;数据契约见下文"Key Entities > 数据契约"。
> 文中"证据"行均为逆向溯源引用,描述的是"系统做什么",不规定"代码怎么做"。

---

## User Scenarios & Testing

> 用户故事按重要性排序并赋予优先级(P1 最关键)。每个故事可独立测试,单独实现其一即可构成可用 MVP 切片。
> 验收场景以 Given/When/Then 表达,由功能需求反推。

### User Story 1 - 账号密码登录 (Priority: P1)

As a 系统用户, I want 通过用户名和密码登录系统, so that 我能访问被授权的功能。

**Why this priority**: 登录是所有功能的入口,未启用 CAS 时的唯一进入方式,属最关键能力。

**Independent Test**: 在未启用 CAS 的环境下,用一个有效账号提交正确凭证,可完成登录并跳转至默认页面。

**Acceptance Scenarios**:
1. **Given** 系统未启用 CAS 且生产环境开关开启,**When** 用户提交正确用户名、密码与匹配的验证码,**Then** 登录成功并跳转至用户默认页面,会话写入用户身份与权限。
2. **Given** 生产环境开关开启,**When** 用户提交的验证码与会话中存储的不一致,**Then** 登录失败并提示验证码错误。
3. **Given** 用户存在且状态为停用(status≠1),**When** 用户提交凭证,**Then** 登录失败并提示用户被停用。
4. **Given** 用户存在且启用,**When** 用户提交的密码与库中密码(忽略大小写)不一致,**Then** 登录失败并提示"用户名或密码错误"。
5. **Given** 测试环境(环境开关非生产值),**When** 用户提交任意用户名(忽略密码),**Then** 系统使用库中密码直接放行,跳转默认页面。

---

### User Story 2 - CAS 单点登录 (Priority: P1)

As a 企业用户, I want 通过 CAS 单点登录直接进入系统而无需再次输入密码, so that 我能在多个系统间无缝切换。

**Why this priority**: 生产环境默认登录方式,影响所有企业用户进入系统。

**Independent Test**: 启用 CAS 后,从 CAS 服务器认证回调本系统,可完成登录并加载权限。

**Acceptance Scenarios**:
1. **Given** 系统启用 CAS,**When** CAS 服务器认证通过并回调本系统携带有效票据,**Then** 系统从断言中提取用户名,不校验密码,加载用户权限并跳转默认页面。
2. **Given** 系统启用 CAS,**When** CAS 断言中用户名为空或用户在本系统不存在,**Then** 系统登出并跳转 CAS 错误页。
3. **Given** CAS 登录成功,**When** 登录完成,**Then** CAS 票据与会话 ID 建立映射存储,供后续单点登出使用。

---

### User Story 3 - 登出 (Priority: P1)

As a 已登录用户, I want 登出系统, so that 我的会话被终止、防止他人继续操作。

**Why this priority**: 会话终止是安全基线,任何登录系统必备。

**Independent Test**: 登录后点击登出,会话销毁,再次访问受保护资源被拒绝。

**Acceptance Scenarios**:
1. **Given** 用户已登录且系统启用 CAS,**When** 用户点击登出,**Then** 重定向至 CAS 登出地址并销毁当前会话。
2. **Given** 用户已登录且系统未启用 CAS,**When** 用户点击登出,**Then** 重定向至首页并销毁当前会话。

---

### User Story 4 - 密码重置 (Priority: P1)

As a 管理员, I want 重置用户密码为随机密码并强制下线, so that 用户忘记密码时能重新获得访问。

**Why this priority**: 密码恢复是账户可用性的关键保障。

**Independent Test**: 对一个已存在用户执行密码重置,密码被更新为随机串、立即过期、用户被强制下线、邮件被发出。

**Acceptance Scenarios**:
1. **Given** 用户存在,**When** 管理员执行密码重置,**Then** 系统生成随机密码加密存储,密码过期时间置为当前时间(立即过期)。
2. **Given** 密码已重置,**When** 重置完成,**Then** 系统调用密码服务将该用户强制下线,并发送含明文随机密码的重置通知邮件。

---

### User Story 5 - 修改密码 (Priority: P1)

As a 用户, I want 修改自己的登录密码, so that 密码更安全。

**Why this priority**: 密码自主管理与过期续期必备。

**Independent Test**: 用户提交新密码后,密码更新且过期时间延后 3 个月。

**Acceptance Scenarios**:
1. **Given** 用户已登录,**When** 用户提交新密码,**Then** 系统更新密码并将密码过期时间置为当前时间 +3 个月。

---

### User Story 6 - 新增用户 (Priority: P1)

As a 管理员, I want 创建新用户并分配角色、菜单、部门, so that 新员工能登录系统使用对应功能。

**Why this priority**: 用户供给是权限体系运转的起点。

**Independent Test**: 管理员提交合法表单后,用户主记录与权限关联被创建,非 CAS 模式下发出开通邮件。

**Acceptance Scenarios**:
1. **Given** 管理员提交合法用户表单,**When** 系统处理新增,**Then** 生成 8 位随机密码加密存储,状态默认启用,写入用户主记录与权限关联,非 CAS 模式发出含明文密码的开通邮件。
2. **Given** 表单中用户名/真实姓名/邮箱/菜单 ID 串/默认页任一为空或含空格,**When** 提交新增,**Then** 校验失败并拒绝。
3. **Given** 用户名已存在,**When** 提交新增,**Then** 校验失败并提示用户名重复。

---

### User Story 7 - 编辑用户 (Priority: P1)

As a 管理员, I want 修改用户基本信息、角色、菜单、部门权限, so that 用户权限变动能及时生效。

**Why this priority**: 权限变更是日常运维的高频操作,直接影响安全与可用性。

**Independent Test**: 管理员修改用户角色与菜单授权后,旧关联被替换为新关联,缓存失效,用户新权限生效。

**Acceptance Scenarios**:
1. **Given** 管理员提交合法编辑表单,**When** 系统处理编辑,**Then** 更新用户主记录,customInfo 以 JSON 合并方式更新,旧菜单授权/角色关联按规则替换。
2. **Given** 编辑时将状态置为停用,**When** 提交编辑,**Then** 生效终止时间被置为当前时间。
3. **Given** 新架构下非 admin 用户编辑他人信息,**When** 提交编辑,**Then** 返回未授权。

---

### User Story 8 - 用户列表查询 (Priority: P2)

As a 管理员, I want 分页查询用户列表并按用户名/角色/部门筛选, so that 我能了解系统所有用户情况。

**Why this priority**: 用户运维的管理入口,依赖登录与权限校验,但非最核心入口。

**Independent Test**: 具备管理角色的用户访问列表页,可分页查看并按条件筛选。

**Acceptance Scenarios**:
1. **Given** 当前用户具备管理角色之一,**When** 访问用户管理页并提交筛选条件,**Then** 返回按用户名/真实姓名/角色/部门筛选后的分页列表与总数,角色 ID 串转换为角色名展示。
2. **Given** 当前用户不具备任一管理角色,**When** 访问用户管理页,**Then** 抛出"没有访问权限"。

---

### User Story 9 - 删除用户 (Priority: P2)

As a 管理员, I want 删除用户及其角色关联, so that 离职员工账号不再占用系统资源。

**Why this priority**: 账户生命周期收尾,重要但频率较低。

**Independent Test**: 管理员删除用户后,该公司下扩展信息与角色关联被清除,若无残留则删除主记录。

**Acceptance Scenarios**:
1. **Given** 用户在某公司下存在扩展信息与角色关联,**When** 管理员执行删除,**Then** 删除该公司下扩展信息与角色关联;若所有公司均无扩展信息残留,则删除用户主记录。

---

### User Story 10 - 用户名唯一性校验 (Priority: P2)

As a 管理员, I want 在创建用户时校验用户名是否已存在, so that 避免账号冲突。

**Why this priority**: 提升新增体验与数据质量,非阻断核心流程。

**Independent Test**: 输入用户名实时查询,返回是否可用。

**Acceptance Scenarios**:
1. **Given** 用户名在有效期内已存在,**When** 提交校验,**Then** 返回不可用。
2. **Given** 新架构下用户主记录存在但当前公司下无扩展信息,**When** 提交校验,**Then** 返回可用。

---

### User Story 11 - 角色列表查询 (Priority: P2)

As a 管理员, I want 分页查询角色列表, so that 我能管理系统中所有角色。

**Why this priority**: 角色管理基础入口,服务于权限分配。

**Independent Test**: 管理员访问角色管理页,可分页查询角色。

**Acceptance Scenarios**:
1. **Given** 管理员访问角色管理,**When** 提交分页与筛选参数,**Then** 返回角色分页列表(含名称/状态/默认页/备注/有效期)。

---

### User Story 12 - 新增角色(含菜单权限) (Priority: P2)

As a 管理员, I want 创建角色并为其分配菜单及操作权限(增/删/查/改), so that 该角色用户获得对应功能权限。

**Why this priority**: 角色权限模型构建的核心操作。

**Independent Test**: 提交合法角色表单与菜单权限列表后,角色与角色菜单关联被创建。

**Acceptance Scenarios**:
1. **Given** 管理员提交合法角色表单与非空菜单权限列表,**When** 系统处理新增,**Then** 写入角色主记录(默认页固定值),并逐条写入角色菜单权限。
2. **Given** 菜单权限列表为空或角色名为空/含空格,**When** 提交新增,**Then** 校验失败并拒绝。

---

### User Story 13 - 编辑角色(含菜单权限) (Priority: P2)

As a 管理员, I want 修改角色信息及其菜单权限, so that 角色权限变更能反映到关联用户。

**Why this priority**: 权限动态调整的必备能力。

**Independent Test**: 修改角色菜单权限后,旧权限被替换,用户缓存失效。

**Acceptance Scenarios**:
1. **Given** 管理员提交合法编辑表单,**When** 系统处理编辑,**Then** 更新角色主记录,删除原角色菜单权限后重新插入新权限,触发用户缓存刷新。

---

### User Story 14 - 删除角色 (Priority: P2)

As a 管理员, I want 删除不再使用的角色, so that 角色列表保持精简。

**Why this priority**: 角色生命周期管理,低频。

**Independent Test**: 删除角色后,角色记录被移除。

**Acceptance Scenarios**:
1. **Given** 角色存在,**When** 管理员执行删除,**Then** 角色记录被删除。

---

### User Story 15 - 角色菜单授权(批量) (Priority: P2)

As a 管理员, I want 为角色批量勾选菜单, so that 角色用户能访问对应菜单。

**Why this priority**: 权限批量调整的高效入口。

**Independent Test**: 提交角色 ID 与菜单 ID 串后,旧关联全删,新关联批量插入。

**Acceptance Scenarios**:
1. **Given** 管理员提交角色 ID 与菜单 ID 串,**When** 系统处理授权,**Then** 删除该角色全部角色菜单关联;若菜单 ID 串非空,按逗号拆分批量插入新关联。

---

### User Story 16 - 用户角色绑定/解绑 (Priority: P2)

As a 管理员, I want 批量为用户绑定或解绑角色(支持多租户), so that 用户能获得或失去一组角色权限。

**Why this priority**: 多租户角色分配能力。

**Independent Test**: 批量绑定后用户角色关联被插入(含公司标识),解绑后按主键删除。

**Acceptance Scenarios**:
1. **Given** 管理员提交绑定列表(JSON 数组,含 userId/roleId),**When** 系统处理绑定,**Then** 为每条记录补当前公司标识后批量插入。
2. **Given** 管理员提交解绑主键数组,**When** 系统处理解绑,**Then** 按主键批量删除用户角色关联。

---

### User Story 17 - 邮件通知(账号开通/密码重置) (Priority: P2)

As a 系统, I want 在用户新增或密码重置时自动发送通知邮件, so that 用户能获知账号与初始密码。

**Why this priority**: 通知可靠性影响首次登录体验,但可降级处理。

**Independent Test**: 用户新增/密码重置时,邮件被持久化到队列并按期望发送时间轮询发送。

**Acceptance Scenarios**:
1. **Given** 用户新增或密码重置完成,**When** 系统生成通知,**Then** 邮件记录(含主题/正文/收件人/期望发送时间)被持久化到邮件队列,发送标志置为未发。
2. **Given** 邮件队列中存在期望发送时间早于当前的未发邮件,**When** 轮询发送,**Then** 邮件被发送,发送标志置为已发,实际发送时间置为当前时间。

---

### User Story 18 - 菜单管理(树) (Priority: P3)

As a 管理员, I want 维护系统菜单树(含层级关系), so that 功能导航结构清晰可配置。

**Why this priority**: 菜单是权限载体,但结构变更低频。

**Independent Test**: 管理员可查询菜单树,新增/编辑/删除菜单,删除时联动清除角色菜单关联。

**Acceptance Scenarios**:
1. **Given** 管理员访问菜单管理,**When** 查询菜单树,**Then** 返回按父级关系组织的有效期内菜单树。
2. **Given** 删除某菜单,**When** 系统处理删除,**Then** 联动删除该菜单在所有角色下的角色菜单关联。

---

### User Story 19 - 部门列表查询 (Priority: P3)

As a 管理员, I want 分页查询部门列表, so that 我能管理组织架构。

**Why this priority**: 组织数据维护入口,服务于区域权限。

**Independent Test**: 管理员可分页查询部门并按编号/名称筛选。

**Acceptance Scenarios**:
1. **Given** 管理员访问部门管理,**When** 提交分页与筛选参数,**Then** 返回按编号/名称模糊筛选的分页部门列表。

---

### User Story 20 - 部门从 SAP 同步刷新 (Priority: P3)

As a 管理员, I want 从 SAP 数据源同步最新部门信息, so that PMS 部门数据与组织主数据一致。

**Why this priority**: 主数据一致性保障,周期性运维操作。

**Independent Test**: 触发刷新后,本地部门表被清空并从 SAP 视图重写。

**Acceptance Scenarios**:
1. **Given** 管理员触发部门刷新,**When** 系统处理同步,**Then** 清空本地部门表,从 SAP 视图查询部门数据,重新写入本地部门表。

---

### User Story 21 - 部门新增 (Priority: P3)

As a 管理员, I want 新增部门, so that 新设组织单元能被纳入系统。

**Why this priority**: SAP 之外的手工补录,低频。

**Independent Test**: 提交合法部门表单后,部门记录被创建。

**Acceptance Scenarios**:
1. **Given** 管理员提交合法部门表单(编号/名称),**When** 系统处理新增,**Then** 写入部门记录,创建时间与生效起始置为当前时间,返回新记录 ID。

---

### User Story 22 - 基础数据管理 (Priority: P3)

As a 管理员, I want 维护基础数据字典(按类型分类), so that 业务表单的下拉选项可动态配置。

**Why this priority**: 数据字典支撑业务表单,但属配置类能力。

**Independent Test**: 按类型查询有效基础数据列表,新增/更新时校验组合唯一性。

**Acceptance Scenarios**:
1. **Given** 管理员按数据类型查询,**When** 提交查询,**Then** 返回有效期内按排序号升序的基础数据列表。
2. **Given** 新增基础数据,**When** 类型编码+数据 ID 组合已存在,**Then** 校验失败并拒绝。

---

### User Story 23 - 操作日志查询 (Priority: P3)

As a 管理员, I want 分页查询系统操作日志, so that 我能审计用户行为。

**Why this priority**: 审计能力,依赖日志记录先于查询。

**Independent Test**: 管理员可分页查询日志,日志补全操作人真实姓名。

**Acceptance Scenarios**:
1. **Given** 管理员访问日志查询,**When** 提交分页与排序参数,**Then** 返回按时间排序的分页日志列表(关联用户表补全真实姓名)与总数。

---

### User Story 24 - 任务委派规则设置 (Priority: P3)

As a 用户, I want 将我的流程任务委派给他人(按流程定义、按时间段), so that 我外出期间流程不被阻塞。

**Why this priority**: 流程委派为便利性能力,非用户与权限核心。

**Independent Test**: 用户设置委派后,可按被委派人查询生效中委派记录。

**Acceptance Scenarios**:
1. **Given** 用户提交委派规则(原负责人/被委派人/时间段/流程定义),**When** 系统处理新增/更新,**Then** 写入委派记录,操作时间置为当前时间。
2. **Given** 按被委派人查询生效中委派,**When** 查询条件为状态生效且时间区间包含当前时间(或起止均空),**Then** 返回委派列表;同一原负责人+被委派人+流程定义模糊命中只取首条。

---

### Edge Cases

> 由功能需求的异常处理与边界条件反推。

- **验证码错误**: 生产环境下提交的验证码与会话中存储的不一致 → 登录失败,提示验证码错误。
- **用户不存在**: 登录或 CAS 回调的用户名在本系统不存在 → 登录失败/CAS 错误页。
- **密码错误**: 提交密码与库中密码(忽略大小写)不一致 → 提示"用户名或密码错误"。
- **用户被停用(status≠1)**: 用户状态非启用 → 登录失败,提示用户被停用。
- **用户不在有效期**: 当前时间不在生效区间内 → 视为无效用户,拒绝登录。
- **CAS 断言缺失**: CAS 断言中用户名为空 → 登出并跳转 CAS 错误页。
- **CAS 单点登出会话映射缺失**: CAS 登出请求携带的票据无对应会话映射 → 标记失效无法生效,后续请求按非登出请求继续过滤链。
- **字段校验失败(含空格/为空)**: 用户名/真实姓名/邮箱/菜单 ID 串/默认页/角色名任一为空或含空格 → 拒绝提交。
- **用户名重复**: 新增时用户名在有效期内已存在 → 校验失败,提示重复。
- **无管理权限访问用户管理**: 当前用户不具备任一管理角色 → 抛出"没有访问权限"。
- **非本人且非 admin 访问用户详情/编辑(新架构)**: → 重定向至未授权页。
- **菜单权限列表为空/角色名非法**: 新增或编辑角色时 → 校验失败并拒绝。
- **角色菜单授权 menuIds 为空**: 批量授权时仅删除原关联,不插入新关联。
- **多租户跨租户访问**: 系统用户访问自身数据时公司标识置 -1(跨租户),其他用户严格按公司隔离。
- **邮件发送失败/延迟**: 邮件按期望发送时间轮询,未到时间不取出;发送失败保留未发标志,后续轮询重试。
- **SAP 部门同步失败**: 同步采用全量清空+重写,同步失败将导致本地部门表为空,需有补偿或确认机制。
- **缓存与库不一致**: 写操作触发缓存失效,但失效与并发读之间存在窗口;手动刷新记录最近刷新时间以供追溯。
- **用户菜单/角色授权更新并发**: 更新采用"全删再重插"策略,并发更新可能产生瞬时数据缺失。
- **老用户表(user)/老角色表(role)与新表并存**: 日志查询、委派查询、密码更新仍引用老表名,与主流程使用的新表存在歧义(详见数据契约中 user/role 表的澄清标注)。

---

## Requirements

### Functional Requirements

> 处理规则以行为描述,不涉及技术实现细节。

- **FR-001: 账号密码登录(非 CAS 模式)**
  - **触发条件**: 用户在登录页提交用户名+密码(+验证码),且系统未启用 CAS。
  - **输入**: 用户名(username)、密码(password)、验证码(validation,可选)、客户端 IP。
  - **处理规则**:
    1. 读取系统参数 `sys.envirment.argu`:若值为 `1`(生产环境)则校验验证码与当前会话中存储的验证码一致,不一致则失败。
    2. 按用户名查询有效用户(status=1,且在有效期内)。
    3. 若环境参数非 `1`(测试环境),忽略提交密码,直接使用库中密码。
    4. 比对提交密码与库中密码(忽略大小写);一致则继续,否则返回"用户名或密码错误"。
    5. 加载用户菜单权限(menuCode→menuValue)。
    6. 计算区域权限(areapower):若为 `-1` 且 dpNo 非空,用 dpNo 替换;若 areapower 不含 dpNo,则追加 dpNo;再按市场/用服办事处规则补充 [待澄清]。
    7. 按 roleIds(格式 `;id;` 分隔)逐个角色加载角色菜单操作权限,menuPower 编码:`8`=新增,`1`=删除,`4`=查询,`2`=更新,逗号分隔。
    8. 查询用户默认页面(defaultPage)。
    9. 将用户身份、菜单权限、角色菜单操作权限、默认页面写入会话上下文。
    10. 记录登录操作日志。
  - **输出**: 登录成功跳转至用户默认页面;失败返回登录页并提示错误。
  - **异常**: 验证码错误;用户不存在;密码错误;用户被停用(status≠1);用户不在有效期。

- **FR-002: CAS 单点登录**
  - **触发条件**: 系统启用 CAS(`sys.cas=1`),用户被重定向至 CAS 服务器完成认证后回调本系统。
  - **输入**: CAS 票据(ticket)、客户端 IP。
  - **处理规则**:
    1. 从 CAS 断言中提取用户名(principal.name)。
    2. 用户名为空或用户不存在则登出并返回 CAS 错误页。
    3. 不校验密码,直接执行 FR-001 步骤 5-10 的后续加载流程。
    4. CAS 票据与会话 ID 建立映射并存储,用于后续单点登出。
  - **输出**: 登录成功跳转默认页面;失败跳转 CAS 错误页。
  - **异常**: CAS 断言缺失;用户不存在于本系统;加载权限异常。

- **FR-003: CAS 单点登出**
  - **触发条件**: CAS 服务器向本系统发送 POST 请求,参数含 `logoutRequest`。
  - **处理规则**:
    1. 从 `logoutRequest` XML 中解析 `SessionIndex`(即原 ticket)。
    2. 根据 ticket 查找已记录的会话 ID,获取对应会话。
    3. 在会话上标记 `logoutRequest=true`,使其失效。
    4. 后续请求检测到该标记时执行 subject 登出。
    5. 非登出请求继续过滤链。
  - **输出**: 对应用户会话被终止。

- **FR-004: 主动登出**
  - **触发条件**: 用户点击登出。
  - **处理规则**:
    1. CAS 模式:重定向至 CAS 登出地址(`https://cas.dptech.com:8443/logout?service={当前URL前缀}/Login.action`)。
    2. 非 CAS 模式:重定向至 `index.jsp`。
    3. 销毁当前会话。
  - **输出**: 跳转至对应登出/首页地址。

- **FR-005: 用户列表分页查询**
  - **触发条件**: 管理员访问用户管理页(需具备 admin/engineemanager/engineemanager-leader 任一角色)。
  - **输入**: 分页参数(offset、pagesize、sort、order)、筛选条件(username/realName 模糊、roleIds 模糊、dpNo)。
  - **处理规则**:
    1. 权限校验:无上述角色则抛出"没有访问权限"。
    2. 按 username/realName 模糊、roleIds 模糊、dpNo(等于或 FIND_IN_SET 于 areapower)动态拼装查询。
    3. 支持排序字段与方向,支持 limit 分页。
    4. 查询结果中 roleIds(格式 `;id;`)转换为角色名展示串。
    5. 同时返回角色下拉、部门下拉供筛选。
  - **输出**: 用户列表(含角色名)、总数。
  - **异常**: 无权限。
  - **新架构补充**: 支持按公司(compId)隔离查询,返回 total/filtered 两种计数。

- **FR-006: 新增用户**
  - **触发条件**: 管理员提交新增用户表单。
  - **输入**: username、realName、email、roleids、usermenuids、defaultPage、dpNo、customInfo(可选)。
  - **处理规则**:
    1. 校验 username/realName/email/usermenuids/defaultPage 均非空且不含空格。
    2. 生成 8 位随机密码,以 MD5(password+username) 方式加密存储。
    3. 默认 status=1、isemail=1,createTime/effectiveFrom=now。
    4. 写入用户主记录,取回自增主键。
    5. 非 CAS 模式下,发送"账号已开通"邮件(含明文随机密码)至用户邮箱。
    6. 老架构:写入用户菜单授权(用户菜单表,menuValue=1);新架构:写入用户角色关联(含 compId)。
  - **输出**: 新用户 ID;重定向至列表页。
  - **异常**: 字段校验失败;用户名重复。

- **FR-007: 编辑用户**
  - **触发条件**: 管理员提交编辑用户表单。
  - **输入**: id、username、email、realName、status、roleids、usermenuids、defaultPage、dpNo、effectiveTo、customInfo、changeType(可选)、newMemberCode(可选)。
  - **处理规则**:
    1. 字段校验同 FR-006。
    2. 若 status=0(停用),设置 effectiveTo=now。
    3. customInfo 以 JSON_MERGE_PATCH 与现有值合并(IFNULL 时以 `{}` 为底)。
    4. 若提供 changeType(service/program/both)与 newMemberCode,联动批量更新项目服务经理/项目经理 [待澄清:具体联动规则]。
    5. 老架构:删除原用户菜单授权后重新插入;新架构:计算新旧角色差集,增量增删用户角色关联。
  - **输出**: 重定向至列表页。
  - **异常**: 字段校验失败;非本人且非 admin 时返回未授权(新架构)。

- **FR-008: 删除用户**
  - **触发条件**: 管理员请求删除用户。
  - **处理规则**:
    1. 删除该用户在当前公司下的用户扩展信息(UserInfo)。
    2. 删除该用户在当前公司下的所有用户角色关联。
    3. 若该用户在所有公司下均无扩展信息残留,则删除用户主记录。
  - **输出**: 无返回体。

- **FR-009: 用户名唯一性校验**
  - **触发条件**: 新增用户前实时校验。
  - **输入**: userName。
  - **处理规则**:
    1. 老架构:统计用户信息表中 username 命中且在有效期内记录数,返回计数。
    2. 新架构:按用户名查用户主记录;若用户主记录存在但当前公司下无扩展信息,仍视为可用。
  - **输出**: 是否可用(valid=true/false)。

- **FR-010: 密码重置**
  - **触发条件**: 管理员对某用户执行密码重置。
  - **处理规则**:
    1. 查询用户信息。
    2. 生成随机密码并以 MD5(password+username) 加密。
    3. 设置 pwdoverdue=now(立即过期,强制改密)。
    4. 更新用户密码。
    5. 发送"账号密码已重置"邮件(含明文随机密码)。
    6. 调用密码服务将该用户强制下线。
  - **输出**: result=1。

- **FR-011: 修改密码**
  - **触发条件**: 用户提交修改密码。
  - **输入**: id、newPassword。
  - **处理规则**:
    1. 更新 password=newPassword。
    2. 设置 pwdoverdue=now+3 月(3 个月后过期)。
  - **输出**: 更新成功。

- **FR-012: 角色列表分页查询**
  - **触发条件**: 管理员访问角色管理。
  - **输入**: 分页参数、roleName(模糊)、id(精确非 0)。
  - **处理规则**: 按条件动态拼装,支持排序与 limit 分页。
  - **输出**: 角色列表(含 roleName/status/defaultPage/roleRemark/effectiveFrom/effectiveTo)。

- **FR-013: 新增角色**
  - **触发条件**: 管理员提交新增角色表单。
  - **输入**: roleName、status、roleRemark、effectiveTo、rolemenuidList(菜单权限列表)。
  - **处理规则**:
    1. 校验:rolemenuidList 非空;roleName 非空且不含空格。
    2. 默认页面固定写入 `module/Welcome1.action` [待澄清:是否应可配置]。
    3. 若 status=0,effectiveTo=now。
    4. 写入角色主记录,取回自增主键。
    5. 逐条写入角色菜单权限(角色菜单表:roleId/menuPower/menuId)。
  - **输出**: 新角色 ID。
  - **异常**: 菜单权限为空;角色名非法。

- **FR-014: 编辑角色**
  - **触发条件**: 管理员提交编辑角色表单。
  - **处理规则**:
    1. 校验同 FR-013。
    2. 更新角色主记录(含默认页面固定值)。
    3. 删除原角色菜单权限,重新插入新菜单权限。
    4. 角色/菜单操作触发用户缓存刷新。
  - **输出**: 更新结果。

- **FR-015: 菜单管理**
  - **触发条件**: 管理员访问菜单管理。
  - **处理规则**:
    1. 查询菜单树(superId 父子关系,有效期内)。
    2. 详情查询时若 pid 非 0,补全父菜单名称。
    3. 新增/编辑/删除菜单。
    4. 删除菜单时联动删除该菜单在所有角色下的角色菜单关联。
  - **输出**: 菜单树/菜单详情。

- **FR-016: 角色菜单授权(批量)**
  - **触发条件**: 管理员为某角色勾选菜单并提交。
  - **输入**: roleId、menuIds(逗号分隔)。
  - **处理规则**:
    1. 删除该角色全部角色菜单关联。
    2. 若 menuIds 非空,按逗号拆分批量插入新关联。
  - **输出**: 无返回体。

- **FR-017: 用户角色绑定/解绑**
  - **触发条件**: 管理员批量绑定或解绑用户角色。
  - **输入**: userRoleListStr(JSON 数组,含 userId/roleId) 或 ids(JSON 整数数组)。
  - **处理规则**:
    1. 绑定:解析 JSON,为每条记录补 compId(当前组织)后批量插入。
    2. 解绑:按主键批量删除。
  - **输出**: 无返回体。

- **FR-018: 部门列表分页查询**
  - **触发条件**: 管理员访问部门管理。
  - **输入**: 分页参数、departmentNum(模糊)、departmentName(模糊)。
  - **处理规则**: 动态拼装查询,支持排序与分页。
  - **输出**: 部门列表。

- **FR-019: 部门从 SAP 同步刷新**
  - **触发条件**: 管理员触发部门刷新。
  - **处理规则**:
    1. TRUNCATE 本地部门表。
    2. 从 SAP 视图 `dp_reports.dp_v_spms_department` 查询部门(OcrCode/OcrName)。
    3. 重新写入本地部门表。
  - **输出**: 重定向至部门列表。

- **FR-020: 部门新增**
  - **触发条件**: 管理员提交新增部门。
  - **输入**: departmentNum、departmentName。
  - **处理规则**: 写入部门表,createTime/effectiveFrom=now。
  - **输出**: 新记录 ID。

- **FR-021: 基础数据字典维护**
  - **触发条件**: 管理员维护基础数据。
  - **处理规则**:
    1. 按数据类型(dataTypeCode)查询在有效期内的基础数据列表,按 sortId 排序。
    2. 新增:校验 dataTypeCode+basicDataId 不重复。
    3. 更新:可改 basicDataName/sortId/effectiveTo。
    4. 支持父子结构(basicDataAttri1 关联父 basicDataId)。
  - **输出**: 基础数据列表/单条。

- **FR-022: 操作日志记录与查询**
  - **触发条件**: 登录、关键操作触发日志记录;管理员查询日志。
  - **处理规则**:
    1. 记录:写入操作日志表(USER_NAME/IP/INFO/TIME)。
    2. 查询:LEFT JOIN 用户表补充 realName,支持排序与分页,返回总数。
  - **输出**: 日志列表。

- **FR-023: 用户数据权限(区域权限)维护**
  - **触发条件**: 用户编辑时维护 areapower。
  - **处理规则**:
    1. 若用户已有区域权限记录,更新 areapower/updateTime/updateBy。
    2. 否则插入新记录(fndUserId/username/areapower/createTime/createBy/effectiveFrom)。
  - **输出**: 更新/插入结果。

- **FR-024: 任务委派规则设置**
  - **触发条件**: 用户设置流程委派。
  - **输入**: owner(原负责人)、assignee(被委派人)、startTime、endTime、procdefId(流程定义,模糊)、status、cause。
  - **处理规则**:
    1. 新增/更新委派记录(handleTime=now)。
    2. 按 assignee 查询生效中委派:status=1 且时间区间包含 now(或起止均为空)。
    3. 同 owner+assignee+procdefId 模糊命中只取首条。
  - **输出**: 委派列表/单条。

- **FR-025: 缓存刷新**
  - **触发条件**: 用户/角色/菜单/权限写操作;或手动触发。
  - **处理规则**:
    1. 写操作(insert/update/delete)触发用户权限缓存或基础数据缓存失效。
    2. 手动刷新:更新系统参数中 `sys.cache.latest.refreshTime`=now。

- **FR-026: 系统参数查询**
  - **触发条件**: 业务读取配置项。
  - **处理规则**: 按 code 查询系统参数表,需在有效期内(effectiveFrom<=now 且 effectiveTo 为空或 >now)。
  - **输出**: var 值。

### Key Entities

> 本域涉及的核心实体(行为视角)。完整字段级数据契约见下文"数据契约"子节,该子节为新系统复用/迁移 DB 的唯一事实来源。

- **用户(User)**: 系统主体的身份与凭证载体,关联角色、菜单授权、区域权限、部门、扩展信息;支持多租户隔离(按公司)。
- **角色(Role)**: 权限聚合单元,通过角色菜单授权关联菜单与操作权限(增/删/查/改编码)。
- **菜单(Menu)**: 系统功能导航与权限载体,树形结构;用户默认页面与菜单路径关联。
- **用户菜单授权(UserMenu)**: 用户直接获得的菜单授权(绕过角色)。
- **角色菜单授权(RoleMenu)**: 角色到菜单及操作权限的关联。
- **用户角色关联(UserRole)**: 用户与角色的多对多关联,按公司隔离。
- **用户区域权限(UserPower)**: 用户的数据/区域权限,登录时与部门编号合并。
- **部门(Department)**: 组织单元,支持从 SAP 主数据同步。
- **基础数据(BasicData)与基础数据类型(BasicDataType)**: 业务表单下拉项字典,按类型分类,支持父子结构。
- **系统参数(SysArg)**: 键值配置项(如 CAS 开关、环境参数、缓存刷新时间)。
- **邮件队列(Mail)**: 异步通知队列,按期望发送时间轮询发送。
- **操作日志(SysLog)**: 登录与关键操作的审计记录。
- **公司(Company)**: 多租户隔离实体,树形结构。
- **流程任务委派(ProcDefDelegate)**: 流程任务按时间段/流程定义委派给他人的规则。
- **流程类型(ProcType)**: 流程定义键与描述的映射。
- **文件(File)**: 上传文件元信息,按上传人关联用户。

#### 数据契约

> 此子节为新系统复用/迁移 DB 的唯一事实来源。
> 分级说明:C=契约字段(业务必需,迁移必须保留);I=内部字段(技术实现,如审计字段);D=废弃字段(代码中已无引用)。
> 字段类型按 SQL-map 中实际使用反推(MySQL 方言)。

##### 表 fnd_user_info(用户信息)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 用户主键 | 自增,唯一 | C |
| username | varchar | 否 | 登录名 | 唯一(查重 SQL 反推);不含空格 | C |
| password | varchar | 否 | 登录密码(MD5(password+username)) | 加密存储 | C |
| email | varchar | 是 | 邮箱(用于通知) | - | C |
| realName | varchar | 是 | 真实姓名 | 不含空格 | C |
| status | int | 否 | 状态:1=启用,0=停用 | 值域 {0,1} | C |
| pwdoverdue | datetime | 是 | 密码过期时间 | 改密后=now+3 月;重置后=now | C |
| roleIds | varchar | 是 | 角色ID串,格式 `;id;` 分隔(如 `;1;;2;`) | 多角色;LIKE 模糊匹配 | C |
| isemail | int | 是 | 是否发送邮件(1=是) | 新增/更新置 1 | I |
| defaultPage | varchar | 是 | 默认落地页路径(对应 fnd_menus.path) | - | C |
| dpNo | varchar | 是 | 所属部门编号(对应 fnd_department.departmentNum) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | 新增时=now | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | 更新时=now | I |
| effectiveFrom | datetime | 是 | 生效起始 | 新增时=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 停用时=now | C |
| customInfo | text/json | 是 | 扩展信息(JSON) | 更新时 JSON_MERGE_PATCH 合并 | C |

##### 表 fnd_roles(角色)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 角色主键 | 自增,唯一 | C |
| roleName | varchar | 否 | 角色名称 | 非空,不含空格 | C |
| defaultPage | varchar | 是 | 默认页面 | 新增/编辑固定 `module/Welcome1.action` | C |
| status | int | 否 | 状态:1=启用,0=停用 | 值域 {0,1} | C |
| roleRemark | varchar | 是 | 角色备注 | - | C |
| createTime | datetime | 是 | 创建时间 | 新增时=now | I |
| effectiveFrom | datetime | 是 | 生效起始 | 新增时=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 停用时=now | C |
| updateTime | datetime | 是 | 更新时间 | 更新时=now | I |

##### 表 fnd_role_menus(角色菜单权限)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| roleId | int | 否 | 角色 ID(外键→fnd_roles.id) | 删除角色时联动清空 | C |
| menuId | int | 否 | 菜单 ID(外键→fnd_menus.id) | 删除菜单时联动清空 | C |
| menuPower | varchar | 否 | 操作权限编码串,逗号分隔 | 值域 `8`(新增)/`1`(删除)/`4`(查询)/`2`(更新) | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now | C |

> 关键不变量:更新角色权限时,先按 roleId 全删再重插。

##### 表 fnd_menus(系统菜单)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 菜单主键 | 自增,唯一 | C |
| menuCode | varchar | 否 | 菜单编码(业务键,用户菜单关联用) | 唯一 [待澄清:未见显式 unique,但作为关联键] | C |
| menuName | varchar | 是 | 菜单名称 | - | C |
| menuLevel | int | 是 | 菜单层级 | - | C |
| superId | int | 是 | 父菜单 ID(自关联) | 树形结构 | C |
| path | varchar | 是 | 菜单路径(用于 defaultPage 关联) | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | 查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |

##### 表 fnd_user_menus(用户直接菜单授权)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 [推断] | C |
| fnd_user_id | int | 否 | 用户 ID(外键→fnd_user_info.id) | 删除用户菜单时按此清空 | C |
| username | varchar | 是 | 用户名(冗余) | - | I |
| menuCode | varchar | 否 | 菜单编码(外键→fnd_menus.menuCode) | - | C |
| menuValue | int | 否 | 授权值:1=有权限 | 值域 {1} | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now;查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |

> 不变量:更新用户菜单时按 fnd_user_id 全删再重插。

##### 表 fnd_user_power(用户数据权限/区域权限)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 [推断] | C |
| fndUserId | int | 否 | 用户 ID(外键→fnd_user_info.id) | 唯一约束 [推断,update 按 fndUserId 定位] | C |
| username | varchar | 是 | 用户名(冗余) | - | I |
| areapower | varchar | 是 | 区域权限串,逗号分隔部门编号 | `-1` 表示无;登录时合并 dpNo | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| createBy | varchar | 是 | 创建人 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | now;查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |
| updateTime | datetime | 是 | 更新时间 | now | I |
| updateBy | varchar | 是 | 更新人 | - | I |

##### 表 fnd_department(部门)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| departmentNum | varchar | 否 | 部门编号(业务键) | SAP 同步来源 OcrCode | C |
| departmentName | varchar | 否 | 部门名称 | SAP 同步来源 OcrName | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now | C |
| status | int | 是 | 状态:1=启用 | 值域 {0,1} | C |
| isparam | int | 是 | 是否参数化部门:1=是 | 用于筛选"办事处" | C |

> 不变量:SAP 刷新时 TRUNCATE 整表后重写。

##### 表 fnd_basic_data(基础数据)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| dataTypeCode | varchar | 否 | 数据类型编码(外键→fnd_basic_data_type.dataTypeCode) | - | C |
| basicDataId | varchar | 否 | 基础数据业务 ID | (dataTypeCode, basicDataId) 组合不重复 | C |
| basicDataName | varchar | 是 | 基础数据名称 | - | C |
| basicDataAttri1 | varchar | 是 | 扩展属性1(可作父级 basicDataId 关联) | 父子结构 | C |
| sortId | int | 是 | 排序号 | 按升序 | C |
| createTime | datetime | 是 | 创建时间 | - | I |
| createBy | varchar | 是 | 创建人 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| effectiveTo | datetime | 是 | 生效终止 | 为空表示永久有效 | C |

##### 表 fnd_basic_data_type(基础数据类型)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| dataTypeCode | varchar | 否 | 类型编码(业务键) | 唯一 | C |
| dataTypeName | varchar | 是 | 类型名称 | - | C |
| status | int | 是 | 状态:1=启用 | 值域 {0,1} | C |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| effectiveTo | datetime | 是 | 生效终止 | - | C |

##### 表 fnd_sys_arg(系统参数)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | varchar | 否 | 参数编码(业务键) | 唯一 | C |
| var | varchar | 是 | 参数值 | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | <=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 为空或 >now | C |

> 已知 code:`sys.cache.latest.refreshTime`(缓存刷新时间)、`sys.envirment.argu`(环境参数,1=生产)、`sys.cas`(CAS 开关)。

##### 表 fnd_files(文件)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| fileName | varchar | 是 | 文件名 | - | C |
| filePath | varchar | 是 | 文件路径 | - | C |
| fileType | varchar | 是 | 文件类型 | - | C |
| uploadBy | varchar | 是 | 上传人(用户名) | 外键→fnd_user_info.username | I |
| uploadTime | datetime | 是 | 上传时间 | - | I |

##### 表 fnd_mails(邮件队列)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| mailSubject | varchar | 是 | 主题 | - | C |
| mailContent | text | 是 | 正文 | - | C |
| mailTos | varchar | 是 | 收件人(分号分隔) | - | C |
| mailCcs | varchar | 是 | 抄送 | - | C |
| mailBcc | varchar | 是 | 密送 | - | C |
| mailAttachFiles | varchar | 是 | 附件 | - | C |
| mailServerPort | varchar | 是 | 邮件服务器端口 | - | I |
| mailServerHost | varchar | 是 | 邮件服务器主机 | - | I |
| mailUsername | varchar | 是 | 邮件账号 | - | I |
| mailPassword | varchar | 是 | 邮件密码 | - | I |
| mailFromaddress | varchar | 是 | 发件地址 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| mailExpectSendTime | datetime | 是 | 期望发送时间 | < now 才被取出 | C |
| mailSendTime | datetime | 是 | 实际发送时间 | 发送时=now | I |
| sendFlag | int | 是 | 发送标志:0=未发,1=已发 | 值域 {0,1} | C |

##### 表 tb_sys_log(系统操作日志)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| ID | int | 否 | 主键 | 自增 | C |
| USER_NAME | varchar | 是 | 操作用户名 | 外键→user.username(老表名) | C |
| IP | varchar | 是 | 客户端 IP | - | C |
| ACTION | varchar | 是 | 操作动作 | - | C |
| RESULT | varchar | 是 | 操作结果 | - | C |
| INFO | varchar | 是 | 操作详情 | - | C |
| TIME | int | 是 | 操作时间(存为整型时间戳) | - | C |

> 注:日志查询 LEFT JOIN `user` 表(老表名,与 fnd_user_info 并存) [待澄清:user 与 fnd_user_info 关系]。

##### 表 fnd_company(公司/组织)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| name | varchar | 是 | 公司名称 | - | C |
| code | varchar | 是 | 公司编码 | - | C |
| status | int | 是 | 状态 | - | C |
| pid | int | 是 | 父公司 ID(自关联) | 树形结构 | C |

> 用途:新架构多租户隔离(compId)。

##### 表 dp_act_procdef_delegate(流程任务委派)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| owner | varchar | 否 | 原负责人(用户名) | 外键→user.username | C |
| assignee | varchar | 否 | 被委派人(用户名) | 外键→user.username | C |
| handleUsername | varchar | 是 | 操作人(用户名) | - | C |
| handleTime | datetime | 是 | 操作时间 | 新增/更新时=now | I |
| startTime | datetime | 是 | 委派起始 | 为空表示立即 | C |
| endTime | datetime | 是 | 委派终止 | 为空表示无限期 | C |
| procdefId | varchar | 是 | 流程定义 ID(模糊匹配) | - | C |
| status | int | 是 | 状态:1=生效 | 值域 {0,1} | C |
| cause | varchar | 是 | 委派原因 | - | C |

> 不变量:生效中查询条件 = status=1 且 (起止均空 或 起空止非空且 now<=止 或 起止均非空且 起<=now<=止)。

##### 表 dp_act_proc_type(流程类型)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| desc | varchar | 是 | 描述 | - | C |
| procDefKey | varchar | 是 | 流程定义键 | - | C |

##### 表 user(老用户表,与新 fnd_user_info 并存)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| - | - | - | 与 fnd_user_info 字段大体一致 | - | D |

> **重要歧义**:代码中同时存在 `fnd_user_info` 与 `user` 两张用户表,部分 SQL(日志查询、委派查询、MD5 密码更新)仍使用老表名 `user`。[待澄清:是否为同义视图或历史遗留]

##### 表 role(老角色表,与新 fnd_roles 并存)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | D |
| roleName | varchar | 否 | 角色名 | - | D |
| status | int | 是 | 状态:1=启用 | - | D |

> **重要歧义**:查询角色名/角色 ID 仍用老表 `role` 而非 `fnd_roles`。[待澄清:两者关系]

---

## Success Criteria

> 将非功能需求转化为可测量的成功标准。技术栈无关。

### Measurable Outcomes

- **SC-001(对应 NFR-001/002 缓存)**: 用户与权限相关查询启用 LRU 缓存(容量 50,只读),缓存命中时不重复查库;以下任一写操作发生后,对应缓存必须失效——用户新增/更新、用户改密、用户菜单授权增删、角色菜单授权增删、角色增改、用户区域权限增改、手动刷新缓存、基础数据增改。基础数据缓存 24 小时自动刷新。
- **SC-002(对应 NFR-003 缓存可追溯)**: 手动触发缓存刷新后,系统参数 `sys.cache.latest.refreshTime` 必须被更新为当前时间,可被查询验证。
- **SC-003(对应 NFR-004/005 权限校验)**: 用户管理操作执行前,系统必须校验当前用户角色——老架构:无 admin/engineemanager/engineemanager-leader 任一角色则抛"没有访问权限";新架构:用户详情/编辑仅本人或 admin 可访问(否则重定向未授权页),删除/角色变更仅 admin 可执行。100% 的越权访问被拦截。
- **SC-004(对应 NFR-006 密码安全)**: 初始/重置密码为 8 位随机串;密码以 MD5(password+username) 或可配置方式加密存储;改密后过期时间为 +3 个月,重置后立即过期并强制改密;重置后用户被强制下线。
- **SC-005(对应 NFR-007 多租户隔离)**: 用户扩展信息与用户角色关联均按公司(compId)隔离,查询/增删均带 compId 限定;系统用户访问自身数据时 compId 置 -1(跨租户)。跨租户越权访问 0 发生。
- **SC-006(对应 NFR-008 单点登出)**: CAS 模式下,CAS 服务器单点登出请求能终止本系统对应会话;通过 ticket→sessionId 映射实现,会话被标记失效后由后续请求触发登出。
- **SC-007(对应 NFR-009 操作日志可审计)**: 登录及标注系统控制器日志的关键操作(创建/修改/删除用户等)100% 写入操作日志;日志可按时间排序分页查询,并补全操作人真实姓名。
- **SC-008(对应 NFR-010 CAS 可开关)**: CAS 单点登录通过系统参数 `sys.cas`(0=关,1=开)控制;关闭时回退至账号密码登录,开关切换无需改代码。
- **SC-009(对应 NFR-011 邮件可靠性)**: 账号开通/密码重置邮件 100% 持久化到邮件队列;按期望发送时间(mailExpectSendTime<now)轮询发送,发送后 sendFlag=1 且 mailSendTime=now。
- **SC-010(对应 NFR-012 部门主数据一致)**: 部门数据支持从 SAP 主数据视图全量同步(TRUNCATE + 重写),同步后本地部门数据与 SAP 视图记录数一致。
- **SC-011(对应 NFR-013 时间有效性)**: 用户、菜单、用户菜单授权、用户区域权限、角色、基础数据、基础数据类型、系统参数均带 effectiveFrom/effectiveTo 字段;查询时统一过滤 effectiveFrom<now 且(effectiveTo 为空或 >now)。所有有效期外的记录不出现在业务查询结果中。
- **SC-012(对应 NFR-014/015 安全校验)**: 用户名/真实姓名/邮箱/菜单 ID 串/默认页/角色名任一为空或含空格时,提交被 100% 拒绝;角色菜单权限列表为空时新增/编辑被拒绝。
- **SC-013(对应 NFR-016 角色变更联动)**: 编辑用户时若指定 changeType(service/program/both)与 newMemberCode,联动批量更新项目服务经理/项目经理 [待澄清:具体联动规则与失败处理]。
- **SC-014(对应 NFR-017 区域权限合并)**: 登录时区域权限(areapower)按规则合并:若 areapower 为 `-1` 且 dpNo 非空,以 dpNo 替换;若 areapower 不含 dpNo,追加 dpNo;再按市场/用服办事处映射规则补充(16x↔31x 前缀互换)[待澄清:映射规则当前是否启用]。

---

## Assumptions

> 基于逆向反推在草稿未明确处采用的合理默认假设。

- **数据库表结构视为契约**: 数据契约章节列出的表与字段(C 级)为新系统复用/迁移的事实来源,字段不得删减(DATA-REUSE-01);类型按 MySQL 方言反推,新系统可替换为等价类型。
- **两套端点并存**: 老架构(`.action`,Struts)与新架构(REST,前缀 `/system`)在过渡期并存,功能需求以"系统做什么"描述,不绑定任一端点风格;新系统应统一为一套。
- **CAS 为生产默认登录方式**: 生产环境默认启用 CAS(`sys.cas=1`),账号密码登录为非 CAS 或回退路径;CAS 服务器地址当前硬编码,需评估是否参数化。
- **环境开关语义**: `sys.envirment.argu=1` 表示生产环境(启用验证码且校验密码);非 1 表示测试环境(跳过验证码且忽略密码,即测试免密)。生产与测试环境区分逻辑需在 clarify 阶段确认。
- **密码加密可演进**: 老架构固定 MD5(password+username);新架构支持可配置加密。新系统不应被 MD5 强约束,但迁移期需兼容历史密码校验。
- **多租户为新架构能力**: 老架构无 compId 隔离;新架构引入公司隔离。spec 同时覆盖两套语义,新系统应以多租户隔离为准。
- **老表(user/role)为待迁移遗留**: 老用户表 `user` 与老角色表 `role` 标记为 D(废弃),与 `fnd_user_info`/`fnd_roles` 并存属历史遗留,迁移策略待 clarify。
- **角色默认页面硬编码(待 clarify)**: `module/Welcome1.action` 硬编码于角色新增/编辑,是否改为可配置待 clarify。
- **SAP 主数据可访问**: 部门 SAP 同步依赖外部 SAP 视图 `dp_reports.dp_v_spms_department` 可达;同步采用全量 TRUNCATE + 重写,期间存在短暂空窗。
- **邮件发送为异步队列模式**: 通知邮件通过队列表持久化后轮询发送,非实时;发送失败由后续轮询重试,无显式重试次数上限(待 clarify)。
- **缓存为本地 LRU**: 用户/权限缓存与基础数据缓存为本地 LRU(容量 50,只读),不跨节点共享;多实例部署下缓存一致性需新系统重新评估。
- **区域权限映射规则待确认**: 市场/用服办事处 16x↔31x 前缀互换规则在源码中部分注释,实际启用状态待 clarify。
- **用户角色变更联动为外部调用**: changeType+newMemberCode 触发的项目服务经理/项目经理批量更新依赖外部项目工具,具体规则与失败处理待 clarify。
