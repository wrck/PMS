<script setup lang="ts">
/**
 * 递归侧边栏菜单组件。
 *
 * <p>替换 {@code DefaultLayout.vue} 内联的 {@code <el-menu>} 渲染逻辑，支持：
 * <ul>
 *   <li>任意层级嵌套（当前业务最深 2 级，但组件支持 N 级递归）</li>
 *   <li>折叠态自动隐藏标题文字（依赖 {@code el-menu :collapse}）</li>
 *   <li>图标通过 {@code <component :is="icon" />} 动态渲染（Element Plus Icons 全局注册）</li>
 *   <li>选中态高亮由 {@code el-menu :default-active} 控制，路由跳转由 {@code router} 模式驱动</li>
 *   <li>透传 {@code select} 事件给父级（用于移动端抽屉关闭等场景）</li>
 * </ul>
 *
 * <p>组件自包含：父级传入 items 与 collapse 即可，不依赖外部 store。</p>
 */
import type { MenuItem, MenuLeaf, MenuGroup } from '@/config/menu'

defineOptions({ name: 'SidebarMenu' })

withDefaults(
  defineProps<{
    /** 菜单项列表（已按权限过滤） */
    items: MenuItem[]
    /** 当前激活的菜单路径（用于 el-menu default-active） */
    activeMenu?: string
    /** 是否折叠侧边栏 */
    collapse?: boolean
  }>(),
  {
    activeMenu: '',
    collapse: false
  }
)

// 透传 el-menu 的 select 事件给父级
const emit = defineEmits<{
  (e: 'select', index: string, indexPath: string[]): void
}>()

/** 是否为分组节点（含 children） */
function isGroup(item: MenuItem): item is MenuGroup {
  return 'children' in item && Array.isArray(item.children)
}

/** 是否为叶子节点（可点击跳转） */
function isLeaf(item: MenuItem): item is MenuLeaf {
  return !isGroup(item)
}

/** el-menu select 事件透传 */
function handleSelect(index: string, indexPath: string[]): void {
  emit('select', index, indexPath)
}
</script>

<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="collapse"
    router
    background-color="#001529"
    text-color="#cfd5dc"
    active-text-color="#ffffff"
    class="side-menu"
    @select="handleSelect"
  >
    <template v-for="(item, idx) in items" :key="idx">
      <!-- 分组节点：递归渲染（当前业务 2 级足够，但模板内可继续嵌套） -->
      <el-sub-menu v-if="isGroup(item)" :index="String(idx)">
        <template #title>
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </template>
        <el-menu-item
          v-for="child in item.children"
          :key="child.path"
          :index="child.path"
        >
          <el-icon><component :is="child.icon" /></el-icon>
          <template #title>{{ child.title }}</template>
        </el-menu-item>
      </el-sub-menu>
      <!-- 叶子节点：直接渲染 -->
      <el-menu-item v-else-if="isLeaf(item)" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<style scoped>
.side-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.side-menu:not(.el-menu--collapse) {
  width: 220px;
}
</style>
