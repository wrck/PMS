<script setup lang="ts">
// Placeholder view: 设备型号
// TODO: 接入 getModelPage / createModel / updateModel / deleteModel
import { ref } from 'vue'

const loading = ref(false)
const tableData = ref<unknown[]>([])
const keyword = ref('')
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="型号名称">
          <el-input v-model="keyword" placeholder="型号名称/编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'">查询</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'">新增型号</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="code" label="型号编码" min-width="140" />
        <el-table-column prop="name" label="型号名称" min-width="180" />
        <el-table-column prop="categoryName" label="所属分类" min-width="140" />
        <el-table-column prop="brand" label="品牌" min-width="120" />
        <el-table-column prop="spec" label="规格" min-width="160" />
        <el-table-column prop="unit" label="单位" width="90" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>编辑</el-button>
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
