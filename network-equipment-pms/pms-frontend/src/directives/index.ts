import type { App, Directive } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * v-debounce：防抖点击指令
 *
 * 用法：
 *   <el-button v-debounce="handleClick">保存</el-button>
 *   <el-button v-debounce="{ handler: handleClick, delay: 500 }">保存</el-button>
 *
 * 在指定 delay 内重复点击只触发最后一次，避免误触发重复提交。
 */
interface DebounceOptions {
  handler: (event: Event) => void
  delay?: number
}

/** 缓存每个元素上的防抖监听器，便于卸载时移除（避免在 HTMLElement 上扩展任意属性） */
const debounceListeners = new WeakMap<HTMLElement, (event: Event) => void>()

const debounce: Directive<HTMLElement, DebounceOptions | ((event: Event) => void)> = {
  mounted(el, binding) {
    const opts: DebounceOptions =
      typeof binding.value === 'function'
        ? { handler: binding.value }
        : binding.value
    const delay = opts.delay ?? 300
    const handler = opts.handler
    if (typeof handler !== 'function') return

    let timer: ReturnType<typeof setTimeout> | null = null
    const listener = (event: Event) => {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => handler(event), delay)
    }
    el.addEventListener('click', listener)
    // 缓存到 WeakMap 以便卸载时移除（避免污染 HTMLElement）
    debounceListeners.set(el, listener)
  },
  unmounted(el) {
    const listener = debounceListeners.get(el)
    if (listener) {
      el.removeEventListener('click', listener)
      debounceListeners.delete(el)
    }
  }
}

/**
 * v-permission：权限按钮控制指令
 *
 * 用法：
 *   <el-button v-permission="'system:user:add'">新增</el-button>
 *   <el-button v-permission="['system:user:add', 'system:user:edit']">操作</el-button>
 *
 * 当前用户不具备任一权限码时，元素将从 DOM 中移除。
 */
const permission: Directive<HTMLElement, string | string[] | undefined> = {
  mounted(el, binding) {
    if (!binding.value) return
    const codes: string[] = Array.isArray(binding.value)
      ? binding.value
      : [binding.value]
    if (!codes.length) return

    const store = useUserStore()
    const hasPermission = store.hasAnyPermission(codes)
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

/** 在应用上注册全部自定义指令 */
export function registerDirectives(app: App): void {
  app.directive('debounce', debounce)
  app.directive('permission', permission)
}
