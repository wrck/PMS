<!-- src/components/EntityDesigner/EntityNode.vue -->
<script setup lang="ts">
import { computed } from 'vue'
import type { LowCodeField } from '@/api/lowcode-entity'

interface EntityNodeData {
  entityName: string
  tableName: string
  fields: LowCodeField[]
}

const props = defineProps<{ data: EntityNodeData }>()

const ports = computed(() => {
  const groups = ['top', 'bottom', 'left', 'right']
  return (props.data.fields || []).map((f, i) => ({
    id: `port-${f.name}`,
    group: groups[i % 4],
    attrs: { text: { text: f.name } }
  }))
})
</script>

<template>
  <div class="entity-node">
    <div class="entity-header">{{ data.entityName }}</div>
    <div class="entity-table">{{ data.tableName }}</div>
    <div class="entity-fields">
      <div v-for="field in data.fields" :key="field.name" class="field-row">
        <span class="field-key" v-if="field.primaryKey === 1">PK</span>
        <span class="field-name">{{ field.name }}</span>
        <span class="field-type">{{ field.fieldType }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default { name: 'EntityNode' }
</script>

<style scoped lang="scss">
.entity-node {
  width: 220px;
  background: #fff;
  border: 2px solid #409eff;
  border-radius: 4px;
  font-size: 12px;
  .entity-header {
    background: #409eff;
    color: #fff;
    padding: 6px 10px;
    font-weight: 600;
    font-size: 13px;
  }
  .entity-table {
    padding: 2px 10px;
    color: #909399;
    font-size: 11px;
    border-bottom: 1px solid #ebeef5;
  }
  .entity-fields {
    max-height: 200px;
    overflow-y: auto;
    .field-row {
      display: flex;
      align-items: center;
      padding: 3px 10px;
      border-bottom: 1px solid #f5f5f5;
      .field-key {
        background: #f56c6c;
        color: #fff;
        border-radius: 2px;
        padding: 0 4px;
        font-size: 10px;
        margin-right: 6px;
      }
      .field-name {
        flex: 1;
        color: #303133;
      }
      .field-type {
        color: #909399;
        font-size: 11px;
      }
    }
  }
}
</style>
