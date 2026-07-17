<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  DELIVERABLE_STATUS_LABELS,
  listDeliverableVersions,
  type DeliverableStatus,
  type DeliverableVersion
} from '@/api/deliverable'
import type { EpTagType } from '@/types'

defineOptions({ name: 'DeliverableVersionList' })

/**
 * 交付件版本历史列表组件（不可变历史记录）。
 *
 * <p>展示交付件的全部版本记录（按版本号倒序），支持下载文件、查看变更说明。
 * Story 5 验收 1 的核心展示组件：修订新建版本不覆盖旧版本，旧版本记录保留不变。</p>
 *
 * <p>关联设计文档：§3.4（行 421-425）、§5.6 验收 1。</p>
 *
 * 用法：
 * ```vue
 * <DeliverableVersionList :deliverable-id="id" />
 * ```
 */
const props = defineProps<{
  /** 交付件ID */
  deliverableId: number
  /** 是否只读（隐藏下载按钮） */
  readonly?: boolean
  /** 是否自动加载（默认 true；false 时由父组件调用 refresh()） */
  autoload?: boolean
}>()

const emit = defineEmits<{
  /** 版本列表加载完成时通知父组件 */
  (e: 'loaded', versions: DeliverableVersion[]): void
  /** 下载某版本文件时通知父组件 */
  (e: 'download', version: DeliverableVersion): void
}>()

const loading = ref(false)
const versions = ref<DeliverableVersion[]>([])

// ============ 状态标签 ============
function statusMeta(status?: DeliverableStatus | string): { label: string; tagType: EpTagType } {
  const s = (status ?? 'DRAFT') as DeliverableStatus
  const label = DELIVERABLE_STATUS_LABELS[s] ?? status ?? '-'
  const tagType: EpTagType = (() => {
    switch (s) {
      case 'DRAFT': return 'info'
      case 'SUBMITTED': return 'warning'
      case 'REVIEWED': return 'primary'
      case 'SIGNED': return 'success'
      case 'PUBLISHED': return 'success'
      case 'REFERENCED': return 'success'
      case 'ARCHIVED': return 'danger'
      default: return 'info'
    }
  })()
  return { label, tagType }
}

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 19)
}

// ============ 数据加载 ============
async function refresh() {
  if (!props.deliverableId) {
    versions.value = []
    return
  }
  loading.value = true
  try {
    versions.value = (await listDeliverableVersions(props.deliverableId)) ?? []
    emit('loaded', versions.value)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

// ============ 下载 ============
function handleDownload(v: DeliverableVersion) {
  if (!v.filePath) {
    ElMessage.warning('该版本无文件路径')
    return
  }
  emit('download', v)
  // 默认实现：新窗口打开文件路径（可由父组件接管 download 事件做更复杂处理）
  if (!props.readonly) {
    window.open(v.filePath, '_blank')
  }
}

// ============ 派生信息 ============
const latestVersion = computed(() => versions.value[0] ?? null)
const totalVersions = computed(() => versions.value.length)

defineExpose({ refresh, versions, latestVersion, totalVersions })

onMounted(() => {
  if (props.autoload !== false) refresh()
})
</script>

<template>
  <div class="deliverable-version-list">
    <div v-if="!readonly" class="toolbar">
      <span class="summary">共 {{ totalVersions }} 个版本</span>
      <el-button link type="primary" size="small" @click="refresh" :loading="loading">刷新</el-button>
    </div>
    <el-table v-loading="loading" :data="versions" border stripe>
      <el-table-column prop="versionNo" label="版本号" width="90" align="center">
        <template #default="{ row }">
          <span class="version-tag">v{{ row.versionNo }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="filePath" label="文件路径" min-width="200" show-overflow-tooltip />
      <el-table-column prop="fileChecksum" label="SHA256" width="160" align="center">
        <template #default="{ row }">
          <span class="mono">{{ row.fileChecksum ?? '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="uploadedBy" label="上传人" width="100" align="center">
        <template #default="{ row }">{{ row.uploadedBy ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="上传时间" width="160" align="center">
        <template #default="{ row }">{{ formatDateTime(row.uploadedAt) }}</template>
      </el-table-column>
      <el-table-column prop="changeLog" label="变更说明" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">{{ row.changeLog ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="版本状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusMeta(row.status).tagType" size="small">
            {{ statusMeta(row.status).label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="!readonly" label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无版本记录" />
      </template>
    </el-table>
  </div>
</template>

<style scoped>
.deliverable-version-list {
  width: 100%;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding: 0 4px;
}
.summary {
  font-size: 13px;
  color: #606266;
}
.version-tag {
  font-weight: 600;
  color: #409eff;
}
.mono {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #909399;
}
</style>
