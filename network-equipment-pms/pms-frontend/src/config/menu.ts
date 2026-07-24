/**
 * 全局菜单静态配置。
 *
 * <p>从 {@code layouts/DefaultLayout.vue} 抽出，便于：
 * <ul>
 *   <li>后续对接后端 {@code /api/system/menu/tree} 动态拉取（仅需替换数据源）</li>
 *   <li>顶部一级 Tab + 侧栏二级菜单的分级渲染（参照钉钉 / 飞书）</li>
 *   <li>递归菜单组件 {@code components/layout/SidebarMenu.vue} 统一消费</li>
 * </ul>
 *
 * <p>权限过滤基于 {@link import('@/stores/user').useUserStore | useUserStore}：
 * <ul>
 *   <li>静态权限：{@code MenuLeaf.permissions}（如 {@code project:template:list}）</li>
 *   <li>低代码运行时权限：路径形如 {@code /lowcode/{form|list|tab|related-page}/{code}}
 *       自动派生 {@code lowcode:page:{type}:{code}}</li>
 *   <li>低代码静态权限：{@link lowCodeMenuPermissions} 显式映射</li>
 * </ul>
 */
import type { ComputedRef } from 'vue'
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

/** 菜单叶子节点（可点击的菜单项） */
export interface MenuLeaf {
  /** 菜单标题 */
  title: string
  /** 路由路径 */
  path: string
  /** Element Plus 图标组件名（如 'HomeFilled'） */
  icon: string
  /** 访问所需权限码列表（任一满足即可）；省略表示无权限要求 */
  permissions?: string[]
  /** 顶级 Tab 分组（用于顶栏一级 Tab 切换；省略默认归到「其他」） */
  group?: string
}

/** 菜单分组节点（含子菜单） */
export interface MenuGroup {
  /** 菜单标题 */
  title: string
  /** Element Plus 图标组件名 */
  icon: string
  /** 子菜单列表 */
  children: MenuLeaf[]
  /** 顶级 Tab 分组（顶栏一级 Tab 标识，省略则使用 title 作为 group） */
  group?: string
}

/** 菜单项（叶子或分组） */
export type MenuItem = MenuLeaf | MenuGroup

/**
 * 顶级菜单数据源。
 *
 * <p>顶级分组（group 字段）用于顶栏一级 Tab 切换：
 * <ul>
 *   <li>{@code 'home'} — 首页（单页直接进入）</li>
 *   <li>{@code 'project'} — 项目管理</li>
 *   <li>{@code 'asset'} — 资产管理</li>
 *   <li>{@code 'implementation'} — 实施管理</li>
 *   <li>{@code 'governance'} — 项目治理 + 交付治理</li>
 *   <li>{@code 'lowcode'} — 低代码 + 演示中心</li>
 *   <li>{@code 'system'} — 系统管理 + 系统监控</li>
 *   <li>{@code 'report'} — 报表统计（单页）</li>
 * </ul>
 */
export const menuGroups: MenuItem[] = [
  { title: '首页', path: '/dashboard', icon: 'HomeFilled', group: 'home' },
  {
    title: '项目管理',
    icon: 'Folder',
    group: 'project',
    children: [
      { title: '项目列表', path: '/project/list', icon: 'Folder' },
      { title: '主子项目树', path: '/project/tree', icon: 'Share' },
      { title: '交付看板', path: '/project/kanban', icon: 'Grid' },
      { title: '项目模板', path: '/project/template', icon: 'Files', permissions: ['project:template:list'] }
    ]
  },
  {
    title: '资产管理',
    icon: 'Box',
    group: 'asset',
    children: [
      { title: '设备分类', path: '/asset/category', icon: 'Files' },
      { title: '设备型号', path: '/asset/model', icon: 'Box' },
      { title: '资产清单', path: '/asset/list', icon: 'List' }
    ]
  },
  {
    title: '实施管理',
    icon: 'Tools',
    group: 'implementation',
    children: [
      { title: '实施任务', path: '/implementation/task', icon: 'Tickets' },
      { title: '任务树列表', path: '/implementation/task/list', icon: 'Connection' },
      { title: '服务商管理', path: '/implementation/agent', icon: 'OfficeBuilding' },
      { title: '结算管理', path: '/implementation/settlement', icon: 'Money' }
    ]
  },
  {
    title: '计划基线',
    icon: 'Histogram',
    group: 'implementation',
    children: [{ title: '基线管理', path: '/baseline/list', icon: 'Histogram' }]
  },
  {
    title: '工作流',
    icon: 'Connection',
    group: 'implementation',
    children: [
      { title: '待办中心', path: '/workflow/todo', icon: 'Bell' },
      { title: '统一审批中心', path: '/workflow/approval-center', icon: 'Checked', permissions: ['workflow:approval:list'] },
      { title: '字段权限配置', path: '/workflow/field-perm', icon: 'Lock', permissions: ['workflow:field:perm'] }
    ]
  },
  {
    title: '交付治理',
    icon: 'Operation',
    group: 'governance',
    children: [
      { title: 'Punch List', path: '/punch-list', icon: 'WarningFilled' },
      { title: 'RMA 返修', path: '/rma', icon: 'RefreshRight' },
      { title: '质保期管理', path: '/warranty', icon: 'Timer' },
      { title: '终验交付物', path: '/deliverable', icon: 'Document' },
      { title: '交付件全生命周期', path: '/deliverable/lifecycle', icon: 'Files' }
    ]
  },
  {
    title: '项目治理',
    icon: 'SetUp',
    group: 'governance',
    children: [
      { title: '风险登记册', path: '/risk', icon: 'Warning' },
      { title: '变更管理', path: '/change-request', icon: 'EditPen' },
      { title: '问题日志', path: '/issue', icon: 'ChatLineSquare' }
    ]
  },
  {
    title: '系统监控',
    icon: 'DataLine',
    group: 'system',
    children: [
      { title: '消息中心', path: '/notification', icon: 'Bell' },
      { title: '集成健康', path: '/integration-health', icon: 'Monitor' },
      { title: '系统状态', path: '/system-status', icon: 'Monitor' },
      { title: '缓存管理', path: '/system/cache', icon: 'Coin' },
      { title: '定时任务', path: '/system/schedule', icon: 'Timer' },
      { title: '审计日志', path: '/system/audit', icon: 'DocumentChecked' },
      { title: '登录日志', path: '/system/login-log', icon: 'Document' },
      { title: '操作日志', path: '/system/operate-log', icon: 'DocumentChecked' },
      { title: '令牌管理', path: '/system/oauth2-token', icon: 'Key' },
      { title: '版本日志', path: '/changelog', icon: 'Notebook' }
    ]
  },
  {
    title: '系统管理',
    icon: 'Setting',
    group: 'system',
    children: [
      { title: '用户管理', path: '/system/user', icon: 'User' },
      { title: '角色管理', path: '/system/role', icon: 'UserFilled' },
      { title: '菜单管理', path: '/system/menu', icon: 'Menu' },
      { title: '部门管理', path: '/system/dept', icon: 'OfficeBuilding' },
      { title: '岗位管理', path: '/system/post', icon: 'Postcard' },
      { title: '字典管理', path: '/system/dict', icon: 'Document' },
      { title: '字典数据', path: '/system/dict/data', icon: 'Document' },
      { title: '通知公告', path: '/system/notice', icon: 'BellFilled' },
      { title: '租户管理', path: '/system/tenant', icon: 'OfficeBuilding' },
      { title: '租户套餐', path: '/system/tenant-package', icon: 'Box' },
      { title: 'OAuth2 客户端', path: '/system/oauth2-client', icon: 'Key' },
      { title: '短信渠道', path: '/system/sms-channel', icon: 'ChatDotRound' },
      { title: '短信模板', path: '/system/sms-template', icon: 'Message' },
      { title: '短信日志', path: '/system/sms-log', icon: 'Document' },
      { title: '邮箱账号', path: '/system/mail-account', icon: 'Message' },
      { title: '邮件模板', path: '/system/mail-template', icon: 'Message' },
      { title: '邮件日志', path: '/system/mail-log', icon: 'Document' },
      { title: '站内信模板', path: '/system/notify-template', icon: 'BellFilled' },
      { title: '站内信消息', path: '/system/notify-message', icon: 'Bell' },
      { title: '我的站内信', path: '/system/notify-my', icon: 'Bell' },
      { title: '社交客户端', path: '/system/social-client', icon: 'Share' },
      { title: '社交用户', path: '/system/social-user', icon: 'User' },
      { title: '地区管理', path: '/system/area', icon: 'Location' }
    ]
  },
  {
    title: '基础设施',
    icon: 'Tools',
    group: 'system',
    children: [
      { title: '参数配置', path: '/infra/config', icon: 'Setting' },
      { title: '定时任务', path: '/infra/job', icon: 'Timer' },
      { title: '任务日志', path: '/infra/job-log', icon: 'Document' },
      { title: 'Redis 监控', path: '/infra/redis', icon: 'DataLine' },
      { title: 'API 访问日志', path: '/infra/api-access-log', icon: 'Document' },
      { title: 'API 错误日志', path: '/infra/api-error-log', icon: 'WarningFilled' },
      { title: '数据源配置', path: '/infra/data-source-config', icon: 'Coin' },
      { title: '文件管理', path: '/infra/file', icon: 'Document' },
      { title: '文件配置', path: '/infra/file-config', icon: 'Files' },
      { title: '代码生成', path: '/infra/codegen', icon: 'EditPen' },
      { title: '构建信息', path: '/infra/build', icon: 'InfoFilled' },
      { title: '服务器监控', path: '/infra/server', icon: 'Monitor' },
      { title: 'Druid 监控', path: '/infra/druid', icon: 'DataLine' },
      { title: 'API 文档', path: '/infra/swagger', icon: 'Document' },
      { title: 'SkyWalking', path: '/infra/skywalking', icon: 'DataLine' },
      { title: 'WebSocket 测试', path: '/infra/web-socket', icon: 'Connection' }
    ]
  },
  {
    title: '工作流 BPM',
    icon: 'Connection',
    group: 'system',
    children: [
      { title: '流程模型', path: '/bpm/model', icon: 'Connection' },
      { title: '流程分类', path: '/bpm/category', icon: 'Files' },
      { title: '动态表单', path: '/bpm/form', icon: 'Document' },
      { title: '用户组', path: '/bpm/group', icon: 'User' },
      { title: '流程表达式', path: '/bpm/process-expression', icon: 'EditPen' },
      { title: '流程监听器', path: '/bpm/process-listener', icon: 'BellFilled' },
      { title: '我的流程', path: '/bpm/process-instance', icon: 'Tickets' },
      { title: '流程实例管理', path: '/bpm/process-instance/manager', icon: 'Tickets' },
      { title: '待办任务', path: '/bpm/task/todo', icon: 'Bell' },
      { title: '已办任务', path: '/bpm/task/done', icon: 'CircleCheck' },
      { title: '任务管理', path: '/bpm/task/manager', icon: 'Tickets' },
      { title: '抄送任务', path: '/bpm/task/copy', icon: 'Document' },
      { title: 'OA 请假', path: '/bpm/oa/leave', icon: 'Calendar' },
      { title: '简单模型设计', path: '/bpm/simple', icon: 'EditPen' }
    ]
  },
  { title: '报表统计', path: '/report', icon: 'TrendCharts', group: 'report' },
  {
    title: '报表平台',
    icon: 'TrendCharts',
    group: 'report',
    children: [
      { title: 'GoView 数据大屏', path: '/report/goview', icon: 'DataLine' },
      { title: '积木报表', path: '/report/jmreport', icon: 'TrendCharts' }
    ]
  },
  {
    title: '低代码',
    icon: 'MagicStick',
    group: 'lowcode',
    children: [
      { title: '实体设计器', path: '/lowcode/entity-designer', icon: 'Connection' },
      { title: '表单配置', path: '/lowcode/form-list', icon: 'Document' },
      { title: '列表配置', path: '/lowcode/list-list', icon: 'List' },
      { title: '标签页配置', path: '/lowcode/tab-list', icon: 'Files' },
      { title: '关联页配置', path: '/lowcode/related-page-list', icon: 'Share' },
      { title: '微流设计器', path: '/lowcode/microflow-designer', icon: 'Share' },
      { title: '规则设计器', path: '/lowcode/rule-designer', icon: 'Filter' },
      { title: '流程设计器', path: '/lowcode/process-designer', icon: 'Connection' },
      { title: '触发器', path: '/lowcode/trigger-list', icon: 'BellFilled' },
      { title: '连接器配置', path: '/lowcode/connector-designer', icon: 'Connection' },
      { title: '发布中心', path: '/lowcode/publish-center', icon: 'Promotion' },
      { title: '审批链配置', path: '/lowcode/approval-chain', icon: 'SetUp' },
      { title: '版本历史', path: '/lowcode/version-history', icon: 'Timer' },
      { title: '模板市场', path: '/lowcode/template-market', icon: 'Goods' },
      { title: 'APM 看板', path: '/lowcode/apm-dashboard', icon: 'TrendCharts' },
      { title: '应用源码导出', path: '/lowcode/app-source-export', icon: 'Download' }
    ]
  },
  {
    title: '演示中心',
    icon: 'Star',
    group: 'lowcode',
    children: [
      { title: '割接申请', path: '/lowcode/form/form_demo_network_cutover', icon: 'EditPen' },
      { title: '割接台账', path: '/lowcode/list/list_demo_network_cutover', icon: 'Connection' },
      { title: '割接工作台', path: '/lowcode/tab/tab_demo_network_cutover', icon: 'Grid' },
      { title: '割接关联视图', path: '/lowcode/related-page/related_demo_network_cutover', icon: 'Share' },
      { title: '员工列表', path: '/lowcode/list/list_demo_employee', icon: 'User' },
      { title: '员工档案', path: '/lowcode/form/form_demo_employee', icon: 'Document' },
      { title: '入职任务', path: '/lowcode/list/list_demo_onboarding_task', icon: 'List' },
      { title: '部门管理', path: '/lowcode/list/list_demo_department', icon: 'OfficeBuilding' }
    ]
  }
]

/** 低代码模块静态菜单路径 → 所需权限码映射（运行时低代码页面走派生权限） */
export const lowCodeMenuPermissions: Record<string, string[]> = {
  '/lowcode/entity-designer': ['lowcode:entity:list'],
  '/lowcode/form-list': ['lowcode:form:list'],
  '/lowcode/list-list': ['lowcode:list:list'],
  '/lowcode/tab-list': ['lowcode:tab:edit', 'lowcode:tab:add'],
  '/lowcode/related-page-list': ['lowcode:relatedPage:edit', 'lowcode:relatedPage:add'],
  '/lowcode/microflow-designer': ['lowcode:microflow:list'],
  '/lowcode/rule-designer': ['lowcode:rule:list'],
  '/lowcode/process-designer': ['lowcode:process:list'],
  '/lowcode/trigger-list': ['lowcode:trigger:list'],
  '/lowcode/connector-designer': ['lowcode:connector:list'],
  '/lowcode/publish-center': ['lowcode:publish:list'],
  '/lowcode/approval-chain': ['lowcode:approval-chain:list'],
  '/lowcode/version-history': ['lowcode:version:list'],
  '/lowcode/template-market': ['lowcode:template:list'],
  '/lowcode/apm-dashboard': ['lowcode:microflow:list', 'lowcode:rule:list'],
  '/lowcode/app-source-export': ['lowcode:app-source:export']
}

/**
 * 判断当前用户是否能访问指定叶子菜单项。
 *
 * <p>权限解析顺序：
 * <ol>
 *   <li>显式声明：{@link MenuLeaf.permissions}</li>
 *   <li>运行时低代码页面：路径形如 {@code /lowcode/{form|list|tab|related-page}/{code}}
 *       派生 {@code lowcode:page:{type}:{code}}</li>
 *   <li>低代码静态映射：{@link lowCodeMenuPermissions}</li>
 *   <li>无任何匹配：默认放行</li>
 * </ol>
 *
 * @param item 待校验的叶子菜单
 * @returns 是否可访问
 */
export function canAccessMenu(item: MenuLeaf): boolean {
  const userStore = useUserStore()
  const runtimePage = item.path.match(/^\/lowcode\/(form|list|tab|related-page)\/([^/?#]+)/)
  const permissions =
    item.permissions ??
    (runtimePage
      ? [`lowcode:page:${runtimePage[1]}:${runtimePage[2]}`]
      : lowCodeMenuPermissions[item.path])
  return !permissions || userStore.hasAnyPermission(permissions)
}

/**
 * 按当前用户权限过滤后的可见菜单列表。
 *
 * <p>分组节点：任一子菜单可见则保留该分组（仅含可见子菜单）。
 * 叶子节点：直接判断 {@link canAccessMenu}。</p>
 *
 * @returns ComputedRef，权限变化时自动重算
 */
export function useVisibleMenuGroups(): ComputedRef<MenuItem[]> {
  return computed<MenuItem[]>(() =>
    menuGroups.reduce<MenuItem[]>((result, item) => {
      if ('children' in item) {
        const children = item.children.filter(canAccessMenu)
        if (children.length > 0) result.push({ ...item, children })
      } else if (canAccessMenu(item)) {
        result.push(item)
      }
      return result
    }, [])
  )
}

/**
 * 顶栏一级 Tab 列表（去重后的 group 标识 + 显示名 + 图标）。
 *
 * <p>用于顶栏一级 Tab 渲染，点击切换后侧栏联动显示该 group 下的二级菜单。</p>
 */
export interface TopTab {
  /** 分组标识 */
  group: string
  /** Tab 显示名 */
  title: string
  /** Tab 图标 */
  icon: string
  /** 该 Tab 下首个可访问路由（点击 Tab 时直接跳转） */
  defaultPath: string
}

/**
 * 计算顶栏一级 Tab 列表（按权限过滤）。
 *
 * <p>逻辑：
 * <ol>
 *   <li>遍历 {@link menuGroups}，按 group 字段聚合</li>
 *   <li>每个 group 的 title / icon 取该 group 下首个分组节点（无子菜单的叶子节点用自身）</li>
 *   <li>defaultPath 取该 group 下首个可访问叶子节点的 path</li>
 *   <li>过滤掉所有子项都不可访问的 group</li>
 * </ol>
 */
export function useTopTabs(): ComputedRef<TopTab[]> {
  return computed<TopTab[]>(() => {
    const tabMap = new Map<string, TopTab>()
    for (const item of menuGroups) {
      const groupKey = 'group' in item && item.group ? item.group : item.title
      if (tabMap.has(groupKey)) continue
      if ('children' in item) {
        const visibleChildren = item.children.filter(canAccessMenu)
        if (visibleChildren.length === 0) continue
        tabMap.set(groupKey, {
          group: groupKey,
          title: item.title,
          icon: item.icon,
          defaultPath: visibleChildren[0].path
        })
      } else {
        if (!canAccessMenu(item)) continue
        tabMap.set(groupKey, {
          group: groupKey,
          title: item.title,
          icon: item.icon,
          defaultPath: item.path
        })
      }
    }
    return Array.from(tabMap.values())
  })
}

/**
 * 根据当前路由路径推断所属的顶级 Tab group。
 *
 * <p>规则：遍历 {@link menuGroups}，找到首个 children 中存在匹配当前路径的叶子节点的分组，
 * 返回其 group 字段；若当前路径是顶级叶子节点，直接返回其 group。
 * 找不到时返回 {@code 'home'}。</p>
 *
 * @param path 当前路由路径
 * @returns 顶级 Tab group 标识
 */
export function inferActiveTabGroup(path: string): string {
  for (const item of menuGroups) {
    if ('children' in item) {
      if (item.children.some((c) => path === c.path || path.startsWith(c.path + '/'))) {
        return item.group ?? item.title
      }
    } else if (path === item.path || path.startsWith(item.path + '/')) {
      return item.group ?? item.title
    }
  }
  return 'home'
}

/**
 * 根据顶级 Tab group 过滤可见菜单（响应式）。
 *
 * @param groupRef 顶级 Tab group 标识的 getter 函数（推荐用 () => activeTabGroup.value 形式）
 * @returns 该 group 下的所有可见菜单项（含分组与叶子），ComputedRef 自动响应 group 变化
 */
export function useMenuByGroup(groupRef: () => string): ComputedRef<MenuItem[]> {
  const visible = useVisibleMenuGroups()
  return computed<MenuItem[]>(() => {
    const group = groupRef()
    return visible.value.filter((item) => {
      const itemGroup = 'group' in item && item.group ? item.group : item.title
      return itemGroup === group
    })
  })
}
