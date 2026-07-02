<template>
  <div class="system-dict">
    <el-card>
      <template #header><span>dict管理</span></template>
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
