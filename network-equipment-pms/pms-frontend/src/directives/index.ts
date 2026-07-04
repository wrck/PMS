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
    // 缓存到元素上以便卸载时移除
    ;(el as any).__debounceListener__ = listener
  },
  unmounted(el) {
    const listener = (el as any).__debounceListener__
    if (typeof listener === 'function') {
      el.removeEventListener('click', listener)
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
const permission: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    const codes: string[] = Array.isArray(binding.value)
      ? binding.value
      : [binding.value]
    if (!codes.length) return // 未传权限码时不处理

    const store = useUserStore()
    const hasPermission = codes.some((code) => store.permissions.includes(code))
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
