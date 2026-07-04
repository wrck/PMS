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

/** 当前激活的标签路径（基于 route.path） */
const activePath = computed(() => route.path)

/** 判断标签是否可关闭（只有一个标签时不允许关闭） */
function isClosable(view: View): boolean {
  return tagsStore.visitedViews.length > 1 || view.path !== '/dashboard'
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

/** 关闭标签 */
function handleClose(view: View, event?: MouseEvent) {
  event?.stopPropagation()
  const wasActive = view.path === activePath.value
  tagsStore.delView(view.path)
  // 关闭的是当前激活标签，需要跳到最后一项
  if (wasActive) {
    const last = tagsStore.visitedViews[tagsStore.visitedViews.length - 1]
    router.push(last ? last.fullPath || last.path : '/dashboard')
  }
}

/** 刷新当前标签（先跳到一个临时路由再回来） */
function refreshCurrent() {
  const path = contextMenuPath.value || activePath.value
  router.replace('/redirect' + path).catch(() => {
    // 没有中转路由时直接 reload
    window.location.reload()
  })
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

/** 关闭所有（保留首页） */
function closeAll() {
  tagsStore.delAll()
  tagsStore.addView({ path: '/dashboard', title: '首页', fullPath: '/dashboard' })
  router.push('/dashboard')
  hideContextMenu()
}

/** 右键打开上下文菜单 */
function openContextMenu(event: MouseEvent, view: View) {
  event.preventDefault()
  contextMenuPath.value = view.path
  contextMenuTop.value = event.clientY
  contextMenuLeft.value = event.clientX
  contextMenuVisible.value = true
}

/** 隐藏右键菜单 */
function hideContextMenu() {
  contextMenuVisible.value = false
}

/** 滚动到当前激活的标签 */
function scrollToActiveTag() {
  const wrap = scrollbarRef.value?.wrapRef
  if (!wrap) return
  // 简单实现：滚到最右侧（最新加入的标签在右侧）
  wrap.scrollLeft = wrap.scrollWidth
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
  // 兜底确保首页存在
  if (!tagsStore.visitedViews.some((v) => v.path === '/dashboard')) {
    tagsStore.addView({ path: '/dashboard', title: '首页', fullPath: '/dashboard' })
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
          :class="{ active: tag.path === activePath }"
          @click="handleClick(tag)"
          @contextmenu="openContextMenu($event, tag)"
        >
          <span class="tag-dot" v-if="tag.path === activePath"></span>
          <span class="tag-title">{{ tag.title }}</span>
          <el-icon
            v-if="isClosable(tag)"
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
      <li @click="refreshCurrent">刷新当前</li>
      <li @click="closeOthers">关闭其他</li>
      <li @click="closeAll">关闭所有</li>
    </ul>
  </div>
</template>

<style scoped>
.tags-view-container {
  display: flex;
  align-items: center;
  height: 34px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6eb;
  padding: 0 8px;
  box-sizing: border-box;
  position: relative;
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

.tag-actions {
  display: flex;
  align-items: center;
  padding-left: 8px;
  border-left: 1px solid #e6e6eb;
  margin-left: 4px;
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
  min-width: 120px;
}

.context-menu li {
  padding: 6px 16px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
}

.context-menu li:hover {
  background-color: #ecf5ff;
  color: #409eff;
}
</style>
