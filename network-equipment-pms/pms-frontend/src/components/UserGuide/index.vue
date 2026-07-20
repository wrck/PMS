<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useFirstLogin } from '@/composables/useFirstLogin'

/**
 * 用户引导组件（5 步）。
 *
 * <p>纯 Vue + Element Plus 实现，不依赖 driver.js。步骤定义：
 * <ol>
 *   <li>欢迎卡片（居中展示，无目标元素）</li>
 *   <li>项目管理（高亮侧边栏「项目管理」菜单）</li>
 *   <li>资产管理（高亮侧边栏「资产管理」菜单）</li>
 *   <li>任务实施（高亮侧边栏「实施管理」菜单）</li>
 *   <li>仪表盘（高亮侧边栏「首页」菜单）</li>
 * </ol>
 * </p>
 *
 * <p>高亮实现：通过 box-shadow 在 spotlight 层上「挖洞」突出目标元素，
 * 目标不可见时（如侧边栏折叠 / 移动端）自动回退为居中卡片。</p>
 *
 * <p>完成后调用 {@link useFirstLogin#markCompleted} 标记已读，
 * 下次登录不再自动触发。</p>
 */

interface GuideStep {
  /** 步骤标题 */
  title: string
  /** 步骤描述（支持换行） */
  description: string
  /** 高亮目标元素 CSS selector；为空表示居中卡片（无目标） */
  selector?: string
  /** 气泡位置：auto 时自动选择屏幕内可见位置 */
  placement: 'top' | 'bottom' | 'left' | 'right' | 'center'
}

const STEPS: GuideStep[] = [
  {
    title: '欢迎使用网络设备工程项目管理系统',
    description:
      '本系统覆盖项目交付、资产管理、实施任务、质保治理等全生命周期。\n\n接下来用 30 秒带您熟悉核心模块，可随时点击「跳过」结束引导。',
    placement: 'center'
  },
  {
    title: '项目管理',
    description:
      '在「项目管理」中创建并跟踪项目全生命周期：项目列表、交付看板、变更管理、风险登记册、问题日志等。',
    selector: 'a[href="/project/list"], .el-menu-item[index="/project/list"]',
    placement: 'right'
  },
  {
    title: '资产管理',
    description:
      '在「资产管理」中维护设备分类、型号、资产清单，跟踪在库 / 调拨 / 报废状态，并与项目、实施任务关联。',
    selector: 'a[href="/asset/list"], .el-menu-item[index="/asset/list"]',
    placement: 'right'
  },
  {
    title: '任务实施',
    description:
      '在「实施管理」中派发施工任务、管理服务商、结算费用，全流程在线协同与进度跟踪。',
    selector: 'a[href="/implementation/task"], .el-menu-item[index="/implementation/task"]',
    placement: 'right'
  },
  {
    title: '仪表盘',
    description:
      '回到「首页」仪表盘，实时查看项目交付、资产状态、待办任务、近期动态等关键指标。\n\n引导到此结束，祝您使用愉快！',
    selector: 'a[href="/dashboard"], .el-menu-item[index="/dashboard"]',
    placement: 'right'
  }
]

const props = defineProps<{
  /** 是否强制显示（用于调试或手动触发，绕过首次登录检测） */
  visible?: boolean
}>()

const emit = defineEmits<{
  (e: 'finish'): void
  (e: 'skip'): void
}>()

const { isFirstLogin, markCompleted } = useFirstLogin()
const route = useRoute() as ReturnType<typeof useRoute> | undefined
const routePath = computed(() => route?.path ?? '/dashboard')

/** 内部可见状态：首次登录（仅 dashboard 页）或 props.visible 为 true 时显示 */
const internalVisible = ref(
  props.visible === true || (isFirstLogin.value && routePath.value === '/dashboard')
)

/** 当前步骤索引 */
const currentStep = ref(0)

/** 当前步骤定义 */
const current = computed<GuideStep>(() => STEPS[currentStep.value] ?? STEPS[0])

/** 是否最后一步 */
const isLast = computed(() => currentStep.value === STEPS.length - 1)

/** 目标元素的 bounding rect（找不到时为 null，回退到居中卡片） */
const targetRect = ref<DOMRect | null>(null)

/** 气泡位置（屏幕坐标，px） */
const popoverStyle = ref<Record<string, string>>({})

/** 滚动事件处理器引用（用于解绑） */
let scrollHandler: (() => void) | null = null

/**
 * 计算目标元素位置并定位气泡。
 * 在 nextTick 后执行，确保 DOM 已更新。
 */
async function updatePosition() {
  if (!internalVisible.value) return
  const step = current.value
  if (step.placement === 'center' || !step.selector) {
    targetRect.value = null
    popoverStyle.value = {}
    return
  }

  // querySelector 可能匹配多个元素，取第一个可见的
  const candidates = document.querySelectorAll(step.selector)
  let el: HTMLElement | null = null
  for (const node of Array.from(candidates)) {
    if (!el) {
      const rect = (node as HTMLElement).getBoundingClientRect()
      // 选取可见（宽高 > 0）的元素
      if (rect.width > 0 && rect.height > 0) {
        el = node as HTMLElement
      }
    }
  }

  if (!el) {
    // 找不到目标元素，回退到居中
    targetRect.value = null
    popoverStyle.value = {}
    return
  }

  const rect = el.getBoundingClientRect()
  targetRect.value = rect

  // 计算气泡位置（基于目标元素的 placement）
  const POPOVER_WIDTH = 360
  const POPOVER_MAX_HEIGHT = 280
  const GAP = 16
  const vw = window.innerWidth
  const vh = window.innerHeight

  let top = 0
  let left = 0

  switch (step.placement) {
    case 'right':
      top = rect.top + rect.height / 2 - POPOVER_MAX_HEIGHT / 2
      left = rect.right + GAP
      // 屏幕右侧空间不足时翻转到左侧
      if (left + POPOVER_WIDTH > vw - 8) {
        left = Math.max(8, rect.left - POPOVER_WIDTH - GAP)
      }
      break
    case 'left':
      top = rect.top + rect.height / 2 - POPOVER_MAX_HEIGHT / 2
      left = rect.left - POPOVER_WIDTH - GAP
      if (left < 8) {
        left = Math.min(vw - POPOVER_WIDTH - 8, rect.right + GAP)
      }
      break
    case 'bottom':
      left = rect.left + rect.width / 2 - POPOVER_WIDTH / 2
      top = rect.bottom + GAP
      if (top + POPOVER_MAX_HEIGHT > vh - 8) {
        top = Math.max(8, rect.top - POPOVER_MAX_HEIGHT - GAP)
      }
      break
    case 'top':
      left = rect.left + rect.width / 2 - POPOVER_WIDTH / 2
      top = rect.top - POPOVER_MAX_HEIGHT - GAP
      if (top < 8) {
        top = Math.min(vh - POPOVER_MAX_HEIGHT - 8, rect.bottom + GAP)
      }
      break
    default:
      top = (vh - POPOVER_MAX_HEIGHT) / 2
      left = (vw - POPOVER_WIDTH) / 2
  }

  // 边界 clamp
  top = Math.max(8, Math.min(top, vh - POPOVER_MAX_HEIGHT - 8))
  left = Math.max(8, Math.min(left, vw - POPOVER_WIDTH - 8))

  popoverStyle.value = {
    top: `${top}px`,
    left: `${left}px`,
    width: `${POPOVER_WIDTH}px`,
    maxHeight: `${POPOVER_MAX_HEIGHT}px`
  }
}

/**
 * spotlight overlay 的样式：使用 box-shadow 在屏幕上「挖洞」突出目标元素。
 * 当 targetRect 为 null（居中卡片）时，使用半透明遮罩。
 */
const overlayStyle = computed<Record<string, string>>(() => {
  if (!targetRect.value) {
    return { boxShadow: 'none' } as Record<string, string>
  }
  const r = targetRect.value
  const padding = 6
  // 通过巨大的 box-shadow 覆盖屏幕其余区域，中间留出透明洞
  const shadow = `0 0 0 9999px rgba(0, 0, 0, 0.55)`
  return {
    position: 'fixed',
    top: `${r.top - padding}px`,
    left: `${r.left - padding}px`,
    width: `${r.width + padding * 2}px`,
    height: `${r.height + padding * 2}px`,
    boxShadow: shadow,
    borderRadius: '6px',
    transition: 'all 0.25s ease',
    pointerEvents: 'none'
  }
})

/** 跳过引导 */
function skip() {
  internalVisible.value = false
  markCompleted()
  emit('skip')
  cleanupListeners()
}

/** 下一步 / 完成 */
async function next() {
  if (isLast.value) {
    internalVisible.value = false
    markCompleted()
    emit('finish')
    cleanupListeners()
    return
  }
  currentStep.value++
  await nextTick()
  updatePosition()
}

/** 上一步 */
async function prev() {
  if (currentStep.value === 0) return
  currentStep.value--
  await nextTick()
  updatePosition()
}

function bindScrollListener() {
  if (scrollHandler) return
  scrollHandler = () => updatePosition()
  window.addEventListener('scroll', scrollHandler, true)
  window.addEventListener('resize', scrollHandler)
}

function cleanupListeners() {
  if (scrollHandler) {
    window.removeEventListener('scroll', scrollHandler, true)
    window.removeEventListener('resize', scrollHandler)
    scrollHandler = null
  }
}

// 监听 visible 变化
watch(
  () => props.visible,
  (v) => {
    if (v && !internalVisible.value) {
      internalVisible.value = true
      currentStep.value = 0
    }
  }
)

watch(
  routePath,
  (path) => {
    if (path === '/dashboard' && isFirstLogin.value && !internalVisible.value) {
      internalVisible.value = true
      currentStep.value = 0
    } else if (path !== '/dashboard' && internalVisible.value) {
      internalVisible.value = false
      cleanupListeners()
    }
  }
)

// 监听内部可见状态：显示时绑定滚动监听并定位，隐藏时清理
watch(
  internalVisible,
  async (v) => {
    if (v) {
      bindScrollListener()
      await nextTick()
      updatePosition()
    } else {
      cleanupListeners()
    }
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  cleanupListeners()
})

defineExpose({
  /** 外部手动启动引导 */
  start() {
    currentStep.value = 0
    internalVisible.value = true
  }
})
</script>

<template>
  <Teleport to="body">
    <div v-if="internalVisible" class="user-guide">
      <!-- 全屏遮罩层（点击空白不关闭，避免误触；通过按钮操作） -->
      <div class="guide-mask" />

      <!-- spotlight 高亮层：通过 box-shadow 挖洞突出目标元素 -->
      <div v-if="targetRect" :style="overlayStyle" class="guide-spotlight" />

      <!-- 引导气泡 -->
      <div
        v-if="current.placement === 'center' || !targetRect"
        class="guide-popover guide-popover--center"
      >
        <div class="guide-popover__header">
          <span class="guide-popover__title">{{ current.title }}</span>
          <button class="guide-popover__close" type="button" @click="skip">×</button>
        </div>
        <div class="guide-popover__body">
          <p class="guide-popover__desc">{{ current.description }}</p>
        </div>
        <div class="guide-popover__footer">
          <span class="guide-popover__step">{{ currentStep + 1 }} / {{ STEPS.length }}</span>
          <div class="guide-popover__actions">
            <el-button size="small" link @click="skip">跳过</el-button>
            <el-button v-if="currentStep > 0" size="small" @click="prev">上一步</el-button>
            <el-button size="small" type="primary" @click="next">
              {{ isLast ? '完成' : '下一步' }}
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="guide-popover guide-popover--anchored" :style="popoverStyle">
        <div class="guide-popover__header">
          <span class="guide-popover__title">{{ current.title }}</span>
          <button class="guide-popover__close" type="button" @click="skip">×</button>
        </div>
        <div class="guide-popover__body">
          <p class="guide-popover__desc">{{ current.description }}</p>
        </div>
        <div class="guide-popover__footer">
          <span class="guide-popover__step">{{ currentStep + 1 }} / {{ STEPS.length }}</span>
          <div class="guide-popover__actions">
            <el-button size="small" link @click="skip">跳过</el-button>
            <el-button v-if="currentStep > 0" size="small" @click="prev">上一步</el-button>
            <el-button size="small" type="primary" @click="next">
              {{ isLast ? '完成' : '下一步' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.user-guide {
  position: fixed;
  inset: 0;
  z-index: 3000;
}

.guide-mask {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.45);
  pointer-events: none;
}

.guide-spotlight {
  z-index: 3001;
  border: 2px solid #409eff;
}

.guide-popover {
  position: fixed;
  z-index: 3002;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
  overflow: hidden;
  font-size: 14px;
}

.guide-popover--center {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 460px;
  max-width: 92vw;
}

.guide-popover--anchored {
  /* width / top / left 由 popoverStyle 动态注入 */
}

.guide-popover__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px 8px;
  background-color: #ecf5ff;
  border-bottom: 1px solid #d9ecff;
}

.guide-popover__title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2d3d;
}

.guide-popover__close {
  border: none;
  background: transparent;
  font-size: 22px;
  line-height: 1;
  color: #909399;
  cursor: pointer;
  padding: 0 4px;
}

.guide-popover__close:hover {
  color: #f56c6c;
}

.guide-popover__body {
  padding: 14px 16px;
}

.guide-popover__desc {
  margin: 0;
  white-space: pre-line;
  line-height: 1.7;
  color: #606266;
}

.guide-popover__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px 14px;
}

.guide-popover__step {
  font-size: 12px;
  color: #909399;
}

.guide-popover__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
