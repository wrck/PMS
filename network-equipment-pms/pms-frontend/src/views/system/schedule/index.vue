<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getScheduleStatistic,
  listFailedScheduleLogs,
  listScheduleLogs,
  SCHEDULE_STATUS,
  TRIGGER_TYPE,
  type ScheduleLog,
  type ScheduleStatistic
} from '@/api/system-schedule'

defineOptions({ name: 'SystemSchedule' })

// ============ 统计卡片 ============
const statLoading = ref(false)
const statistic = ref<ScheduleStatistic>({})

async function loadStatistic() {
  statLoading.value = true
  try {
    statistic.value = await getScheduleStatistic()
  } catch {
    /* handled by interceptor */
  } finally {
    statLoading.value = false
  }
}

// ============ Tab 切换 ============
const activeTab = ref<'recent' | 'failed'>('recent')

// ============ 最近日志 ============
const recentLoading = ref(false)
const recentData = ref<ScheduleLog[]>([])
const recentTotal = ref(0)

// 状态筛选选项：ALL / SUCCESS / FAILED / MANUAL_TRIGGER
const statusOptions = [
  { value: 'ALL', label: '全部' },
  { value: 'SUCCESS', label: '成功' },
  { value: 'FAILED', label: '失败' },
  { value: 'MANUAL_TRIGGER', label: '手动触发' }
]

const filter = reactive<{
  status: string
  dateRange: [string, string] | null
  page: number
  size: number
}>({
  status: 'ALL',
  dateRange: null,
  page: 1,
  size: 10
})

function buildParams() {
  const params: Record<string, unknown> = {
    page: filter.page,
    size: filter.size
  }
  if (filter.status === 'SUCCESS') {
    params.status = SCHEDULE_STATUS.SUCCESS
  } else if (filter.status === 'FAILED') {
    // 后端 ScheduleLog.status 取值为 SUCCESS / FAIL
    params.status = SCHEDULE_STATUS.FAIL
  } else if (filter.status === 'MANUAL_TRIGGER') {
    params.triggerType = TRIGGER_TYPE.MANUAL
  }
  if (filter.dateRange && filter.dateRange.length === 2) {
    params.startDate = filter.dateRange[0]
    params.endDate = filter.dateRange[1]
  }
  return params
}

async function loadRecent() {
  recentLoading.value = true
  try {
    const res = await listScheduleLogs(buildParams())
    recentData.value = res?.records ?? []
    recentTotal.value = res?.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    recentLoading.value = false
  }
}

function handleSearch() {
  filter.page = 1
  loadRecent()
}

function handleReset() {
  filter.status = 'ALL'
  filter.dateRange = null
  filter.page = 1
  loadRecent()
}

function handlePageChange(p: number) {
  filter.page = p
  loadRecent()
}

function handleSizeChange(s: number) {
  filter.size = s
  filter.page = 1
  loadRecent()
}

// ============ 失败列表 ============
const failedLoading = ref(false)
const failedData = ref<ScheduleLog[]>([])
const failedTotal = ref(0)
const failedQuery = reactive({ page: 1, size: 10 })

async function loadFailed() {
  failedLoading.value = true
  try {
    const res = await listFailedScheduleLogs({
      page: failedQuery.page,
      size: failedQuery.size
    })
    failedData.value = res?.records ?? []
    failedTotal.value = res?.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    failedLoading.value = false
  }
}

function handleFailedPageChange(p: number) {
  failedQuery.page = p
  loadFailed()
}

function handleFailedSizeChange(s: number) {
  failedQuery.size = s
  failedQuery.page = 1
  loadFailed()
}

function handleTabChange(name: string | number) {
  if (name === 'failed' && failedData.value.length === 0) {
    loadFailed()
  }
}

// ============ 表格辅助 ============
function statusTagType(status?: string): 'success' | 'danger' | 'info' {
  if (status === SCHEDULE_STATUS.SUCCESS) return 'success'
  if (status === SCHEDULE_STATUS.FAIL) return 'danger'
  return 'info'
}

function statusLabel(status?: string) {
  if (status === SCHEDULE_STATUS.SUCCESS) return '成功'
  if (status === SCHEDULE_STATUS.FAIL) return '失败'
  return status ?? '-'
}

function triggerLabel(t?: string) {
  if (t === TRIGGER_TYPE.AUTO) return '自动'
  if (t === TRIGGER_TYPE.MANUAL) return '手动'
  return t ?? '-'
}

// 失败行高亮
function recentRowClass({ row }: { row: ScheduleLog }): string {
  return row.status === SCHEDULE_STATUS.FAIL ? 'row-failed' : ''
}

function formatDateTime(val?: string) {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

function handleManualRetry(_row: ScheduleLog) {
  // 占位：调度任务的手动重试需后端提供触发接口
  ElMessage.info('手动重试功能待后端提供触发接口')
}

// ============ 初始化 ============
onMounted(() => {
  loadStatistic()
  loadRecent()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title">定时任务监控</div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" v-loading="statLoading">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-total">
          <div class="stat-label">总任务数</div>
          <div class="stat-value">{{ statistic.totalTasks ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-success">
          <div class="stat-label">最近 24h 成功</div>
          <div class="stat-value">{{ statistic.success24h ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-fail">
          <div class="stat-label">最近 24h 失败</div>
          <div class="stat-value">{{ statistic.failed24h ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 最近日志 -->
        <el-tab-pane label="最近日志" name="recent">
          <el-form :inline="true" @submit.prevent>
            <el-form-item label="状态">
              <el-select v-model="filter.status" placeholder="全部" style="width: 160px">
                <el-option
                  v-for="o in statusOptions"
                  :key="o.value"
                  :label="o.label"
                  :value="o.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="filter.dateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
              <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="recentLoading"
            :data="recentData"
            border
            stripe
            :row-class-name="recentRowClass"
          >
            <el-table-column prop="taskName" label="任务名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="taskGroup" label="分组" width="120" show-overflow-tooltip />
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">
                  {{ statusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="触发类型" width="90" align="center">
              <template #default="{ row }">{{ triggerLabel(row.triggerType) }}</template>
            </el-table-column>
            <el-table-column label="开始时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="耗时(ms)" width="100" align="right">
              <template #default="{ row }">{{ row.costMs ?? '-' }}</template>
            </el-table-column>
            <el-table-column
              prop="errorMessage"
              label="异常信息"
              min-width="220"
              show-overflow-tooltip
            />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === SCHEDULE_STATUS.FAIL"
                  link
                  type="primary"
                  @click="handleManualRetry(row)"
                >
                  重试
                </el-button>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无调度日志" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="filter.page"
            :page-size="filter.size"
            :total="recentTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </el-tab-pane>

        <!-- 失败列表 -->
        <el-tab-pane label="失败列表" name="failed">
          <el-table v-loading="failedLoading" :data="failedData" border stripe>
            <el-table-column prop="taskName" label="任务名称" min-width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">
                  {{ statusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="触发类型" width="90" align="center">
              <template #default="{ row }">{{ triggerLabel(row.triggerType) }}</template>
            </el-table-column>
            <el-table-column label="开始时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="耗时(ms)" width="100" align="right">
              <template #default="{ row }">{{ row.costMs ?? '-' }}</template>
            </el-table-column>
            <el-table-column
              prop="errorMessage"
              label="异常信息"
              min-width="240"
              show-overflow-tooltip
            />
            <template #empty>
              <el-empty description="暂无失败任务" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="failedQuery.page"
            :page-size="failedQuery.size"
            :total="failedTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleFailedPageChange"
            @size-change="handleFailedSizeChange"
          />
        </el-tab-pane>
      </el-tabs>
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
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.stat-card {
  margin-bottom: 16px;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: 600;
}
.stat-total .stat-value {
  color: #409eff;
}
.stat-success .stat-value {
  color: #67c23a;
}
.stat-fail .stat-value {
  color: #f56c6c;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.text-muted {
  color: #c0c4cc;
}
</style>

<style>
/* 失败行高亮（非 scoped 以命中 el-table 行） */
.el-table .row-failed {
  background-color: #fef0f0 !important;
}
.el-table .row-failed:hover > td {
  background-color: #fde2e2 !important;
}
</style>
