<script setup lang="ts">
// Placeholder view: 设备分类（树形）
// TODO: 接入 getCategoryTree 实现分类树的新增/编辑/删除
import { ref } from 'vue'

const loading = ref(false)
const treeData = ref<unknown[]>([])
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>设备分类</span>
          <el-button type="primary" :icon="'Plus'">新增分类</el-button>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="treeData"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        default-expand-all
      >
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="code" label="编码" min-width="160" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default>
            <el-button link type="primary" disabled>新增</el-button>
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
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
