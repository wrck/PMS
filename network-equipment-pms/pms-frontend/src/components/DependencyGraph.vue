<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { Graph, type NodeData, type EdgeData } from '@antv/g6'
import { listDependencies, type TaskDependency } from '@/api/task-dependency'
import { getTasksByProject, type ImplTask } from '@/api/implementation'

/**
 * 任务依赖关系图（AntV G6 v5 DAG）。
 *
 * <p>关联设计文档 §7.4。以 dagre LR 布局渲染项目任务依赖有向图：节点为任务
 * （rect），边为依赖关系（quadratic，标注依赖类型+滞后天数）。检测到循环依赖时，
 * 通过 {@link Props.highlightCycle} 高亮闭环路径上的节点与边（cycle 状态）。</p>
 */
interface Props {
  /** 项目ID（变化时自动刷新图） */
  projectId: number
  /** 闭环路径任务ID列表（首尾相同），用于高亮循环依赖 */
  highlightCycle?: number[]
}

const props = defineProps<Props>()
const emit = defineEmits<{ 'select-task': [number] }>()

const containerRef = ref<HTMLDivElement>()
let graph: Graph | null = null
/** 上一次高亮的元素ID，用于切换高亮前清除旧状态 */
let prevHighlightIds: string[] = []

onMounted(() => {
  if (!containerRef.value) return
  graph = new Graph({
    container: containerRef.value,
    width: containerRef.value.offsetWidth || 800,
    height: 600,
    layout: { type: 'dagre', rankdir: 'LR', nodesep: 40, ranksep: 80 },
    node: {
      type: 'rect',
      style: {
        size: [140, 40],
        radius: 6,
        labelText: (d: NodeData) => (d.data as { taskName?: string })?.taskName ?? '',
        labelFill: '#333',
        fill: '#e6f7ff',
        stroke: '#1890ff'
      },
      state: {
        cycle: { fill: '#fff1f0', stroke: '#ff4d4f' },
        critical: { fill: '#fff7e6', stroke: '#fa8c16' }
      }
    },
    edge: {
      type: 'quadratic',
      style: {
        labelText: (d: EdgeData) => {
          const data = d.data as unknown as TaskDependency
          return `${data.dependencyType ?? 'FS'}${data.lagDays ? '+' + data.lagDays : ''}`
        },
        endArrow: true
      },
      state: { cycle: { stroke: '#ff4d4f', lineWidth: 2 } }
    },
    behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element'],
    autoFit: 'view'
  })
  graph.on('node:click', (evt: any) => {
    const id = evt.target?.id ?? evt.itemId
    if (id != null) emit('select-task', Number(id))
  })
  refresh()
})

watch(() => props.projectId, refresh)
watch(() => props.highlightCycle, applyCycleHighlight)

async function refresh() {
  if (!graph || !props.projectId) return
  const [deps, tasks] = await Promise.all([
    listDependencies(props.projectId),
    getTasksByProject(props.projectId)
  ])
  const nodes: NodeData[] = tasks.map((t: ImplTask) => ({
    id: String(t.id),
    data: { taskName: t.taskName, status: t.status }
  }))
  const edges: EdgeData[] = deps.map((d: TaskDependency) => ({
    id: `${d.predecessorTaskId}-${d.successorTaskId}`,
    source: String(d.predecessorTaskId),
    target: String(d.successorTaskId),
    data: { ...d }
  }))
  graph.setData({ nodes, edges })
  graph.render()
  // 重新渲染后清除高亮记忆，若 highlightCycle 仍存在则重新应用
  prevHighlightIds = []
  if (props.highlightCycle?.length) {
    applyCycleHighlight(props.highlightCycle)
  }
}

function applyCycleHighlight(cycleIds?: number[]) {
  if (!graph) return
  // 先清除上一次高亮
  if (prevHighlightIds.length) {
    for (const id of prevHighlightIds) {
      graph.setElementState(id, [])
    }
    prevHighlightIds = []
  }
  if (!cycleIds?.length) return
  const idStrs = cycleIds.map(String)
  for (const id of idStrs) {
    graph.setElementState(id, ['cycle'])
  }
  prevHighlightIds.push(...idStrs)
  for (let i = 0; i < cycleIds.length - 1; i++) {
    const edgeId = `${cycleIds[i]}-${cycleIds[i + 1]}`
    graph.setElementState(edgeId, ['cycle'])
    prevHighlightIds.push(edgeId)
  }
}

onUnmounted(() => {
  graph?.destroy()
  graph = null
})

defineExpose({ refresh })
</script>

<template>
  <div ref="containerRef" class="dependency-graph" />
</template>

<style scoped>
.dependency-graph {
  width: 100%;
  height: 600px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}
</style>
