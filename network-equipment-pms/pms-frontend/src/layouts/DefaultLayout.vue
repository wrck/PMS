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

interface MenuLeaf {
  title: string
  path: string
  icon: string
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
      { title: '交付看板', path: '/project/kanban', icon: 'Grid' }
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
      { title: '服务商管理', path: '/implementation/agent', icon: 'OfficeBuilding' },
      { title: '结算管理', path: '/implementation/settlement', icon: 'Money' }
    ]
  },
  {
    title: '工作流',
    icon: 'Connection',
    children: [{ title: '待办中心', path: '/workflow/todo', icon: 'Bell' }]
  },
  {
    title: '交付治理',
    icon: 'Operation',
    children: [
      { title: 'Punch List', path: '/punch-list', icon: 'WarningFilled' },
      { title: 'RMA 返修', path: '/rma', icon: 'RefreshRight' },
      { title: '质保期管理', path: '/warranty', icon: 'Timer' },
      { title: '终验交付物', path: '/deliverable', icon: 'Document' }
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
      { title: '缓存管理', path: '/system/cache', icon: 'Coin' },
      { title: '定时任务', path: '/system/schedule', icon: 'Timer' },
      { title: '审计日志', path: '/system/audit', icon: 'DocumentChecked' }
    ]
  },
  { title: '报表统计', path: '/report', icon: 'TrendCharts' }
]

const activeMenu = computed(() => route.path)

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
        <template v-for="(item, idx) in menuGroups" :key="idx">
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
        <template v-for="(item, idx) in menuGroups" :key="idx">
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

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
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
}

.header-notification {
  margin-right: 16px;
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
