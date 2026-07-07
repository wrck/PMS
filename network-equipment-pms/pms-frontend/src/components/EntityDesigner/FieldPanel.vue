<!-- src/components/EntityDesigner/FieldPanel.vue -->
<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { LowCodeEntity, LowCodeField } from '@/api/lowcode-entity'

const props = defineProps<{
  entity: LowCodeEntity
  fields: LowCodeField[]
}>()

const emit = defineEmits<{
  'update:entity': [entity: LowCodeEntity]
  'update:fields': [fields: LowCodeField[]]
}>()

const activeTab = ref<'entity' | 'fields'>('entity')

const formData = reactive<LowCodeEntity>({ ...props.entity })

watch(() => props.entity, (val) => {
  Object.assign(formData, val)
}, { deep: true })

// 实体属性变更时同步给父组件（el-form 无 change 事件，通过深度 watch formData 触发）
watch(formData, () => {
  emit('update:entity', { ...formData })
}, { deep: true })

function addField() {
  const newField: LowCodeField = {
    name: 'new_field',
    label: '新字段',
    fieldType: 'STRING',
    length: 255,
    nullable: 1,
    primaryKey: 0,
    indexed: 0,
    uniqueFlag: 0,
    sortOrder: props.fields.length
  }
  emit('update:fields', [...props.fields, newField])
}

function removeField(index: number) {
  const updated = [...props.fields]
  updated.splice(index, 1)
  emit('update:fields', updated)
}

const FIELD_TYPES = ['STRING', 'INTEGER', 'LONG', 'DECIMAL', 'BOOLEAN', 'DATE', 'DATETIME', 'TEXT']
</script>

<template>
  <div class="field-panel">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="实体属性" name="entity">
        <el-form :model="formData" label-width="90px" size="small">
          <el-form-item label="实体编码">
            <el-input v-model="formData.code" placeholder="如 device" />
          </el-form-item>
          <el-form-item label="实体名称">
            <el-input v-model="formData.name" placeholder="如 设备" />
          </el-form-item>
          <el-form-item label="物理表名">
            <el-input v-model="formData.tableName" placeholder="pms_lc_device" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="formData.description" type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="业务类型">
            <el-input v-model="formData.bizType" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="字段管理" name="fields">
        <el-button type="primary" size="small" @click="addField" style="margin-bottom: 10px">
          新增字段
        </el-button>
        <el-table :data="props.fields" size="small" border>
          <el-table-column label="字段名" prop="name" width="120">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="显示名" prop="label" width="120">
            <template #default="{ row }">
              <el-input v-model="row.label" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="fieldType" width="110">
            <template #default="{ row }">
              <el-select v-model="row.fieldType" size="small">
                <el-option v-for="t in FIELD_TYPES" :key="t" :label="t" :value="t" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="长度" prop="length" width="70">
            <template #default="{ row }">
              <el-input-number v-model="row.length" size="small" :min="1" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column label="PK" prop="primaryKey" width="50">
            <template #default="{ row }">
              <el-checkbox v-model="row.primaryKey" :true-value="1" :false-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="可空" prop="nullable" width="50">
            <template #default="{ row }">
              <el-checkbox v-model="row.nullable" :true-value="1" :false-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template #default="{ $index }">
              <el-button type="danger" size="small" link @click="removeField($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts">
export default { name: 'FieldPanel' }
</script>

<style scoped lang="scss">
.field-panel {
  padding: 10px;
  height: 100%;
  overflow-y: auto;
}
</style>
