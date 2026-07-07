<template>
  <div class="lowcode-property-panel">
    <div class="panel-header">{{ meta?.displayName || '属性配置' }}</div>
    <el-form label-width="100px" size="small">
      <el-form-item v-for="prop in meta?.propsSchema || []" :key="prop.key" :label="prop.key">
        <el-switch v-if="prop.type === 'boolean'" v-model="local[prop.key]" />
        <el-input-number v-else-if="prop.type === 'number'" v-model="local[prop.key]" />
        <el-input v-else v-model="local[prop.key]" :placeholder="prop.default !== undefined ? `默认: ${prop.default}` : ''" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { ComponentMeta } from '@/components/LowCodeComponentRegistry/types'

const props = defineProps<{ meta: ComponentMeta | null; modelValue: Record<string, any> }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: Record<string, any>): void }>()

// 双向绑定（modelValue 是对象，需深拷贝后修改）
const local = reactive<Record<string, any>>({ ...props.modelValue })
watch(local, () => emit('update:modelValue', { ...local }), { deep: true })
</script>

<style scoped>
.lowcode-property-panel {
  padding: 8px;
}
.panel-header {
  font-weight: 600;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
}
</style>
