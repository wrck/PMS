<script setup lang="ts">
// Placeholder view: 项目详情
// TODO: 接入 getProjectById / listMilestones / 资产 / 实施 / 终验 等接口
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const activeTab = ref('basic')

function goBack() {
  router.back()
}
</script>

<template>
  <div class="page-container">
    <el-page-header :icon="null" @back="goBack">
      <template #content>
        <span class="header-title">项目详情 #{{ route.params.id }}</span>
      </template>
    </el-page-header>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <!-- TODO: 展示项目基本信息表单 -->
          <el-empty description="基本信息（占位）" />
        </el-tab-pane>
        <el-tab-pane label="里程碑" name="milestone">
          <!-- TODO: 里程碑列表与进度更新 -->
          <el-empty description="里程碑（占位）" />
        </el-tab-pane>
        <el-tab-pane label="设备资产" name="asset">
          <!-- TODO: 关联设备列表 -->
          <el-empty description="设备资产（占位）" />
        </el-tab-pane>
        <el-tab-pane label="实施任务" name="implementation">
          <!-- TODO: 实施任务列表 -->
          <el-empty description="实施任务（占位）" />
        </el-tab-pane>
        <el-tab-pane label="终验" name="acceptance">
          <!-- TODO: 终验申请/审批 -->
          <el-empty description="终验（占位）" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
