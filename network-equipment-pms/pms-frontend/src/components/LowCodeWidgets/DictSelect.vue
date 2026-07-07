<template>
  <el-select v-model="model" placeholder="请选择">
    <el-option v-for="d in dictItems" :key="d.value" :label="d.label" :value="d.value" />
  </el-select>
</template>
<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { get } from '@/utils/request'

const props = defineProps<{ modelValue: any; dictCode: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: any): void }>()
const dictItems = ref<any[]>([])
const model = ref(props.modelValue)
watch(model, (v) => emit('update:modelValue', v))

async function loadDict() {
  if (!props.dictCode) return
  try {
    dictItems.value = await get<any[]>('/api/system/dict/items', { code: props.dictCode })
  } catch {
    /* 静默 */
  }
}
onMounted(loadDict)
watch(() => props.dictCode, loadDict)
</script>
