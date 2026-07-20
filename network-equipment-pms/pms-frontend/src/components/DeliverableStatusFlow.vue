<script setup lang="ts">
import { computed } from 'vue'
import {
  DELIVERABLE_STATUS_LABELS,
  DELIVERABLE_STATUS_ORDER,
  type DeliverableStatus
} from '@/api/deliverable'

defineOptions({ name: 'DeliverableStatusFlow' })

/**
 * 7 态交付件状态流可视化组件。
 *
 * <p>横向排列 7 个状态节点（DRAFT → SUBMITTED → REVIEWED → SIGNED →
 * PUBLISHED → REFERENCED → ARCHIVED），根据 {@code current} 高亮当前状态，
 * 已通过状态显示绿色 ✓，未到达状态显示灰色序号。</p>
 *
 * <p>关联设计文档：§3.4 交付件状态机 7 态（行 393-428）。</p>
 *
 * 用法：
 * ```vue
 * <DeliverableStatusFlow :current="deliverable.status" />
 * ```
 */
const props = defineProps<{
  /** 当前状态码（DRAFT/SUBMITTED/.../ARCHIVED）。可空，表示未知 */
  current?: DeliverableStatus | string
  /** 是否显示标签文字（默认 true） */
  showLabels?: boolean
  /** 是否紧凑模式（小尺寸，默认 false） */
  compact?: boolean
}>()

/** 当前状态在 7 态顺序中的索引（-1 表示未知） */
const currentIndex = computed(() => {
  const s = props.current as DeliverableStatus | undefined
  if (!s) return -1
  return DELIVERABLE_STATUS_ORDER.indexOf(s)
})

/** 节点状态类名 */
function nodeClass(idx: number): string {
  if (idx === currentIndex.value) return 'is-current'
  if (idx < currentIndex.value) return 'is-passed'
  return 'is-pending'
}

/** 节点圆圈内显示的字符 */
function nodeSymbol(idx: number): string {
  if (idx < currentIndex.value) return '✓'
  return String(idx + 1)
}
</script>

<template>
  <div class="deliverable-status-flow" :class="{ 'is-compact': compact }">
    <div
      v-for="(s, idx) in DELIVERABLE_STATUS_ORDER"
      :key="s"
      class="flow-node"
      :class="nodeClass(idx)"
    >
      <div class="node-circle">{{ nodeSymbol(idx) }}</div>
      <div v-if="showLabels !== false" class="node-label">
        {{ DELIVERABLE_STATUS_LABELS[s] }}
      </div>
      <div
        v-if="idx < DELIVERABLE_STATUS_ORDER.length - 1"
        class="node-arrow"
        :class="idx < currentIndex ? 'is-passed' : ''"
      >→</div>
    </div>
  </div>
</template>

<style scoped>
.deliverable-status-flow {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
  gap: 0;
}
.flow-node {
  display: flex;
  align-items: center;
  position: relative;
}
.node-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  border: 2px solid #dcdfe6;
  background: #fff;
  color: #909399;
  margin-right: 6px;
  flex-shrink: 0;
  transition: all 0.2s ease;
}
.node-label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
  white-space: nowrap;
}
.node-arrow {
  color: #dcdfe6;
  margin-right: 8px;
  font-size: 14px;
  transition: color 0.2s ease;
}
.node-arrow.is-passed {
  color: #67c23a;
}

/* 已通过 */
.flow-node.is-passed .node-circle {
  background: #67c23a;
  border-color: #67c23a;
  color: #fff;
}
.flow-node.is-passed .node-label {
  color: #67c23a;
}

/* 当前 */
.flow-node.is-current .node-circle {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.2);
}
.flow-node.is-current .node-label {
  color: #409eff;
  font-weight: 600;
}

/* 紧凑模式 */
.is-compact .node-circle {
  width: 24px;
  height: 24px;
  font-size: 11px;
}
.is-compact .node-label {
  font-size: 12px;
  margin-right: 6px;
}
.is-compact .node-arrow {
  font-size: 12px;
  margin-right: 6px;
}
</style>
