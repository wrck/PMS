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
  NOT_STARTED: { label: '未开始', type: 'info', color: '#9ca3af' },
  IN_PROGRESS: { label: '进行中', type: 'primary', color: '#3b82f6' },
  COMPLETED: { label: '已完成', type: 'success', color: '#10b981' },
  SKIPPED: { label: '已跳过', type: 'warning', color: '#f59e0b' }
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
