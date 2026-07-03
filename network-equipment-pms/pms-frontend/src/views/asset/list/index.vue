<script setup lang="ts">
// Placeholder view: 资产清单
// TODO: 接入 getAssetPage / inboundAsset / allocateAsset / returnAsset / getAssetLifecycle
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
        <el-form-item label="序列号">
          <el-input v-model="keyword" placeholder="资产序列号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="status" placeholder="全部" clearable style="width: 140px">
            <el-option label="在库" value="in_stock" />
            <el-option label="已领用" value="allocated" />
            <el-option label="已归还" value="returned" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'">查询</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'">入库</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="sn" label="序列号" min-width="160" />
        <el-table-column prop="modelName" label="型号" min-width="160" />
        <el-table-column prop="projectName" label="所属项目" min-width="160" />
        <el-table-column prop="location" label="位置" min-width="140" />
        <el-table-column prop="ownerName" label="持有人" min-width="120" />
        <el-table-column prop="status" label="状态" width="110" />
        <el-table-column prop="inboundDate" label="入库日期" min-width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>领用</el-button>
            <el-button link type="primary" disabled>归还</el-button>
            <el-button link type="primary" disabled>生命周期</el-button>
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
