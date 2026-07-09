<template>
  <div class="swipe-actions">
    <div
      ref="contentRef"
      class="swipe-content"
      :style="{ transform: `translateX(${translateX}px)` }"
      @touchstart="onTouchStart"
      @click="onContentClick"
    >
      <slot />
    </div>
    <div class="swipe-actions-bar">
      <button
        v-for="action in actions"
        :key="action.key"
        class="swipe-action-btn"
        :class="`action-${action.type || 'default'}`"
        :style="{ width: `${action.width || 72}px` }"
        @click.stop="onAction(action)"
      >
        {{ action.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * SwipeActions — 移动端手势滑动操作（批次4-T10）。
 *
 * <p>借鉴 iOS TableView 的左滑删除与 Material Design 的 SwipeToDismiss，
 * 向左滑动列表项显示操作按钮（删除/编辑/收藏等），适用于：
 * <ul>
 *   <li>列表项快捷操作（无需进入详情页）</li>
 *   <li>危险操作确认（删除按钮红色，需滑动后才可见）</li>
 *   <li>节省操作栏空间（移动端横向空间有限）</li>
 * </ul>
 * </p>
 *
 * <h3>交互逻辑</h3>
 * <ol>
 *   <li>默认状态：translateX=0，操作按钮隐藏在内容右侧</li>
 *   <li>左滑：translateX 为负值，露出操作按钮</li>
 *   <li>松手：滑动距离 > 阈值则保持打开，否则回弹关闭</li>
 *   <li>点击操作按钮：触发 action 事件，自动关闭</li>
 *   <li>点击内容区：若当前已打开，先关闭；否则触发 click 事件</li>
 * </ol>
 */
import { ref, computed } from 'vue'

/** 操作按钮定义 */
export interface SwipeAction {
  key: string
  label: string
  type?: 'default' | 'primary' | 'danger' | 'warning'
  width?: number
}

interface Props {
  /** 操作按钮列表（从左到右排列） */
  actions: SwipeAction[]
  /** 自动打开阈值（px，默认 40） */
  openThreshold?: number
}

const props = withDefaults(defineProps<Props>(), {
  openThreshold: 40
})

const emit = defineEmits<{
  (e: 'action', actionKey: string): void
  (e: 'click'): void
}>()

const contentRef = ref<HTMLElement | null>(null)
const translateX = ref(0)
const touchStartX = ref(0)
const touchStartY = ref(0)
const currentTouchX = ref(0)
const isDragging = ref(false)
const isOpen = ref(false)
/** 操作按钮总宽度 */
const actionsWidth = computed(() =>
  props.actions.reduce((sum, a) => sum + (a.width || 72), 0)
)

/** 手势开始 */
function onTouchStart(e: TouchEvent) {
  touchStartX.value = e.touches[0].clientX
  touchStartY.value = e.touches[0].clientY
  currentTouchX.value = touchStartX.value
  isDragging.value = true
  document.addEventListener('touchmove', onTouchMove, { passive: false })
  document.addEventListener('touchend', onTouchEnd)
}

/** 手势移动 */
function onTouchMove(e: TouchEvent) {
  if (!isDragging.value) return
  const currentX = e.touches[0].clientX
  const currentY = e.touches[0].clientY
  const deltaX = currentX - touchStartX.value
  const deltaY = currentY - touchStartY.value
  // 垂直滑动时不处理（避免与列表滚动冲突）
  if (Math.abs(deltaY) > Math.abs(deltaX)) {
    return
  }
  currentTouchX.value = currentX
  // 基础位移 = 当前位移 + 已打开的偏移
  let base = isOpen.value ? -actionsWidth.value : 0
  let delta = deltaX + base
  // 限制范围：[-actionsWidth, 0]，向右滑不超过 0
  delta = Math.max(-actionsWidth.value, Math.min(0, delta))
  translateX.value = delta
  e.preventDefault()
}

/** 手势结束 */
function onTouchEnd() {
  isDragging.value = false
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
  const totalDelta = currentTouchX.value - touchStartX.value
  // 判断最终状态
  if (isOpen.value) {
    // 已打开：向右滑超过阈值则关闭
    if (totalDelta > props.openThreshold) {
      close()
    } else {
      open()
    }
  } else {
    // 已关闭：向左滑超过阈值则打开
    if (-totalDelta > props.openThreshold) {
      open()
    } else {
      close()
    }
  }
}

function open() {
  translateX.value = -actionsWidth.value
  isOpen.value = true
}

function close() {
  translateX.value = 0
  isOpen.value = false
}

/** 操作按钮点击 */
function onAction(action: SwipeAction) {
  emit('action', action.key)
  close()
}

/** 内容区点击：已打开则先关闭，否则触发 click */
function onContentClick() {
  if (isOpen.value) {
    close()
  } else {
    emit('click')
  }
}

defineExpose({ open, close, isOpen })
</script>

<style scoped>
.swipe-actions {
  position: relative;
  overflow: hidden;
  width: 100%;
}
.swipe-content {
  position: relative;
  z-index: 2;
  background: #fff;
  transition: transform 0.25s ease;
}
.swipe-actions-bar {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  display: flex;
  z-index: 1;
}
.swipe-action-btn {
  border: none;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  white-space: nowrap;
}
.action-default {
  background: #909399;
}
.action-primary {
  background: #409eff;
}
.action-danger {
  background: #f56c6c;
}
.action-warning {
  background: #e6a23c;
}
</style>
