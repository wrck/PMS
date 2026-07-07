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
    // 帮助中心：公开访问（无需登录），后端 /api/system/help-content 也已放行
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
      },
      {
        path: 'system/user',
        name: 'SysUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'SysRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/menu',
        name: 'SysMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'system/dict',
        name: 'SysDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Document' }
      },
      {
        path: 'system/cache',
        name: 'SysCache',
        component: () => import('@/views/system/cache/index.vue'),
        meta: { title: '缓存管理', icon: 'Coin', requiresAuth: true }
      },
      {
        path: 'system/schedule',
        name: 'SysSchedule',
        component: () => import('@/views/system/schedule/index.vue'),
        meta: { title: '定时任务', icon: 'Timer', requiresAuth: true }
      },
      {
        path: 'system/audit',
        name: 'SysAudit',
        component: () => import('@/views/system/audit/index.vue'),
        meta: { title: '审计日志', icon: 'DocumentChecked', requiresAuth: true }
      },
      {
        path: 'project/list',
        name: 'ProjectList',
        component: () => import('@/views/project/list/index.vue'),
        meta: { title: '项目列表', icon: 'Folder' }
      },
      {
        path: 'project/detail/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail/index.vue'),
        meta: { title: '项目详情', hidden: true }
      },
      {
        path: 'project/kanban',
        name: 'ProjectKanban',
        component: () => import('@/views/project/kanban/index.vue'),
        meta: { title: '交付看板', icon: 'Grid' }
      },
      {
        path: 'asset/category',
        name: 'AssetCategory',
        component: () => import('@/views/asset/category/index.vue'),
        meta: { title: '设备分类', icon: 'Files' }
      },
      {
        path: 'asset/model',
        name: 'AssetModel',
        component: () => import('@/views/asset/model/index.vue'),
        meta: { title: '设备型号', icon: 'Box' }
      },
      {
        path: 'asset/list',
        name: 'AssetList',
        component: () => import('@/views/asset/list/index.vue'),
        meta: { title: '资产清单', icon: 'List' }
      },
      {
        path: 'implementation/task',
        name: 'ImplTask',
        component: () => import('@/views/implementation/task/index.vue'),
        meta: { title: '实施任务', icon: 'Tickets' }
      },
      {
        path: 'implementation/agent',
        name: 'AgentManage',
        component: () => import('@/views/implementation/agent/index.vue'),
        meta: { title: '服务商管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'implementation/settlement',
        name: 'Settlement',
        component: () => import('@/views/implementation/settlement/index.vue'),
        meta: { title: '结算管理', icon: 'Money' }
      },
      {
        path: 'workflow/todo',
        name: 'WorkflowTodo',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办中心', icon: 'Bell' }
      },
      {
        path: 'punch-list',
        name: 'PunchList',
        component: () => import('@/views/punch-list/index.vue'),
        meta: { title: 'Punch List', icon: 'WarningFilled', requiresAuth: true }
      },
      {
        path: 'rma',
        name: 'Rma',
        component: () => import('@/views/rma/index.vue'),
        meta: { title: 'RMA 返修', icon: 'RefreshRight', requiresAuth: true }
      },
      {
        path: 'warranty',
        name: 'Warranty',
        component: () => import('@/views/warranty/index.vue'),
        meta: { title: '质保期管理', icon: 'Timer', requiresAuth: true }
      },
      {
        path: 'deliverable',
        name: 'Deliverable',
        component: () => import('@/views/deliverable/index.vue'),
        meta: { title: '终验交付物', icon: 'Document', requiresAuth: true }
      },
      {
        path: 'notification',
        name: 'NotificationCenter',
        component: () => import('@/views/notification/index.vue'),
        meta: { title: '消息中心', icon: 'Bell', requiresAuth: true }
      },
      {
        path: 'integration-health',
        name: 'IntegrationHealth',
        component: () => import('@/views/integration-health/index.vue'),
        meta: { title: '集成健康检查', icon: 'Monitor', requiresAuth: true }
      },
      {
        path: 'risk',
        name: 'Risk',
        component: () => import('@/views/risk/index.vue'),
        meta: { title: '风险登记册', icon: 'Warning', requiresAuth: true }
      },
      {
        path: 'change-request',
        name: 'ChangeRequest',
        component: () => import('@/views/change-request/index.vue'),
        meta: { title: '变更管理', icon: 'EditPen', requiresAuth: true }
      },
      {
        path: 'issue',
        name: 'Issue',
        component: () => import('@/views/issue/index.vue'),
        meta: { title: '问题日志', icon: 'ChatLineSquare', requiresAuth: true }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '报表统计', icon: 'TrendCharts' }
      },
      {
        path: 'lowcode/form-list',
        name: 'LowCodeFormList',
        component: () => import('@/views/lowcode/form-list/index.vue'),
        meta: { title: '表单配置', icon: 'Document', requiresAuth: true }
      },
      {
        path: 'lowcode/form-designer',
        name: 'LowCodeFormDesigner',
        component: () => import('@/views/lowcode/form-designer/index.vue'),
        meta: { title: '表单设计器', icon: 'EditPen', requiresAuth: true, hidden: true }
      },
      {
        path: 'lowcode/list-list',
        name: 'LowCodeListList',
        component: () => import('@/views/lowcode/list-list/index.vue'),
        meta: { title: '列表配置', icon: 'List', requiresAuth: true }
      },
      {
        path: 'lowcode/list-designer',
        name: 'LowCodeListDesigner',
        component: () => import('@/views/lowcode/list-designer/index.vue'),
        meta: { title: '列表设计器', icon: 'Grid', requiresAuth: true, hidden: true }
      },
      {
        path: 'lowcode/tab-list',
        name: 'LowCodeTabList',
        component: () => import('@/views/lowcode/tab-list/index.vue'),
        meta: { title: '标签页配置', icon: 'Files', requiresAuth: true }
      },
      {
        path: 'lowcode/tab-designer',
        name: 'LowCodeTabDesigner',
        component: () => import('@/views/lowcode/tab-designer/index.vue'),
        meta: { title: '标签页设计器', icon: 'EditPen', requiresAuth: true, hidden: true }
      },
      {
        path: 'lowcode/related-page-list',
        name: 'LowCodeRelatedPageList',
        component: () => import('@/views/lowcode/related-page-list/index.vue'),
        meta: { title: '关联页配置', icon: 'Share', requiresAuth: true }
      },
      {
        path: 'lowcode/related-page-designer',
        name: 'LowCodeRelatedPageDesigner',
        component: () => import('@/views/lowcode/related-page-designer/index.vue'),
        meta: { title: '关联页设计器', icon: 'EditPen', requiresAuth: true, hidden: true }
      },
      {
        path: 'lowcode/entity-designer',
        name: 'LowcodeEntityDesigner',
        component: () => import('@/views/lowcode/entity-designer/index.vue'),
        meta: { title: '实体设计器', icon: 'Connection', requiresAuth: true }
      },
      {
        path: 'lowcode/version-history',
        name: 'LowcodeVersionHistory',
        component: () => import('@/views/lowcode/version-history/index.vue'),
        meta: { title: '版本历史', icon: 'Timer', requiresAuth: true }
      },
      {
        // 低代码页面通用渲染入口：3 段路径与上方 2 段静态路径（lowcode/form-list 等）不冲突。
        // pageType: form | list | tab | related-page
        // pageCode: 低代码配置编码
        path: 'lowcode/:pageType/:pageCode',
        name: 'LowCodeRender',
        component: () => import('@/views/lowcode/render/index.vue'),
        meta: { title: '低代码页面', requiresAuth: true, hidden: true }
      },
      {
        // 系统状态：后端健康 / 磁盘 / 反馈统计 / 近期动态
        path: 'system-status',
        name: 'SystemStatus',
        component: () => import('@/views/system-status/index.vue'),
        meta: { title: '系统状态', icon: 'Monitor', requiresAuth: true }
      },
      {
        // 版本日志：按版本展示变更记录
        path: 'changelog',
        name: 'Changelog',
        component: () => import('@/views/changelog/index.vue'),
        meta: { title: '版本日志', icon: 'Notebook', requiresAuth: true }
      }
    ]
  },
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

// Navigation guard: require authentication for protected routes
router.beforeEach((to, _from, next) => {
  startRouteLoading()
  const userStore = useUserStore()
  const title = (to.meta.title as string | undefined) ?? ''
  document.title = title ? `${title} - 网络设备工程项目管理系统` : '网络设备工程项目管理系统'

  // Public routes (e.g. login). If already authenticated, skip the login page.
  if (to.meta.requiresAuth === false) {
    if (to.path === '/login' && userStore.token) {
      next('/dashboard')
      return
    }
    next()
    return
  }

  // Protected routes: redirect to login when there is no token
  if (!userStore.token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }
  next()
})

// 路由结束后关闭顶部加载条
router.afterEach(() => {
  stopRouteLoading()
})

export default router
