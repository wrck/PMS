<script setup lang="ts">
/**
 * 微流节点面板（左侧，借鉴 Mendix Microflows 工具栏）。
 *
 * <p>列出 11 种节点类型，每种带图标 + 文字 + 主题色，支持 HTML5 draggable。
 * 拖到中间画布时，父组件监听 drop 事件并调用 X6 addNode 创建节点。</p>
 *
 * <p>同时支持点击节点项将其添加到画布默认位置（无 drag 时的降级路径）。</p>
 */
import type { MicroflowNodeType } from '@/api/lowcode-microflow'

defineOptions({ name: 'MicroflowNodePalette' })

const emit = defineEmits<{
  (e: 'add-node', type: MicroflowNodeType): void
}>()

interface PaletteItem {
  type: MicroflowNodeType
  label: string
  icon: string
  color: string
  /** 节点形状 hint：圆形/矩形/菱形（仅用于面板徽章样式，不影响 X6 实际形状） */
  shape: 'circle' | 'rect' | 'diamond'
  description: string
}

/** 11 种节点类型定义 */
const PALETTE: PaletteItem[] = [
  { type: 'START', label: '开始', icon: '▶', color: '#67c23a', shape: 'circle', description: '流程起点' },
  { type: 'END', label: '结束', icon: '■', color: '#f56c6c', shape: 'circle', description: '流程终点' },
  { type: 'ASSIGN', label: '赋值', icon: '=', color: '#409eff', shape: 'rect', description: '变量赋值' },
  { type: 'CONDITION', label: '条件', icon: '?', color: '#e6a23c', shape: 'diamond', description: '条件分支' },
  { type: 'LOOP', label: '循环', icon: '↻', color: '#9c27b0', shape: 'rect', description: '迭代循环' },
  { type: 'CALL_SERVICE', label: '调用服务', icon: '⚙', color: '#00bcd4', shape: 'rect', description: '调用 Spring Bean 方法' },
  { type: 'CALL_MICROFLOW', label: '调用子微流', icon: '✦', color: '#00bcd4', shape: 'rect', description: '调用其他微流' },
  { type: 'CALL_RULE', label: '调用规则', icon: '§', color: '#00bcd4', shape: 'rect', description: '调用规则引擎' },
  { type: 'CALL_CONNECTOR', label: '调用连接器', icon: '⇄', color: '#00bcd4', shape: 'rect', description: '调用 REST/DB 连接器' },
  { type: 'THROW_EXCEPTION', label: '抛异常', icon: '!', color: '#ff9800', shape: 'rect', description: '抛出业务异常' },
  { type: 'RETURN', label: '返回', icon: '←', color: '#67c23a', shape: 'rect', description: '返回结果' }
]

function onDragStart(e: DragEvent, item: PaletteItem) {
  if (!e.dataTransfer) return
  e.dataTransfer.setData('microflow-node-type', item.type)
  e.dataTransfer.effectAllowed = 'copy'
}

function onClick(item: PaletteItem) {
  emit('add-node', item.type)
}
</script>

<template>
  <div class="node-palette">
    <div class="palette-header">节点面板</div>
    <div class="palette-list">
      <div
        v-for="item in PALETTE"
        :key="item.type"
        class="palette-item"
        :class="`shape-${item.shape}`"
        draggable="true"
        :title="item.description"
        @dragstart="onDragStart($event, item)"
        @click="onClick(item)"
      >
        <span class="palette-icon" :style="{ background: item.color }">{{ item.icon }}</span>
        <span class="palette-label">{{ item.label }}</span>
      </div>
    </div>
    <div class="palette-tip">提示：拖拽或点击节点添加到画布</div>
  </div>
</template>

<style scoped lang="scss">
.node-palette {
  width: 200px;
  background: #fff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #ebeef5;
  flex-shrink: 0;
  height: 100%;
}

.palette-header {
  padding: 10px;
  font-weight: 600;
  font-size: 13px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.palette-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.palette-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: grab;
  user-select: none;
  transition: all 0.15s;
  background: #fff;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.15);
  }

  &:active {
    cursor: grabbing;
  }

  &.shape-circle .palette-icon {
    border-radius: 50%;
  }
  &.shape-rect .palette-icon {
    border-radius: 4px;
  }
  &.shape-diamond .palette-icon {
    border-radius: 0;
    transform: rotate(45deg);
    width: 24px;
    height: 24px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  &.shape-diamond .palette-icon > span {
    transform: rotate(-45deg);
  }
}

.palette-icon {
  width: 26px;
  height: 26px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.palette-label {
  font-size: 13px;
  color: #303133;
  flex: 1;
}

.palette-tip {
  padding: 8px 10px;
  font-size: 11px;
  color: #909399;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}
</style>
