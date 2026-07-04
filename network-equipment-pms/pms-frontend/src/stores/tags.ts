import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 标签视图数据结构 */
export interface View {
  path: string
  title: string
  name?: string
  /** 路由完整路径（含参数），用于激活高亮 */
  fullPath?: string
}

/**
 * 标签页 store
 * - 管理已访问过的路由标签列表
 * - 标签持久化到 localStorage，刷新页面可恢复
 */
export const useTagsStore = defineStore('tags', () => {
  const TAGS_KEY = 'pms_tags_view'

  /** 已访问视图列表（从 localStorage 恢复） */
  const visitedViews = ref<View[]>(
    safeParse(localStorage.getItem(TAGS_KEY) || '[]')
  )

  /** 安全解析 JSON，失败时返回空数组 */
  function safeParse(raw: string): View[] {
    try {
      const arr = JSON.parse(raw)
      return Array.isArray(arr) ? arr : []
    } catch {
      return []
    }
  }

  /** 持久化到 localStorage */
  function persist() {
    localStorage.setItem(TAGS_KEY, JSON.stringify(visitedViews.value))
  }

  /** 添加视图（已存在则跳过） */
  function addView(view: View) {
    if (visitedViews.value.some((v) => v.path === view.path)) {
      // 路径已存在时仅更新 fullPath（参数可能变化）
      const target = visitedViews.value.find((v) => v.path === view.path)
      if (target) target.fullPath = view.fullPath || view.path
      return
    }
    visitedViews.value.push({ ...view, fullPath: view.fullPath || view.path })
    persist()
  }

  /** 删除指定路径的视图 */
  function delView(path: string) {
    const idx = visitedViews.value.findIndex((v) => v.path === path)
    if (idx !== -1) {
      visitedViews.value.splice(idx, 1)
      persist()
    }
  }

  /** 关闭其他视图，仅保留指定路径 */
  function delOthers(path: string) {
    visitedViews.value = visitedViews.value.filter((v) => v.path === path)
    persist()
  }

  /** 关闭全部视图 */
  function delAll() {
    visitedViews.value = []
    persist()
  }

  return { visitedViews, addView, delView, delOthers, delAll }
})
