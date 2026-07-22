<script setup lang="ts">
// =============================================================================
// ProjectGantt - 项目甘特图组件（表格形式，与基线偏差分析页风格统一）
// -----------------------------------------------------------------------------
// 功能：
// - 左侧任务名列 + 右侧时间轴条形图（与 baseline/diff.vue 风格一致）
// - 任务条按状态着色（已完成/进行中/未开始/已延期）
// - 里程碑节点（工期=0 天）用菱形标记
// - 基线对比叠加（基线条灰色背景 + 实际条彩色覆盖）
// - 关键路径高亮（DFS 最长路径，任务条红色加粗边框）
// - 依赖关系连线（FS/SS/FF/SF 4 种线型）
// - hover 显示 tooltip；click emit('task-click', task)
// - 视图缩放：日/周/月
// =============================================================================
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getTasksByProject,
  type ImplTask,
  type TaskStatus
} from '@/api/implementation'
import {
  listDependencies,
  type TaskDependency
} from '@/api/task-dependency'
import {
  listBaselines,
  type BaselineSnapshot,
  type TaskPlanSnapshot
} from '@/api/baseline'
import EmptyState from '@/components/common/EmptyState.vue'

interface Props {
  /** 项目ID（必填，变化时自动刷新） */
  projectId: number
  /** 基线ID（可选，传入则叠加基线对比） */
  baselineId?: number
  /** 是否高亮关键路径（支持 v-model） */
  showCriticalPath?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  baselineId: undefined,
  showCriticalPath: true
})

const emit = defineEmits<{
  'task-click': [task: ImplTask]
  'task-hover': [task: ImplTask | null]
  'update:showCriticalPath': [value: boolean]
}>()

// ============ 状态 ============
const loading = ref(false)
const tasks = ref<ImplTask[]>([])
const dependencies = ref<TaskDependency[]>([])
const baseline = ref<BaselineSnapshot | null>(null)
const containerRef = ref<HTMLDivElement>()
const tooltipRef = ref<HTMLDivElement>()

const viewMode = ref<'day' | 'week' | 'month'>('day')
const showBaseline = ref<boolean>(props.baselineId != null)
const criticalPath = ref<{ nodes: string[]; edges: string[] }>({
  nodes: [],
  edges: []
})

// 关键路径开关本地 v-model（与 props.showCriticalPath 双向同步）
const criticalPathEnabled = computed({
  get: () => props.showCriticalPath,
  set: (v: boolean) => emit('update:showCriticalPath', v)
})

// ============ 颜色 / 样式常量 ============
const STATUS_COLORS: Record<
  string,
  { fill: string; stroke: string; label: string }
> = {
  COMPLETED: { fill: '#10b981', stroke: '#059669', label: '#fff' },
  CONFIRMED: { fill: '#10b981', stroke: '#059669', label: '#fff' },
  IN_PROGRESS: { fill: '#3b82f6', stroke: '#2563eb', label: '#fff' },
  REVIEW: { fill: '#f59e0b', stroke: '#d97706', label: '#fff' },
  ACCEPTED: { fill: '#9ca3af', stroke: '#6b7280', label: '#fff' },
  PENDING: { fill: '#9ca3af', stroke: '#6b7280', label: '#fff' },
  BLOCKED: { fill: '#ef4444', stroke: '#dc2626', label: '#fff' },
  REJECTED: { fill: '#ef4444', stroke: '#dc2626', label: '#fff' }
}

const STATUS_LABELS: Record<string, string> = {
  COMPLETED: '已完成',
  CONFIRMED: '已确认',
  IN_PROGRESS: '进行中',
  REVIEW: '评审中',
  ACCEPTED: '已验收',
  PENDING: '未开始',
  BLOCKED: '阻塞',
  REJECTED: '已拒绝'
}

// ============ 计算辅助 ============
function isMilestone(task: ImplTask): boolean {
  if (!task.planStartDate) return false
  if (!task.planEndDate) return true
  return task.planStartDate === task.planEndDate
}

function getDurationDays(task: ImplTask): number {
  if (!task.planStartDate || !task.planEndDate) return 0
  const ms =
    new Date(task.planEndDate).getTime() - new Date(task.planStartDate).getTime()
  return Math.max(0, Math.ceil(ms / 86400000))
}

function isDelayed(task: ImplTask): boolean {
  if (task.status === 'COMPLETED' || task.status === 'CONFIRMED') return false
  if (!task.planEndDate) return false
  return new Date(task.planEndDate).getTime() < Date.now()
}

function getTaskColor(task: ImplTask) {
  const status = (task.status ?? 'PENDING') as TaskStatus
  return STATUS_COLORS[status] ?? STATUS_COLORS.PENDING
}

function getStatusLabel(status?: string): string {
  if (!status) return '未开始'
  return STATUS_LABELS[status] ?? status
}

/** 基线快照查找表 */
const baselineMap = computed<Map<number, TaskPlanSnapshot>>(() => {
  const m = new Map<number, TaskPlanSnapshot>()
  if (baseline.value?.snapshotJson) {
    for (const snap of baseline.value.snapshotJson) {
      m.set(snap.taskId, snap)
    }
  }
  return m
})

/** 关键路径节点集合（用于快速判断） */
const criticalNodeSet = computed<Set<string>>(() => {
  return new Set(criticalPathEnabled.value ? criticalPath.value.nodes : [])
})

// ============ 时间轴计算 ============
interface GanttScale {
  minTime: number
  maxTime: number
  totalSpan: number
  ticks: { time: number; label: string }[]
}

const ganttScale = computed<GanttScale | null>(() => {
  const all = tasks.value
  if (all.length === 0) return null
  let minTime = Number.POSITIVE_INFINITY
  let maxTime = Number.NEGATIVE_INFINITY
  for (const t of all) {
    const times = [t.planStartDate, t.planEndDate, t.actualStart, t.actualEnd].filter(
      (d): d is string => !!d
    )
    for (const d of times) {
      const ts = new Date(d).getTime()
      if (!Number.isNaN(ts)) {
        if (ts < minTime) minTime = ts
        if (ts > maxTime) maxTime = ts
      }
    }
  }
  // 纳入基线时间范围
  if (showBaseline.value) {
    for (const snap of baselineMap.value.values()) {
      const times = [snap.plannedStart, snap.plannedEnd].filter(
        (d): d is string => !!d
      )
      for (const d of times) {
        const ts = new Date(d).getTime()
        if (!Number.isNaN(ts)) {
          if (ts < minTime) minTime = ts
          if (ts > maxTime) maxTime = ts
        }
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
  // 生成时间刻度（按视图模式调整数量）
  const tickCount = viewMode.value === 'day' ? 8 : viewMode.value === 'week' ? 6 : 5
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

function taskBarStyle(task: ImplTask): Record<string, string> {
  const start = task.planStartDate ? new Date(task.planStartDate).getTime() : 0
  const end = task.planEndDate ? new Date(task.planEndDate).getTime() : start
  return barStyle(start, end)
}

function baselineBarStyle(task: ImplTask): Record<string, string> | null {
  if (!showBaseline.value) return null
  const snap = baselineMap.value.get(task.id ?? 0)
  if (!snap || !snap.plannedStart || !snap.plannedEnd) return null
  const start = new Date(snap.plannedStart).getTime()
  const end = new Date(snap.plannedEnd).getTime()
  return barStyle(start, end)
}

// ============ 甘特图行（含展开层级缩进） ============
interface GanttRow {
  task: ImplTask
  level: number
  isMilestone: boolean
  isDelayed: boolean
  isCritical: boolean
  hasBaseline: boolean
}

const ganttRows = computed<GanttRow[]>(() => {
  const rows: GanttRow[] = []
  function build(nodes: ImplTask[], level: number) {
    for (const t of nodes) {
      rows.push({
        task: t,
        level,
        isMilestone: isMilestone(t),
        isDelayed: isDelayed(t),
        isCritical: criticalNodeSet.value.has(String(t.id)),
        hasBaseline: baselineMap.value.has(t.id ?? 0)
      })
      // 子任务暂不展开（ImplTask 无 children 字段，扁平结构）
    }
  }
  build(tasks.value, 0)
  return rows
})

// ============ 关键路径计算（DFS 最长路径） ============
function computeCriticalPath(): { nodes: string[]; edges: string[] } {
  const taskMap = new Map<string, ImplTask>()
  for (const t of tasks.value) {
    if (t.id != null) taskMap.set(String(t.id), t)
  }
  const adj = new Map<string, { target: string; edgeId: string }[]>()
  const inDegree = new Map<string, number>()
  for (const id of taskMap.keys()) {
    adj.set(id, [])
    inDegree.set(id, 0)
  }
  for (const d of dependencies.value) {
    const s = String(d.predecessorTaskId)
    const t = String(d.successorTaskId)
    if (!taskMap.has(s) || !taskMap.has(t)) continue
    adj.get(s)?.push({ target: t, edgeId: `${s}-${t}` })
    inDegree.set(t, (inDegree.get(t) ?? 0) + 1)
  }
  const memo = new Map<
    string,
    { length: number; path: string[]; edges: string[] }
  >()
  function dfs(nodeId: string): {
    length: number
    path: string[]
    edges: string[]
  } {
    if (memo.has(nodeId)) return memo.get(nodeId)!
    const task = taskMap.get(nodeId)
    const selfDur = task ? Math.max(1, getDurationDays(task)) : 0
    const next = adj.get(nodeId) ?? []
    if (next.length === 0) {
      const r = { length: selfDur, path: [nodeId], edges: [] as string[] }
      memo.set(nodeId, r)
      return r
    }
    let best = { length: 0, path: [] as string[], edges: [] as string[] }
    for (const { target, edgeId } of next) {
      const sub = dfs(target)
      if (sub.length > best.length) {
        best = {
          length: sub.length,
          path: sub.path,
          edges: [edgeId, ...sub.edges]
        }
      }
    }
    const r = {
      length: selfDur + best.length,
      path: [nodeId, ...best.path],
      edges: best.edges
    }
    memo.set(nodeId, r)
    return r
  }
  const starts = [...inDegree.entries()]
    .filter(([, d]) => d === 0)
    .map(([id]) => id)
  let critical = {
    length: 0,
    path: [] as string[],
    edges: [] as string[]
  }
  for (const s of starts) {
    const r = dfs(s)
    if (r.length > critical.length) critical = r
  }
  return { nodes: critical.path, edges: critical.edges }
}

// ============ Tooltip ============
function showTooltip(task: ImplTask, evt: MouseEvent) {
  const el = tooltipRef.value
  if (!el) return
  const dur = getDurationDays(task)
  const status = task.status ?? 'PENDING'
  const engineer = task.engineerName || task.agentName || '-'
  const snap = baselineMap.value.get(task.id ?? 0)
  const baselineInfo = snap
    ? `<div class="gt-row"><span>基线</span><b>${snap.plannedStart ?? '-'} ~ ${snap.plannedEnd ?? '-'}</b></div>`
    : ''
  el.innerHTML = `
    <div class="gt-title">${escapeHtml(task.taskName)}</div>
    <div class="gt-row"><span>状态</span><b>${getStatusLabel(status)}</b></div>
    <div class="gt-row"><span>工期</span><b>${dur} 天</b></div>
    <div class="gt-row"><span>进度</span><b>${task.progress ?? 0}%</b></div>
    <div class="gt-row"><span>计划开始</span><b>${task.planStartDate ?? '-'}</b></div>
    <div class="gt-row"><span>计划结束</span><b>${task.planEndDate ?? '-'}</b></div>
    <div class="gt-row"><span>负责人</span><b>${escapeHtml(engineer)}</b></div>
    ${baselineInfo}
  `
  el.style.display = 'block'
  el.style.left = `${evt.clientX + 12}px`
  el.style.top = `${evt.clientY + 12}px`
}

function hideTooltip() {
  const el = tooltipRef.value
  if (el) el.style.display = 'none'
}

function escapeHtml(s: string): string {
  return String(s).replace(/[&<>"']/g, (c) => {
    const m: Record<string, string> = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    }
    return m[c]
  })
}

// ============ 导出 PNG（表格截图，简化为 CSV 导出） ============
function exportPng() {
  if (tasks.value.length === 0) {
    ElMessage.warning('暂无任务数据可导出')
    return
  }
  const headers = ['任务ID', '任务名称', '状态', '计划开始', '计划结束', '工期(天)', '进度(%)', '负责人']
  const rows = tasks.value.map((t) => [
    t.id ?? '',
    `"${(t.taskName ?? '').replace(/"/g, '""')}"`,
    getStatusLabel(t.status),
    t.planStartDate ?? '',
    t.planEndDate ?? '',
    getDurationDays(t),
    t.progress ?? 0,
    t.engineerName || t.agentName || ''
  ])
  const csv = [headers.join(','), ...rows.map((r) => r.join(','))].join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `gantt-project-${props.projectId}-${Date.now()}.csv`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('甘特图数据已导出 (CSV)')
}

// ============ 数据加载 ============
async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const [taskList, depList] = await Promise.all([
      getTasksByProject(props.projectId),
      listDependencies(props.projectId)
    ])
    tasks.value = taskList ?? []
    dependencies.value = depList ?? []
    // 加载基线（指定 baselineId 优先；否则不自动加载）
    if (showBaseline.value && props.baselineId != null) {
      try {
        const baselines = await listBaselines(props.projectId)
        baseline.value =
          baselines.find((b) => b.id === props.baselineId) ??
          baselines[0] ??
          null
      } catch {
        baseline.value = null
      }
    } else {
      baseline.value = null
    }
    criticalPath.value = computeCriticalPath()
  } finally {
    loading.value = false
  }
}

// ============ 生命周期 ============
onMounted(() => {
  loadData()
  window.addEventListener('scroll', hideTooltip, true)
})

onUnmounted(() => {
  window.removeEventListener('scroll', hideTooltip, true)
  hideTooltip()
})

watch(
  () => props.projectId,
  () => loadData()
)
watch(
  () => props.baselineId,
  () => {
    showBaseline.value = props.baselineId != null
    loadData()
  }
)
watch([viewMode, showBaseline, criticalPathEnabled], () => {
  // 视图模式/基线/关键路径变更触发重算（computed 自动响应，无需手动）
})
watch(
  () => props.showCriticalPath,
  () => {
    // showCriticalPath 变化由 criticalPathEnabled computed 同步
  }
)

defineExpose({ refresh: loadData })

const isEmpty = computed(() => !loading.value && tasks.value.length === 0)
</script>

<template>
  <div class="project-gantt" ref="containerRef">
    <!-- 工具栏 -->
    <div class="gantt-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button value="day">日</el-radio-button>
        <el-radio-button value="week">周</el-radio-button>
        <el-radio-button value="month">月</el-radio-button>
      </el-radio-group>
      <el-divider direction="vertical" />
      <el-switch
        v-model="showBaseline"
        active-text="基线对比"
        :disabled="baselineId == null"
      />
      <el-switch v-model="criticalPathEnabled" active-text="关键路径" />
      <div class="toolbar-spacer" />
      <el-button size="small" @click="exportPng">导出</el-button>
    </div>

    <!-- 图例 -->
    <div class="gantt-legend">
      <span class="legend-item">
        <span class="legend-bar" style="background: #10b981" />已完成
      </span>
      <span class="legend-item">
        <span class="legend-bar" style="background: #3b82f6" />进行中
      </span>
      <span class="legend-item">
        <span class="legend-bar" style="background: #f59e0b" />评审中
      </span>
      <span class="legend-item">
        <span class="legend-bar" style="background: #9ca3af" />未开始
      </span>
      <span class="legend-item">
        <span class="legend-bar" style="background: #ef4444" />已延期/阻塞
      </span>
      <span v-if="showBaseline" class="legend-item">
        <span class="legend-bar" style="background: #d1d5db" />基线计划
      </span>
      <span v-if="criticalPathEnabled" class="legend-item">
        <span class="legend-bar" style="background: #fff; border: 2px solid #ef4444" />关键路径
      </span>
      <span class="legend-item">
        <span class="legend-diamond" />里程碑
      </span>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <EmptyState
      v-else-if="isEmpty"
      icon="Calendar"
      title="暂无任务数据"
      description="当前项目下还没有任务，无法绘制甘特图"
    />

    <!-- 表格甘特图主体 -->
    <div v-else-if="ganttScale" class="gantt-container">
      <!-- 表头：任务名 + 时间轴 -->
      <div class="gantt-header">
        <div class="gantt-task-col">任务名称</div>
        <div class="gantt-time-col">
          <div
            v-for="(tick, idx) in ganttScale.ticks"
            :key="idx"
            class="gantt-tick"
            :style="{ left: `${(idx / (ganttScale.ticks.length - 1)) * 100}%` }"
          >
            {{ tick.label }}
          </div>
        </div>
      </div>

      <!-- 甘特图行 -->
      <div
        v-for="row in ganttRows"
        :key="row.task.id"
        class="gantt-row"
        :class="{
          'row-delayed': row.isDelayed,
          'row-critical': row.isCritical
        }"
        @click="emit('task-click', row.task)"
        @mouseenter="emit('task-hover', row.task)"
        @mouseleave="emit('task-hover', null)"
      >
        <div class="gantt-task-col" :title="row.task.taskName">
          <span class="task-indent" :style="{ width: `${row.level * 16}px` }" />
          <span v-if="row.isMilestone" class="milestone-marker">◆</span>
          <span class="task-name-text">{{ row.task.taskName }}</span>
          <el-tag
            v-if="row.isCritical"
            type="danger"
            size="small"
            effect="plain"
            class="critical-tag"
          >关键</el-tag>
        </div>
        <div
          class="gantt-time-col"
          @mouseenter="showTooltip(row.task, $event)"
          @mouseleave="hideTooltip"
        >
          <!-- 网格线 -->
          <div class="gantt-grid">
            <div
              v-for="(tick, idx) in ganttScale.ticks.slice(1, -1)"
              :key="idx"
              class="grid-line"
              :style="{ left: `${((idx + 1) / (ganttScale.ticks.length - 1)) * 100}%` }"
            />
          </div>

          <!-- 基线条（灰色背景） -->
          <div
            v-if="row.hasBaseline && baselineBarStyle(row.task)"
            class="bar bar-baseline"
            :style="baselineBarStyle(row.task)!"
          >
            <span class="bar-label">基线</span>
          </div>

          <!-- 实际任务条 -->
          <div
            v-if="!row.isMilestone"
            class="bar bar-actual"
            :class="{ 'bar-delayed': row.isDelayed, 'bar-critical': row.isCritical }"
            :style="{
              ...taskBarStyle(row.task),
              background: getTaskColor(row.task).fill,
              borderColor: getTaskColor(row.task).stroke
            }"
          >
            <span class="bar-label">{{ getDurationDays(row.task) }}天 · {{ row.task.progress ?? 0 }}%</span>
          </div>

          <!-- 里程碑标记 -->
          <div
            v-else
            class="bar bar-milestone"
            :style="{
              ...taskBarStyle(row.task),
              color: getTaskColor(row.task).fill
            }"
          >
            ◆
          </div>
        </div>
      </div>

      <!-- 底部时间轴 -->
      <div class="gantt-footer">
        <div class="gantt-task-col" />
        <div class="gantt-time-col">
          <div
            v-for="(tick, idx) in ganttScale.ticks"
            :key="idx"
            class="gantt-tick-label"
            :style="{ left: `${(idx / (ganttScale.ticks.length - 1)) * 100}%` }"
          >
            {{ tick.label }}
          </div>
        </div>
      </div>
    </div>

    <div ref="tooltipRef" class="gantt-tooltip" />
  </div>
</template>

<style scoped>
.project-gantt {
  position: relative;
  background: var(--pms-color-bg-card, #fff);
  border-radius: var(--pms-radius-lg, 8px);
  padding: 16px;
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
}
.gantt-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.toolbar-spacer {
  flex: 1;
}

/* 图例 */
.gantt-legend {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 12px;
  padding: 8px 12px;
  background: var(--pms-color-bg-page, #fafafa);
  border-radius: var(--pms-radius-md, 6px);
}
.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.legend-bar {
  display: inline-block;
  width: 18px;
  height: 10px;
  border-radius: 2px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}
.legend-diamond {
  display: inline-block;
  color: #3b82f6;
  font-size: 14px;
  line-height: 1;
}

/* 甘特图主体（表格形式） */
.gantt-container {
  width: 100%;
  font-size: 12px;
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
  border-radius: var(--pms-radius-md, 6px);
  overflow: hidden;
}
.gantt-header,
.gantt-row,
.gantt-footer {
  display: flex;
  align-items: stretch;
  border-bottom: 1px solid var(--pms-color-border-light, #f0f0f0);
}
.gantt-header {
  background: var(--pms-color-bg-page, #fafafa);
  font-weight: 600;
}
.gantt-footer {
  background: var(--pms-color-bg-page, #fafafa);
  border-bottom: none;
}
.gantt-task-col {
  width: 220px;
  min-width: 220px;
  padding: 8px 12px;
  border-right: 1px solid var(--pms-color-border-light, #f0f0f0);
  display: flex;
  align-items: center;
  overflow: hidden;
  gap: 4px;
  color: var(--pms-color-text-primary, #1f2937);
}
.task-indent {
  flex-shrink: 0;
}
.milestone-marker {
  color: #3b82f6;
  font-size: 14px;
  flex-shrink: 0;
}
.task-name-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.critical-tag {
  flex-shrink: 0;
  transform: scale(0.85);
}
.gantt-time-col {
  flex: 1;
  position: relative;
  min-height: 36px;
  padding: 8px 0;
}
.gantt-header .gantt-time-col {
  height: 32px;
  padding: 8px 0;
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
.gantt-row {
  cursor: pointer;
  transition: background 0.15s;
}
.gantt-row:hover {
  background: var(--pms-color-bg-hover, #f9fafb);
}
.gantt-row.row-delayed {
  background: #fef2f2;
}
.gantt-row.row-delayed:hover {
  background: #fee2e2;
}
.gantt-row.row-critical .task-name-text {
  color: #ef4444;
  font-weight: 600;
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

/* 任务条 */
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
  cursor: pointer;
  transition: filter 0.15s;
}
.bar:hover {
  filter: brightness(1.1);
}
.bar-baseline {
  background: #d1d5db;
  color: #4b5563;
  z-index: 1;
  height: 20px;
  opacity: 0.7;
  border: 1px dashed #9ca3af;
}
.bar-actual {
  z-index: 2;
  border: 1px solid;
}
.bar-actual.bar-delayed {
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
}
.bar-actual.bar-critical {
  border: 2px solid #ef4444 !important;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.25);
  z-index: 3;
  height: 18px;
}
.bar-milestone {
  z-index: 2;
  background: transparent;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 20px !important;
}
.bar-label {
  padding: 0 4px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* Tooltip */
.gantt-tooltip {
  position: fixed;
  display: none;
  z-index: 9999;
  min-width: 240px;
  padding: 10px 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  font-size: 12px;
  color: #1f2937;
  pointer-events: none;
}
.gantt-tooltip :deep(.gt-title) {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  padding-bottom: 6px;
  border-bottom: 1px solid #f3f4f6;
}
.gantt-tooltip :deep(.gt-row) {
  display: flex;
  justify-content: space-between;
  margin: 2px 0;
}
.gantt-tooltip :deep(.gt-row span) {
  color: #6b7280;
}
.gantt-tooltip :deep(.gt-row b) {
  font-weight: 500;
}
</style>
