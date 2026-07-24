<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRedisMonitorInfo, type RedisMonitorRespVO } from '@/api/yudao-infra'

const loading = ref(false)
const monitorInfo = ref<RedisMonitorRespVO | null>(null)

// 需要重点展示的 Redis 基本信息字段
const importantFields: Array<{ field: string; label: string }> = [
  { field: 'redis_version', label: 'Redis 版本' },
  { field: 'connected_clients', label: '客户端连接数' },
  { field: 'used_memory', label: '已使用内存' },
  { field: 'used_memory_human', label: '已使用内存(人类可读)' },
  { field: 'used_memory_peak', label: '内存使用峰值' },
  { field: 'used_memory_peak_human', label: '内存使用峰值(人类可读)' },
  { field: 'used_memory_rss', label: 'RSS 内存' },
  { field: 'uptime_in_days', label: '已运行天数' },
  { field: 'uptime_in_seconds', label: '已运行秒数' },
  { field: 'db0', label: 'db0' },
  { field: 'maxmemory', label: '最大内存' },
  { field: 'maxmemory_human', label: '最大内存(人类可读)' },
  { field: 'maxmemory_policy', label: '内存淘汰策略' },
  { field: 'total_connections_received', label: '累计接收连接数' },
  { field: 'total_commands_processed', label: '累计执行命令数' },
  { field: 'instantaneous_ops_per_sec', label: '每秒执行命令数' },
  { field: 'rejected_connections', label: '被拒绝连接数' },
  { field: 'expired_keys', label: '已过期 key 数' },
  { field: 'evicted_keys', label: '被淘汰 key 数' },
  { field: 'keyspace_hits', label: '键空间命中次数' },
  { field: 'keyspace_misses', label: '键空间未命中次数' }
]

async function loadData() {
  loading.value = true
  try {
    monitorInfo.value = await getRedisMonitorInfo()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function getInfoValue(field: string): string {
  return monitorInfo.value?.info?.[field] ?? '-'
}

function refresh() {
  loadData().then(() => {
    ElMessage.success('刷新成功')
  })
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-bar">
          <span>Redis 监控</span>
          <el-button type="primary" :icon="'Refresh'" size="small" @click="refresh">刷新</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-row :gutter="12">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">Key 总数 (dbSize)</div>
              <div class="stat-value">{{ monitorInfo?.dbSize ?? '-' }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">Redis 版本</div>
              <div class="stat-value">{{ getInfoValue('redis_version') }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">已运行天数</div>
              <div class="stat-value">{{ getInfoValue('uptime_in_days') }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider content-position="left">Redis 基本信息</el-divider>

        <el-descriptions :column="3" border size="small">
          <el-descriptions-item
            v-for="item in importantFields"
            :key="item.field"
            :label="item.label"
          >
            {{ getInfoValue(item.field) }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">命令统计</el-divider>

        <el-table :data="monitorInfo?.commandStats ?? []" border stripe size="small">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="command" label="命令" min-width="160" />
          <el-table-column prop="calls" label="调用次数" min-width="120" />
          <el-table-column prop="usec" label="消耗 CPU (usec)" min-width="140" />
        </el-table>
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
.stat-card {
  text-align: center;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
</style>
