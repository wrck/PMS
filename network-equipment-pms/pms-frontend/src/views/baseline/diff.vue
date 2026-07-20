<script setup lang="ts">
// =============================================================================
// BaselineDiff - 基线偏差分析页（甘特图叠加对比）
// -----------------------------------------------------------------------------
// - PageHeader: 标题"基线偏差分析 · {基线名}" + 返回 + 操作（导出报告 / 触发变更审批）
// - 偏差概览卡片: 总任务数 / 偏差任务数 / 偏差率 / 双阈值统计 / 最大延期 / 平均延期
// - 偏差甘特图（核心）: 横向基线条（灰色） + 实际条（彩色），超阈值高亮红框，hover 显示详情
// - 偏差明细表: 集成 BaselineDiffTable，超阈值行红色背景，行展开显示原因/影响
// - 偏差分布图: 按偏差天数区间分布柱状图（简化 div 实现）
// - 操作: 触发变更审批（批量选择 + 变更原因 + API）/ 导出报告（按钮占位）
// - 加载中: SkeletonCard；空状态: EmptyState
// =============================================================================
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  diffBaseline,
  requestBaselineChange,
  type BaselineDiffResult,
  type TaskDiff
} from '@/api/baseline'
import type { EpTagType } from '@/types'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import BaselineDiffTable from '@/components/BaselineDiffTable.vue'

defineOptions({ name: 'BaselineDiff' })

const route = useRoute()
const router = useRouter()

const baselineId = computed(() => Number(route.params.baselineId))

// ============ 状态 ============
const loading = ref(false)
const result = ref<BaselineDiffResult | null>(null)

// ============ 双阈值配置 ============
const THRESHOLD_DAYS = 7 // 天数阈值
const THRESHOLD_PERCENT = 10 // 百分比阈值（%）

// ============ 选中行（用于触发变更审批） ============
const selectedTaskIds = ref<number[]>([])

// ============ 触发审批对话框 ============
const approvalVisible = ref(false)
const approvalForm = reactive({
  changeReason: '',
  taskIds: [] as number[]
})

// ============ 计算属性 ============
const baseline = computed(() => result.value?.baseline ?? null)
const diffs = computed<TaskDiff[]>(() => result.value?.diffs ?? [])
const hasDiff = computed(() => diffs.value.length > 0)

const headerTitle = computed(() => `基线偏差分析 · ${baseline.value?.baselineName ?? ''}`)

// ============ 偏差统计 ============
interface DiffStats {
  totalTasks: number
  variancedTasks: number
  varianceRate: number
  daysOverThreshold: number
  percentOverThreshold: number
  bothOverThreshold: number
  maxVariance: number
  avgVariance: number
}

const diffStats = computed<DiffStats>(() => {
  const all = diffs.value
  const total = all.length
  if (total === 0) {
    return {
      totalTasks: 0,
      variancedTasks: 0,
      varianceRate: 0,
      daysOverThreshold: 0,
      percentOverThreshold: 0,
      bothOverThreshold: 0,
      maxVariance: 0,
      avgVariance: 0
    }
  }
  let varianced = 0
  let daysOver = 0
  let percentOver = 0
  let bothOver = 0
  let maxVar = 0
  let sumVar = 0
  for (const d of all) {
    const endVar = Math.abs(d.endVariance ?? 0)
    const startVar = Math.abs(d.startVariance ?? 0)
    const maxAbsVar = Math.max(endVar, startVar)
    const pctVar = Math.abs(d.percentVariance ?? 0)
    const isVarianced = endVar > 0 || startVar > 0
    if (isVarianced) varianced++
    const daysOverFlag = maxAbsVar > THRESHOLD_DAYS
    const percentOverFlag = pctVar > THRESHOLD_PERCENT
    if (daysOverFlag) daysOver++
    if (percentOverFlag) percentOver++
    if (daysOverFlag && percentOverFlag) bothOver++
    if (maxAbsVar > maxVar) maxVar = maxAbsVar
    sumVar += maxAbsVar
  }
  return {
    totalTasks: total,
    variancedTasks: varianced,
    varianceRate: total > 0 ? Math.round((varianced / total) * 1000) / 10 : 0,
    daysOverThreshold: daysOver,
    percentOverThreshold: percentOver,
    bothOverThreshold: bothOver,
    maxVariance: maxVar,
    avgVariance: total > 0 ? Math.round((sumVar / total) * 10) / 10 : 0
  }
})

// ============ 甘特图数据计算 ============
interface GanttRow {
  task: TaskDiff
  baselineStart: number
  baselineEnd: number
  actualStart: number
  actualEnd: number
  isOverThreshold: boolean
}

interface GanttScale {
  minTime: number
  maxTime: number
  totalSpan: number
  ticks: { time: number; label: string }[]
}

const ganttScale = computed<GanttScale | null>(() => {
  const all = diffs.value
  if (all.length === 0) return null
  let minTime = Number.POSITIVE_INFINITY
  let maxTime = Number.NEGATIVE_INFINITY
  for (const d of all) {
    const times = [
      d.baselineStart,
      d.baselineEnd,
      d.currentStart,
      d.currentEnd
    ].filter((t): t is string => !!t)
    for (const t of times) {
      const ts = new Date(t).getTime()
      if (!Number.isNaN(ts)) {
        if (ts < minTime) minTime = ts
        if (ts > maxTime) maxTime = ts
      }
    }
  }
  if (!Number.isFinite(minTime) || !Number.isFinite(maxTime)) return null
  // 扩展两端 5% 留白
  const span = Math.max(86400000, maxTime - minTime)
  const padding = span * 0.05
  minTime -= padding
  maxTime += padding
  const totalSpan = maxTime - minTime
  // 生成 6 个时间刻度
  const tickCount = 6
  const ticks: { time: number; label: string }[] = []
  for (let i = 0; i <= tickCount; i++) {
    const ts = minTime + (totalSpan * i) / tickCount
    const d = new Date(ts)
    const label = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(
      d.getDate()
    ).padStart(2, '0')}`
    ticks.push({ time: ts, label })
  }
  return { minTime, maxTime, totalSpan, ticks }
})

const ganttRows = computed<GanttRow[]>(() => {
  const scale = ganttScale.value
  if (!scale) return []
  return diffs.value
    .map((task) => {
      const bs = task.baselineStart ? new Date(task.baselineStart).getTime() : null
      const be = task.baselineEnd ? new Date(task.baselineEnd).getTime() : null
      const as = task.currentStart ? new Date(task.currentStart).getTime() : null
      const ae = task.currentEnd ? new Date(task.currentEnd).getTime() : null
      const endVar = Math.abs(task.endVariance ?? 0)
      const startVar = Math.abs(task.startVariance ?? 0)
      const pctVar = Math.abs(task.percentVariance ?? 0)
      const isOver =
        Math.max(endVar, startVar) > THRESHOLD_DAYS || pctVar > THRESHOLD_PERCENT
      return {
        task,
        baselineStart: bs ?? scale.minTime,
        baselineEnd: be ?? bs ?? scale.minTime,
        actualStart: as ?? bs ?? scale.minTime,
        actualEnd: ae ?? be ?? bs ?? scale.minTime,
        isOverThreshold: isOver
      }
    })
    .filter((r) => r.baselineEnd >= r.baselineStart)
})

function percentPos(time: number): number {
  const scale = ganttScale.value
  if (!scale || scale.totalSpan === 0) return 0
  return Math.max(0, Math.min(100, ((time - scale.minTime) / scale.totalSpan) * 100))
}

function barStyle(start: number, end: number): Record<string, string> {
  const left = percentPos(start)
  const right = percentPos(end)
  const width = Math.max(0.5, right - left)
  return {
    left: `${left}%`,
    width: `${width}%`
  }
}

// ============ 偏差分布柱状图 ============
interface DistributionBin {
  label: string
  count: number
}

const distributionBins = computed<DistributionBin[]>(() => {
  const all = diffs.value
  const bins = [
    { label: '0 天', min: 0, max: 0, count: 0 },
    { label: '1-3 天', min: 1, max: 3, count: 0 },
    { label: '4-7 天', min: 4, max: 7, count: 0 },
    { label: '8-15 天', min: 8, max: 15, count: 0 },
    { label: '>15 天', min: 16, max: Number.POSITIVE_INFINITY, count: 0 }
  ]
  for (const d of all) {
    const v = Math.max(Math.abs(d.endVariance ?? 0), Math.abs(d.startVariance ?? 0))
    for (const b of bins) {
      if (v >= b.min && v <= b.max) {
        b.count++
        break
      }
    }
  }
  return bins.map((b) => ({ label: b.label, count: b.count }))
})

const maxDistributionCount = computed(() => {
  return Math.max(1, ...distributionBins.value.map((b) => b.count))
})

// ============ 表格行样式 ============
function rowClassName({ row }: { row: TaskDiff }): string {
  const endVar = Math.abs(row.endVariance ?? 0)
  const startVar = Math.abs(row.startVariance ?? 0)
  const pctVar = Math.abs(row.percentVariance ?? 0)
  if (
    Math.max(endVar, startVar) > THRESHOLD_DAYS ||
    pctVar > THRESHOLD_PERCENT
  ) {
    return 'row-over-threshold'
  }
  if (endVar > 0 || startVar > 0) return 'row-varianced'
  return ''
}

// ============ 数据加载 ============
async function loadDiff() {
  if (!baselineId.value || Number.isNaN(baselineId.value)) {
    ElMessage.error('基线 ID 无效')
    return
  }
  loading.value = true
  try {
    result.value = await diffBaseline(baselineId.value)
  } catch {
    result.value = null
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

function backToBaselineList() {
  router.push({ name: 'BaselineList' })
}

// ============ 触发变更审批 ============
function openApprovalDialog() {
  if (selectedTaskIds.value.length === 0) {
    ElMessage.warning('请先在下方明细表勾选超阈值的任务')
    return
  }
  approvalForm.taskIds = [...selectedTaskIds.value]
  approvalForm.changeReason = ''
  approvalVisible.value = true
}

function handleSelectionChange(rows: TaskDiff[]) {
  selectedTaskIds.value = rows
    .filter((r) => {
      const endVar = Math.abs(r.endVariance ?? 0)
      const startVar = Math.abs(r.startVariance ?? 0)
      const pctVar = Math.abs(r.percentVariance ?? 0)
      return (
        Math.max(endVar, startVar) > THRESHOLD_DAYS || pctVar > THRESHOLD_PERCENT
      )
    })
    .map((r) => r.taskId)
}

async function handleSubmitApproval() {
  if (!approvalForm.changeReason.trim()) {
    ElMessage.warning('请填写变更原因')
    return
  }
  try {
    const res = await requestBaselineChange(
      baselineId.value,
      approvalForm.changeReason.trim()
    )
    if (res.needsApproval) {
      ElMessage.warning(
        `偏差超阈值，已触发审批。原因：${res.approvalReason ?? ''}`
      )
    } else {
      ElMessage.success('偏差未超阈值，基线已直接批准')
    }
    approvalVisible.value = false
    await loadDiff()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 导出报告（CSV 简化实现） ============
function handleExportReport() {
  if (!result.value || diffs.value.length === 0) {
    ElMessage.warning('当前没有偏差数据可导出')
    return
  }
  const headers = [
    '任务ID',
    '任务名称',
    '基线开始',
    '基线结束',
    '当前开始',
    '当前结束',
    '开始偏差(天)',
    '结束偏差(天)',
    '偏差百分比(%)'
  ]
  const rows = diffs.value.map((d) => [
    d.taskId,
    `"${(d.taskName ?? '').replace(/"/g, '""')}"`,
    d.baselineStart ?? '',
    d.baselineEnd ?? '',
    d.currentStart ?? '',
    d.currentEnd ?? '',
    d.startVariance ?? 0,
    d.endVariance ?? 0,
    d.percentVariance ?? 0
  ])
  const csv = [headers.join(','), ...rows.map((r) => r.join(','))].join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `baseline-diff-${baselineId.value}-${Date.now()}.csv`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('偏差报告已导出 (CSV)')
}

// ============ 偏差天数/百分比文本 ============
function varianceText(v?: number): string {
  if (v == null) return '-'
  if (v === 0) return '准时'
  return v > 0 ? `延迟 ${v} 天` : `提前 ${-v} 天`
}

function varianceType(v?: number): EpTagType {
  if (v == null) return 'info'
  if (v > 0) return 'danger'
  if (v < 0) return 'success'
  return 'info'
}

function percentText(v?: number): string {
  if (v == null) return '-'
  return `${v.toFixed(2)}%`
}

// ============ 行展开内容 ============
function expandContent(row: TaskDiff): string {
  const endVar = row.endVariance ?? 0
  const startVar = row.startVariance ?? 0
  const pctVar = row.percentVariance ?? 0
  const reasons: string[] = []
  if (endVar > 0) reasons.push(`结束延迟 ${endVar} 天`)
  if (endVar < 0) reasons.push(`结束提前 ${-endVar} 天`)
  if (startVar > 0) reasons.push(`开始延迟 ${startVar} 天`)
  if (startVar < 0) reasons.push(`开始提前 ${-startVar} 天`)
  const reasonText = reasons.length > 0 ? reasons.join('；') : '无偏差'
  const impactText =
    Math.abs(endVar) > THRESHOLD_DAYS || Math.abs(pctVar) > THRESHOLD_PERCENT
      ? `偏差超阈值（天数>${THRESHOLD_DAYS} 或 百分比>${THRESHOLD_PERCENT}%），需触发变更审批`
      : '偏差在阈值范围内，无需审批'
  return `偏差原因：${reasonText} | 影响分析：${impactText}`
}

onMounted(loadDiff)
</script>

<template>
  <div class="baseline-diff-page">
    <PageHeader
      :title="headerTitle"
      :description="`双阈值：天数 > ${THRESHOLD_DAYS} 天 / 百分比 > ${THRESHOLD_PERCENT}%`"
    >
      <template #actions>
        <el-button @click="goBack">返回</el-button>
        <el-button @click="backToBaselineList">基线列表</el-button>
        <el-button :icon="'Refresh'" @click="loadDiff">刷新</el-button>
        <el-button type="success" @click="handleExportReport">导出报告</el-button>
        <el-button
          type="warning"
          :disabled="!hasDiff"
          @click="openApprovalDialog"
        >
          触发变更审批
        </el-button>
      </template>
    </PageHeader>

    <!-- 加载骨架屏 -->
    <SkeletonCard v-if="loading" :loading="true" :rows="8">
      <div />
    </SkeletonCard>

    <template v-else>
      <!-- 空状态：基线不存在 -->
      <EmptyState
        v-if="!result"
        title="基线不存在"
        description="基线不存在或加载失败，请返回列表重试"
        icon="Warning"
      >
        <template #action>
          <el-button type="primary" @click="backToBaselineList">返回基线列表</el-button>
        </template>
      </EmptyState>

      <!-- 空状态：无偏差 -->
      <EmptyState
        v-else-if="!hasDiff"
        title="基线与实际完全一致"
        description="当前基线快照与项目计划之间没有偏差数据"
        icon="CircleCheck"
      />

      <template v-else>
        <!-- 偏差概览卡片 -->
        <div class="overview-cards">
          <div class="overview-card overview-total">
            <div class="ov-label">总任务数</div>
            <div class="ov-value">{{ diffStats.totalTasks }}</div>
          </div>
          <div class="overview-card overview-varianced">
            <div class="ov-label">偏差任务数</div>
            <div class="ov-value">{{ diffStats.variancedTasks }}</div>
            <div class="ov-extra">偏差率 {{ diffStats.varianceRate }}%</div>
          </div>
          <div class="overview-card overview-days">
            <div class="ov-label">天数超阈值数</div>
            <div class="ov-value">{{ diffStats.daysOverThreshold }}</div>
            <div class="ov-extra">> {{ THRESHOLD_DAYS }} 天</div>
          </div>
          <div class="overview-card overview-percent">
            <div class="ov-label">百分比超阈值数</div>
            <div class="ov-value">{{ diffStats.percentOverThreshold }}</div>
            <div class="ov-extra">> {{ THRESHOLD_PERCENT }}%</div>
          </div>
          <div class="overview-card overview-both">
            <div class="ov-label">双阈值均超数</div>
            <div class="ov-value">{{ diffStats.bothOverThreshold }}</div>
            <div class="ov-extra">需触发审批</div>
          </div>
          <div class="overview-card overview-max">
            <div class="ov-label">最大延期天数</div>
            <div class="ov-value">{{ diffStats.maxVariance }}</div>
            <div class="ov-extra">天</div>
          </div>
          <div class="overview-card overview-avg">
            <div class="ov-label">平均延期天数</div>
            <div class="ov-value">{{ diffStats.avgVariance }}</div>
            <div class="ov-extra">天</div>
          </div>
          <div
            class="overview-card overview-approval"
            :class="{ 'needs-approval': result.needsApproval }"
          >
            <div class="ov-label">是否需审批</div>
            <div class="ov-value">{{ result.needsApproval ? '是' : '否' }}</div>
            <div class="ov-extra">
              {{ result.needsApproval ? '已超阈值' : '未超阈值' }}
            </div>
          </div>
        </div>

        <!-- 偏差甘特图（叠加对比） -->
        <el-card shadow="never" class="gantt-card">
          <template #header>
            <div class="card-header">
              <span>基线 vs 实际 甘特图叠加对比</span>
              <div class="legend">
                <span class="legend-item">
                  <span class="legend-bar legend-baseline" />基线计划
                </span>
                <span class="legend-item">
                  <span class="legend-bar legend-actual" />实际计划
                </span>
                <span class="legend-item">
                  <span class="legend-bar legend-over" />超阈值
                </span>
              </div>
            </div>
          </template>

          <div class="gantt-container">
            <!-- 表头：任务名 + 时间轴 -->
            <div class="gantt-header">
              <div class="gantt-task-col">任务名称</div>
              <div class="gantt-time-col">
                <div
                  v-for="(tick, idx) in ganttScale?.ticks ?? []"
                  :key="idx"
                  class="gantt-tick"
                  :style="{ left: `${(idx / (ganttScale?.ticks.length ?? 1 - 1)) * 100}%` }"
                >
                  {{ tick.label }}
                </div>
              </div>
            </div>

            <!-- 甘特图行 -->
            <div
              v-for="row in ganttRows"
              :key="row.task.taskId"
              class="gantt-row"
              :class="{ 'row-over': row.isOverThreshold }"
            >
              <div class="gantt-task-col" :title="row.task.taskName">
                {{ row.task.taskName }}
              </div>
              <div class="gantt-time-col">
                <!-- 网格线 -->
                <div class="gantt-grid">
                  <div
                    v-for="(tick, idx) in (ganttScale?.ticks ?? []).slice(1, -1)"
                    :key="idx"
                    class="grid-line"
                    :style="{ left: `${((idx + 1) / ((ganttScale?.ticks.length ?? 1) - 1)) * 100}%` }"
                  />
                </div>
                <!-- 基线条（灰色背景） -->
                <div
                  class="bar bar-baseline"
                  :style="barStyle(row.baselineStart, row.baselineEnd)"
                >
                  <span class="bar-label">基线</span>
                </div>
                <!-- 实际条（彩色覆盖） -->
                <div
                  class="bar bar-actual"
                  :class="{ 'bar-over': row.isOverThreshold }"
                  :style="barStyle(row.actualStart, row.actualEnd)"
                >
                  <el-tooltip
                    :content="`${row.task.taskName} | 基线: ${row.task.baselineStart ?? '-'} ~ ${row.task.baselineEnd ?? '-'} | 实际: ${row.task.currentStart ?? '-'} ~ ${row.task.currentEnd ?? '-'} | 偏差: ${varianceText(row.task.endVariance)} / ${percentText(row.task.percentVariance)}`"
                    placement="top"
                  >
                    <span class="bar-label">实际</span>
                  </el-tooltip>
                </div>
              </div>
            </div>

            <!-- 底部时间轴 -->
            <div class="gantt-footer">
              <div class="gantt-task-col" />
              <div class="gantt-time-col">
                <div
                  v-for="(tick, idx) in ganttScale?.ticks ?? []"
                  :key="idx"
                  class="gantt-tick-label"
                  :style="{ left: `${(idx / ((ganttScale?.ticks.length ?? 1) - 1)) * 100}%` }"
                >
                  {{ tick.label }}
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 偏差分布柱状图 -->
        <el-card shadow="never" class="distribution-card">
          <template #header>
            <span>偏差分布（按天数区间）</span>
          </template>
          <div class="distribution-chart">
            <div
              v-for="bin in distributionBins"
              :key="bin.label"
              class="dist-bar-wrapper"
            >
              <div class="dist-bar-container">
                <div
                  class="dist-bar"
                  :style="{ height: `${(bin.count / maxDistributionCount) * 100}%` }"
                >
                  <span class="dist-count">{{ bin.count }}</span>
                </div>
              </div>
              <div class="dist-label">{{ bin.label }}</div>
            </div>
          </div>
        </el-card>

        <!-- 偏差明细表（集成 BaselineDiffTable） -->
        <el-card shadow="never" class="diff-table-card">
          <template #header>
            <div class="card-header">
              <span>偏差明细</span>
              <span class="header-summary">
                共 {{ diffs.length }} 个任务，已选 {{ selectedTaskIds.length }} 个超阈值任务
              </span>
            </div>
          </template>

          <el-table
            :data="diffs"
            border
            stripe
            :row-class-name="rowClassName"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column type="expand">
              <template #default="{ row }">
                <div class="expand-content">{{ expandContent(row) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="任务名称" min-width="180" prop="taskName" show-overflow-tooltip />
            <el-table-column label="基线开始" width="120" align="center">
              <template #default="{ row }">{{ row.baselineStart || '-' }}</template>
            </el-table-column>
            <el-table-column label="基线结束" width="120" align="center">
              <template #default="{ row }">{{ row.baselineEnd || '-' }}</template>
            </el-table-column>
            <el-table-column label="实际开始" width="120" align="center">
              <template #default="{ row }">{{ row.currentStart || '-' }}</template>
            </el-table-column>
            <el-table-column label="实际结束" width="120" align="center">
              <template #default="{ row }">{{ row.currentEnd || '-' }}</template>
            </el-table-column>
            <el-table-column label="偏差天数" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="varianceType(row.endVariance)" size="small" effect="dark">
                  {{ varianceText(row.endVariance) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="偏差百分比" width="120" align="center">
              <template #default="{ row }">
                <span :class="{ 'percent-warn': (row.percentVariance ?? 0) > THRESHOLD_PERCENT }">
                  {{ percentText(row.percentVariance) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="是否超阈值" width="120" align="center">
              <template #default="{ row }">
                <el-tag
                  v-if="
                    Math.abs(row.endVariance ?? 0) > THRESHOLD_DAYS ||
                    Math.abs(row.startVariance ?? 0) > THRESHOLD_DAYS ||
                    Math.abs(row.percentVariance ?? 0) > THRESHOLD_PERCENT
                  "
                  type="danger"
                  size="small"
                  effect="dark"
                >
                  超阈值
                </el-tag>
                <el-tag v-else type="success" size="small" effect="plain">正常</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="是否触发审批" width="120" align="center">
              <template #default="{ row }">
                <el-tag
                  v-if="
                    Math.abs(row.endVariance ?? 0) > THRESHOLD_DAYS ||
                    Math.abs(row.startVariance ?? 0) > THRESHOLD_DAYS ||
                    Math.abs(row.percentVariance ?? 0) > THRESHOLD_PERCENT
                  "
                  type="warning"
                  size="small"
                  effect="dark"
                >
                  需审批
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 兼容旧组件 BaselineDiffTable 作为对照视图 -->
        <el-card shadow="never" class="legacy-diff-card">
          <template #header>
            <span>偏差明细（精简视图）</span>
          </template>
          <BaselineDiffTable
            :diffs="diffs"
            :total-varianced="result.totalVarianced"
            :needs-approval="result.needsApproval"
            :approval-reason="result.approvalReason"
          />
        </el-card>
      </template>
    </template>

    <!-- 触发变更审批对话框 -->
    <el-dialog v-model="approvalVisible" title="触发变更审批" width="520px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="基线ID">
          <span>{{ baselineId }}</span>
        </el-form-item>
        <el-form-item label="选中任务">
          <span>{{ approvalForm.taskIds.length }} 个超阈值任务</span>
        </el-form-item>
        <el-form-item label="变更原因" required>
          <el-input
            v-model="approvalForm.changeReason"
            type="textarea"
            :rows="4"
            placeholder="说明本次计划变更的原因与影响"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApproval">提交审批</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.baseline-diff-page {
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 偏差概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}
.overview-card {
  background: var(--pms-color-bg-card, #fff);
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
  border-radius: var(--pms-radius-md, 6px);
  padding: 12px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.overview-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
}
.overview-total::before {
  background: #3b82f6;
}
.overview-varianced::before {
  background: #f59e0b;
}
.overview-days::before {
  background: #ef4444;
}
.overview-percent::before {
  background: #8b5cf6;
}
.overview-both::before {
  background: #dc2626;
}
.overview-max::before {
  background: #f97316;
}
.overview-avg::before {
  background: #06b6d4;
}
.overview-approval::before {
  background: #6b7280;
}
.overview-approval.needs-approval::before {
  background: #ef4444;
}
.overview-approval.needs-approval {
  background: #fef2f2;
  border-color: #fecaca;
}
.ov-label {
  font-size: 11px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 4px;
}
.ov-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2937);
  line-height: 1.2;
}
.ov-extra {
  font-size: 10px;
  color: var(--pms-color-text-placeholder, #9ca3af);
  margin-top: 4px;
}

/* 甘特图卡片 */
.gantt-card {
  margin-bottom: 0;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2937);
}
.legend {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  font-weight: normal;
}
.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.legend-bar {
  display: inline-block;
  width: 18px;
  height: 8px;
  border-radius: 2px;
}
.legend-baseline {
  background: #d1d5db;
}
.legend-actual {
  background: #3b82f6;
}
.legend-over {
  background: #ef4444;
  border: 1px solid #b91c1c;
}

/* 甘特图主体 */
.gantt-container {
  width: 100%;
  font-size: 12px;
}
.gantt-header,
.gantt-row,
.gantt-footer {
  display: flex;
  align-items: stretch;
  border-bottom: 1px solid var(--pms-color-border-light, #f0f0f0);
}
.gantt-task-col {
  width: 180px;
  min-width: 180px;
  padding: 8px 12px;
  border-right: 1px solid var(--pms-color-border-light, #f0f0f0);
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--pms-color-text-primary, #1f2937);
}
.gantt-time-col {
  flex: 1;
  position: relative;
  min-height: 32px;
  padding: 8px 0;
}
.gantt-header .gantt-time-col {
  height: 32px;
  padding: 8px 0;
  background: var(--pms-color-bg-page, #fafafa);
}
.gantt-tick {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px;
  color: var(--pms-color-text-secondary, #6b7280);
  white-space: nowrap;
}
.gantt-tick-label {
  position: absolute;
  top: 4px;
  transform: translateX(-50%);
  font-size: 11px;
  color: var(--pms-color-text-secondary, #6b7280);
  white-space: nowrap;
}
.gantt-row.row-over {
  background: #fef2f2;
}
.gantt-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}
.grid-line {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 1px;
  background: var(--pms-color-border-light, #f0f0f0);
}
.bar {
  position: absolute;
  height: 16px;
  top: 50%;
  transform: translateY(-50%);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #fff;
  overflow: hidden;
}
.bar-baseline {
  background: #d1d5db;
  color: #4b5563;
  z-index: 1;
  height: 20px;
  opacity: 0.7;
}
.bar-actual {
  background: #3b82f6;
  z-index: 2;
  border: 1px solid #2563eb;
}
.bar-actual.bar-over {
  background: #ef4444;
  border: 2px solid #b91c1c;
  z-index: 3;
  height: 22px;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
}
.bar-label {
  padding: 0 4px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* 偏差分布柱状图 */
.distribution-chart {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  height: 200px;
  padding: 16px 8px;
}
.dist-bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.dist-bar-container {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.dist-bar {
  width: 60%;
  min-height: 4px;
  background: linear-gradient(180deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 4px 4px 0 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
  transition: all 0.2s;
}
.dist-bar:hover {
  filter: brightness(1.1);
  transform: scale(1.02);
}
.dist-count {
  color: #fff;
  font-size: 11px;
  font-weight: 600;
}
.dist-label {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
}

/* 偏差明细表 */
.header-summary {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  font-weight: normal;
}
.expand-content {
  padding: 12px 24px;
  background: var(--pms-color-bg-page, #f9fafb);
  color: var(--pms-color-text-regular, #4b5563);
  font-size: 13px;
}
.percent-warn {
  color: #ef4444;
  font-weight: 600;
}

:deep(.row-over-threshold) {
  background-color: #fef2f2 !important;
}
:deep(.row-over-threshold td) {
  background-color: #fef2f2 !important;
}
:deep(.row-varianced) {
  background-color: #fffbeb !important;
}
:deep(.row-varianced td) {
  background-color: #fffbeb !important;
}

/* 兼容旧组件 */
.legacy-diff-card {
  margin-bottom: 0;
}
</style>
