<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getIntegrationHealth,
  listIntegrationLogs,
  retryPush,
  type IntegrationHealthItem,
  type IntegrationLog
} from '@/api/integration-health'

defineOptions({ name: 'IntegrationHealth' })

// ============== 健康总览 ==============
const healthLoading = ref(false)
const healthItems = ref<IntegrationHealthItem[]>([])
const overallStatus = ref<string>('HEALTHY')
const timestamp = ref<string>('')

// 总体状态横幅
const alertMeta = computed(() => {
  switch (overallStatus.value) {
    case 'HEALTHY':
      return { type: 'success', title: '集成系统总体状态：健康', desc: '所有集成系统运行正常' }
    case 'DEGRADED':
      return { type: 'warning', title: '集成系统总体状态：降级', desc: '部分集成系统存在异常' }
    case 'DOWN':
      return { type: 'error', title: '集成系统总体状态：不可用', desc: '集成系统存在严重故障' }
    default:
      return { type: 'info', title: '集成系统状态：未知', desc: '' }
  }
})

// 系统状态指示灯
function statusMeta(status?: string): { color: string; label: string } {
  switch (status) {
    case 'UP':
      return { color: '#67C23A', label: '正常' }
    case 'DEGRADED':
      return { color: '#E6A23C', label: '降级' }
    case 'DOWN':
      return { color: '#F56C6C', label: '不可用' }
    default:
      return { color: '#909399', label: '未知' }
  }
}

function tokenTagType(valid: boolean): any {
  return valid ? 'success' : 'danger'
}

// 成功率进度条状态
function rateStatus(rate: number): any {
  if (rate >= 95) return 'success'
  if (rate >= 80) return 'warning'
  return 'exception'
}

async function loadHealth() {
  healthLoading.value = true
  try {
    const res = await getIntegrationHealth()
    healthItems.value = res.items ?? []
    overallStatus.value = res.overallStatus ?? 'HEALTHY'
    timestamp.value = res.timestamp ?? ''
  } catch {
    /* handled by interceptor */
  } finally {
    healthLoading.value = false
  }
}

// 卡片刷新按钮
function handleRefreshSystem(item: IntegrationHealthItem) {
  ElMessage.info(`正在刷新「${item.system}」状态...`)
  loadHealth()
}

// ============== 推送日志 ==============
const logLoading = ref(false)
const logData = ref<IntegrationLog[]>([])
const logTotal = ref(0)

const query = reactive<{ page: number; size: number; logType?: string; responseStatus?: string }>({
  page: 1,
  size: 10,
  logType: '',
  responseStatus: ''
})

async function loadLogs() {
  logLoading.value = true
  try {
    const params: any = { page: query.page, size: query.size }
    if (query.logType) params.logType = query.logType
    if (query.responseStatus) params.responseStatus = query.responseStatus
    const res = await listIntegrationLogs(params)
    logData.value = res.records ?? []
    logTotal.value = res.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    logLoading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadLogs()
}

function handleReset() {
  query.logType = ''
  query.responseStatus = ''
  query.page = 1
  loadLogs()
}

function handlePageChange(p: number) {
  query.page = p
  loadLogs()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadLogs()
}

// 响应状态标签
function responseTagType(status?: string): any {
  return status === 'SUCCESS' ? 'success' : 'danger'
}

// 重试单条日志
async function handleRetry(row: IntegrationLog) {
  try {
    await retryPush(row.id)
    ElMessage.success('已触发重试')
    loadLogs()
    loadHealth()
  } catch {
    /* handled by interceptor */
  }
}

// ============== 自动刷新 ==============
let refreshTimer: ReturnType<typeof setInterval> | null = null

function startAutoRefresh() {
  stopAutoRefresh()
  refreshTimer = setInterval(() => {
    loadHealth()
    loadLogs()
  }, 60000)
}

function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(() => {
  loadHealth()
  loadLogs()
  startAutoRefresh()
})

onBeforeUnmount(() => {
  stopAutoRefresh()
})
</script>

<template>
  <div class="page-container">
    <!-- 总体状态横幅 -->
    <el-alert
      :title="alertMeta.title"
      :description="alertMeta.desc + (timestamp ? `（更新时间：${timestamp}）` : '')"
      :type="alertMeta.type"
      :closable="false"
      show-icon
    />

    <!-- 三个系统卡片 -->
    <el-row :gutter="16" v-loading="healthLoading">
      <el-col v-for="item in healthItems" :key="item.system" :span="8">
        <el-card shadow="hover" class="system-card">
          <div class="system-card-header">
            <span class="system-name">{{ item.system }}</span>
            <span class="status-indicator">
              <span
                class="status-dot"
                :style="{ background: statusMeta(item.status).color }"
              />
              <el-tag size="small" :type="item.status === 'UP' ? 'success' : item.status === 'DEGRADED' ? 'warning' : 'danger'">
                {{ statusMeta(item.status).label }}
              </el-tag>
            </span>
          </div>

          <div class="system-row">
            <span class="row-label">Token 有效性：</span>
            <el-tag size="small" :type="tokenTagType(item.tokenValid)">
              {{ item.tokenValid ? '有效' : '失效' }}
            </el-tag>
          </div>

          <div class="system-row">
            <span class="row-label">最近推送时间：</span>
            <span class="row-value">{{ item.lastPushTime || '-' }}</span>
          </div>

          <div class="system-row">
            <span class="row-label">最近推送状态：</span>
            <el-tag
              v-if="item.lastPushStatus"
              size="small"
              :type="responseTagType(item.lastPushStatus)"
            >
              {{ item.lastPushStatus }}
            </el-tag>
            <span v-else class="row-value">-</span>
          </div>

          <div class="system-row">
            <span class="row-label">推送统计：</span>
            <span class="row-value">
              总数 {{ item.totalPushes }} / 失败 {{ item.failedPushes }}
            </span>
          </div>

          <div class="system-row">
            <span class="row-label">成功率：</span>
            <el-progress
              :percentage="Number(item.successRate ?? 0)"
              :status="rateStatus(Number(item.successRate ?? 0))"
              :stroke-width="10"
            />
          </div>

          <div v-if="item.message" class="system-message">{{ item.message }}</div>

          <div class="system-card-footer">
            <el-button size="small" type="primary" plain @click="handleRefreshSystem(item)">
              手动重试
            </el-button>
          </div>
        </el-card>
      </el-col>
      <template v-if="healthItems.length === 0">
        <el-col :span="24">
          <el-empty description="暂无集成系统数据" />
        </el-col>
      </template>
    </el-row>

    <!-- 推送历史记录 -->
    <el-card shadow="never">
      <template #header>
        <span class="page-title">推送历史记录</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="系统">
          <el-select v-model="query.logType" placeholder="全部系统" clearable style="width: 140px">
            <el-option label="D365" value="D365" />
            <el-option label="FP" value="FP" />
            <el-option label="OA" value="OA" />
          </el-select>
        </el-form-item>
        <el-form-item label="响应状态">
          <el-select v-model="query.responseStatus" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="SUCCESS" value="SUCCESS" />
            <el-option label="FAIL" value="FAIL" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="logLoading" :data="logData" border stripe>
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column prop="logType" label="系统" width="90" align="center" />
        <el-table-column prop="businessType" label="业务类型" width="140" show-overflow-tooltip />
        <el-table-column prop="requestUrl" label="URL" min-width="220" show-overflow-tooltip />
        <el-table-column label="响应状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="responseTagType(row.responseStatus)" size="small">
              {{ row.responseStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="retryCount" label="重试次数" width="100" align="center" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.responseStatus === 'FAIL'"
              link
              type="primary"
              @click="handleRetry(row)"
            >
              重试
            </el-button>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无推送日志" />
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="logTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.system-card {
  margin-bottom: 12px;
}
.system-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.system-name {
  font-size: 16px;
  font-weight: 600;
}
.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
}
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.system-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
}
.row-label {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
  width: 110px;
}
.row-value {
  color: var(--el-text-color-primary);
}
.system-row .el-progress {
  flex: 1;
}
.system-message {
  margin-top: 4px;
  padding: 6px 10px;
  font-size: 12px;
  color: var(--el-color-warning);
  background: var(--el-fill-color-light);
  border-radius: 4px;
}
.system-card-footer {
  margin-top: 12px;
  text-align: right;
}
.text-muted {
  color: var(--el-text-color-secondary);
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
