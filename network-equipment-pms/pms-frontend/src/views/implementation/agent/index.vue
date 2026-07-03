<script setup lang="ts">
// Placeholder view: 服务商管理
// TODO: 接入 getAgentPage / createAgent / updateAgent / deleteAgent / getAgentScores
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref<unknown[]>([])
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'">新增服务商</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="name" label="服务商名称" min-width="180" />
        <el-table-column prop="company" label="公司" min-width="180" />
        <el-table-column prop="contact" label="联系人" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="score" label="评分" width="100" />
        <el-table-column label="状态" width="90">
          <template #default>
            <el-tag type="success">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>编辑</el-button>
            <el-button link type="primary" disabled>评分</el-button>
            <el-button link type="danger" disabled>删除</el-button>
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
.toolbar {
  margin-bottom: 12px;
}
</style>
