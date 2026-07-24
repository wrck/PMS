<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getBuildInfo, type BuildInfoRespVO } from '@/api/yudao-infra'

defineOptions({ name: 'InfraBuild' })

const loading = ref(false)
const buildInfo = ref<BuildInfoRespVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    buildInfo.value = await getBuildInfo()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function refresh() {
  loadData().then(() => ElMessage.success('刷新成功'))
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-bar">
          <span>构建信息</span>
          <el-button type="primary" :icon="'Refresh'" size="small" @click="refresh">刷新</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="构建名称">
            {{ buildInfo?.name || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="构建产物">
            {{ buildInfo?.artifact || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="版本号">
            <el-tag type="success">{{ buildInfo?.version || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="构建时间">
            {{ formatDateTime(buildInfo?.timestamp) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
