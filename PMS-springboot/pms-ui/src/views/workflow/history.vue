<template>
  <div class="workflow-history">
    <el-card>
      <template #header><span>工作流history</span></template>
      <el-table :data="tableData" stripe>
        <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" />
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const tableData = ref([])
const columns = ref([])
</script>
