<script setup lang="ts">
import { watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import { usePWA } from '@/composables/usePWA'

// 激活 PWA：Service Worker 注册 + 离线状态监听 + 更新检测（批次4-T10）
const { isOnline, needRefresh, applyUpdate } = usePWA()

// 检测到新版本时提示用户刷新
watch(needRefresh, (v) => {
  if (!v) return
  ElMessageBox.confirm('检测到新版本可用，是否立即刷新以应用更新？', '应用更新', {
    confirmButtonText: '刷新',
    cancelButtonText: '稍后',
    type: 'success'
  })
    .then(() => applyUpdate())
    .catch(() => {
      // 用户选择稍后，不强制刷新
    })
})

// 离线状态变化提示（可选，避免频繁打扰，仅首次离线时提示）
watch(isOnline, (online, prev) => {
  if (prev === true && !online) {
    ElMessageBox.alert('当前网络不可用，已切换到离线模式。部分功能可能受限。', '离线提示', {
      confirmButtonText: '知道了',
      type: 'warning'
    }).catch(() => {})
  }
})
</script>

<template>
  <router-view v-slot="{ Component, route }">
    <transition
      :name="(route.meta.transitionName as string) || 'fade-slide-up'"
      mode="out-in"
    >
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<style>
/* ============================================================================
 * 钉飞风格全局过渡动画
 * ----------------------------------------------------------------------------
 * 统一缓动曲线：cubic-bezier(0.4, 0, 0.2, 1)（Material Design standard，
 * 飞书/钉钉通用），时长 200~250ms，位移幅度 10~20px，保持精致克制。
 *
 * 动画语义：
 *   - fade-slide-up  : 默认。垂直淡入（飞书风格 - 内容区切换）
 *   - slide-fade-x   : 水平滑动（钉钉风格 - Tab 切换/前进后退）
 *   - fade-scale     : 缩放淡入（弹窗/对话框/卡片切换）
 *   - fade-slide     : fade-slide-up 别名（向后兼容）
 *   - fade-transform : slide-fade-x 别名（向后兼容 DefaultLayout 内层默认）
 * ============================================================================ */

/* ---- 垂直 slide-fade（飞书风格 - 默认路由切换） ---- */
.fade-slide-up-enter-active,
.fade-slide-up-leave-active,
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-slide-up-enter-from,
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-slide-up-leave-to,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ---- 水平 slide-fade（钉钉风格 - Tab 切换/前进后退语义） ---- */
.slide-fade-x-enter-active,
.slide-fade-x-leave-active,
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: opacity 0.2s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-fade-x-enter-from,
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.slide-fade-x-leave-to,
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* ---- scale + fade（弹窗/缩放容器 - 钉飞共用） ---- */
.fade-scale-enter-active {
  transition: opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-scale-leave-active {
  transition: opacity 0.2s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-scale-enter-from {
  opacity: 0;
  transform: scale(0.96);
}
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>
