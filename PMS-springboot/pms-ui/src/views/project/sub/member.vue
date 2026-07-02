<template>
  <div class="member-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目成员</span>
          <el-button type="primary" size="small" @click="handleAdd">添加成员</el-button>
        </div>
      </template>
      <el-table :data="memberList" stripe>
        <el-table-column prop="memberCode" label="工号" />
        <el-table-column prop="memberName" label="姓名" />
        <el-table-column prop="memberRole" label="角色" />
        <el-table-column prop="memberPhone" label="电话" />
        <el-table-column prop="memberEmail" label="邮箱" />
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

const memberList = ref([])
const handleAdd = () => {}
const handleEdit = (row) => {}
const handleDelete = (row) => {}
</script>
