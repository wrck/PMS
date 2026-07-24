<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getServerInfo, type ServerRespVO } from '@/api/yudao-infra'

defineOptions({ name: 'InfraServer' })

const loading = ref(false)
const serverInfo = ref<ServerRespVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    serverInfo.value = await getServerInfo()
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

/** 进度条颜色：使用率越高越红 */
function usageColor(usage: number): string {
  if (usage >= 90) return '#f56c6c'
  if (usage >= 70) return '#e6a23c'
  return '#67c23a'
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-bar">
          <span>服务器监控</span>
          <el-button type="primary" :icon="'Refresh'" size="small" @click="refresh">刷新</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <template v-if="serverInfo">
          <!-- CPU 信息 -->
          <el-divider content-position="left">CPU 信息</el-divider>
          <el-row :gutter="12" class="stat-row">
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-label">CPU 核心数</div>
                <div class="stat-value">{{ serverInfo.cpu.cpuNum }}</div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-label">系统使用率</div>
                <div class="stat-value">{{ serverInfo.cpu.sys }}%</div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-label">用户使用率</div>
                <div class="stat-value">{{ serverInfo.cpu.used }}%</div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-label">空闲率</div>
                <div class="stat-value">{{ serverInfo.cpu.free }}%</div>
              </el-card>
            </el-col>
          </el-row>
          <div class="usage-bar">
            <span class="usage-label">CPU 总使用率</span>
            <el-progress
              :percentage="Number(serverInfo.cpu.used) || 0"
              :color="usageColor(Number(serverInfo.cpu.used) || 0)"
              :stroke-width="14"
            />
          </div>

          <!-- 内存信息 -->
          <el-divider content-position="left">内存信息</el-divider>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="总内存">{{ serverInfo.mem.total }} G</el-descriptions-item>
            <el-descriptions-item label="已用内存">{{ serverInfo.mem.used }} G</el-descriptions-item>
            <el-descriptions-item label="空闲内存">{{ serverInfo.mem.free }} G</el-descriptions-item>
          </el-descriptions>
          <div class="usage-bar">
            <span class="usage-label">内存使用率</span>
            <el-progress
              :percentage="Number(serverInfo.mem.usage) || 0"
              :color="usageColor(Number(serverInfo.mem.usage) || 0)"
              :stroke-width="14"
            />
          </div>

          <!-- JVM 信息 -->
          <el-divider content-position="left">JVM 信息</el-divider>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="JVM 名称">{{ serverInfo.jvm.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="JVM 版本">{{ serverInfo.jvm.version || '-' }}</el-descriptions-item>
            <el-descriptions-item label="JVM 安装路径">{{ serverInfo.jvm.home || '-' }}</el-descriptions-item>
            <el-descriptions-item label="JVM 启动时间">{{ formatDateTime(serverInfo.jvm.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="JVM 运行时长">{{ serverInfo.jvm.runTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="JVM 总内存">{{ serverInfo.jvm.total }} M</el-descriptions-item>
            <el-descriptions-item label="JVM 已用内存">{{ (Number(serverInfo.jvm.total) - Number(serverInfo.jvm.free)).toFixed(2) }} M</el-descriptions-item>
            <el-descriptions-item label="JVM 最大内存">{{ serverInfo.jvm.max }} M</el-descriptions-item>
          </el-descriptions>
          <div class="usage-bar">
            <span class="usage-label">JVM 内存使用率</span>
            <el-progress
              :percentage="Number(serverInfo.jvm.usage) || 0"
              :color="usageColor(Number(serverInfo.jvm.usage) || 0)"
              :stroke-width="14"
            />
          </div>

          <!-- 磁盘信息 -->
          <el-divider content-position="left">磁盘信息</el-divider>
          <el-table :data="serverInfo.sysFiles" border stripe size="small">
            <el-table-column prop="dirName" label="盘符路径" min-width="120" />
            <el-table-column prop="typeName" label="文件系统类型" min-width="120" />
            <el-table-column prop="sysTypeName" label="系统类型" min-width="120" />
            <el-table-column prop="total" label="总大小" min-width="100" />
            <el-table-column prop="used" label="已用大小" min-width="100" />
            <el-table-column prop="free" label="可用大小" min-width="100" />
            <el-table-column label="已用百分比" min-width="180">
              <template #default="{ row }">
                <el-progress
                  :percentage="Number(row.usage) || 0"
                  :color="usageColor(Number(row.usage) || 0)"
                  :stroke-width="12"
                />
              </template>
            </el-table-column>
          </el-table>

          <!-- 系统信息 -->
          <el-divider content-position="left">系统信息</el-divider>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item
              v-for="(value, key) in serverInfo.sys"
              :key="key"
              :label="String(key)"
            >
              {{ value || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </template>
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
.stat-row {
  margin-bottom: 12px;
}
.stat-card {
  text-align: center;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}
.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.usage-bar {
  margin-top: 12px;
}
.usage-label {
  display: inline-block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}
</style>
