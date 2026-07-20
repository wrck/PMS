<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue'
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
import SidebarMenu from '@/components/layout/SidebarMenu.vue'
import {
  useVisibleMenuGroups,
  useTopTabs,
  useMenuByGroup,
  inferActiveTabGroup
} from '@/config/menu'
import { provideProjectContext } from '@/composables/useProjectContext'

const appStore = useAppStore()
const userStore = useUserStore()
const websocketStore = useWebSocketStore()
const route = useRoute()
const router = useRouter()

// 在 Layout 根节点提供项目上下文，供项目树侧栏 / 工作区 / 阶段面板注入
provideProjectContext()

// ===== 菜单数据源（已按权限过滤） — 从 @/config/menu 注入 =====
const visibleMenuGroups = useVisibleMenuGroups()
const topTabs = useTopTabs()

// ===== 顶栏一级 Tab + 侧栏联动 =====
/**
 * 当前激活的顶级 Tab group 标识。
 *
 * <p>初始化时根据当前路由推断（如 /project/list → 'project'），
 * 路由变化时自动同步（如点击 TagsView 标签跳转到 /system/user 时，
 * activeTabGroup 自动从 'project' 切换到 'system'）。
 * 用户手动点击顶栏 Tab 时，会触发路由跳转，watch 会同步 activeTabGroup。</p>
 */
const activeTabGroup = ref<string>(inferActiveTabGroup(route.path))

watch(
  () => route.path,
  (newPath) => {
    const inferred = inferActiveTabGroup(newPath)
    if (inferred !== activeTabGroup.value) {
      activeTabGroup.value = inferred
    }
  }
)

/** 侧栏二级菜单：仅显示当前 Tab group 下的菜单项（响应式） */
const sidebarMenuItems = useMenuByGroup(() => activeTabGroup.value)

/** 当前 Tab 标题（用于侧栏顶部显示） */
const currentTabTitle = computed(() => {
  const tab = topTabs.value.find((t) => t.group === activeTabGroup.value)
  return tab?.title ?? ''
})

/** 顶栏 Tab 点击：跳转到该 Tab 的 defaultPath */
function handleTabClick(tab: { group: string; defaultPath: string }): void {
  if (tab.group === activeTabGroup.value) return
  activeTabGroup.value = tab.group
  router.push(tab.defaultPath).catch(() => {
    /* 路由跳转失败（如权限不足）忽略 */
  })
}

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
    <!-- ============ 顶栏：Logo + 一级 Tab + 工具区 + 用户菜单 ============ -->
    <el-header class="layout-header">
      <!-- 左侧：Logo + 一级 Tab -->
      <div class="header-left">
        <div class="logo">
          <el-icon :size="22" color="#fff"><Cpu /></el-icon>
          <span v-if="!isMobile" class="logo-text">网络设备 PMS</span>
        </div>
        <!-- 一级 Tab 列表（移动端隐藏，改用汉堡按钮） -->
        <div v-if="!isMobile" class="top-tabs">
          <div
            v-for="tab in topTabs"
            :key="tab.group"
            class="top-tab-item"
            :class="{ active: tab.group === activeTabGroup }"
            @click="handleTabClick(tab)"
          >
            <el-icon :size="15"><component :is="tab.icon" /></el-icon>
            <span class="top-tab-label">{{ tab.title }}</span>
          </div>
        </div>
        <!-- 移动端汉堡按钮 -->
        <el-icon
          v-else
          class="collapse-btn"
          :size="22"
          @click="toggleMobileDrawer"
        >
          <Expand />
        </el-icon>
      </div>

      <!-- 右侧：工具区 + 用户菜单 -->
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

    <el-container class="layout-body">
      <!-- ============ 桌面端侧边栏：仅显示当前 Tab group 的二级菜单 ============ -->
      <el-aside
        v-if="!isMobile"
        :width="appStore.sidebarCollapsed ? '64px' : '200px'"
        class="layout-aside"
      >
        <!-- 侧栏顶部：当前 Tab 标题 + 折叠按钮 -->
        <div class="sidebar-header">
          <span v-if="!appStore.sidebarCollapsed" class="sidebar-title">{{ currentTabTitle }}</span>
          <el-icon
            class="collapse-btn"
            :size="18"
            @click="appStore.toggleSidebar()"
          >
            <Fold v-if="!appStore.sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>
        <SidebarMenu
          :items="sidebarMenuItems"
          :active-menu="activeMenu"
          :collapse="appStore.sidebarCollapsed"
        />
      </el-aside>

      <!-- 移动端抽屉侧边栏：保持原全部菜单（移动端无顶栏 Tab） -->
      <el-drawer
        v-if="isMobile"
        v-model="drawerVisible"
        direction="ltr"
        :size="220"
        :with-header="false"
        class="mobile-drawer"
      >
        <div class="logo mobile-logo">
          <el-icon :size="24" color="#fff"><Cpu /></el-icon>
          <span class="logo-text">网络设备 PMS</span>
        </div>
        <SidebarMenu
          :items="visibleMenuGroups"
          :active-menu="activeMenu"
          @select="handleMenuSelect"
        />
      </el-drawer>

      <el-container class="layout-content">
        <!-- 路由切换顶部加载条 -->
        <div v-if="routeLoading" class="route-loading-bar"></div>

        <!-- 面包屑 + 标签栏行（移动端隐藏面包屑，标签栏移动端隐藏） -->
        <div v-if="!isMobile" class="breadcrumb-tags-row">
          <el-breadcrumb separator="/" class="layout-breadcrumb">
            <el-breadcrumb-item
              v-for="(b, i) in breadcrumbs"
              :key="i"
              :to="b.path"
            >
              {{ b.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <TagsView />
        </div>

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
  display: flex;
  flex-direction: column;
}

/* ============ 顶栏 ============ */
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  background-color: #001529;
  border-bottom: 1px solid #002140;
  padding: 0 16px;
  color: #fff;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 100%;
  padding-right: 16px;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  color: #fff;
}

/* 顶栏一级 Tab */
.top-tabs {
  display: flex;
  align-items: center;
  gap: 4px;
  height: 100%;
  overflow-x: auto;
  scrollbar-width: none;
}

.top-tabs::-webkit-scrollbar {
  display: none;
}

.top-tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 14px;
  border-radius: 6px;
  color: #cfd5dc;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.top-tab-item:hover {
  background-color: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.top-tab-item.active {
  background-color: #409eff;
  color: #fff;
  font-weight: 500;
}

.top-tab-label {
  line-height: 1;
}

/* 右侧工具区 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-action-btn {
  cursor: pointer;
  color: #cfd5dc;
  outline: none;
}

.header-action-btn:hover {
  color: #fff;
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
  color: #fff;
}

.user-avatar {
  background-color: #409eff;
}

.username {
  font-size: 13px;
  color: #fff;
}

.collapse-btn {
  cursor: pointer;
  color: #cfd5dc;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #fff;
}

/* ============ 主体区域 ============ */
.layout-body {
  flex: 1;
  min-height: 0;
}

.layout-aside {
  background-color: #001529;
  transition: width 0.28s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  background-color: #002140;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mobile-logo {
  height: 56px;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.layout-content {
  flex: 1;
  min-height: 0;
  flex-direction: column;
}

/* 面包屑 + TagsView 行 */
.breadcrumb-tags-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 36px;
  padding: 0 16px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f3;
  flex-shrink: 0;
  gap: 16px;
}

.layout-breadcrumb {
  font-size: 13px;
  flex-shrink: 0;
}

.layout-main {
  background-color: #f5f7fa;
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
  flex-shrink: 0;
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
