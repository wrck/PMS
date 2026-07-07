import { computed, shallowRef } from 'vue'
import type { ComputedRef, Ref } from 'vue'

/**
 * 通用撤销/重做 composable（借鉴 Power Apps Studio 50 步栈）。
 *
 * <p>维护三段式历史：past（已操作栈）、present（当前值）、future（重做栈）。
 * 每次调用 {@link set} 会把当前 present 压入 past（超过 maxHistory 时丢弃最旧），
 * 并清空 future；{@link undo} / {@link redo} 在 past/future 之间移动 present。</p>
 *
 * <p>使用 shallowRef 保证泛型 T 的类型签名精确为 Ref&lt;T&gt;（避免深层解包），
 * 内部以不可变方式重新赋值数组以触发响应式更新。</p>
 *
 * @param initial 初始 present 值
 * @param maxHistory 历史栈最大长度，默认 50
 */
export function useUndoRedo<T>(initial: T, maxHistory = 50) {
  const past = shallowRef<T[]>([]) as Ref<T[]>
  const present = shallowRef<T>(initial) as Ref<T>
  const future = shallowRef<T[]>([]) as Ref<T[]>

  /** 设置新值：当前 present 压入 past，超限则丢弃最旧，清空 future */
  function set(newValue: T) {
    const nextPast = past.value.length >= maxHistory
      ? past.value.slice(1)
      : past.value.slice()
    past.value = [...nextPast, present.value]
    present.value = newValue
    future.value = []
  }

  /** 撤销：past 非空时把 present 压入 future 头部，取 past 末尾为新 present */
  function undo() {
    if (past.value.length === 0) return
    const previous = past.value[past.value.length - 1]
    future.value = [present.value, ...future.value]
    past.value = past.value.slice(0, -1)
    present.value = previous
  }

  /** 重做：future 非空时把 present 压入 past 末尾，取 future 头部为新 present */
  function redo() {
    if (future.value.length === 0) return
    const next = future.value[0]
    past.value = [...past.value, present.value]
    future.value = future.value.slice(1)
    present.value = next
  }

  /** 重置：清空 past/future 并设置 present */
  function reset(newValue: T) {
    past.value = []
    future.value = []
    present.value = newValue
  }

  const canUndo: ComputedRef<boolean> = computed(() => past.value.length > 0)
  const canRedo: ComputedRef<boolean> = computed(() => future.value.length > 0)
  const historySize: ComputedRef<number> = computed(
    () => past.value.length + future.value.length
  )

  return { present, set, undo, redo, reset, canUndo, canRedo, historySize }
}
