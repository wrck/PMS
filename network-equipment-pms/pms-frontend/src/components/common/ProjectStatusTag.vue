<template>
  <el-tag :type="tagType" :effect="effect" :size="size" :round="round">
    <span class="status-dot" :style="{ backgroundColor: dotColor }"></span>
    {{ label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElTag } from 'element-plus'

type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'
type TagSize = 'small' | 'default' | 'large'
type TagEffect = 'light' | 'dark' | 'plain'

interface Props {
  status: string
  size?: TagSize
  effect?: TagEffect
  round?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  effect: 'light',
  round: true
})

const STATUS_MAP: Record<string, { label: string; type: TagType; color: string }> = {
  PLANNING: { label: '规划中', type: 'warning', color: '#8b5cf6' },
  EXECUTING: { label: '执行中', type: 'primary', color: '#3b82f6' },
  CLOSING: { label: '收尾中', type: 'warning', color: '#f59e0b' },
  CLOSED: { label: '已关闭', type: 'success', color: '#10b981' },
  SUSPENDED: { label: '已暂停', type: 'info', color: '#6b7280' },
  CANCELLED: { label: '已取消', type: 'danger', color: '#ef4444' }
}

const FALLBACK: { label: string; type: TagType; color: string } = {
  label: props.status,
  type: 'info',
  color: '#9ca3af'
}

const config = computed(() => STATUS_MAP[props.status] ?? FALLBACK)
const label = computed(() => config.value.label)
const tagType = computed<TagType>(() => config.value.type)
const dotColor = computed(() => config.value.color)
</script>

<style scoped>
.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
</style>
