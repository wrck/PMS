<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { get, service } from '@/utils/request'
import { getFeedbackStatusStats, type FeedbackStatusStats } from '@/api/feedback'
import { getRecentActivities, type ActivityItem } from '@/api/report'

/**
 * 系统状态页面。
 *
 * <p>展示：
 * <ul>
 *   <li>后端服务状态（调用 /actuator/health）</li>
 *   <li>数据库状态、Redis 状态、磁盘使用率（来自 actuator health 详情）</li>
 *   <li>当前用户反馈的处理状态统计（待处理 / 处理中 / 已解决 / 已关闭）</li>
 *   <li>最近 5 条系统动态（操作日志）</li>
 * </ul>
 * </p>
 */

interface HealthComponent {
  status: string
  details?: Record<string, unknown>
}

interface HealthResponse {
  status: string
  components?: Record<string, HealthComponent>
}

interface DiskInfo {
  total: number
  free: number
  used: number
  usagePercent: number
}

const loading = ref(false)
const health = ref<HealthResponse | null>(null)
const feedbackStats = ref<FeedbackStatusStats>({
  PENDING: 0,
  PROCESSING: 0,
  RESOLVED: 0,
  CLOSED: 0
})
const recentActivities = ref<ActivityItem[]>([])
const lastUpdated = ref<string>('')

const overallStatus = computed(() => health.value?.status ?? 'UNKNOWN')

const dbStatus = computed(() => health.value?.components?.db?.status ?? 'UNKNOWN')
const redisStatus = computed(() => health.value?.components?.redis?.status ?? 'UNKNOWN')
const diskStatus = computed(() => health.value?.components?.diskSpace?.status ?? 'UNKNOWN')

const diskInfo = computed<DiskInfo>(() => {
  const details = health.value?.components?.diskSpace?.details
  if (!details) {
    return { total: 0, free: 0, used: 0, usagePercent: 0 }
  }
  const total = (details.total as number) || 0
  const free = (details.free as number) || 0
  const used = total - free
  const usagePercent = total > 0 ? Math.round((used / total) * 100) : 0
  return { total, free, used, usagePercent }
})

const totalFeedback = computed(() =>
  Object.values(feedbackStats.value).reduce((sum, n) => sum + n, 0)
)

function formatBytes(bytes: number): string {
  if (!bytes || bytes <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let i = 0
  let n = bytes
  while (n >= 1024 && i < units.length - 1) {
    n /= 1024
    i++
  }
  return `${n.toFixed(2)} ${units[i]}`
}

function statusTagType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  switch (status?.toUpperCase()) {
    case 'UP':
      return 'success'
    case 'DOWN':
      return 'danger'
    case 'OUT_OF_SERVICE':
    case 'DEGRADED':
      return 'warning'
    default:
      return 'info'
  }
}

function statusLabel(status: string): string {
  switch (status?.toUpperCase()) {
    case 'UP':
      return '正常运行'
    case 'DOWN':
      return '已宕机'
    case 'OUT_OF_SERVICE':
      return '服务不可用'
    case 'DEGRADED':
      return '降级运行'
    case 'UNKNOWN':
      return '未知'
    default:
      return status || '未知'
  }
}

function feedbackStatusTagType(status: string): 'success' | 'warning' | 'info' | 'danger' {
  switch (status) {
    case 'PENDING':
      return 'danger'
    case 'PROCESSING':
      return 'warning'
    case 'RESOLVED':
      return 'success'
    case 'CLOSED':
      return 'info'
    default:
      return 'info'
  }
}

function feedbackStatusLabel(status: string): string {
  switch (status) {
    case 'PENDING':
      return '待处理'
    case 'PROCESSING':
      return '处理中'
    case 'RESOLVED':
      return '已解决'
    case 'CLOSED':
      return '已关闭'
    default:
      return status
  }
}

async function loadHealth() {
  try {
    // /actuator/health 已在 SecurityConfig 中放行；通过 axios 实例直接请求以获取原始响应
    const res = await service.get('/actuator/health')
    // actuator 直接返回 JSON（非 Result 信封），无需解包
    health.value = res.data as HealthResponse
  } catch {
    // 降级：标记为 UNKNOWN
    health.value = { status: 'UNKNOWN' }
    ElMessage.warning('无法获取后端健康状态，请检查网络或服务是否启动')
  }
}

async function loadFeedbackStats() {
  try {
    feedbackStats.value = await getFeedbackStatusStats()
  } catch {
    // 静默失败
  }
}

async function loadRecentActivities() {
  try {
    recentActivities.value = await getRecentActivities(5)
  } catch {
    recentActivities.value = []
  }
}

async function refresh() {
  loading.value = true
  await Promise.all([loadHealth(), loadFeedbackStats(), loadRecentActivities()])
  lastUpdated.value = new Date().toLocaleString('zh-CN')
  loading.value = false
}

function activityTagType(type: string): 'success' | 'warning' | 'info' | 'danger' {
  switch (type) {
    case 'LOGIN':
      return 'success'
    case 'OPER':
      return 'info'
    case 'SCHEDULE':
      return 'warning'
    case 'INTEGRATION':
      return 'danger'
    default:
      return 'info'
  }
}

function activityTypeLabel(type: string): string {
  switch (type) {
    case 'LOGIN':
      return '登录'
    case 'OPER':
      return '操作'
    case 'SCHEDULE':
      return '定时任务'
    case 'INTEGRATION':
      return '集成'
    default:
      return type
  }
}

function formatActivityTime(time: string): string {
  if (!time) return ''
  return time.length >= 16 ? time.slice(0, 16).replace('T', ' ') : time
}

// 显式引用 get 以避免 lint 报未使用（保留以便后续扩展）
void get

onMounted(() => {
  refresh()
})
</script>

<template>
  <div v-loading="loading" class="system-status-page">
    <header class="page-header">
      <h2 class="page-title">系统状态</h2>
      <div class="page-actions">
        <span v-if="lastUpdated" class="last-updated">最近更新：{{ lastUpdated }}</span>
        <el-button type="primary" @click="refresh">刷新</el-button>
      </div>
    </header>

    <!-- 总体状态 -->
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <span>总体状态</span>
          <el-tag :type="statusTagType(overallStatus)" size="large">
            {{ statusLabel(overallStatus) }}
          </el-tag>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8">
          <div class="status-item">
            <div class="status-item__label">后端服务</div>
            <el-tag :type="statusTagType(overallStatus)">
              {{ statusLabel(overallStatus) }}
            </el-tag>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="status-item">
            <div class="status-item__label">数据库</div>
            <el-tag :type="statusTagType(dbStatus)">
              {{ statusLabel(dbStatus) }}
            </el-tag>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="status-item">
            <div class="status-item__label">Redis 缓存</div>
            <el-tag :type="statusTagType(redisStatus)">
              {{ statusLabel(redisStatus) }}
            </el-tag>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 磁盘使用率 -->
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <span>磁盘使用率</span>
          <el-tag :type="statusTagType(diskStatus)">
            {{ statusLabel(diskStatus) }}
          </el-tag>
        </div>
      </template>
      <div class="disk-usage">
        <el-progress
          :percentage="diskInfo.usagePercent"
          :color="diskInfo.usagePercent > 85 ? '#f56c6c' : '#409eff'"
          :stroke-width="20"
          :text-inside="true"
        />
        <div class="disk-usage__detail">
          <span>已用：{{ formatBytes(diskInfo.used) }}</span>
          <span>可用：{{ formatBytes(diskInfo.free) }}</span>
          <span>总计：{{ formatBytes(diskInfo.total) }}</span>
        </div>
      </div>
    </el-card>

    <!-- 反馈处理状态统计 -->
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <span>我的反馈处理状态</span>
          <span class="card-header__extra">共 {{ totalFeedback }} 条</span>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col v-for="(count, status) in feedbackStats" :key="status" :span="6">
          <div class="feedback-stat">
            <div class="feedback-stat__count">{{ count }}</div>
            <div class="feedback-stat__label">
              <el-tag :type="feedbackStatusTagType(String(status))" size="small">
                {{ feedbackStatusLabel(String(status)) }}
              </el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最近系统动态 -->
    <el-card class="status-card">
      <template #header>
        <div class="card-header">
          <span>最近系统动态</span>
        </div>
      </template>
      <el-empty v-if="recentActivities.length === 0" description="暂无动态" />
      <ul v-else class="activity-list">
        <li v-for="item in recentActivities" :key="item.id" class="activity-item">
          <div class="activity-item__left">
            <el-tag :type="activityTagType(item.type)" size="small">
              {{ activityTypeLabel(item.type) }}
            </el-tag>
            <span class="activity-item__desc">{{ item.description }}</span>
          </div>
          <div class="activity-item__right">
            <span v-if="item.operatorName" class="activity-item__operator">
              {{ item.operatorName }}
            </span>
            <span class="activity-item__time">{{ formatActivityTime(item.createdAt) }}</span>
          </div>
        </li>
      </ul>
    </el-card>
  </div>
</template>

<style scoped>
.system-status-page {
  padding: 16px 24px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.last-updated {
  font-size: 12px;
  color: #909399;
}

.status-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #1f2d3d;
}

.card-header__extra {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px 0;
}

.status-item__label {
  font-size: 13px;
  color: #909399;
}

.disk-usage {
  padding: 8px 0;
}

.disk-usage__detail {
  margin-top: 12px;
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: #606266;
}

.feedback-stat {
  text-align: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.feedback-stat__count {
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
  margin-bottom: 8px;
}

.feedback-stat__label {
  font-size: 13px;
}

.activity-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item__left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.activity-item__desc {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-item__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.activity-item__operator {
  font-size: 12px;
  color: #606266;
}

.activity-item__time {
  font-size: 12px;
  color: #909399;
}
</style>
