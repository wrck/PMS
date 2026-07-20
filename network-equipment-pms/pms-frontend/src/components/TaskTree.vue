<script setup lang="ts">
import { ref } from 'vue'
import type { ImplTaskNode, TaskPriority, TaskStatus } from '@/api/implementation'
import type { EpTagType } from '@/types'

defineOptions({ name: 'TaskTree' })

const props = defineProps<{
  /** 当前节点（含 children 递归子任务） */
  task: ImplTaskNode
  /** 当前层级（用于缩进，根节点为 0） */
  level?: number
  /** 是否默认展开 */
  defaultExpanded?: boolean
}>()

const emit = defineEmits<{
  (e: 'node-click', id: number): void
  (e: 'add-child', parentId: number): void
  (e: 'edit', id: number): void
}>()

const currentLevel = props.level ?? 0
const expanded = ref(props.defaultExpanded ?? currentLevel < 1)

const statusMeta: Record<TaskStatus, { label: string; tagType: EpTagType }> = {
  PENDING: { label: '待接单', tagType: 'info' },
  ACCEPTED: { label: '已接单', tagType: 'warning' },
  IN_PROGRESS: { label: '进行中', tagType: 'primary' },
  REVIEW: { label: '评审中', tagType: 'warning' },
  BLOCKED: { label: '已阻塞', tagType: 'danger' },
  COMPLETED: { label: '已完成', tagType: 'success' },
  CONFIRMED: { label: '已确认', tagType: 'success' },
  REJECTED: { label: '已驳回', tagType: 'danger' }
}

const priorityMeta: Record<TaskPriority, { label: string; tagType: EpTagType }> = {
  LOW: { label: '低', tagType: 'info' },
  MEDIUM: { label: '中', tagType: 'primary' },
  HIGH: { label: '高', tagType: 'warning' },
  CRITICAL: { label: '紧急', tagType: 'danger' }
}

function statusLabel(status?: string): string {
  if (!status) return '-'
  // REVIEW / BLOCKED 由 Story 3 新增的状态，原 statusMeta 不包含，回退展示原值
  if (status === 'REVIEW') return '评审中'
  if (status === 'BLOCKED') return '已阻塞'
  return statusMeta[status as TaskStatus]?.label ?? status
}

function statusTagType(status?: string): EpTagType {
  if (!status) return 'info'
  if (status === 'REVIEW') return 'warning'
  if (status === 'BLOCKED') return 'danger'
  return statusMeta[status as TaskStatus]?.tagType ?? 'info'
}

function priorityLabel(priority?: TaskPriority): string {
  return priority ? (priorityMeta[priority]?.label ?? priority) : '-'
}

function priorityTagType(priority?: TaskPriority): EpTagType {
  return priority ? (priorityMeta[priority]?.tagType ?? 'info') : 'info'
}

function assigneeText(task: ImplTaskNode): string {
  if (task.taskType === 'AGENT') return task.agentName || '-'
  return task.engineerName || '-'
}

function hasChildren(task: ImplTaskNode): boolean {
  return !!task.children?.length
}

function toggleExpand() {
  if (!hasChildren(props.task)) return
  expanded.value = !expanded.value
}

function onClick() {
  if (props.task.id) emit('node-click', props.task.id)
}

function onChildClick(id: number) {
  emit('node-click', id)
}

function onAddChild() {
  if (props.task.id) emit('add-child', props.task.id)
}

function onEdit() {
  if (props.task.id) emit('edit', props.task.id)
}
</script>

<template>
  <div class="task-tree-node">
    <div
      class="node-row"
      :style="{ paddingLeft: `${currentLevel * 22 + 8}px` }"
    >
      <el-icon v-if="hasChildren(task)" class="node-toggle" @click.stop="toggleExpand">
        <ArrowDown v-if="expanded" />
        <ArrowRight v-else />
      </el-icon>
      <el-icon v-else class="node-toggle node-toggle-leaf"><Document /></el-icon>

      <span class="node-name" @click="onClick">{{ task.taskName }}</span>

      <el-tag
        v-if="task.priority && task.priority !== 'MEDIUM'"
        :type="priorityTagType(task.priority)"
        size="small"
        effect="plain"
        class="node-tag"
      >
        {{ priorityLabel(task.priority) }}
      </el-tag>

      <el-tag
        :type="statusTagType(task.status)"
        size="small"
        effect="plain"
        class="node-tag"
      >
        {{ statusLabel(task.status) }}
      </el-tag>

      <el-progress
        :percentage="Number(task.progress ?? 0)"
        :stroke-width="8"
        :show-text="false"
        class="node-progress"
      />
      <span class="node-progress-text">{{ task.progress ?? 0 }}%</span>

      <span class="node-assignee">{{ assigneeText(task) }}</span>

      <el-button link type="primary" size="small" @click.stop="onEdit">编辑</el-button>
      <el-button link type="primary" size="small" @click.stop="onAddChild">加子任务</el-button>
    </div>

    <div v-show="expanded && hasChildren(task)" class="node-children">
      <TaskTree
        v-for="child in task.children"
        :key="child.id"
        :task="child"
        :level="currentLevel + 1"
        :default-expanded="defaultExpanded"
        @node-click="onChildClick"
        @add-child="(pid) => emit('add-child', pid)"
        @edit="(id) => emit('edit', id)"
      />
    </div>
  </div>
</template>

<style scoped>
.task-tree-node {
  display: flex;
  flex-direction: column;
}
.node-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 4px;
  transition: background-color 0.15s;
}
.node-row:hover {
  background-color: #f5f7fa;
}
.node-toggle {
  color: #909399;
  font-size: 14px;
  cursor: pointer;
  width: 14px;
  flex-shrink: 0;
}
.node-toggle-leaf {
  color: #c0c4cc;
  cursor: default;
}
.node-name {
  font-size: 14px;
  color: #303133;
  min-width: 160px;
  cursor: pointer;
  flex-shrink: 0;
}
.node-name:hover {
  color: var(--el-color-primary);
}
.node-tag {
  flex-shrink: 0;
}
.node-progress {
  width: 120px;
  flex-shrink: 0;
}
.node-progress-text {
  font-size: 12px;
  color: #909399;
  min-width: 36px;
  flex-shrink: 0;
}
.node-assignee {
  font-size: 12px;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}
.node-children {
  display: flex;
  flex-direction: column;
}
</style>
