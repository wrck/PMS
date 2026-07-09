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
  <router-view />
</template>
