<script setup lang="ts">
// Placeholder view: 实施任务
// TODO: 接入 getTaskPage / assignTask / acceptTask / startTask / reportTaskProgress / completeTask / confirmTask / rejectTask
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref<unknown[]>([])
const keyword = ref('')
const status = ref('')
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="任务名称">
          <el-input v-model="keyword" placeholder="任务名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待分配" value="pending" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="done" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="name" label="任务名称" min-width="180" />
        <el-table-column prop="projectName" label="所属项目" min-width="160" />
        <el-table-column prop="agentName" label="服务商" min-width="140" />
        <el-table-column prop="assigneeName" label="执行人" min-width="120" />
        <el-table-column prop="progress" label="进度" width="160">
          <template #default>
            <el-progress :percentage="0" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>分配</el-button>
            <el-button link type="primary" disabled>进度</el-button>
            <el-button link type="success" disabled>完成</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据（占位页面，待接入接口）" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
