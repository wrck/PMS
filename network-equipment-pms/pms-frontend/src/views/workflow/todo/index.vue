<script setup lang="ts">
// Placeholder view: 待办中心
// TODO: 接入 getTodoTasks / getDoneTasks / completeTask / getProcessHistory / getProcessDiagram
import { ref } from 'vue'

const activeTab = ref('todo')
const loading = ref(false)
const tableData = ref<unknown[]>([])
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待办任务" name="todo" />
        <el-tab-pane label="已办任务" name="done" />
      </el-tabs>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="name" label="任务名称" min-width="160" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="160" />
        <el-table-column prop="businessKey" label="业务编号" min-width="160" />
        <el-table-column prop="assignee" label="办理人" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>办理</el-button>
            <el-button link type="primary" disabled>流程图</el-button>
            <el-button link type="primary" disabled>历史</el-button>
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
