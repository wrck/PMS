<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorState }">
    <!-- 加载中遮罩 -->
    <div v-if="loading" class="sandbox-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>组件加载中...</span>
    </div>

    <!-- 错误态 -->
    <div v-else-if="errorState" class="sandbox-error-content">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ errorMessage }}</span>
    </div>

    <!-- URL 不在白名单 -->
    <div v-else-if="!isUrlAllowed" class="sandbox-blocked">
      <el-icon><Lock /></el-icon>
      <span>组件 URL 未通过安全校验，已被 CSP 白名单拦截</span>
      <code class="sandbox-blocked-url">{{ src }}</code>
    </div>

    <!-- iframe 沙箱容器 -->
    <iframe
      v-else
      ref="iframeRef"
      :src="computedSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      :title="title || componentName"
      frameborder="0"
      allow="clipboard-write 'self'"
      class="sandbox-iframe"
      @load="onIframeLoad"
      @error="onIframeError"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * ComponentSandbox — 组件 iframe 沙箱（批次4-T7 核心）。
 *
 * <p>本组件是低代码平台远程/自定义组件的安全隔离容器，借鉴 ToolJet 的 iframe 沙箱
 * 与 Power Apps PCF 的组件隔离机制，提供以下能力：</p>
 *
 * <h3>核心能力</h3>
 * <ol>
 *   <li><b>iframe 隔离</b>：通过 sandbox 属性限制 iframe 内可执行的操作（最小权限原则）</li>
 *   <li><b>CSP 白名单</b>：加载前校验 src URL 是否在允许的域名/协议范围内</li>
 *   <li><b>postMessage 通信</b>：与 iframe 内组件双向通信，传递 props/context/事件</li>
 *   <li><b>配置实时同步</b>：父组件 props/context 变化时自动通过 postMessage 推送到 iframe</li>
 *   <li><b>v-model 双向绑定</b>：iframe 内组件值变更通过 postMessage 上报，触发 update:modelValue</li>
 *   <li><b>自适应高度</b>：iframe 上报内容高度，父容器自动调整 iframe 高度</li>
 *   <li><b>事件透传</b>：iframe 内组件的 change/blur/focus 等事件透传到父组件</li>
 * </ol>
 *
 * <h3>使用示例</h3>
 * <pre>
 * &lt;ComponentSandbox
 *   v-model="formData.progress"
 *   src="https://cdn.example.com/lowcode-components/progress-indicator/index.html"
 *   :component-name="'ProgressIndicator'"
 *   :props="{ status: 'success', showLabel: true }"
 *   :context="{ entityCode: 'order', mode: 'EDIT' }"
 *   @change="onProgressChange"
 * /&gt;
 * </pre>
 *
 * <h3>iframe 内组件需实现的协议</h3>
 * <p>iframe 内的组件页面需监听 message 事件，按 {@link ./protocol.ts} 定义的协议响应：</p>
 * <pre>
 * // iframe 内组件示例
 * window.addEventListener('message', (event) => {
 *   if (event.origin !== 'https://your-app.com') return
 *   const msg = event.data
 *   if (msg.type === 'LC_SANDBOX_INIT') {
 *     // 接收初始 props + context，渲染组件
 *     renderComponent(msg.payload.props, msg.payload.context)
 *   } else if (msg.type === 'LC_SANDBOX_UPDATE_PROPS') {
 *     // props 变更，更新组件
 *     updateProps(msg.payload.props)
 *   }
 * })
 * // 加载完成通知父页面
 * window.parent.postMessage({ version: '1.0', type: 'LC_SANDBOX_READY', timestamp: Date.now(), payload: {} }, '*')
 * // 值变更上报
 * function onValueChange(newValue) {
 *   window.parent.postMessage({
 *     version: '1.0', type: 'LC_SANDBOX_UPDATE_VALUE', timestamp: Date.now(),
 *     payload: { value: newValue }
 *   }, '*')
 * }
 * </pre>
 */
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Loading, WarningFilled, Lock } from '@element-plus/icons-vue'
import { isAllowedUrl, buildSandboxAttribute } from '@/sdk/csp-allowlist'
import {
  HostToGuestMessage,
  GuestToHostMessage,
  isSandboxMessage,
  createMessage,
  type InitPayload,
  type UpdatePropsPayload,
  type UpdateContextPayload,
  type UpdateValuePayload,
  type EventPayload,
  type ReportHeightPayload,
  type ResizePayload,
  type ErrorPayload,
  type LogPayload
} from './protocol'

interface Props {
  /** iframe 加载的 URL（远程组件页面地址） */
  src: string
  /** 组件名（注册 key，传递给 iframe 用于初始化） */
  componentName: string
  /** iframe title 属性（无障碍） */
  title?: string
  /** 组件 props（响应式，变化时自动同步到 iframe） */
  props?: Record<string, unknown>
  /** 运行时上下文（LowCodeContext，变化时自动同步） */
  context?: Record<string, unknown>
  /** v-model 绑定值 */
  modelValue?: unknown
  /** iframe 固定高度（px，不设则自适应） */
  height?: number | string
  /** iframe 最小高度（px，自适应时下限） */
  minHeight?: number
  /** 是否允许同源访问（trusted 场景才开启，降低隔离强度） */
  sameOrigin?: boolean
  /** 是否允许表单提交 */
  allowForms?: boolean
  /** 是否允许弹窗 */
  allowPopups?: boolean
  /** 是否启用自适应高度（监听 iframe 上报的 REPORT_HEIGHT） */
  autoResize?: boolean
  /** 是否开启调试日志 */
  debug?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  props: () => ({}),
  context: () => ({}),
  modelValue: null,
  height: undefined,
  minHeight: 60,
  sameOrigin: false,
  allowForms: true,
  allowPopups: false,
  autoResize: true,
  debug: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: unknown): void
  (e: 'change', ...args: unknown[]): void
  (e: 'blur'): void
  (e: 'focus'): void
  (e: 'event', eventName: string, args: unknown[]): void
  (e: 'ready'): void
  (e: 'error', error: ErrorPayload): void
  (e: 'log', log: LogPayload): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const loading = ref(true)
const errorState = ref(false)
const errorMessage = ref('')
const iframeReady = ref(false)
const adaptiveHeight = ref<number | null>(null)

/** URL 安全校验 */
const isUrlAllowed = computed(() => isAllowedUrl(props.src))

/** 计算 iframe src（可附加初始 props 作为 URL hash 减少首屏闪烁） */
const computedSrc = computed(() => props.src)

/** sandbox 属性值（最小权限组合） */
const sandboxAttr = computed(() =>
  buildSandboxAttribute(props.sameOrigin, props.allowForms, props.allowPopups)
)

/** iframe 样式（高度自适应或固定） */
const iframeStyle = computed(() => {
  const style: Record<string, string> = { width: '100%', border: 'none' }
  if (props.height !== undefined) {
    style.height = typeof props.height === 'number' ? `${props.height}px` : props.height
  } else if (props.autoResize && adaptiveHeight.value !== null) {
    style.height = `${Math.max(adaptiveHeight.value, props.minHeight)}px`
  } else {
    style.height = `${props.minHeight}px`
  }
  return style
})

/** iframe 的 origin（用于 postMessage 的 targetOrigin 校验） */
const iframeOrigin = computed(() => {
  try {
    return new URL(props.src).origin
  } catch {
    return '*'
  }
})

/**
 * 向 iframe 发送消息（带 origin 校验，防止消息泄漏到其他域）。
 */
function postToGuest<T>(type: HostToGuestMessage, payload: T, id?: string): void {
  if (!iframeRef.value || !iframeRef.value.contentWindow) {
    if (props.debug) console.warn('[ComponentSandbox] iframe 未就绪，消息丢弃', type)
    return
  }
  const msg = createMessage(type, payload, id)
  // targetOrigin 限制为 iframe 的 origin，防止消息被其他域截获
  iframeRef.value.contentWindow.postMessage(msg, iframeOrigin.value)
  if (props.debug) console.debug('[ComponentSandbox] → guest', type, payload)
}

/**
 * iframe 加载完成回调。
 *
 * <p>注意：iframe load 事件触发不代表组件 READY（组件可能还在初始化），
 * 真正的 ready 信号来自 iframe 主动上报的 LC_SANDBOX_READY 消息。
 * 此处仅标记 loading 结束，等待 READY 消息后再发送 INIT。</p>
 */
function onIframeLoad() {
  loading.value = false
  if (props.debug) console.debug('[ComponentSandbox] iframe load 事件触发', props.src)
  // 不立即发 INIT，等待 iframe 主动 READY 后再发
}

function onIframeError(e: Event) {
  loading.value = false
  errorState.value = true
  errorMessage.value = 'iframe 加载失败'
  emit('error', { message: 'iframe load error', source: 'iframe' })
  if (props.debug) console.error('[ComponentSandbox] iframe error', e)
}

/**
 * 发送 INIT 消息（注入初始 props + context）。
 *
 * <p>仅在 iframe READY 后调用一次，后续 props/context 变更通过 UPDATE_PROPS/UPDATE_CONTEXT。</p>
 */
function sendInit() {
  const payload: InitPayload = {
    componentName: props.componentName,
    props: { ...props.props, modelValue: props.modelValue },
    context: { ...props.context }
  }
  postToGuest(HostToGuestMessage.INIT, payload)
}

/**
 * 处理来自 iframe 的消息。
 */
function onMessage(event: MessageEvent) {
  // origin 校验：仅接受 iframe 自身 origin 的消息
  if (event.origin !== iframeOrigin.value) {
    if (props.debug) {
      console.warn('[ComponentSandbox] 忽略来源不匹配的消息', event.origin, '!=', iframeOrigin.value)
    }
    return
  }
  if (!isSandboxMessage(event.data)) {
    return
  }
  const msg = event.data
  if (props.debug) console.debug('[ComponentSandbox] ← guest', msg.type, msg.payload)

  switch (msg.type) {
    case GuestToHostMessage.READY:
      iframeReady.value = true
      loading.value = false
      sendInit()
      emit('ready')
      break
    case GuestToHostMessage.UPDATE_VALUE: {
      const payload = msg.payload as UpdateValuePayload
      emit('update:modelValue', payload.value)
      break
    }
    case GuestToHostMessage.EVENT: {
      const payload = msg.payload as EventPayload
      // 标准事件透传
      if (payload.eventName === 'change') emit('change', ...(payload.args || []))
      else if (payload.eventName === 'blur') emit('blur')
      else if (payload.eventName === 'focus') emit('focus')
      // 通用事件透传
      emit('event', payload.eventName, payload.args || [])
      break
    }
    case GuestToHostMessage.REPORT_HEIGHT: {
      const payload = msg.payload as ReportHeightPayload
      if (props.autoResize && typeof payload.height === 'number' && payload.height > 0) {
        adaptiveHeight.value = payload.height
      }
      break
    }
    case GuestToHostMessage.ERROR: {
      const payload = msg.payload as ErrorPayload
      errorState.value = true
      errorMessage.value = payload.message
      emit('error', payload)
      break
    }
    case GuestToHostMessage.LOG: {
      const payload = msg.payload as LogPayload
      emit('log', payload)
      if (props.debug) {
        const logger = console[payload.level] || console.log
        logger('[ComponentSandbox guest]', ...payload.args)
      }
      break
    }
    default:
      if (props.debug) console.warn('[ComponentSandbox] 未知消息类型', msg.type)
  }
}

/** 监听 props 变化，自动同步到 iframe */
watch(
  () => props.props,
  (newProps) => {
    if (!iframeReady.value) return
    const payload: UpdatePropsPayload = {
      props: { ...newProps, modelValue: props.modelValue }
    }
    postToGuest(HostToGuestMessage.UPDATE_PROPS, payload)
  },
  { deep: true }
)

/** 监听 modelValue 变化，作为 props 的一部分同步 */
watch(
  () => props.modelValue,
  (newVal) => {
    if (!iframeReady.value) return
    const payload: UpdatePropsPayload = {
      props: { ...props.props, modelValue: newVal }
    }
    postToGuest(HostToGuestMessage.UPDATE_PROPS, payload)
  }
)

/** 监听 context 变化，自动同步到 iframe */
watch(
  () => props.context,
  (newCtx) => {
    if (!iframeReady.value) return
    const payload: UpdateContextPayload = { context: { ...newCtx } }
    postToGuest(HostToGuestMessage.UPDATE_CONTEXT, payload)
  },
  { deep: true }
)

/** 监听容器尺寸变化（ResizeObserver），通知 iframe */
let resizeObserver: ResizeObserver | null = null
function setupResizeObserver() {
  if (!iframeRef.value?.parentElement || !props.autoResize) return
  resizeObserver = new ResizeObserver((entries) => {
    if (!iframeReady.value) return
    for (const entry of entries) {
      const payload: ResizePayload = {
        width: entry.contentRect.width,
        height: entry.contentRect.height
      }
      postToGuest(HostToGuestMessage.RESIZE, payload)
    }
  })
  resizeObserver.observe(iframeRef.value.parentElement)
}

onMounted(() => {
  window.addEventListener('message', onMessage)
  nextTick(() => {
    setupResizeObserver()
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('message', onMessage)
  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }
})

/** 暴露给父组件的命令式 API */
defineExpose({
  /** 请求 iframe 上报高度 */
  requestHeight: () => postToGuest(HostToGuestMessage.REQUEST_HEIGHT, {}),
  /** 手动重新发送 INIT */
  reinit: () => sendInit(),
  /** 获取 iframe 元素 */
  getIframe: () => iframeRef.value
})
</script>

<style scoped>
.component-sandbox {
  position: relative;
  width: 100%;
  min-height: 60px;
}
.sandbox-iframe {
  display: block;
  width: 100%;
  border: none;
  background: transparent;
}
.sandbox-loading,
.sandbox-error-content,
.sandbox-blocked {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 80px;
  padding: 16px;
  color: #909399;
  gap: 8px;
}
.sandbox-error-content {
  color: #f56c6c;
}
.sandbox-blocked {
  color: #e6a23c;
  background: #fdf6ec;
  border: 1px dashed #e6a23c;
  border-radius: 4px;
}
.sandbox-blocked-url {
  font-size: 12px;
  color: #909399;
  word-break: break-all;
  max-width: 100%;
}
.sandbox-error {
  border: 1px dashed #f56c6c;
  border-radius: 4px;
}
</style>
