<template>
  <el-tag :type="tagType" :effect="effect" :size="size" :round="round">
    <span class="priority-dot" :style="{ backgroundColor: dotColor }"></span>
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
  priority: string
  size?: TagSize
  effect?: TagEffect
  round?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  effect: 'light',
  round: true
})

const PRIORITY_MAP: Record<string, { label: string; type: TagType; color: string }> = {
  LOW: { label: '低', type: 'info', color: '#6b7280' },
  MEDIUM: { label: '中', type: 'primary', color: '#3b82f6' },
  HIGH: { label: '高', type: 'warning', color: '#f59e0b' },
  CRITICAL: { label: '紧急', type: 'danger', color: '#ef4444' }
}

const FALLBACK: { label: string; type: TagType; color: string } = {
  label: props.priority,
  type: 'info',
  color: '#9ca3af'
}

const config = computed(() => PRIORITY_MAP[props.priority] ?? FALLBACK)
const label = computed(() => config.value.label)
const tagType = computed<TagType>(() => config.value.type)
const dotColor = computed(() => config.value.color)
</script>

<style scoped>
.priority-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
</style>
