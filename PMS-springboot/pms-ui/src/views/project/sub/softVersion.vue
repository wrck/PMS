<template>
  <div class="softVersion-container">
    <el-card>
      <template #header><span>softVersion管理</span></template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const loading = ref(false)
const tableData = ref([])
const columns = ref([])
const handleEdit = (row) => {}
const handleDelete = (row) => {}
</script>
