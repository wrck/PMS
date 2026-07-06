<script setup lang="ts">
import { onBeforeUnmount, onMounted, nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { useUserStore } from '@/stores/user'
import {
  getAssetStats,
  getDashboardStats,
  getProjectTrend,
  getRecentActivities,
  getTodoList,
  type ActivityItem,
  type AssetStats,
  type DashboardStats,
  type ProjectTrendItem,
  type TodoItem
} from '@/api/report'

defineOptions({ name: 'DashboardView' })

const userStore = useUserStore()
const router = useRouter()

// ---- Overview cards (keep titles & initial values aligned with tests) ----
const statCards = ref([
  { title: '项目总数', value: 0, icon: 'Folder', color: '#409eff', sub: '进行中 0' },
  { title: '在库设备', value: 0, icon: 'Box', color: '#67c23a', sub: '本月新增 0' },
  { title: '待办任务', value: 0, icon: 'Bell', color: '#e6a23c', sub: '告警 0' },
  { title: '本月交付', value: 0, icon: 'TrendCharts', color: '#f56c6c', sub: '本月立项 0' }
])

const dashboardStats = ref<DashboardStats>({
  projectTotal: 0,
  assetInStock: 0,
  todoCount: 0,
  monthDelivery: 0,
  projectInProgress: 0,
  monthNewProject: 0,
  monthNewAsset: 0,
  alertCount: 0
})
const projectTrend = ref<ProjectTrendItem[]>([])
const todoList = ref<TodoItem[]>([])
const recentActivities = ref<ActivityItem[]>([])
const assetStats = ref<AssetStats>({
  byStatus: {},
  byCategory: {},
  totalValue: 0,
  total: 0,
  inStock: 0,
  allocated: 0,
  inTransfer: 0,
  scrapped: 0
})
const loading = ref(false)

const welcomeName = userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员'

// ---- Charts ----
const trendChartRef = ref<HTMLElement>()
const statusPieRef = ref<HTMLElement>()
const assetChartRef = ref<HTMLElement>()
let chartInstances: echarts.ECharts[] = []

function initChart(dom: HTMLElement | undefined): echarts.ECharts | null {
  if (!dom) return null
  const existing = chartInstances.find((c) => c.getDom() === dom)
  if (existing) return existing
  try {
    const inst = echarts.init(dom)
    chartInstances.push(inst)
    return inst
  } catch (e) {
    // jsdom or hidden container: skip rendering
    console.warn('echarts init failed', e)
    return null
  }
}

const STATUS_LABELS: Record<string, string> = {
  PENDING: '待立项',
  APPROVED: '已立项',
  IN_PROGRESS: '进行中',
  INITIAL_ACCEPTANCE: '初验',
  FINAL_ACCEPTANCE: '终验',
  COMPLETED: '已完成',
  CLOSED: '已关闭',
  REJECTED: '已拒绝'
}

const STATUS_COLORS: Record<string, string> = {
  PENDING: '#909399',
  APPROVED: '#a0cfff',
  IN_PROGRESS: '#409eff',
  INITIAL_ACCEPTANCE: '#e6a23c',
  FINAL_ACCEPTANCE: '#f56c6c',
  COMPLETED: '#67c23a',
  CLOSED: '#b3b3b3',
  REJECTED: '#9c27b0'
}

function renderTrendChart() {
  const inst = initChart(trendChartRef.value)
  if (!inst) return
  const months = [...new Set(projectTrend.value.map((t) => t.month))]
  const statuses = [...new Set(projectTrend.value.map((t) => t.status))]
  const series = statuses.map((status) => ({
    name: STATUS_LABELS[status] || status,
    type: 'bar' as const,
    stack: 'total',
    emphasis: { focus: 'series' as const },
    data: months.map(
      (m) => projectTrend.value.find((t) => t.month === m && t.status === status)?.count || 0
    ),
    itemStyle: { color: STATUS_COLORS[status] || '#409eff' }
  }))
  inst.setOption(
    {
      title: { text: '项目趋势（最近 6 月状态分布）', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { bottom: 0, type: 'scroll' },
      grid: { left: 40, right: 20, top: 50, bottom: 50 },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', minInterval: 1 },
      series
    },
    true
  )
}

function renderStatusPie() {
  const inst = initChart(statusPieRef.value)
  if (!inst) return
  const statusMap: Record<string, number> = {}
  projectTrend.value.forEach((t) => {
    statusMap[t.status] = (statusMap[t.status] || 0) + t.count
  })
  const data = Object.entries(statusMap).map(([k, v]) => ({
    name: STATUS_LABELS[k] || k,
    value: v,
    itemStyle: { color: STATUS_COLORS[k] || '#409eff' }
  }))
  inst.setOption(
    {
      title: { text: '项目状态分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, type: 'scroll' },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: true,
          label: { show: true, formatter: '{b}\n{d}%' },
          data
        }
      ]
    },
    true
  )
}

function renderAssetChart() {
  const inst = initChart(assetChartRef.value)
  if (!inst) return
  const a = assetStats.value
  inst.setOption(
    {
      title: { text: '设备状态分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, type: 'scroll' },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          label: { show: true, formatter: '{b}\n{d}%' },
          data: [
            { value: a.inStock, name: '在库', itemStyle: { color: '#409EFF' } },
            { value: a.allocated, name: '已分配', itemStyle: { color: '#67C23A' } },
            { value: a.inTransfer, name: '调拨中', itemStyle: { color: '#E6A23C' } },
            { value: a.scrapped, name: '报废', itemStyle: { color: '#909399' } }
          ]
        }
      ]
    },
    true
  )
}

function renderAllCharts() {
  nextTick(() => {
    renderTrendChart()
    renderStatusPie()
    renderAssetChart()
  })
}

function handleResize() {
  chartInstances.forEach((c) => c.resize())
}

function disposeCharts() {
  chartInstances.forEach((c) => {
    try {
      c.dispose()
    } catch (e) {
      console.warn('echarts dispose failed', e)
    }
  })
  chartInstances = []
}

function applyStats(s: DashboardStats) {
  statCards.value[0].value = s.projectTotal
  statCards.value[0].sub = `进行中 ${s.projectInProgress}`
  statCards.value[1].value = s.assetInStock
  statCards.value[1].sub = `本月新增 ${s.monthNewAsset}`
  statCards.value[2].value = s.todoCount
  statCards.value[2].sub = `告警 ${s.alertCount}`
  statCards.value[3].value = s.monthDelivery
  statCards.value[3].sub = `本月立项 ${s.monthNewProject}`
}

async function loadAll() {
  loading.value = true
  try {
    const [statsRes, trendRes, todoRes, activityRes, assetRes] = await Promise.all([
      getDashboardStats().catch(() => null),
      getProjectTrend().catch(() => [] as ProjectTrendItem[]),
      getTodoList(5).catch(() => [] as TodoItem[]),
      getRecentActivities(10).catch(() => [] as ActivityItem[]),
      getAssetStats().catch(() => null)
    ])
    if (statsRes) {
      dashboardStats.value = statsRes
      applyStats(statsRes)
    }
    projectTrend.value = trendRes || []
    todoList.value = todoRes || []
    recentActivities.value = activityRes || []
    if (assetRes) assetStats.value = assetRes
    renderAllCharts()
  } catch (e) {
    console.warn('Failed to load dashboard data', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // Defer to next tick so chart containers are laid out before init
  nextTick(() => loadAll())
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})

// ---- Quick actions ----
const shortcuts = [
  { title: '新建项目', icon: 'FolderAdd', color: '#409eff', route: '/project/list' },
  { title: '资产入库', icon: 'Box', color: '#67c23a', route: '/asset/list' },
  { title: '创建任务', icon: 'Tickets', color: '#e6a23c', route: '/implementation/task' },
  { title: '待办中心', icon: 'Bell', color: '#f56c6c', route: '/workflow/todo' }
]

function goTo(route: string) {
  router.push(route)
}

// ---- Helpers ----
function priorityTagType(p?: string) {
  if (p === 'HIGH') return 'danger'
  if (p === 'NORMAL') return 'warning'
  return 'info'
}

function priorityLabel(p?: string) {
  if (p === 'HIGH') return '高'
  if (p === 'NORMAL') return '中'
  return '低'
}

function activityTagType(t: string) {
  if (t === 'LOGIN') return 'success'
  if (t === 'OPER') return 'primary'
  return 'info'
}

function activityTypeLabel(t: string) {
  if (t === 'LOGIN') return '登录'
  if (t === 'OPER') return '操作'
  if (t === 'SCHEDULE') return '定时'
  if (t === 'INTEGRATION') return '集成'
  return t
}

function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').slice(0, 16)
}

function todoLink(t: TodoItem) {
  return `/implementation/task`
}
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <el-card shadow="never" class="welcome-card">
      <h2 class="welcome-title">欢迎回来，{{ welcomeName }} 👋</h2>
      <p class="welcome-desc">这里是网络设备工程项目管理系统工作台，下面是系统概览信息。</p>
    </el-card>

    <!-- 概览卡片：移动端单列 / 小平板双列 / 大屏四列 -->
    <el-row :gutter="16" class="stat-row">
      <el-col
        v-for="item in statCards"
        :key="item.title"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
      >
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="28" color="#fff"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-sub">{{ item.sub }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card shadow="never" class="shortcut-card">
      <template #header>
        <span class="section-title">快捷入口</span>
      </template>
      <el-row :gutter="16">
        <el-col
          v-for="item in shortcuts"
          :key="item.title"
          :xs="12"
          :sm="6"
          :md="6"
          :lg="6"
        >
          <div class="shortcut-item" @click="goTo(item.route)">
            <div class="shortcut-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="22" color="#fff"><component :is="item.icon" /></el-icon>
            </div>
            <div class="shortcut-text">{{ item.title }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="section-title">项目趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="section-title">项目状态分布</span>
          </template>
          <div ref="statusPieRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="section-title">设备状态分布</span>
          </template>
          <div ref="assetChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="todo-card">
          <template #header>
            <div class="card-header-flex">
              <span class="section-title">待办事项</span>
              <el-button link type="primary" @click="goTo('/workflow/todo')">更多</el-button>
            </div>
          </template>
          <el-empty v-if="todoList.length === 0" description="暂无待办" :image-size="80" />
          <ul v-else class="todo-list">
            <li v-for="t in todoList" :key="t.id" class="todo-item" @click="goTo(todoLink(t))">
              <div class="todo-main">
                <el-tag
                  :type="priorityTagType(t.priority)"
                  size="small"
                  effect="plain"
                  class="todo-priority"
                >{{ priorityLabel(t.priority) }}</el-tag>
                <span class="todo-title">{{ t.title }}</span>
              </div>
              <div class="todo-meta">
                <span v-if="t.projectName" class="todo-project">{{ t.projectName }}</span>
                <span v-if="t.deadline" class="todo-deadline">截止 {{ t.deadline }}</span>
              </div>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近期动态 -->
    <el-card shadow="never" class="activity-card">
      <template #header>
        <span class="section-title">近期动态</span>
      </template>
      <el-empty v-if="recentActivities.length === 0" description="暂无动态" :image-size="80" />
      <ul v-else class="activity-list">
        <li v-for="a in recentActivities" :key="`${a.type}-${a.id}`" class="activity-item">
          <div class="activity-dot" :class="`dot-${a.type.toLowerCase()}`"></div>
          <div class="activity-body">
            <div class="activity-head">
              <el-tag :type="activityTagType(a.type)" size="small" effect="plain">
                {{ activityTypeLabel(a.type) }}
              </el-tag>
              <span class="activity-desc">{{ a.description }}</span>
            </div>
            <div class="activity-foot">
              <span v-if="a.operatorName" class="activity-operator">{{ a.operatorName }}</span>
              <span class="activity-time">{{ formatTime(a.createdAt) }}</span>
            </div>
          </div>
        </li>
      </ul>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
@use '../../styles/design-tokens' as *;

.dashboard {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.welcome-card {
  background: linear-gradient(120deg, #{$color-primary} 0%, #2a5298 100%);
  border: none;
}

.welcome-card :deep(.el-card__body) {
  padding: $spacing-6;
}

.welcome-title {
  margin: 0 0 $spacing-2;
  color: #fff;
  font-size: $font-size-2xl;
  font-weight: $font-weight-semibold;
}

.welcome-desc {
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: $font-size-base;
}

.stat-row {
  margin: 0;
}

.stat-card {
  height: 100%;
  margin-bottom: $spacing-4;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: $spacing-4;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: $font-size-3xl;
  font-weight: $font-weight-semibold;
  color: $color-text-primary;
  line-height: $line-height-tight;
}

.stat-title {
  font-size: $font-size-base;
  color: $color-text-secondary;
  margin-top: $spacing-1;
}

.stat-sub {
  font-size: $font-size-xs;
  color: $color-text-placeholder;
  margin-top: 2px;
}

.section-title {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-text-primary;
}

// 快捷入口
.shortcut-card :deep(.el-card__body) {
  padding: $spacing-4;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 0;
  cursor: pointer;
  border-radius: $radius-md;
  transition: background $transition-fast;

  &:hover {
    background: $color-primary-light-9;
  }
}

.shortcut-icon {
  width: 44px;
  height: 44px;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shortcut-text {
  font-size: $font-size-sm;
  color: $color-text-regular;
}

// 图表
.chart-row {
  margin: 0;
}

.chart-card,
.todo-card,
.activity-card {
  height: 100%;
  margin-bottom: $spacing-4;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.card-header-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

// 待办列表
.todo-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.todo-item {
  padding: $spacing-3 0;
  border-bottom: 1px solid $color-border-light;
  cursor: pointer;
  transition: background $transition-fast;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: $color-bg-page;
  }
}

.todo-main {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  margin-bottom: $spacing-1;
}

.todo-priority {
  flex-shrink: 0;
}

.todo-title {
  font-size: $font-size-base;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  font-size: $font-size-xs;
  color: $color-text-secondary;
  padding-left: calc(#{ $spacing-2 } + 28px);
}

.todo-deadline {
  color: $color-danger;
}

// 动态时间线
.activity-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.activity-item {
  display: flex;
  gap: $spacing-3;
  padding: $spacing-2 0;

  &:not(:last-child) {
    border-bottom: 1px dashed $color-border-light;
  }
}

.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
  background: $color-info;

  &.dot-login {
    background: $color-success;
  }

  &.dot-oper {
    background: $color-primary;
  }

  &.dot-schedule {
    background: $color-warning;
  }

  &.dot-integration {
    background: $color-danger;
  }
}

.activity-body {
  flex: 1;
  min-width: 0;
}

.activity-head {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: $font-size-base;
  color: $color-text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-foot {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  font-size: $font-size-xs;
  color: $color-text-secondary;
}

.activity-operator {
  font-weight: $font-weight-medium;
}

// 移动端紧凑
@media (max-width: $breakpoint-md - 1px) {
  .welcome-card :deep(.el-card__body) {
    padding: $spacing-4;
  }

  .welcome-title {
    font-size: $font-size-xl;
  }

  .stat-card {
    margin-bottom: $spacing-3;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
  }

  .stat-icon :deep(.el-icon) {
    --font-size: 22px;
  }

  .stat-value {
    font-size: $font-size-2xl;
  }

  .chart-container {
    height: 260px;
  }
}
</style>
