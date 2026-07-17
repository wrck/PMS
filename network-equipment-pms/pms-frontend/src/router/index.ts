import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { startRouteLoading, stopRouteLoading } from '@/directives/loading'

const Layout = () => import('@/layouts/DefaultLayout.vue')

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
  // ============ 项目管理（嵌套） ============
  {
    path: '/project',
    component: Layout,
    redirect: '/project/list',
    meta: { title: '项目管理', icon: 'Folder', requiresAuth: true },
    children: [
      {
        path: 'list',
        name: 'ProjectList',
        component: () => import('@/views/project/list/index.vue'),
        meta: { title: '项目列表', icon: 'Folder' }
      },
      {
        path: 'detail/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail/index.vue'),
        meta: { title: '项目详情', hidden: true }
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
      { path: 'agent', name: 'AgentManage',
        component: () => import('@/views/implementation/agent/index.vue'),
        meta: { title: '服务商管理', icon: 'OfficeBuilding' } },
      { path: 'settlement', name: 'Settlement',
        component: () => import('@/views/implementation/settlement/index.vue'),
        meta: { title: '结算管理', icon: 'Money' } }
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
        meta: { title: '待办中心', icon: 'Bell' } }
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
router.beforeEach((to, _from, next) => {
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

  next()
})

router.afterEach(() => {
  stopRouteLoading()
})

export default router
