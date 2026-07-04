import { ref } from 'vue'

/**
 * 全局路由切换 loading 状态服务。
 *
 * 设计说明：
 * - Element Plus 已提供 `v-loading` 指令用于局部加载，此处不复用，
 *   而是提供一个轻量的"路由切换顶部进度条"状态，由路由守卫触发。
 * - `startRouteLoading` 带 200ms 延迟，避免快速路由切换时的闪烁。
 * - DefaultLayout 顶部渲染一个进度条绑定到 `routeLoading`。
 */
export const routeLoading = ref(false)

let startTimer: ReturnType<typeof setTimeout> | null = null

/** 开始路由加载（延迟 200ms 显示，避免快速切换闪烁） */
export function startRouteLoading(): void {
  if (startTimer) clearTimeout(startTimer)
  startTimer = setTimeout(() => {
    routeLoading.value = true
  }, 200)
}

/** 结束路由加载，立即隐藏进度条 */
export function stopRouteLoading(): void {
  if (startTimer) {
    clearTimeout(startTimer)
    startTimer = null
  }
  routeLoading.value = false
}
