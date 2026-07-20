import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { startRouteLoading, stopRouteLoading } from '@/directives/loading'

const Layout = () => import('@/layouts/DefaultLayout.vue')

const LOWCODE_ROUTE_PERMISSIONS: Array<{ prefix: string; permissions: string[] }> = [
  { prefix: '/lowcode/entity-designer', permissions: ['lowcode:entity:list'] },
  { prefix: '/lowcode/form-designer', permissions: ['lowcode:form:edit', 'lowcode:form:add'] },
  { prefix: '/lowcode/form-list', permissions: ['lowcode:form:list'] },
  { prefix: '/lowcode/list-designer', permissions: ['lowcode:list:edit', 'lowcode:list:add'] },
  { prefix: '/lowcode/list-list', permissions: ['lowcode:list:list'] },
  { prefix: '/lowcode/tab-designer', permissions: ['lowcode:tab:edit', 'lowcode:tab:add'] },
  { prefix: '/lowcode/tab-list', permissions: ['lowcode:tab:edit', 'lowcode:tab:add'] },
  { prefix: '/lowcode/related-page-designer', permissions: ['lowcode:relatedPage:edit', 'lowcode:relatedPage:add'] },
  { prefix: '/lowcode/related-page-list', permissions: ['lowcode:relatedPage:edit', 'lowcode:relatedPage:add'] },
  { prefix: '/lowcode/microflow-designer', permissions: ['lowcode:microflow:list'] },
  { prefix: '/lowcode/rule-designer', permissions: ['lowcode:rule:list'] },
  { prefix: '/lowcode/process-designer', permissions: ['lowcode:process:list'] },
  { prefix: '/lowcode/trigger-list', permissions: ['lowcode:trigger:list'] },
  { prefix: '/lowcode/connector-designer', permissions: ['lowcode:connector:list'] },
  { prefix: '/lowcode/publish-center', permissions: ['lowcode:publish:list'] },
  { prefix: '/lowcode/approval-chain', permissions: ['lowcode:approval-chain:list'] },
  { prefix: '/lowcode/version-history', permissions: ['lowcode:version:list'] },
  { prefix: '/lowcode/template-market', permissions: ['lowcode:template:list'] },
  { prefix: '/lowcode/apm-dashboard', permissions: ['lowcode:microflow:list', 'lowcode:rule:list'] },
  { prefix: '/lowcode/app-source-export', permissions: ['lowcode:app-source:export'] }
]

function requiredLowCodePermissions(path: string): string[] {
  const runtimePage = path.match(/^\/lowcode\/(form|list|tab|related-page)\/([^/?#]+)/)
  if (runtimePage) return [`lowcode:page:${runtimePage[1]}:${runtimePage[2]}`]
  return LOWCODE_ROUTE_PERMISSIONS.find((item) => path.startsWith(item.prefix))?.permissions ?? []
}

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/help',
    name: 'Help',
    component: () => import('@/views/help/index.vue'),
    meta: { title: '帮助中心', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  // ============ 项目管理（嵌套，以 workspace/:id 为枢纽） ============
  // 父级 meta.showProjectSidebar: true 由 Vue Router 合并继承到所有子路由，
  // 让 DefaultLayout 中的 ProjectTreeSidebar 在项目管理相关路由常驻显示。
  {
    path: '/project',
    component: Layout,
    redirect: '/project/list',
    meta: { title: '项目管理', icon: 'Folder', requiresAuth: true, showProjectSidebar: true },
    children: [
      {
        path: 'list',
        name: 'ProjectList',
        component: () => import('@/views/project/list/index.vue'),
        meta: { title: '项目列表', icon: 'Folder' }
      },
      // 兼容旧路径 /project/detail/:id —— 重定向到工作区枢纽
      // (detail/index.vue 组件内部 onMounted 执行 router.replace)
      {
        path: 'detail/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail/index.vue'),
        meta: { title: '项目详情', hidden: true }
      },
      // ★ 项目工作区枢纽（8 Tab 通过组件内部动态组件加载，不占用独立路由）
      {
        path: 'workspace/:id',
        name: 'ProjectWorkspace',
        component: () => import('@/views/project/workspace/index.vue'),
        meta: { title: '项目工作区', hidden: true }
      },
      {
        path: 'todo/:id',
        name: 'ProjectTodo',
        component: () => import('@/views/project/todo/index.vue'),
        meta: { title: '项目待办', hidden: true }
      },
      {
        path: ':id/gantt',
        name: 'ProjectGantt',
        component: () => import('@/views/project/gantt/index.vue'),
        meta: { title: '项目甘特图', hidden: true }
      },
      {
        path: 'tree',
        name: 'ProjectTree',
        component: () => import('@/views/project/tree/index.vue'),
        meta: { title: '主子项目树', icon: 'Share' }
      },
      {
        path: 'phase/:projectId',
        name: 'ProjectPhaseManage',
        component: () => import('@/views/phase/index.vue'),
        meta: { title: '阶段管理', hidden: true }
      },
      {
        path: 'kanban',
        name: 'ProjectKanban',
        component: () => import('@/views/project/kanban/index.vue'),
        meta: { title: '交付看板', icon: 'Grid' }
      },
      {
        path: 'template',
        name: 'ProjectTemplate',
        component: () => import('@/views/project/template/index.vue'),
        meta: { title: '项目模板', icon: 'Files', perms: 'project:template:list' }
      },
      {
        path: 'template/form/:id?',
        name: 'ProjectTemplateForm',
        component: () => import('@/views/project/template/form.vue'),
        meta: { title: '模板编辑', hidden: true }
      },
      {
        path: 'template/version/:id',
        name: 'ProjectTemplateVersion',
        component: () => import('@/views/project/template/version.vue'),
        meta: { title: '版本管理', hidden: true }
      },
      {
        path: 'config/:id',
        name: 'ProjectConfig',
        component: () => import('@/views/project-config/index.vue'),
        meta: { title: '项目配置', hidden: true }
      }
    ]
  },
  // ============ 资产管理（嵌套） ============
  {
    path: '/asset',
    component: Layout,
    redirect: '/asset/category',
    meta: { title: '资产管理', icon: 'Files', requiresAuth: true },
    children: [
      { path: 'category', name: 'AssetCategory',
        component: () => import('@/views/asset/category/index.vue'),
        meta: { title: '设备分类', icon: 'Files' } },
      { path: 'model', name: 'AssetModel',
        component: () => import('@/views/asset/model/index.vue'),
        meta: { title: '设备型号', icon: 'Box' } },
      { path: 'list', name: 'AssetList',
        component: () => import('@/views/asset/list/index.vue'),
        meta: { title: '资产清单', icon: 'List' } }
    ]
  },
  // ============ 实施管理（嵌套） ============
  {
    path: '/implementation',
    component: Layout,
    redirect: '/implementation/task',
    meta: { title: '实施管理', icon: 'Tickets', requiresAuth: true },
    children: [
      { path: 'task', name: 'ImplTask',
        component: () => import('@/views/implementation/task/index.vue'),
        meta: { title: '实施任务', icon: 'Tickets' } },
      { path: 'task/list', name: 'TaskList',
        component: () => import('@/views/task/list/index.vue'),
        meta: { title: '任务树列表', icon: 'Connection' } },
      { path: 'task/detail/:id', name: 'TaskDetail',
        component: () => import('@/views/task/detail/index.vue'),
        meta: { title: '任务详情', hidden: true } },
      { path: 'task/dependency/:projectId', name: 'TaskDependencyGraph',
        component: () => import('@/views/task/dependency/index.vue'),
        meta: { title: '任务依赖关系图', hidden: true } },
      { path: 'agent', name: 'AgentManage',
        component: () => import('@/views/implementation/agent/index.vue'),
        meta: { title: '服务商管理', icon: 'OfficeBuilding' } },
      { path: 'settlement', name: 'Settlement',
        component: () => import('@/views/implementation/settlement/index.vue'),
        meta: { title: '结算管理', icon: 'Money' } }
    ]
  },
  // ============ 计划基线（嵌套） ============
  {
    path: '/baseline',
    component: Layout,
    redirect: '/baseline/list',
    meta: { title: '计划基线', icon: 'Histogram', requiresAuth: true },
    children: [
      { path: 'list', name: 'BaselineList',
        component: () => import('@/views/baseline/index.vue'),
        meta: { title: '基线管理', icon: 'Histogram' } },
      { path: 'diff/:baselineId', name: 'BaselineDiff',
        component: () => import('@/views/baseline/diff.vue'),
        meta: { title: '基线偏差分析', hidden: true } }
    ]
  },
  // ============ 工作流与审批（嵌套） ============
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/todo',
    meta: { title: '工作流', icon: 'Bell', requiresAuth: true },
    children: [
      { path: 'todo', name: 'WorkflowTodo',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办中心', icon: 'Bell' } },
      { path: 'approval-center', name: 'ApprovalCenter',
        component: () => import('@/views/workflow/approval-center/index.vue'),
        meta: { title: '统一审批中心', icon: 'Checked', perms: 'workflow:approval:handle' } },
      { path: 'approval-detail/:id', name: 'ApprovalDetail',
        component: () => import('@/views/workflow/approval-detail/index.vue'),
        meta: { title: '审批详情', hidden: true } },
      { path: 'approval-history/:recordId', name: 'ApprovalHistory',
        component: () => import('@/views/workflow/approval-history/index.vue'),
        meta: { title: '审批历史', hidden: true } },
      { path: 'field-perm', name: 'ApprovalFieldPerm',
        component: () => import('@/views/workflow/field-perm/index.vue'),
        meta: { title: '字段权限配置', icon: 'Lock', perms: 'workflow:field:perm' } }
    ]
  },
  // ============ 其他业务（保留平铺，逐步迁移） ============
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'punch-list', name: 'PunchList',
        component: () => import('@/views/punch-list/index.vue'),
        meta: { title: 'Punch List', icon: 'WarningFilled' } },
      { path: 'rma', name: 'Rma',
        component: () => import('@/views/rma/index.vue'),
        meta: { title: 'RMA 返修', icon: 'RefreshRight' } },
      { path: 'warranty', name: 'Warranty',
        component: () => import('@/views/warranty/index.vue'),
        meta: { title: '质保期管理', icon: 'Timer' } },
      { path: 'deliverable', name: 'Deliverable',
        component: () => import('@/views/deliverable/index.vue'),
        meta: { title: '终验交付物', icon: 'Document' } },
      { path: 'deliverable/lifecycle', name: 'DeliverableLifecycle',
        component: () => import('@/views/deliverable/lifecycle.vue'),
        meta: { title: '交付件全生命周期', icon: 'Files' } },
      { path: 'deliverable/detail/:id', name: 'DeliverableDetail',
        component: () => import('@/views/deliverable/detail/index.vue'),
        meta: { title: '交付件详情', hidden: true } },
      { path: 'notification', name: 'NotificationCenter',
        component: () => import('@/views/notification/index.vue'),
        meta: { title: '消息中心', icon: 'Bell' } },
      { path: 'integration-health', name: 'IntegrationHealth',
        component: () => import('@/views/integration-health/index.vue'),
        meta: { title: '集成健康检查', icon: 'Monitor' } },
      { path: 'risk', name: 'Risk',
        component: () => import('@/views/risk/index.vue'),
        meta: { title: '风险登记册', icon: 'Warning' } },
      { path: 'change-request', name: 'ChangeRequest',
        component: () => import('@/views/change-request/index.vue'),
        meta: { title: '变更管理', icon: 'EditPen' } },
      { path: 'issue', name: 'Issue',
        component: () => import('@/views/issue/index.vue'),
        meta: { title: '问题日志', icon: 'ChatLineSquare' } },
      { path: 'report', name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '报表统计', icon: 'TrendCharts' } }
    ]
  },
  // ============ 低代码平台（保留原结构） ============
  {
    path: '/lowcode',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'form-list', name: 'LowCodeFormList',
        component: () => import('@/views/lowcode/form-list/index.vue'),
        meta: { title: '表单配置', icon: 'Document' } },
      { path: 'form-designer', name: 'LowCodeFormDesigner',
        component: () => import('@/views/lowcode/form-designer/index.vue'),
        meta: { title: '表单设计器', icon: 'EditPen', hidden: true } },
      { path: 'list-list', name: 'LowCodeListList',
        component: () => import('@/views/lowcode/list-list/index.vue'),
        meta: { title: '列表配置', icon: 'List' } },
      { path: 'list-designer', name: 'LowCodeListDesigner',
        component: () => import('@/views/lowcode/list-designer/index.vue'),
        meta: { title: '列表设计器', icon: 'Grid', hidden: true } },
      { path: 'tab-list', name: 'LowCodeTabList',
        component: () => import('@/views/lowcode/tab-list/index.vue'),
        meta: { title: '标签页配置', icon: 'Files' } },
      { path: 'tab-designer', name: 'LowCodeTabDesigner',
        component: () => import('@/views/lowcode/tab-designer/index.vue'),
        meta: { title: '标签页设计器', icon: 'EditPen', hidden: true } },
      { path: 'related-page-list', name: 'LowCodeRelatedPageList',
        component: () => import('@/views/lowcode/related-page-list/index.vue'),
        meta: { title: '关联页配置', icon: 'Share' } },
      { path: 'related-page-designer', name: 'LowCodeRelatedPageDesigner',
        component: () => import('@/views/lowcode/related-page-designer/index.vue'),
        meta: { title: '关联页设计器', icon: 'EditPen', hidden: true } },
      { path: 'entity-designer', name: 'LowcodeEntityDesigner',
        component: () => import('@/views/lowcode/entity-designer/index.vue'),
        meta: { title: '实体设计器', icon: 'Connection' } },
      { path: 'version-history', name: 'LowcodeVersionHistory',
        component: () => import('@/views/lowcode/version-history/index.vue'),
        meta: { title: '版本历史', icon: 'Timer' } },
      { path: 'microflow-designer', name: 'LowcodeMicroflowDesigner',
        component: () => import('@/views/lowcode/microflow-designer/index.vue'),
        meta: { title: '微流设计器', icon: 'Share' } },
      { path: 'rule-designer', name: 'LowcodeRuleDesigner',
        component: () => import('@/views/lowcode/rule-designer/index.vue'),
        meta: { title: '规则设计器', icon: 'Filter' } },
      { path: 'process-designer', name: 'LowcodeProcessDesigner',
        component: () => import('@/views/lowcode/process-designer/index.vue'),
        meta: { title: '流程设计器', icon: 'Connection' } },
      { path: 'trigger-list', name: 'LowcodeTriggerList',
        component: () => import('@/views/lowcode/trigger-list/index.vue'),
        meta: { title: '触发器', icon: 'BellFilled' } },
      { path: 'connector-designer', name: 'LowcodeConnectorDesigner',
        component: () => import('@/views/lowcode/connector-designer/index.vue'),
        meta: { title: '连接器配置', icon: 'Connection' } },
      { path: 'preview', name: 'LowcodePreview',
        component: () => import('@/views/lowcode/preview/index.vue'),
        meta: { title: '预览', icon: 'View', hidden: true } },
      { path: 'publish-center', name: 'LowcodePublishCenter',
        component: () => import('@/views/lowcode/publish-center/index.vue'),
        meta: { title: '发布中心', icon: 'Promotion' } },
      { path: 'approval-chain', name: 'LowcodeApprovalChain',
        component: () => import('@/views/lowcode/approval-chain/index.vue'),
        meta: { title: '审批链配置', icon: 'SetUp' } },
      { path: 'template-market', name: 'LowcodeTemplateMarket',
        component: () => import('@/views/lowcode/template-market/index.vue'),
        meta: { title: '模板市场', icon: 'Goods' } },
      { path: 'apm-dashboard', name: 'LowcodeApmDashboard',
        component: () => import('@/views/lowcode/apm-dashboard/index.vue'),
        meta: { title: 'APM 看板', icon: 'TrendCharts' } },
      { path: 'app-source-export', name: 'LowcodeAppSourceExport',
        component: () => import('@/views/lowcode/app-source-export/index.vue'),
        meta: { title: '应用源码导出', icon: 'Download' } },
      { path: ':pageType/:pageCode', name: 'LowCodeRender',
        component: () => import('@/views/lowcode/render/index.vue'),
        meta: { title: '低代码页面', hidden: true } }
    ]
  },
  // ============ 系统管理（嵌套） ============
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting', requiresAuth: true },
    children: [
      { path: 'user', name: 'SysUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' } },
      { path: 'role', name: 'SysRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' } },
      { path: 'menu', name: 'SysMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' } },
      { path: 'dict', name: 'SysDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Document' } },
      { path: 'cache', name: 'SysCache',
        component: () => import('@/views/system/cache/index.vue'),
        meta: { title: '缓存管理', icon: 'Coin' } },
      { path: 'schedule', name: 'SysSchedule',
        component: () => import('@/views/system/schedule/index.vue'),
        meta: { title: '定时任务', icon: 'Timer' } },
      { path: 'audit', name: 'SysAudit',
        component: () => import('@/views/system/audit/index.vue'),
        meta: { title: '审计日志', icon: 'DocumentChecked' } }
    ]
  },
  // ============ 其他单页（保留平铺） ============
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: 'system-status', name: 'SystemStatus',
        component: () => import('@/views/system-status/index.vue'),
        meta: { title: '系统状态', icon: 'Monitor' } },
      { path: 'changelog', name: 'Changelog',
        component: () => import('@/views/changelog/index.vue'),
        meta: { title: '版本日志', icon: 'Notebook' } }
    ]
  },
  // ============ 404 兜底 ============
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// Navigation guard
router.beforeEach(async (to, _from, next) => {
  startRouteLoading()
  const userStore = useUserStore()
  const title = (to.meta.title as string | undefined) ?? ''
  document.title = title ? `${title} - 网络设备工程项目管理系统` : '网络设备工程项目管理系统'

  if (to.meta.requiresAuth === false) {
    if (to.path === '/login' && userStore.token) {
      next('/dashboard')
      return
    }
    next()
    return
  }

  if (!userStore.token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  // localStorage only persists the token. Restore user details and permission
  // codes before evaluating route access after a browser refresh/direct visit.
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      userStore.reset()
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }
  }

  const requiredPermissions = requiredLowCodePermissions(to.path)
  if (requiredPermissions.length > 0 && !userStore.hasAnyPermission(requiredPermissions)) {
    ElMessage.error('您没有访问该低代码功能的权限')
    next('/dashboard')
    return
  }

  next()
})

router.afterEach(() => {
  stopRouteLoading()
})

export default router
