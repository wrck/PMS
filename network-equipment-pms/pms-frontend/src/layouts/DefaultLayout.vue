<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { useWebSocketStore } from '@/stores/websocket'
import { routeLoading } from '@/directives/loading'
import TagsView from '@/components/TagsView/index.vue'
import NotificationBell from '@/components/NotificationBell/index.vue'
import UserGuide from '@/components/UserGuide/index.vue'
import FeedbackButton from '@/components/FeedbackButton/index.vue'
import ProjectTreeSidebar from '@/components/project/ProjectTreeSidebar.vue'
import { provideProjectContext } from '@/composables/useProjectContext'

interface MenuLeaf {
  title: string
  path: string
  icon: string
  permissions?: string[]
}
interface MenuGroup {
  title: string
  icon: string
  children: MenuLeaf[]
}

const appStore = useAppStore()
const userStore = useUserStore()
const websocketStore = useWebSocketStore()
const route = useRoute()
const router = useRouter()

// 在 Layout 根节点提供项目上下文，供项目树侧栏 / 工作区 / 阶段面板注入
provideProjectContext()

// ===== 移动端响应式检测 =====
const MOBILE_BREAKPOINT = 768
const isMobile = ref(false)
/** 移动端抽屉可见性 */
const drawerVisible = ref(false)

function updateMobileFlag() {
  isMobile.value =
    typeof window !== 'undefined' &&
    (window.matchMedia
      ? window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT}px)`).matches
      : window.innerWidth <= MOBILE_BREAKPOINT)
  // 切回桌面端时关闭抽屉
  if (!isMobile.value) drawerVisible.value = false
}

if (typeof window !== 'undefined') {
  updateMobileFlag()
  window.addEventListener('resize', updateMobileFlag)
}

// Sidebar menu structure (grouped by business module)
const menuGroups: (MenuGroup | MenuLeaf)[] = [
  { title: '首页', path: '/dashboard', icon: 'HomeFilled' },
  {
    title: '系统管理',
    icon: 'Setting',
    children: [
      { title: '用户管理', path: '/system/user', icon: 'User' },
      { title: '角色管理', path: '/system/role', icon: 'UserFilled' },
      { title: '菜单管理', path: '/system/menu', icon: 'Menu' },
      { title: '字典管理', path: '/system/dict', icon: 'Document' }
    ]
  },
  {
    title: '项目管理',
    icon: 'Folder',
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
    children: [
      { title: '设备分类', path: '/asset/category', icon: 'Files' },
      { title: '设备型号', path: '/asset/model', icon: 'Box' },
      { title: '资产清单', path: '/asset/list', icon: 'List' }
    ]
  },
  {
    title: '实施管理',
    icon: 'Tools',
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
    children: [
      { title: '基线管理', path: '/baseline/list', icon: 'Histogram' }
    ]
  },
  {
    title: '工作流',
    icon: 'Connection',
    children: [
      { title: '待办中心', path: '/workflow/todo', icon: 'Bell' },
      { title: '统一审批中心', path: '/workflow/approval-center', icon: 'Checked', permissions: ['workflow:approval:handle'] },
      { title: '字段权限配置', path: '/workflow/field-perm', icon: 'Lock', permissions: ['workflow:field:perm'] }
    ]
  },
  {
    title: '交付治理',
    icon: 'Operation',
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
    children: [
      { title: '风险登记册', path: '/risk', icon: 'Warning' },
      { title: '变更管理', path: '/change-request', icon: 'EditPen' },
      { title: '问题日志', path: '/issue', icon: 'ChatLineSquare' }
    ]
  },
  {
    title: '系统监控',
    icon: 'DataLine',
    children: [
      { title: '消息中心', path: '/notification', icon: 'Bell' },
      { title: '集成健康', path: '/integration-health', icon: 'Monitor' },
      { title: '系统状态', path: '/system-status', icon: 'Monitor' },
      { title: '缓存管理', path: '/system/cache', icon: 'Coin' },
      { title: '定时任务', path: '/system/schedule', icon: 'Timer' },
      { title: '审计日志', path: '/system/audit', icon: 'DocumentChecked' },
      { title: '版本日志', path: '/changelog', icon: 'Notebook' }
    ]
  },
  { title: '报表统计', path: '/report', icon: 'TrendCharts' },
  {
    title: '低代码',
    icon: 'MagicStick',
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

const lowCodeMenuPermissions: Record<string, string[]> = {
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

function canAccessMenu(item: MenuLeaf): boolean {
  const runtimePage = item.path.match(/^\/lowcode\/(form|list|tab|related-page)\/([^/?#]+)/)
  const permissions =
    item.permissions ??
    (runtimePage
      ? [`lowcode:page:${runtimePage[1]}:${runtimePage[2]}`]
      : lowCodeMenuPermissions[item.path])
  return !permissions || userStore.hasAnyPermission(permissions)
}

const visibleMenuGroups = computed<(MenuGroup | MenuLeaf)[]>(() =>
  menuGroups.reduce<(MenuGroup | MenuLeaf)[]>((result, item) => {
    if ('children' in item) {
      const children = item.children.filter(canAccessMenu)
      if (children.length > 0) result.push({ ...item, children })
    } else if (canAccessMenu(item)) {
      result.push(item)
    }
    return result
  }, [])
)

const activeMenu = computed(() => route.path)

/**
 * 是否在主内容区左侧显示项目树侧栏。
 * 优先看 route.meta.showProjectSidebar 显式声明，否则 fallback 到 /project 前缀。
 * 其他业务模块路由（资产 / 实施管理等）不受影响。
 */
const showProjectSidebar = computed<boolean>(() => {
  const meta = route.meta as { showProjectSidebar?: boolean }
  if (typeof meta.showProjectSidebar === 'boolean') return meta.showProjectSidebar
  return route.path.startsWith('/project')
})

// Breadcrumb derived from the matched route records
const breadcrumbs = computed(() =>
  route.matched
    .filter((r) => r.meta?.title)
    .map((r) => ({ title: r.meta!.title as string, path: r.path }))
)

const username = computed(
  () => userStore.userInfo?.nickname || userStore.userInfo?.username || '用户'
)

/** 移动端点击菜单项后关闭抽屉 */
function handleMenuSelect() {
  if (isMobile.value) {
    drawerVisible.value = false
  }
}

/** 切换移动端抽屉 */
function toggleMobileDrawer() {
  drawerVisible.value = !drawerVisible.value
}

function handleUserCommand(command: string) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
      .then(() => userStore.logout())
      .catch(() => {
        /* user cancelled */
      })
  } else if (command === 'dashboard') {
    router.push('/dashboard')
  }
}

/** 用户引导组件实例（用于手动触发引导） */
const userGuideRef = ref<InstanceType<typeof UserGuide> | null>(null)

/** 跳转到帮助中心页 */
function goHelp() {
  router.push('/help')
}

/** 手动触发用户引导（点击「引导」按钮时调用） */
function startGuide() {
  userGuideRef.value?.start()
}

// 已登录时建立 WebSocket 连接，组件销毁时断开
onMounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('resize', updateMobileFlag)
    window.addEventListener('resize', updateMobileFlag)
  }
  if (userStore.token) {
    websocketStore.connect()
  }
})

onBeforeUnmount(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('resize', updateMobileFlag)
  }
  websocketStore.disconnect()
})
</script>

<template>
  <el-container class="layout-root">
    <!-- 桌面端侧边栏 -->
    <el-aside
      v-if="!isMobile"
      :width="appStore.sidebarCollapsed ? '64px' : '220px'"
      class="layout-aside"
    >
      <div class="logo">
        <el-icon :size="24" color="#fff"><Cpu /></el-icon>
        <span v-show="!appStore.sidebarCollapsed" class="logo-text">网络设备 PMS</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        router
        background-color="#001529"
        text-color="#cfd5dc"
        active-text-color="#ffffff"
        class="side-menu"
      >
        <template v-for="(item, idx) in visibleMenuGroups" :key="idx">
          <el-sub-menu v-if="'children' in item" :index="String(idx)">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
              <el-icon><component :is="child.icon" /></el-icon>
              <template #title>{{ child.title }}</template>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="(item as MenuLeaf).path">
            <el-icon><component :is="(item as MenuLeaf).icon" /></el-icon>
            <template #title>{{ (item as MenuLeaf).title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 移动端抽屉侧边栏 -->
    <el-drawer
      v-if="isMobile"
      v-model="drawerVisible"
      direction="ltr"
      :size="220"
      :with-header="false"
      class="mobile-drawer"
    >
      <div class="logo">
        <el-icon :size="24" color="#fff"><Cpu /></el-icon>
        <span class="logo-text">网络设备 PMS</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#cfd5dc"
        active-text-color="#ffffff"
        class="side-menu"
        @select="handleMenuSelect"
      >
        <template v-for="(item, idx) in visibleMenuGroups" :key="idx">
          <el-sub-menu v-if="'children' in item" :index="String(idx)">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
              <el-icon><component :is="child.icon" /></el-icon>
              <template #title>{{ child.title }}</template>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="(item as MenuLeaf).path">
            <el-icon><component :is="(item as MenuLeaf).icon" /></el-icon>
            <template #title>{{ (item as MenuLeaf).title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-drawer>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <!-- 桌面端：折叠按钮；移动端：汉堡按钮 -->
          <el-icon
            v-if="!isMobile"
            class="collapse-btn"
            :size="20"
            @click="appStore.toggleSidebar()"
          >
            <Fold v-if="!appStore.sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-icon
            v-else
            class="collapse-btn"
            :size="22"
            @click="toggleMobileDrawer"
          >
            <Expand />
          </el-icon>
          <!-- 面包屑在移动端隐藏 -->
          <el-breadcrumb v-if="!isMobile" separator="/">
            <el-breadcrumb-item
              v-for="(b, i) in breadcrumbs"
              :key="i"
              :to="b.path"
            >
              {{ b.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="功能引导" placement="bottom">
            <el-icon class="header-action-btn" :size="18" @click="startGuide">
              <Guide />
            </el-icon>
          </el-tooltip>
          <el-tooltip content="帮助中心" placement="bottom">
            <el-icon class="header-action-btn" :size="18" @click="goHelp">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
          <NotificationBell class="header-notification" />
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="30" class="user-avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span v-if="!isMobile" class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="dashboard">首页</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 路由切换顶部加载条 -->
      <div v-if="routeLoading" class="route-loading-bar"></div>

      <!-- 标签栏在移动端隐藏 -->
      <TagsView v-if="!isMobile" />

      <!-- 主内容区：项目管理相关路由左侧额外渲染项目树侧栏 -->
      <div class="main-wrapper">
        <ProjectTreeSidebar v-if="showProjectSidebar && !isMobile" />
        <el-main class="layout-main">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </div>
    </el-container>

    <!-- 用户引导：首次登录自动触发，也可通过顶部「引导」按钮手动触发 -->
    <UserGuide ref="userGuideRef" />

    <!-- 浮动反馈按钮：右下角悬浮 -->
    <FeedbackButton />
  </el-container>
</template>

<style scoped>
.layout-root {
  height: 100vh;
}

.layout-aside {
  background-color: #001529;
  transition: width 0.28s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  background-color: #002140;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.side-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.side-menu:not(.el-menu--collapse) {
  width: 220px;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6eb;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #5a5e66;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-action-btn {
  cursor: pointer;
  color: #5a5e66;
  outline: none;
}

.header-action-btn:hover {
  color: #409eff;
}

.header-notification {
  margin-right: 4px;
  display: inline-flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.user-avatar {
  background-color: #409eff;
}

.username {
  font-size: 14px;
  color: #303133;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}

/* 主内容区包裹：当项目管理路由展示项目树侧栏时，水平排列侧栏 + 主区域 */
.main-wrapper {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.main-wrapper > .layout-main {
  flex: 1;
}

/* Route transition */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.25s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-12px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(12px);
}

/* 路由切换顶部加载条 */
.route-loading-bar {
  height: 3px;
  width: 100%;
  background: linear-gradient(90deg, #409eff 0%, #79bbff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: route-loading-move 1s linear infinite;
}

@keyframes route-loading-move {
  0% {
    background-position: 100% 0;
  }
  100% {
    background-position: 0 0;
  }
}
</style>

<style>
/* 移动端抽屉：覆盖 el-drawer 默认背景，匹配侧边栏深色风格 */
.mobile-drawer .el-drawer__body {
  padding: 0;
  background-color: #001529;
}
</style>
