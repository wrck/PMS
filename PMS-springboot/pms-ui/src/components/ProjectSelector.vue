<template>
  <el-select v-model="selectedValue" filterable remote :remote-method="search" placeholder="选择项目" clearable>
    <el-option v-for="item in options" :key="item.id" :label="item.projectName" :value="item.id" />
  </el-select>
</template>
<script setup>
import { ref } from 'vue'
import { listProjects } from '@/api/project'
const props = defineProps({ modelValue: [String, Number] })
const emit = defineEmits(['update:modelValue'])
const selectedValue = ref(props.modelValue)
const options = ref([])
const search = async (query) => {
  if (query) {
    const res = await listProjects({ projectName: query, pageNum: 1, pageSize: 20 })
    options.value = res.data?.records || []
  }
}
</script>
