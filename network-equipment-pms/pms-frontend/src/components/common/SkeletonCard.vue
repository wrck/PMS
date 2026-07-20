<template>
  <div class="skeleton-card" :style="{ padding: padding }">
    <el-skeleton :rows="rows" animated :loading="loading">
      <template #default><slot /></template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ElSkeleton } from 'element-plus'

interface Props {
  loading: boolean
  rows?: number
  padding?: string
}

withDefaults(defineProps<Props>(), {
  rows: 3,
  padding: '16px'
})
</script>

<style scoped>
.skeleton-card {
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-lg);
  box-shadow: var(--pms-shadow-card);
}
</style>
