<template>
  <transition :name="`drawer-${placement}`">
    <div v-if="modelValue" class="drawer-mask" @click.self="onMaskClick">
      <div
        ref="drawerRef"
        class="mobile-drawer"
        :class="[`drawer-${placement}`]"
        :style="drawerStyle"
      >
        <!-- 手势拖拽区（与滑出方向一致） -->
        <div
          v-if="draggable"
          class="drawer-grip"
          :class="`grip-${placement}`"
          @touchstart="onTouchStart"
        ></div>
        <!-- 标题栏 -->
        <div v-if="title" class="drawer-header">
          <span class="drawer-title">{{ title }}</span>
          <el-icon class="drawer-close" @click="close"><Close /></el-icon>
        </div>
        <!-- 内容区 -->
        <div class="drawer-content">
          <slot />
        </div>
        <!-- 底部操作区 -->
        <div v-if="$slots.footer" class="drawer-footer">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
/**
 * MobileDrawer — 移动端抽屉（批次4-T10）。
 *
 * <p>从屏幕侧边滑出的抽屉面板，支持 left/right 两种方向，可拖拽关闭。
 * 适用于：导航菜单、筛选面板、详情侧滑等场景。</p>
 *
 * <p>与 Element Plus el-drawer 的区别：
 * <ul>
 *   <li>针对移动端优化：手势拖拽关闭、全高度、紧凑间距</li>
 *   <li>更轻量：不依赖 el-drawer 的复杂动画与嵌套结构</li>
 *   <li>可与 BottomSheet 组合使用</li>
 * </ul>
 * </p>
 */
import { ref, computed, watch, onBeforeUnmount } from 'vue'
import { Close } from '@element-plus/icons-vue'

type DrawerPlacement = 'left' | 'right'

interface Props {
  modelValue: boolean
  title?: string
  placement?: DrawerPlacement
  draggable?: boolean
  closeThreshold?: number
  closeOnMask?: boolean
  /** 抽屉宽度（占视口宽度比例，0-1） */
  widthRatio?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  placement: 'right',
  draggable: true,
  closeThreshold: 80,
  closeOnMask: true,
  widthRatio: 0.85
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'close'): void
}>()

const drawerRef = ref<HTMLElement | null>(null)
const translateX = ref(0)
const touchStartX = ref(0)
const currentTouchX = ref(0)
const isDragging = ref(false)

const drawerStyle = computed(() => {
  const transform =
    props.placement === 'right'
      ? `translateX(${translateX.value}px)` // right: 向右拖拽（正值）
      : `translateX(${-translateX.value}px)` // left: 向左拖拽（负值）
  return {
    width: `${props.widthRatio * 100}vw`,
    transform
  }
})

function close() {
  emit('update:modelValue', false)
  emit('close')
  translateX.value = 0
}

function onMaskClick() {
  if (props.closeOnMask) close()
}

/** 手势开始 */
function onTouchStart(e: TouchEvent) {
  if (!props.draggable) return
  touchStartX.value = e.touches[0].clientX
  currentTouchX.value = touchStartX.value
  isDragging.value = true
  document.addEventListener('touchmove', onTouchMove, { passive: false })
  document.addEventListener('touchend', onTouchEnd)
}

/** 手势移动 */
function onTouchMove(e: TouchEvent) {
  if (!isDragging.value) return
  currentTouchX.value = e.touches[0].clientX
  const delta = currentTouchX.value - touchStartX.value
  // right 抽屉：向右拖拽关闭（delta > 0）；left 抽屉：向左拖拽关闭（delta < 0）
  if (props.placement === 'right') {
    translateX.value = Math.max(0, delta)
  } else {
    translateX.value = Math.max(0, -delta)
  }
  if (translateX.value > 0) e.preventDefault()
}

/** 手势结束 */
function onTouchEnd() {
  isDragging.value = false
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
  if (translateX.value > props.closeThreshold) {
    close()
  } else {
    translateX.value = 0
  }
}

watch(
  () => props.modelValue,
  (visible) => {
    document.body.style.overflow = visible ? 'hidden' : ''
  }
)

onBeforeUnmount(() => {
  document.body.style.overflow = ''
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
})
</script>

<style scoped>
.drawer-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
}
.mobile-drawer {
  position: absolute;
  top: 0;
  bottom: 0;
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}
.drawer-right {
  right: 0;
  border-radius: 16px 0 0 16px;
}
.drawer-left {
  left: 0;
  border-radius: 0 16px 16px 0;
}
.drawer-grip {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 20px;
  cursor: grab;
  background: transparent;
}
.grip-right {
  left: 0;
}
.grip-left {
  right: 0;
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.drawer-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.drawer-close {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
}
.drawer-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  -webkit-overflow-scrolling: touch;
}
.drawer-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}
/* 进入/离开动画 */
.drawer-right-enter-active,
.drawer-right-leave-active,
.drawer-left-enter-active,
.drawer-left-leave-active {
  transition: opacity 0.25s ease;
}
.drawer-right-enter-active .mobile-drawer,
.drawer-right-leave-active .mobile-drawer,
.drawer-left-enter-active .mobile-drawer,
.drawer-left-leave-active .mobile-drawer {
  transition: transform 0.3s ease;
}
.drawer-right-enter-from,
.drawer-right-leave-to,
.drawer-left-enter-from,
.drawer-left-leave-to {
  opacity: 0;
}
.drawer-right-enter-from .mobile-drawer,
.drawer-right-leave-to .mobile-drawer {
  transform: translateX(100%);
}
.drawer-left-enter-from .mobile-drawer,
.drawer-left-leave-to .mobile-drawer {
  transform: translateX(-100%);
}
</style>
