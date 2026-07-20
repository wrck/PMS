<script setup lang="ts">
import { ref, watch, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsStore, type View } from '@/stores/tags'
import { RefreshRight, Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()

/** 标签栏横向滚动容器 */
const scrollbarRef = ref<{ wrapRef?: HTMLElement } | null>(null)

/** 右键菜单可见性 */
const contextMenuVisible = ref(false)
/** 右键菜单位置 */
const contextMenuTop = ref(0)
const contextMenuLeft = ref(0)
/** 当前右键选中的标签路径 */
const contextMenuPath = ref('')

/** 右键菜单宽度（用于边界翻转计算） */
const CONTEXT_MENU_WIDTH = 140
const CONTEXT_MENU_HEIGHT = 200

/** 当前激活的标签路径（基于 route.path） */
const activePath = computed(() => route.path)

/** 判断标签是否可关闭（固定标签不可关闭） */
function isClosable(view: View): boolean {
  if (view.affix) return false
  return true
}

/** 路由变化时自动添加标签 */
function addCurrentView() {
  const title = (route.meta?.title as string | undefined) || '未命名'
  if (route.path === '/' || route.meta?.hidden) return
  tagsStore.addView({
    path: route.path,
    title,
    name: route.name as string | undefined,
    fullPath: route.fullPath
  })
  // 滚动到当前标签
  nextTick(() => scrollToActiveTag())
}

/** 点击标签跳转 */
function handleClick(view: View) {
  if (view.path === activePath.value) return
  router.push(view.fullPath || view.path)
}

/** 双击标签刷新当前 */
function handleDblClick(view: View) {
  refreshTarget(view.path)
}

/** 关闭标签 */
function handleClose(view: View, event?: MouseEvent) {
  event?.stopPropagation()
  if (view.affix) return
  const wasActive = view.path === activePath.value
  tagsStore.delView(view.path)
  // 关闭的是当前激活标签，需要跳到最后一项
  if (wasActive) {
    const last = tagsStore.visitedViews[tagsStore.visitedViews.length - 1]
    router.push(last ? last.fullPath || last.path : '/dashboard')
  }
}

/** 刷新指定路径的标签 */
function refreshTarget(path: string) {
  const target = tagsStore.visitedViews.find((v) => v.path === path)
  if (!target) return
  // 跳到 /redirect 中转路由再回来，触发组件重新创建
  router.replace('/redirect' + (target.fullPath || target.path)).catch(() => {
    // 没有中转路由时直接 reload
    window.location.reload()
  })
}

/** 刷新当前标签（顶部刷新按钮） */
function refreshCurrent() {
  refreshTarget(contextMenuPath.value || activePath.value)
  hideContextMenu()
}

/** 关闭其他 */
function closeOthers() {
  const path = contextMenuPath.value || activePath.value
  tagsStore.delOthers(path)
  const current = tagsStore.visitedViews.find((v) => v.path === path)
  if (current && path !== activePath.value) {
    router.push(current.fullPath || current.path)
  }
  hideContextMenu()
}

/** 关闭左侧 */
function closeLeft() {
  const path = contextMenuPath.value || activePath.value
  tagsStore.delLeft(path)
  // 如果当前激活标签被关闭，跳到右键选中的标签
  const current = tagsStore.visitedViews.find((v) => v.path === activePath.value)
  if (!current) {
    const target = tagsStore.visitedViews.find((v) => v.path === path)
    if (target) router.push(target.fullPath || target.path)
  }
  hideContextMenu()
}

/** 关闭右侧 */
function closeRight() {
  const path = contextMenuPath.value || activePath.value
  tagsStore.delRight(path)
  // 如果当前激活标签被关闭，跳到右键选中的标签
  const current = tagsStore.visitedViews.find((v) => v.path === activePath.value)
  if (!current) {
    const target = tagsStore.visitedViews.find((v) => v.path === path)
    if (target) router.push(target.fullPath || target.path)
  }
  hideContextMenu()
}

/** 关闭所有（保留固定标签） */
function closeAll() {
  tagsStore.delAll()
  const first = tagsStore.visitedViews[0]
  router.push(first ? first.fullPath || first.path : '/dashboard')
  hideContextMenu()
}

/** 右键打开上下文菜单（带边界翻转） */
function openContextMenu(event: MouseEvent, view: View) {
  event.preventDefault()
  contextMenuPath.value = view.path
  // 边界翻转：靠右/靠下时向左/向上展开
  const winW = window.innerWidth
  const winH = window.innerHeight
  let left = event.clientX
  let top = event.clientY
  if (left + CONTEXT_MENU_WIDTH > winW - 8) {
    left = winW - CONTEXT_MENU_WIDTH - 8
  }
  if (top + CONTEXT_MENU_HEIGHT > winH - 8) {
    top = winH - CONTEXT_MENU_HEIGHT - 8
  }
  contextMenuTop.value = top
  contextMenuLeft.value = left
  contextMenuVisible.value = true
}

/** 隐藏右键菜单 */
function hideContextMenu() {
  contextMenuVisible.value = false
}

/** 滚动到当前激活的标签（精确滚到可视区） */
function scrollToActiveTag() {
  const wrap = scrollbarRef.value?.wrapRef
  if (!wrap) return
  // 找到当前激活标签的 DOM 元素
  const activeEl = wrap.querySelector('.tag-item.active') as HTMLElement | null
  if (!activeEl) {
    // 找不到时滚到最右
    wrap.scrollLeft = wrap.scrollWidth
    return
  }
  // 计算激活标签相对于滚动容器的位置
  const wrapRect = wrap.getBoundingClientRect()
  const elRect = activeEl.getBoundingClientRect()
  const margin = 8
  if (elRect.left < wrapRect.left + margin) {
    // 标签在可视区左侧，向左滚
    wrap.scrollLeft -= wrapRect.left + margin - elRect.left
  } else if (elRect.right > wrapRect.right - margin) {
    // 标签在可视区右侧，向右滚
    wrap.scrollLeft += elRect.right - (wrapRect.right - margin)
  }
}

// ============ 拖拽排序 ============
/** 当前正在拖拽的标签路径 */
const draggingPath = ref<string | null>(null)
/** 拖拽悬停的目标路径（用于视觉反馈） */
const dragOverPath = ref<string | null>(null)

function handleDragStart(event: DragEvent, view: View) {
  if (view.affix) {
    event.preventDefault()
    return
  }
  draggingPath.value = view.path
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', view.path)
  }
}

function handleDragOver(event: DragEvent, view: View) {
  if (!draggingPath.value || draggingPath.value === view.path) return
  event.preventDefault()
  if (event.dataTransfer) event.dataTransfer.dropEffect = 'move'
  dragOverPath.value = view.path
}

function handleDragLeave(_event: DragEvent, view: View) {
  if (dragOverPath.value === view.path) {
    dragOverPath.value = null
  }
}

function handleDrop(event: DragEvent, view: View) {
  event.preventDefault()
  if (!draggingPath.value || draggingPath.value === view.path) return
  // 不允许拖到固定标签位置
  if (view.affix) return
  tagsStore.moveView(draggingPath.value, view.path)
  draggingPath.value = null
  dragOverPath.value = null
}

function handleDragEnd() {
  draggingPath.value = null
  dragOverPath.value = null
}

/** 监听路由变化 */
watch(
  () => route.fullPath,
  () => addCurrentView(),
  { immediate: true }
)

/** 点击页面其他位置隐藏右键菜单 */
function onDocumentClick() {
  hideContextMenu()
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick)
  // 兜底确保首页固定标签存在
  if (!tagsStore.visitedViews.some((v) => v.path === '/dashboard')) {
    tagsStore.addView({ path: '/dashboard', title: '首页', fullPath: '/dashboard', affix: true })
  } else {
    // 标记已有的首页标签为固定
    const dashboard = tagsStore.visitedViews.find((v) => v.path === '/dashboard')
    if (dashboard && !dashboard.affix) dashboard.affix = true
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick)
})
</script>

<template>
  <div class="tags-view-container">
    <el-scrollbar ref="scrollbarRef" class="tags-scrollbar">
      <div class="tags-wrapper">
        <div
          v-for="tag in tagsStore.visitedViews"
          :key="tag.path"
          class="tag-item"
          :class="{
            active: tag.path === activePath,
            affix: tag.affix,
            'drag-over': dragOverPath === tag.path,
            'dragging': draggingPath === tag.path
          }"
          :draggable="!tag.affix"
          @click="handleClick(tag)"
          @dblclick="handleDblClick(tag)"
          @contextmenu="openContextMenu($event, tag)"
          @dragstart="handleDragStart($event, tag)"
          @dragover="handleDragOver($event, tag)"
          @dragleave="handleDragLeave($event, tag)"
          @drop="handleDrop($event, tag)"
          @dragend="handleDragEnd"
        >
          <span class="tag-dot" v-if="tag.path === activePath"></span>
          <span class="tag-title">{{ tag.title }}</span>
          <el-icon
            v-if="tag.affix"
            class="tag-affix-icon"
            title="固定标签"
          >
            <Star />
          </el-icon>
          <el-icon
            v-else-if="isClosable(tag)"
            class="tag-close"
            @click="handleClose(tag, $event)"
          >
            <Close />
          </el-icon>
        </div>
      </div>
    </el-scrollbar>

    <div class="tag-actions">
      <el-tooltip content="刷新当前" placement="bottom">
        <el-icon class="action-icon" @click="refreshCurrent">
          <RefreshRight />
        </el-icon>
      </el-tooltip>
    </div>

    <!-- 右键菜单 -->
    <ul
      v-show="contextMenuVisible"
      class="context-menu"
      :style="{ top: contextMenuTop + 'px', left: contextMenuLeft + 'px' }"
    >
      <li @click="refreshCurrent">
        <el-icon><RefreshRight /></el-icon>
        <span>刷新当前</span>
      </li>
      <li @click="closeLeft">
        <el-icon><Back /></el-icon>
        <span>关闭左侧</span>
      </li>
      <li @click="closeRight">
        <el-icon><Right /></el-icon>
        <span>关闭右侧</span>
      </li>
      <li @click="closeOthers">
        <el-icon><Minus /></el-icon>
        <span>关闭其他</span>
      </li>
      <li @click="closeAll">
        <el-icon><CircleClose /></el-icon>
        <span>关闭所有</span>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.tags-view-container {
  display: flex;
  align-items: center;
  height: 34px;
  background-color: #fff;
  padding: 0 4px 0 8px;
  box-sizing: border-box;
  position: relative;
  flex: 1;
  min-width: 0;
}

.tags-scrollbar {
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
}

.tags-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
  height: 34px;
  white-space: nowrap;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 26px;
  padding: 0 8px;
  font-size: 12px;
  color: #495060;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 3px;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.tag-item:hover {
  color: #409eff;
  border-color: #409eff;
}

.tag-item.active {
  color: #fff;
  background-color: #409eff;
  border-color: #409eff;
}

.tag-item.affix {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
}

.tag-item.affix.active {
  color: #fff;
  background-color: #409eff;
  border-color: #409eff;
}

.tag-item.drag-over {
  border-color: #409eff;
  border-style: dashed;
  background-color: #ecf5ff;
}

.tag-item.dragging {
  opacity: 0.4;
}

.tag-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #fff;
  display: inline-block;
}

.tag-title {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-close {
  font-size: 12px;
  border-radius: 50%;
  padding: 1px;
}

.tag-close:hover {
  background-color: rgba(0, 0, 0, 0.2);
  color: #fff;
}

.tag-affix-icon {
  font-size: 11px;
  color: #e6a23c;
}

.tag-item.active .tag-affix-icon {
  color: #fff;
}

.tag-actions {
  display: flex;
  align-items: center;
  padding-left: 8px;
  border-left: 1px solid #e6e6eb;
  margin-left: 4px;
  flex-shrink: 0;
}

.action-icon {
  cursor: pointer;
  color: #5a5e66;
  font-size: 16px;
}

.action-icon:hover {
  color: #409eff;
}

.context-menu {
  position: fixed;
  z-index: 3000;
  margin: 0;
  padding: 4px 0;
  list-style: none;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-width: 140px;
}

.context-menu li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
}

.context-menu li:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.context-menu li .el-icon {
  font-size: 14px;
}
</style>
