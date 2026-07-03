<script setup lang="ts">
// Placeholder view: 结算管理
// TODO: 接入 getSettlementPage / createSettlement / approveSettlement / rejectSettlement
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref<unknown[]>([])
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'">新增结算单</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="projectName" label="所属项目" min-width="180" />
        <el-table-column prop="agentName" label="服务商" min-width="160" />
        <el-table-column prop="period" label="结算周期" min-width="140" />
        <el-table-column prop="amount" label="结算金额" min-width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default>
            <el-button link type="success" disabled>审批</el-button>
            <el-button link type="danger" disabled>驳回</el-button>
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
