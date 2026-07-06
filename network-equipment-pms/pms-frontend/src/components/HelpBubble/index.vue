<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'

/**
 * 功能气泡组件：在指定元素旁显示帮助提示。
 *
 * <p>用法：
 * <pre>
 *   &lt;HelpBubble target=".save-btn" content="点击保存当前表单" placement="right" /&gt;
 * </pre>
 * </p>
 *
 * <p>target 为 CSS selector；组件挂载后查找目标元素，未找到则不显示。
 * 点击气泡外部或目标元素外部自动关闭。</p>
 */

type Placement = 'top' | 'bottom' | 'left' | 'right'

const props = withDefaults(
  defineProps<{
    /** 高亮目标元素 CSS selector */
    target: string
    /** 气泡内容 */
    content: string
    /** 气泡位置，默认 right */
    placement?: Placement
    /** 是否默认显示，默认 true。点击外部关闭后不再自动显示 */
    defaultVisible?: boolean
  }>(),
  {
    placement: 'right',
    defaultVisible: true
  }
)

const emit = defineEmits<{
  (e: 'close'): void
}>()

const visible = ref(props.defaultVisible)
const popoverStyle = ref<Record<string, string>>({})
const arrowStyle = ref<Record<string, string>>({})

const POPOVER_MAX_WIDTH = 280
const GAP = 10

async function updatePosition() {
  if (!visible.value || !props.target) {
    return
  }
  await nextTick()
  const el = document.querySelector(props.target) as HTMLElement | null
  if (!el) {
    visible.value = false
    return
  }
  const rect = el.getBoundingClientRect()
  const vw = window.innerWidth
  const vh = window.innerHeight

  let top = 0
  let left = 0
  let arrowTop = '50%'
  let arrowLeft = '0'

  switch (props.placement) {
    case 'right':
      top = rect.top + rect.height / 2
      left = rect.right + GAP
      arrowTop = `${rect.top + rect.height / 2 - top}px`
      arrowLeft = '-6px'
      // 右侧空间不足时翻转到左侧
      if (left + POPOVER_MAX_WIDTH > vw - 8) {
        left = Math.max(8, rect.left - POPOVER_MAX_WIDTH - GAP)
        arrowLeft = 'auto'
        arrowStyle.value = { right: '-6px', top: arrowTop }
      } else {
        arrowStyle.value = { left: arrowLeft, top: arrowTop }
      }
      top = Math.max(8, Math.min(top, vh - 80))
      break
    case 'left':
      top = rect.top + rect.height / 2
      left = Math.max(8, rect.left - POPOVER_MAX_WIDTH - GAP)
      arrowStyle.value = { right: '-6px', top: '50%' }
      break
    case 'bottom':
      left = rect.left + rect.width / 2 - POPOVER_MAX_WIDTH / 2
      top = rect.bottom + GAP
      arrowStyle.value = { top: '-6px', left: '50%' }
      break
    case 'top':
      left = rect.left + rect.width / 2 - POPOVER_MAX_WIDTH / 2
      top = Math.max(8, rect.top - 80 - GAP)
      arrowStyle.value = { bottom: '-6px', left: '50%' }
      break
  }

  left = Math.max(8, Math.min(left, vw - POPOVER_MAX_WIDTH - 8))
  top = Math.max(8, Math.min(top, vh - 80))

  popoverStyle.value = {
    top: `${top}px`,
    left: `${left}px`,
    maxWidth: `${POPOVER_MAX_WIDTH}px`
  }
}

function handleClickOutside(e: MouseEvent) {
  if (!visible.value) return
  const target = e.target as HTMLElement
  // 点击目标元素或气泡内部时不关闭
  const popoverEl = document.querySelector('.help-bubble__popover')
  const targetEl = document.querySelector(props.target)
  if (popoverEl && popoverEl.contains(target)) return
  if (targetEl && targetEl.contains(target)) return
  visible.value = false
  emit('close')
}

function close() {
  visible.value = false
  emit('close')
}

watch(
  () => [props.target, props.content, props.placement],
  () => updatePosition(),
  { immediate: true }
)

watch(visible, async (v) => {
  if (v) {
    await nextTick()
    updatePosition()
  }
})

if (typeof window !== 'undefined') {
  window.addEventListener('click', handleClickOutside, true)
  window.addEventListener('resize', updatePosition)
  window.addEventListener('scroll', updatePosition, true)
}

onBeforeUnmount(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('click', handleClickOutside, true)
    window.removeEventListener('resize', updatePosition)
    window.removeEventListener('scroll', updatePosition, true)
  }
})

defineExpose({
  show() {
    visible.value = true
    updatePosition()
  },
  hide() {
    visible.value = false
  }
})
</script>

<template>
  <Teleport to="body">
    <div v-if="visible" class="help-bubble__popover" :style="popoverStyle">
      <div class="help-bubble__arrow" :style="arrowStyle" />
      <div class="help-bubble__content">
        {{ content }}
      </div>
      <button class="help-bubble__close" type="button" @click="close">×</button>
    </div>
  </Teleport>
</template>

<style scoped>
.help-bubble__popover {
  position: fixed;
  z-index: 2500;
  background-color: #303133;
  color: #fff;
  padding: 8px 24px 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  max-width: 280px;
}

.help-bubble__arrow {
  position: absolute;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 6px;
  border-color: transparent;
}

.help-bubble__content {
  white-space: pre-line;
}

.help-bubble__close {
  position: absolute;
  top: 2px;
  right: 4px;
  border: none;
  background: transparent;
  color: #c0c4cc;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 0 4px;
}

.help-bubble__close:hover {
  color: #fff;
}
</style>
