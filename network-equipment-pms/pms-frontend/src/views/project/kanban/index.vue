<script setup lang="ts">
// Placeholder view: 交付看板
// TODO: 接入项目看板数据，按状态分列展示可拖拽卡片
import { ref } from 'vue'

const columns = ref([
  { title: '待启动', status: 'pending' },
  { title: '进行中', status: 'in_progress' },
  { title: '交付中', status: 'delivering' },
  { title: '已完成', status: 'done' }
])
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>交付看板</span>
      </template>
      <el-row :gutter="12">
        <el-col v-for="col in columns" :key="col.status" :xs="24" :sm="12" :md="6">
          <div class="kanban-col">
            <div class="kanban-col-title">{{ col.title }}</div>
            <div class="kanban-col-body">
              <!-- TODO: 渲染该状态下的项目卡片 -->
              <el-empty :image-size="60" description="暂无项目" />
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.kanban-col {
  background-color: #f5f7fa;
  border-radius: 6px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
}
.kanban-col-title {
  padding: 10px 12px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e6e6eb;
}
.kanban-col-body {
  padding: 10px;
  flex: 1;
}
</style>
