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

// 交付件 7 态徽章
const STATUS_MAP: Record<string, { label: string; type: TagType; color: string }> = {
  DRAFT: { label: '草稿', type: 'info', color: '#9ca3af' },
  SUBMITTED: { label: '已提交', type: 'primary', color: '#3b82f6' },
  REVIEWED: { label: '已评审', type: 'warning', color: '#8b5cf6' },
  SIGNED: { label: '已签署', type: 'primary', color: '#06b6d4' },
  PUBLISHED: { label: '已发布', type: 'success', color: '#10b981' },
  REFERENCED: { label: '已引用', type: 'primary', color: '#6366f1' },
  ARCHIVED: { label: '已归档', type: 'info', color: '#6b7280' }
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
