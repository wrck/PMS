<script setup lang="ts">
// =============================================================================
// ProjectGantt - 项目甘特图组件（基于 @antv/g6 v5）
// -----------------------------------------------------------------------------
// 功能：
// - Dagre LR 布局，任务节点（rect）+ 里程碑节点（diamond，工期=0 天判定）
// - 节点按状态着色（已完成/进行中/未开始/已延期）
// - 4 种依赖边样式（FS 实线 / SS 虚线 / FF 点线 / SF 双线）
// - 关键路径高亮（DFS 最长路径，红色加粗）
// - 基线对比叠加（节点 label 附加基线日期范围）
// - 节点 hover 显示 tooltip；click emit('task-click', task)
// - 工具栏：放大/缩小/适配/导出 PNG
// - 加载中 skeleton + 空状态 EmptyState
// =============================================================================
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { Graph, type EdgeData, type NodeData } from '@antv/g6'
import { ElMessage } from 'element-plus'
import {
  getTasksByProject,
  type ImplTask,
  type TaskStatus
} from '@/api/implementation'
import {
  listDependencies,
  type DependencyType,
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
const containerRef = ref<HTMLDivElement>()
const canvasRef = ref<HTMLDivElement>()
const tooltipRef = ref<HTMLDivElement>()

const loading = ref(false)
const tasks = ref<ImplTask[]>([])
const dependencies = ref<TaskDependency[]>([])
const baseline = ref<BaselineSnapshot | null>(null)

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

let graph: Graph | null = null
let prevCriticalNodeIds: string[] = []
let prevCriticalEdgeIds: string[] = []

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

const DEPENDENCY_STYLE: Record<
  DependencyType,
  { lineDash: number[]; label: string }
> = {
  FS: { lineDash: [], label: '完成-开始' },
  SS: { lineDash: [5, 5], label: '开始-开始' },
  FF: { lineDash: [2, 4], label: '完成-完成' },
  SF: { lineDash: [10, 3, 2, 3], label: '开始-完成' }
}

const DAY_WIDTH: Record<'day' | 'week' | 'month', number> = {
  day: 30,
  week: 15,
  month: 5
}

// ============ 计算辅助 ============
function isMilestone(task: ImplTask): boolean {
  // 工期为 0 天（同日开始）视为里程碑
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

function getTaskStroke(task: ImplTask): string {
  // 延期任务使用红色虚线边框（闪烁动画在 G6 v5 中较复杂，暂用边框颜色区分）
  if (isDelayed(task)) return '#dc2626'
  const status = (task.status ?? 'PENDING') as TaskStatus
  return (STATUS_COLORS[status] ?? STATUS_COLORS.PENDING).stroke
}

function getTaskColor(task: ImplTask) {
  const status = (task.status ?? 'PENDING') as TaskStatus
  return STATUS_COLORS[status] ?? STATUS_COLORS.PENDING
}

function getTaskWidth(task: ImplTask): number {
  if (isMilestone(task)) return 24
  const days = getDurationDays(task)
  return Math.max(60, days * DAY_WIDTH[viewMode.value])
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

// ============ 节点 / 边构建 ============
function buildNodes(): NodeData[] {
  return tasks.value.map((task) => {
    const color = getTaskColor(task)
    const milestone = isMilestone(task)
    const width = getTaskWidth(task)
    const baseLabel = task.taskName ?? ''
    const snap = baselineMap.value.get(task.id ?? 0)
    const baselineLabel =
      showBaseline.value && snap
        ? `\n基线: ${snap.plannedStart ?? '-'} ~ ${snap.plannedEnd ?? '-'}`
        : ''
    return {
      id: String(task.id),
      data: { ...task },
      style: {
        type: milestone ? 'diamond' : 'rect',
        size: milestone ? 24 : [width, 32],
        fill: color.fill,
        stroke: getTaskStroke(task),
        lineWidth: isDelayed(task) ? 2 : 1,
        lineDash: isDelayed(task) ? [4, 4] : [],
        radius: milestone ? 0 : 4,
        labelText: `${baseLabel}${baselineLabel}`,
        labelFill: '#1f2937',
        labelFontSize: 12,
        labelPosition: milestone ? 'bottom' : 'right',
        labelOffsetY: milestone ? 8 : 0,
        labelWordWrap: true,
        labelMaxWidth: 160
      }
    }
  })
}

function buildEdges(): EdgeData[] {
  return dependencies.value.map((dep) => {
    const depType = (dep.dependencyType ?? 'FS') as DependencyType
    const style = DEPENDENCY_STYLE[depType] ?? DEPENDENCY_STYLE.FS
    const lag = dep.lagDays ? `+${dep.lagDays}d` : ''
    const s = String(dep.predecessorTaskId)
    const t = String(dep.successorTaskId)
    return {
      id: `${s}-${t}`,
      source: s,
      target: t,
      data: { ...dep },
      style: {
        type: 'line',
        stroke: '#94a3b8',
        lineWidth: 1.5,
        lineDash: style.lineDash,
        endArrow: true,
        labelText: `${depType}${lag}`,
        labelFontSize: 10,
        labelFill: '#64748b',
        labelBackground: true,
        labelBackgroundFill: '#fff',
        labelBackgroundRadius: 2,
        labelBackgroundPadding: [2, 2, 2, 2]
      }
    }
  })
}

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

function applyCriticalPath() {
  if (!graph) return
  // 清除旧高亮
  for (const id of prevCriticalNodeIds) graph.setElementState(id, [])
  for (const id of prevCriticalEdgeIds) graph.setElementState(id, [])
  prevCriticalNodeIds = []
  prevCriticalEdgeIds = []
  if (!criticalPathEnabled.value) return
  const cp = criticalPath.value
  for (const id of cp.nodes) {
    graph.setElementState(id, 'critical')
    prevCriticalNodeIds.push(id)
  }
  for (const id of cp.edges) {
    graph.setElementState(id, 'critical')
    prevCriticalEdgeIds.push(id)
  }
}

// ============ Graph 初始化 ============
function initGraph() {
  if (!canvasRef.value) return
  const width = containerRef.value?.clientWidth ?? 800
  const height = 560
  graph = new Graph({
    container: canvasRef.value,
    width,
    height,
    layout: {
      type: 'dagre',
      rankdir: 'LR',
      nodesep: 24,
      ranksep: 60
    },
    node: {
      type: 'rect',
      style: {
        size: [120, 32],
        radius: 4,
        fill: '#9ca3af',
        stroke: '#6b7280',
        lineWidth: 1,
        labelText: (d: NodeData) => (d.data as ImplTask)?.taskName ?? '',
        labelFill: '#1f2937',
        labelFontSize: 12,
        labelPosition: 'right'
      },
      state: {
        critical: { stroke: '#ef4444', lineWidth: 3, shadowColor: '#ef4444' },
        hover: { lineWidth: 2, stroke: '#2563eb' }
      }
    },
    edge: {
      type: 'line',
      style: {
        stroke: '#94a3b8',
        lineWidth: 1.5,
        endArrow: true
      },
      state: {
        critical: { stroke: '#ef4444', lineWidth: 2.5 }
      }
    },
    behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element'],
    autoFit: 'view'
  })

  // 节点点击 → emit
  graph.on('node:click', (evt: any) => {
    const id = evt.target?.id ?? evt.itemId
    if (id == null) return
    const task = tasks.value.find((t) => String(t.id) === String(id))
    if (task) emit('task-click', task)
  })

  // hover tooltip
  graph.on('node:mouseenter', (evt: any) => {
    const id = evt.target?.id ?? evt.itemId
    if (id == null) return
    const task = tasks.value.find((t) => String(t.id) === String(id))
    if (task) {
      emit('task-hover', task)
      showTooltip(task, evt)
    }
  })
  graph.on('node:mouseleave', () => {
    emit('task-hover', null)
    hideTooltip()
  })
}

function showTooltip(task: ImplTask, evt: any) {
  const el = tooltipRef.value
  if (!el) return
  const dur = getDurationDays(task)
  const status = task.status ?? 'PENDING'
  const engineer = task.engineerName || task.agentName || '-'
  el.innerHTML = `
    <div class="gt-title">${escapeHtml(task.taskName)}</div>
    <div class="gt-row"><span>状态</span><b>${status}</b></div>
    <div class="gt-row"><span>工期</span><b>${dur} 天</b></div>
    <div class="gt-row"><span>进度</span><b>${task.progress ?? 0}%</b></div>
    <div class="gt-row"><span>计划开始</span><b>${task.planStartDate ?? '-'}</b></div>
    <div class="gt-row"><span>计划结束</span><b>${task.planEndDate ?? '-'}</b></div>
    <div class="gt-row"><span>负责人</span><b>${escapeHtml(engineer)}</b></div>
  `
  el.style.display = 'block'
  const canvas = canvasRef.value
  if (canvas) {
    const rect = canvas.getBoundingClientRect()
    const x = (evt.canvasX ?? evt.x ?? 0) as number
    const y = (evt.canvasY ?? evt.y ?? 0) as number
    el.style.left = `${rect.left + x + 12}px`
    el.style.top = `${rect.top + y + 12}px`
  }
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

// ============ 渲染 ============
function renderGraph() {
  if (!graph) return
  const nodes = buildNodes()
  const edges = buildEdges()
  graph.setData({ nodes, edges })
  graph.render()
  // 重新渲染后重新应用关键路径
  prevCriticalNodeIds = []
  prevCriticalEdgeIds = []
  applyCriticalPath()
}

// ============ 工具栏操作 ============
function zoomIn() {
  const cur = (graph?.getZoom?.() as number) ?? 1
  graph?.zoomTo(cur * 1.2)
}
function zoomOut() {
  const cur = (graph?.getZoom?.() as number) ?? 1
  graph?.zoomTo(cur / 1.2)
}
function fitView() {
  if (!graph) return
  if (typeof graph.fitView === 'function') {
    graph.fitView(20)
  } else if (typeof graph.autoFit === 'function') {
    graph.autoFit('view')
  }
}
async function exportPng() {
  if (!graph) return
  try {
    const dataUrl = (await (graph as any).toDataURL('image/png', 1)) as string
    const link = document.createElement('a')
    link.download = `gantt-project-${props.projectId}.png`
    link.href = dataUrl
    link.click()
    ElMessage.success('已导出甘特图')
  } catch {
    ElMessage.error('导出失败')
  }
}

function handleResize() {
  if (graph && containerRef.value) {
    const w = containerRef.value.clientWidth
    if (typeof (graph as any).resize === 'function') {
      ;(graph as any).resize(w, 560)
    } else if (typeof (graph as any).changeSize === 'function') {
      ;(graph as any).changeSize(w, 560)
    }
  }
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
    renderGraph()
  } finally {
    loading.value = false
  }
}

// ============ 生命周期 ============
onMounted(() => {
  initGraph()
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  hideTooltip()
  graph?.destroy()
  graph = null
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
watch([viewMode, showBaseline], () => {
  if (graph) renderGraph()
})
watch(
  () => props.showCriticalPath,
  () => applyCriticalPath()
)

defineExpose({ refresh: loadData })

const isEmpty = computed(() => !loading.value && tasks.value.length === 0)
</script>

<template>
  <div class="project-gantt" ref="containerRef">
    <div class="gantt-toolbar">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="day">日</el-radio-button>
        <el-radio-button label="week">周</el-radio-button>
        <el-radio-button label="month">月</el-radio-button>
      </el-radio-group>
      <el-divider direction="vertical" />
      <el-switch
        v-model="showBaseline"
        active-text="基线对比"
        :disabled="baselineId == null"
      />
      <el-switch v-model="criticalPathEnabled" active-text="关键路径" />
      <div class="toolbar-spacer" />
      <el-button-group>
        <el-button size="small" @click="zoomIn">放大</el-button>
        <el-button size="small" @click="zoomOut">缩小</el-button>
        <el-button size="small" @click="fitView">适配</el-button>
        <el-button size="small" @click="exportPng">导出 PNG</el-button>
      </el-button-group>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <EmptyState
      v-else-if="isEmpty"
      icon="Calendar"
      title="暂无任务数据"
      description="当前项目下还没有任务，无法绘制甘特图"
    />

    <div v-show="!loading && !isEmpty" class="gantt-canvas" ref="canvasRef" />

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
.gantt-canvas {
  width: 100%;
  height: 560px;
  background: var(--pms-color-bg-page, #fafafa);
  border-radius: var(--pms-radius-md, 6px);
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
}
.gantt-tooltip {
  position: fixed;
  display: none;
  z-index: 9999;
  min-width: 220px;
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
