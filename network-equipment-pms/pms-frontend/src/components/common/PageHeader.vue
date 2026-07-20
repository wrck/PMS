<template>
  <div class="page-header">
    <div class="header-left">
      <div v-if="$slots.breadcrumb" class="breadcrumb">
        <slot name="breadcrumb" />
      </div>
      <h2 class="page-title">
        <slot name="title">{{ title }}</slot>
      </h2>
      <p v-if="description || $slots.description" class="page-description">
        <slot name="description">{{ description }}</slot>
      </p>
    </div>
    <div v-if="$slots.actions" class="header-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title?: string
  description?: string
}

defineProps<Props>()
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0 0 16px;
  border-bottom: 1px solid var(--pms-color-border-light);
  margin-bottom: 16px;
}
.header-left {
  flex: 1;
  min-width: 0;
}
.breadcrumb {
  margin-bottom: 8px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin: 0;
}
.page-description {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  margin: 4px 0 0;
}
.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
