<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listBaselines,
  requestBaselineChange,
  saveBaseline,
  type BaselineSnapshot,
  type BaselineStatus
} from '@/api/baseline'
import { listProjects, type Project } from '@/api/project'
import type { EpTagType } from '@/types'

defineOptions({ name: 'BaselineList' })

const router = useRouter()

const loading = ref(false)
const projectOptions = ref<Project[]>([])
const selectedProjectId = ref<number | undefined>(undefined)
const baselines = ref<BaselineSnapshot[]>([])

const selectedProject = computed(
  () => projectOptions.value.find((p) => p.id === selectedProjectId.value) ?? null
)

function statusMeta(status?: BaselineStatus): { label: string; tagType: EpTagType } {
  switch (status) {
    case 'APPROVED':
      return { label: '已批准', tagType: 'success' }
    case 'SUPERSEDED':
      return { label: '已取代', tagType: 'info' }
    case 'DRAFT':
    default:
      return { label: '草稿', tagType: 'warning' }
  }
}

function snapshotCount(row: BaselineSnapshot): number {
  return row.snapshotJson?.length ?? 0
}

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 19)
}

// ============ 数据加载 ============
async function loadProjects() {
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records
    if (res.records.length > 0 && selectedProjectId.value === undefined) {
      selectedProjectId.value = res.records[0].id
      await loadBaselines()
    }
  } catch {
    /* handled by interceptor */
  }
}

async function loadBaselines() {
  if (!selectedProjectId.value) {
    baselines.value = []
    return
  }
  loading.value = true
  try {
    baselines.value = await listBaselines(selectedProjectId.value)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function onProjectChange() {
  await loadBaselines()
}

// ============ 保存基线 ============
async function handleSaveBaseline() {
  if (!selectedProjectId.value) {
    ElMessage.warning('请先选择项目')
    return
  }
  let name = ''
  try {
    const { value } = await ElMessageBox.prompt('请输入基线名称（留空则按时间自动生成）', '保存基线', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputPlaceholder: '如：初始基线'
    })
    name = value ?? ''
  } catch {
    return
  }
  try {
    await saveBaseline(selectedProjectId.value, name || undefined)
    ElMessage.success('保存基线成功（状态：草稿）')
    await loadBaselines()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 申请变更 ============
async function handleRequestChange(row: BaselineSnapshot) {
  let reason = ''
  try {
    const { value } = await ElMessageBox.prompt('请输入变更原因', '申请基线变更', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '说明本次计划变更的原因与影响'
    })
    reason = value ?? ''
  } catch {
    return
  }
  try {
    const result = await requestBaselineChange(row.id!, reason || undefined)
    if (result.needsApproval) {
      ElMessage.warning(
        `偏差超阈值，已触发审批（Phase 7 实现）。原因：${result.approvalReason ?? ''}`
      )
    } else {
      ElMessage.success('偏差未超阈值，基线已直接批准（APPROVED）')
    }
    await loadBaselines()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 偏差分析 ============
function goDiff(row: BaselineSnapshot) {
  router.push(`/baseline/diff/${row.id}`)
}

onMounted(loadProjects)
</script>

<template>
  <el-page-header :icon="null">
    <template #content>
      <div class="header-content">
        <span class="header-title">计划基线管理</span>
        <el-select
          v-model="selectedProjectId"
          placeholder="选择项目"
          filterable
          style="width: 260px"
          @change="onProjectChange"
        >
          <el-option
            v-for="p in projectOptions"
            :key="p.id"
            :label="p.name"
            :value="p.id!"
          />
        </el-select>
      </div>
    </template>
    <template #extra>
      <el-button type="primary" :disabled="!selectedProjectId" @click="handleSaveBaseline">
        保存基线
      </el-button>
      <el-button :icon="'Refresh'" :disabled="!selectedProjectId" @click="loadBaselines">
        刷新
      </el-button>
    </template>
  </el-page-header>

  <el-card v-loading="loading" shadow="never">
    <template #header>
      <span>基线列表{{ selectedProject ? ` · ${selectedProject.name}` : '' }}</span>
    </template>
    <el-table :data="baselines" stripe>
      <el-table-column label="基线名称" min-width="160">
        <template #default="{ row }">{{ row.baselineName }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="statusMeta(row.status).tagType" size="small" effect="dark">
            {{ statusMeta(row.status).label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="任务快照数" width="110" align="center">
        <template #default="{ row }">{{ snapshotCount(row) }}</template>
      </el-table-column>
      <el-table-column label="批准时间" width="170" align="center">
        <template #default="{ row }">{{ formatDateTime(row.approvedAt) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="170" align="center">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="变更原因" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">{{ row.changeReason || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="goDiff(row)">偏差分析</el-button>
          <el-button
            v-if="row.status === 'DRAFT'"
            type="warning"
            link
            @click="handleRequestChange(row)"
          >
            申请变更
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="baselines.length === 0" description="暂无基线，点击「保存基线」创建项目计划快照" />
  </el-card>
</template>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
