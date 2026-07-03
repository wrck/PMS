<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// Overview cards shown on the dashboard home page
const stats = ref([
  { title: '项目总数', value: 0, icon: 'Folder', color: '#409eff' },
  { title: '在库设备', value: 0, icon: 'Box', color: '#67c23a' },
  { title: '待办任务', value: 0, icon: 'Bell', color: '#e6a23c' },
  { title: '本月交付', value: 0, icon: 'TrendCharts', color: '#f56c6c' }
])

onMounted(() => {
  // TODO: fetch real dashboard statistics from the backend
})

const welcomeName = userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员'
</script>

<template>
  <div class="dashboard">
    <el-card shadow="never" class="welcome-card">
      <h2 class="welcome-title">欢迎回来，{{ welcomeName }} 👋</h2>
      <p class="welcome-desc">这里是网络设备工程项目管理系统工作台，下面是系统概览信息。</p>
    </el-card>

    <el-row :gutter="16" class="stat-row">
      <el-col v-for="item in stats" :key="item.title" :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="28" color="#fff"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="placeholder-card">
      <template #header>
        <span>快捷入口</span>
      </template>
      <p class="placeholder-text">
        此处后续将展示项目进展图表、待办事项列表与近期动态等内容。
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-card {
  background: linear-gradient(120deg, #409eff 0%, #2a5298 100%);
  border: none;
}

.welcome-card :deep(.el-card__body) {
  padding: 24px;
}

.welcome-title {
  margin: 0 0 8px;
  color: #fff;
  font-size: 22px;
  font-weight: 600;
}

.welcome-desc {
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.stat-row {
  margin: 0;
}

.stat-card {
  height: 100%;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
  line-height: 1.2;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.placeholder-card .placeholder-text {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style>
