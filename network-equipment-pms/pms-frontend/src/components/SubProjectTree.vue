<script setup lang="ts">
import type { ProjectTreeNode } from '@/api/project'
import type { EpTagType } from '@/types'

defineOptions({ name: 'SubProjectTree' })

const props = defineProps<{
  /** 树形数据（单个节点，含 children 递归子节点） */
  projectTree: ProjectTreeNode
  /** 当前层级（用于缩进，根节点为 0） */
  level?: number
}>()

const emit = defineEmits<{
  (e: 'node-click', id: number): void
}>()

const currentLevel = props.level ?? 0

const statusMeta = (status?: string): { label: string; tagType: EpTagType } => {
  switch (status) {
    case 'PLANNING':
      return { label: '规划中', tagType: 'info' }
    case 'EXECUTING':
    case 'IN_PROGRESS':
      return { label: '执行中', tagType: 'primary' }
    case 'CLOSING':
      return { label: '收尾中', tagType: 'warning' }
    case 'CLOSED':
      return { label: '已关闭', tagType: 'info' }
    case 'CANCELLED':
      return { label: '已取消', tagType: 'danger' }
    case 'COMPLETED':
      return { label: '已完成', tagType: 'success' }
    default:
      return { label: status ?? '-', tagType: 'info' }
  }
}

function onClick() {
  emit('node-click', props.projectTree.id)
}

function onChildClick(id: number) {
  emit('node-click', id)
}
</script>

<template>
  <div class="sub-tree-node">
    <div
      class="node-row"
      :style="{ paddingLeft: `${currentLevel * 20 + 8}px` }"
      @click="onClick"
    >
      <el-icon v-if="projectTree.children?.length" class="node-icon"><ArrowDown /></el-icon>
      <el-icon v-else class="node-icon node-icon-leaf"><Document /></el-icon>
      <span class="node-name">{{ projectTree.projectName }}</span>
      <el-tag
        :type="statusMeta(projectTree.status).tagType"
        size="small"
        effect="plain"
      >
        {{ statusMeta(projectTree.status).label }}
      </el-tag>
      <el-progress
        :percentage="Number(projectTree.progress ?? 0)"
        :stroke-width="8"
        :show-text="false"
        class="node-progress"
      />
      <span class="node-progress-text">{{ projectTree.progress ?? 0 }}%</span>
    </div>
    <!-- 递归渲染子节点，支持无限嵌套 -->
    <SubProjectTree
      v-for="child in projectTree.children"
      :key="child.id"
      :project-tree="child"
      :level="currentLevel + 1"
      @node-click="onChildClick"
    />
  </div>
</template>

<style scoped>
.sub-tree-node {
  display: flex;
  flex-direction: column;
}
.node-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.15s;
}
.node-row:hover {
  background-color: #f5f7fa;
}
.node-icon {
  color: #909399;
  font-size: 14px;
}
.node-icon-leaf {
  color: #c0c4cc;
}
.node-name {
  font-size: 14px;
  color: #303133;
  min-width: 120px;
}
.node-progress {
  width: 100px;
}
.node-progress-text {
  font-size: 12px;
  color: #909399;
  min-width: 36px;
}
</style>
