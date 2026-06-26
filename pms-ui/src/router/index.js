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
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'project',
        name: 'ProjectList',
        component: () => import('@/views/project/index.vue'),
        meta: { title: '项目管理' }
      },
      {
        path: 'project/detail/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/detail.vue'),
        meta: { title: '项目详情' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/dept',
        name: 'DeptManage',
        component: () => import('@/views/system/dept.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'system/basic-data',
        name: 'BasicDataManage',
        component: () => import('@/views/system/basicData.vue'),
        meta: { title: '基础数据' }
      },
      {
        path: 'system/operate-log',
        name: 'OperateLog',
        component: () => import('@/views/system/operateLog.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'presales',
        name: 'PresalesList',
        component: () => import('@/views/presales/index.vue'),
        meta: { title: '售前管理' }
      },
      {
        path: 'callback',
        name: 'CallBackList',
        component: () => import('@/views/callback/index.vue'),
        meta: { title: '回访管理' }
      },
      {
        path: 'closed-loop',
        name: 'ClosedLoopList',
        component: () => import('@/views/closedloop/index.vue'),
        meta: { title: '项目闭环' }
      },
      {
        path: 'subcontract',
        name: 'SubcontractList',
        component: () => import('@/views/subcontract/index.vue'),
        meta: { title: '转包管理' }
      },
      {
        path: 'prob',
        name: 'ProbList',
        component: () => import('@/views/prob/index.vue'),
        meta: { title: '技术公告' }
      },
      {
        path: 'maintenance',
        name: 'MaintenanceList',
        component: () => import('@/views/maintenance/index.vue'),
        meta: { title: '维保管理' }
      },
      {
        path: 'supervision',
        name: 'SupervisionList',
        component: () => import('@/views/supervision/index.vue'),
        meta: { title: '项目督查' }
      },
      {
        path: 'certificate',
        name: 'CertificateList',
        component: () => import('@/views/certificate/index.vue'),
        meta: { title: '合格证' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
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
