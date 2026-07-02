import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '工作台' } },
      { path: 'project', name: 'ProjectList', component: () => import('@/views/project/index.vue'), meta: { title: '项目管理' } },
      { path: 'project/detail/:id', name: 'ProjectDetail', component: () => import('@/views/project/detail.vue'), meta: { title: '项目详情' } },
      { path: 'presales', name: 'PresalesList', component: () => import('@/views/presales/index.vue'), meta: { title: '售前管理' } },
      { path: 'presales/detail/:id', name: 'PresalesDetail', component: () => import('@/views/presales/detail.vue'), meta: { title: '售前详情' } },
      { path: 'presales/apply', name: 'PresalesApply', component: () => import('@/views/presales/apply.vue'), meta: { title: '售前申请' } },
      { path: 'presales/audit/:id', name: 'PresalesAudit', component: () => import('@/views/presales/audit.vue'), meta: { title: '售前审批' } },
      { path: 'callback', name: 'CallbackList', component: () => import('@/views/callback/index.vue'), meta: { title: '回访管理' } },
      { path: 'callback/detail/:id', name: 'CallbackDetail', component: () => import('@/views/callback/detail.vue'), meta: { title: '回访详情' } },
      { path: 'callback/audit/:id', name: 'CallbackAudit', component: () => import('@/views/callback/audit.vue'), meta: { title: '回访审批' } },
      { path: 'closed-loop', name: 'ClosedLoopList', component: () => import('@/views/closedloop/index.vue'), meta: { title: '闭环管理' } },
      { path: 'closed-loop/detail/:id', name: 'ClosedLoopDetail', component: () => import('@/views/closedloop/detail.vue'), meta: { title: '闭环详情' } },
      { path: 'subcontract', name: 'SubcontractList', component: () => import('@/views/subcontract/index.vue'), meta: { title: '转包管理' } },
      { path: 'subcontract/detail/:id', name: 'SubcontractDetail', component: () => import('@/views/subcontract/detail.vue'), meta: { title: '转包详情' } },
      { path: 'subcontract/apply', name: 'SubcontractApply', component: () => import('@/views/subcontract/apply.vue'), meta: { title: '转包申请' } },
      { path: 'subcontract/audit/:id', name: 'SubcontractAudit', component: () => import('@/views/subcontract/audit.vue'), meta: { title: '转包审批' } },
      { path: 'prob', name: 'ProbList', component: () => import('@/views/prob/index.vue'), meta: { title: '技术公告' } },
      { path: 'prob/detail/:id', name: 'ProbDetail', component: () => import('@/views/prob/detail.vue'), meta: { title: '公告详情' } },
      { path: 'prob/apply', name: 'ProbApply', component: () => import('@/views/prob/apply.vue'), meta: { title: '新建公告' } },
      { path: 'prob/task', name: 'ProbTask', component: () => import('@/views/prob/task.vue'), meta: { title: '修复任务' } },
      { path: 'workflow', name: 'Workflow', component: () => import('@/views/workflow/index.vue'), meta: { title: '工作流' } },
      { path: 'report', name: 'Report', component: () => import('@/views/report/index.vue'), meta: { title: '报表' } },
      { path: 'maintenance', name: 'Maintenance', component: () => import('@/views/maintenance/index.vue'), meta: { title: '维保' } },
      { path: 'maintenance/detail/:id', name: 'MaintenanceDetail', component: () => import('@/views/maintenance/detail.vue'), meta: { title: '维保详情' } },
      { path: 'supervision', name: 'Supervision', component: () => import('@/views/supervision/index.vue'), meta: { title: '督查' } },
      { path: 'supervision/detail/:id', name: 'SupervisionDetail', component: () => import('@/views/supervision/detail.vue'), meta: { title: '督查详情' } },
      { path: 'certificate', name: 'Certificate', component: () => import('@/views/certificate/index.vue'), meta: { title: '合格证' } },
      { path: 'weekly', name: 'Weekly', component: () => import('@/views/weekly/index.vue'), meta: { title: '周报' } },
      { path: 'weekly/detail/:id', name: 'WeeklyDetail', component: () => import('@/views/weekly/detail.vue'), meta: { title: '周报详情' } },
      { path: 'notification', name: 'Notification', component: () => import('@/views/notification/index.vue'), meta: { title: '通知' } },
      { path: 'system/user', name: 'UserManage', component: () => import('@/views/system/user.vue'), meta: { title: '用户管理' } },
      { path: 'system/role', name: 'RoleManage', component: () => import('@/views/system/role.vue'), meta: { title: '角色管理' } },
      { path: 'system/dept', name: 'DeptManage', component: () => import('@/views/system/dept.vue'), meta: { title: '部门管理' } },
      { path: 'system/basic-data', name: 'BasicData', component: () => import('@/views/system/basicData.vue'), meta: { title: '基础数据' } },
      { path: 'system/operate-log', name: 'OperateLog', component: () => import('@/views/system/operateLog.vue'), meta: { title: '操作日志' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('pms_token')
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
