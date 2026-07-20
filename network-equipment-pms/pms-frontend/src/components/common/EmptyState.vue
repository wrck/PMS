<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon :size="64"><component :is="icon" /></el-icon>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-action">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElIcon } from 'element-plus'

interface Props {
  title: string
  description?: string
  icon?: string
}

withDefaults(defineProps<Props>(), {
  icon: 'Folder',
  description: ''
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
}
.empty-icon {
  color: var(--pms-color-text-placeholder);
  margin-bottom: 16px;
}
.empty-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--pms-color-text-regular);
  margin: 0 0 8px;
}
.empty-description {
  font-size: 14px;
  color: var(--pms-color-text-secondary);
  margin: 0 0 16px;
  max-width: 400px;
}
.empty-action {
  margin-top: 8px;
}
</style>
