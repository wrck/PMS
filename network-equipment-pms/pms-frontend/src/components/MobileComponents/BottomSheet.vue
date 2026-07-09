<template>
  <transition name="bottom-sheet">
    <div v-if="modelValue" class="bottom-sheet-mask" @click.self="onMaskClick">
      <div
        ref="sheetRef"
        class="bottom-sheet"
        :style="{ transform: `translateY(${translateY}px)` }"
      >
        <!-- 拖拽手柄 -->
        <div v-if="draggable" class="sheet-handle" @touchstart="onTouchStart">
          <span class="handle-bar"></span>
        </div>
        <!-- 标题栏 -->
        <div v-if="title" class="sheet-header">
          <span class="sheet-title">{{ title }}</span>
          <el-icon class="sheet-close" @click="close"><Close /></el-icon>
        </div>
        <!-- 内容区 -->
        <div class="sheet-content" :style="{ maxHeight: contentMaxHeight }">
          <slot />
        </div>
        <!-- 底部操作区 -->
        <div v-if="$slots.footer" class="sheet-footer">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
/**
 * BottomSheet — 移动端底部弹出层（批次4-T10）。
 *
 * <p>借鉴 Material Design Bottom Sheet 与 iOS Action Sheet 的设计，
 * 从屏幕底部滑出的面板，适用于：
 * <ul>
 *   <li>选项菜单（分享/操作）</li>
 *   <li>表单输入（小范围表单，避免全屏跳转）</li>
 *   <li>详情查看（点击列表卡片弹出详情）</li>
 *   <li>筛选面板（移动端筛选从底部滑出）</li>
 * </ul>
 * </p>
 *
 * <h3>手势交互</h3>
 * <p>支持向下拖拽关闭：拖拽超过阈值（默认 100px）自动关闭，否则回弹。
 * 手势通过 touchstart/touchmove/touchend 实现，仅在 draggable=true 时启用。</p>
 */
import { ref, computed, watch, onBeforeUnmount } from 'vue'
import { Close } from '@element-plus/icons-vue'

interface Props {
  /** v-model 控制显隐 */
  modelValue: boolean
  /** 标题 */
  title?: string
  /** 是否可拖拽关闭 */
  draggable?: boolean
  /** 拖拽关闭阈值（px） */
  closeThreshold?: number
  /** 点击遮罩是否关闭 */
  closeOnMask?: boolean
  /** 内容区最大高度（占视口高度比例，0-1） */
  maxHeightRatio?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  draggable: true,
  closeThreshold: 100,
  closeOnMask: true,
  maxHeightRatio: 0.7
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'close'): void
}>()

const sheetRef = ref<HTMLElement | null>(null)
const translateY = ref(0)
const touchStartY = ref(0)
const currentTouchY = ref(0)
const isDragging = ref(false)

const contentMaxHeight = computed(() => `${props.maxHeightRatio * 100}vh`)

function close() {
  emit('update:modelValue', false)
  emit('close')
  translateY.value = 0
}

function onMaskClick() {
  if (props.closeOnMask) close()
}

/** 手势开始 */
function onTouchStart(e: TouchEvent) {
  if (!props.draggable) return
  touchStartY.value = e.touches[0].clientY
  currentTouchY.value = touchStartY.value
  isDragging.value = true
  // 监听后续 move/end
  document.addEventListener('touchmove', onTouchMove, { passive: false })
  document.addEventListener('touchend', onTouchEnd)
}

/** 手势移动 */
function onTouchMove(e: TouchEvent) {
  if (!isDragging.value) return
  currentTouchY.value = e.touches[0].clientY
  const delta = currentTouchY.value - touchStartY.value
  // 仅向下拖拽产生位移（向上拖拽不移动）
  translateY.value = Math.max(0, delta)
  // 阻止默认滚动（仅在手柄区域）
  if (translateY.value > 0) e.preventDefault()
}

/** 手势结束 */
function onTouchEnd() {
  isDragging.value = false
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
  // 超过阈值则关闭，否则回弹
  if (translateY.value > props.closeThreshold) {
    close()
  } else {
    translateY.value = 0
  }
}

/** 监听显隐，控制 body 滚动锁定 */
watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  }
)

onBeforeUnmount(() => {
  document.body.style.overflow = ''
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
})
</script>

<style scoped>
.bottom-sheet-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.bottom-sheet {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  transition: transform 0.3s ease;
}
.sheet-handle {
  padding: 12px 0 4px;
  display: flex;
  justify-content: center;
  cursor: grab;
}
.handle-bar {
  width: 36px;
  height: 4px;
  background: #dcdfe6;
  border-radius: 2px;
}
.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.sheet-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.sheet-close {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
}
.sheet-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  -webkit-overflow-scrolling: touch;
}
.sheet-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
}
/* 进入/离开动画 */
.bottom-sheet-enter-active,
.bottom-sheet-leave-active {
  transition: opacity 0.25s ease;
}
.bottom-sheet-enter-active .bottom-sheet,
.bottom-sheet-leave-active .bottom-sheet {
  transition: transform 0.3s ease;
}
.bottom-sheet-enter-from,
.bottom-sheet-leave-to {
  opacity: 0;
}
.bottom-sheet-enter-from .bottom-sheet,
.bottom-sheet-leave-to .bottom-sheet {
  transform: translateY(100%);
}
</style>
