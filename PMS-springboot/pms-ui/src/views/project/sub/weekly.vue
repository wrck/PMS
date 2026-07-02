<template>
  <div class="weekly-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目周报</span>
          <el-button type="primary" size="small" @click="handleCreate">新建周报</el-button>
        </div>
      </template>
      <el-table :data="weeklyList" stripe>
        <el-table-column prop="weeklyDate" label="周期" />
        <el-table-column prop="weeklyPerson" label="填报人" />
        <el-table-column prop="weeklyState" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.weeklyState === 1 ? 'success' : 'info'">
              {{ row.weeklyState === 1 ? '已提交' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const weeklyList = ref([])
const handleCreate = () => {}
const handleEdit = (row) => {}
const handleView = (row) => {}
</script>
