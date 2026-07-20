<script setup lang="ts">
// =============================================================================
// BaselineList - 计划基线列表页（列表/时间轴双视图）
// -----------------------------------------------------------------------------
// - 视图切换：列表视图（默认）/ 时间轴视图
// - 列表视图：el-table 列含 编号/名称/项目/版本/状态/创建人/创建时间/任务数/偏差数/操作
// - 时间轴视图：el-timeline 按创建时间倒序，节点点击进入偏差分析
// - 顶部统计卡片：活跃基线数 / 历史基线数 / 总任务数 / 待审批基线数
// - 状态徽章：DRAFT=warning / APPROVED=success / SUPERSEDED=info
// - 筛选：项目 / 状态 / 关键字
// - 分页：10 / 20 / 50
// - 加载中：SkeletonCard；空状态：EmptyState
// - 新建基线对话框：基线名 + 描述 + 选择项目 + 是否触发审批（双阈值超限时）
// =============================================================================
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listBaselines,
  saveBaseline,
  type BaselineSnapshot,
  type BaselineStatus
} from '@/api/baseline'
import { listProjects, type Project } from '@/api/project'
import type { EpTagType } from '@/types'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

defineOptions({ name: 'BaselineList' })

const router = useRouter()

// ============ 视图模式 ============
const viewMode = ref<'list' | 'timeline'>('list')

// ============ 数据 ============
const loading = ref(false)
const baselines = ref<BaselineSnapshot[]>([])
const projectOptions = ref<Project[]>([])
const page = reactive({ current: 1, size: 10 })

// ============ 筛选 ============
const query = reactive({
  projectId: undefined as number | undefined,
  status: undefined as BaselineStatus | undefined,
  keyword: ''
})

const statusOptions: { label: string; value: BaselineStatus }[] = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已批准', value: 'APPROVED' },
  { label: '已取代', value: 'SUPERSEDED' }
]

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

// ============ 统计卡片 ============
const stats = computed(() => {
  const all = baselines.value
  const active = all.filter((b) => b.status === 'APPROVED').length
  const archived = all.filter((b) => b.status === 'SUPERSEDED').length
  const draft = all.filter((b) => b.status === 'DRAFT').length
  const totalTasks = all.reduce((sum, b) => sum + (b.snapshotJson?.length ?? 0), 0)
  return { active, archived, draft, totalTasks, total: all.length }
})

// ============ 客户端筛选 ============
const filteredBaselines = computed<BaselineSnapshot[]>(() => {
  let list = baselines.value
  if (query.status) list = list.filter((b) => b.status === query.status)
  if (query.keyword) {
    const kw = query.keyword.trim().toLowerCase()
    list = list.filter((b) => (b.baselineName || '').toLowerCase().includes(kw))
  }
  return list
})

// 时间轴视图：按创建时间倒序
const timelineBaselines = computed<BaselineSnapshot[]>(() => {
  return [...filteredBaselines.value].sort((a, b) => {
    const ta = new Date(a.createTime ?? 0).getTime()
    const tb = new Date(b.createTime ?? 0).getTime()
    return tb - ta
  })
})

// 列表视图分页
const pagedBaselines = computed<BaselineSnapshot[]>(() => {
  if (viewMode.value !== 'list') return filteredBaselines.value
  const start = (page.current - 1) * page.size
  return filteredBaselines.value.slice(start, start + page.size)
})

// ============ 项目名解析 ============
const currentProjectName = computed(() => {
  if (!query.projectId) return '全部项目'
  return projectOptions.value.find((p) => p.id === query.projectId)?.projectName ?? '未知项目'
})

const headerTitle = computed(() => `基线管理 · ${currentProjectName.value}`)

function projectName(id?: number): string {
  if (!id) return '-'
  return projectOptions.value.find((p) => p.id === id)?.projectName ?? `#${id}`
}

// ============ 数据加载 ============
async function loadProjectOptions() {
  try {
    const res = await listProjects({ page: 1, size: 200 })
    projectOptions.value = res.records ?? []
    if (res.records.length > 0 && query.projectId === undefined) {
      query.projectId = res.records[0].id
      await loadBaselines()
    }
  } catch {
    projectOptions.value = []
  }
}

async function loadBaselines() {
  if (!query.projectId) {
    baselines.value = []
    return
  }
  loading.value = true
  try {
    baselines.value = (await listBaselines(query.projectId)) ?? []
  } catch {
    baselines.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.current = 1
}

function handleReset() {
  query.status = undefined
  query.keyword = ''
  page.current = 1
}

function handlePageChange(p: number) {
  page.current = p
}

function handleSizeChange(s: number) {
  page.size = s
  page.current = 1
}

// ============ 跳转偏差分析 ============
function goDiff(row: BaselineSnapshot) {
  if (!row.id) return
  router.push(`/baseline/diff/${row.id}`)
}

// ============ 新建基线对话框 ============
const createVisible = ref(false)
const createForm = ref<{
  baselineName: string
  description: string
  projectId: number | undefined
  triggerApproval: boolean
}>({
  baselineName: '',
  description: '',
  projectId: undefined,
  triggerApproval: false
})

function openCreate() {
  createForm.value = {
    baselineName: `基线 ${new Date().toLocaleString()}`,
    description: '',
    projectId: query.projectId,
    triggerApproval: false
  }
  createVisible.value = true
}

async function handleCreate() {
  if (!createForm.value.projectId) {
    ElMessage.warning('请选择项目')
    return
  }
  if (!createForm.value.baselineName.trim()) {
    ElMessage.warning('请填写基线名称')
    return
  }
  try {
    await saveBaseline(createForm.value.projectId, createForm.value.baselineName.trim())
    ElMessage.success('新建基线成功（状态：草稿）')
    createVisible.value = false
    await loadBaselines()
    if (createForm.value.triggerApproval) {
      ElMessage.info('已勾选触发审批：请在新建基线后到偏差分析页发起变更审批')
    }
  } catch {
    /* handled by interceptor */
  }
}

// ============ 编辑（占位：当前 API 不支持更新基线元数据） ============
function handleEdit(row: BaselineSnapshot) {
  ElMessage.info(`编辑基线「${row.baselineName}」：当前 API 暂不支持，待后端补全`)
}

// ============ 归档（占位：当前 API 不支持直接归档） ============
async function handleArchive(row: BaselineSnapshot) {
  try {
    await ElMessageBox.confirm(
      `确认归档基线「${row.baselineName}」？归档后将变为「已取代」状态。`,
      '归档基线',
      { type: 'warning' }
    )
    ElMessage.info('归档接口待后端实现')
  } catch {
    /* cancelled */
  }
}

// ============ 删除（占位：当前 API 不支持删除基线） ============
async function handleDelete(row: BaselineSnapshot) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(
      `确认删除草稿基线「${row.baselineName}」？此操作不可恢复。`,
      '删除基线',
      { type: 'warning' }
    )
    ElMessage.info('删除接口待后端实现')
  } catch {
    /* cancelled */
  }
}

// ============ 监听 ============
watch(
  () => query.projectId,
  () => {
    page.current = 1
    loadBaselines()
  }
)

onMounted(loadProjectOptions)
</script>

<template>
  <div class="baseline-list-page">
    <PageHeader :title="headerTitle" description="计划基线快照管理，支持列表/时间轴双视图与偏差分析">
      <template #actions>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="list">列表视图</el-radio-button>
          <el-radio-button label="timeline">时间轴视图</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="openCreate">新建基线</el-button>
      </template>
    </PageHeader>

    <!-- 顶部统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card stat-active">
        <div class="stat-label">活跃基线数</div>
        <div class="stat-value">{{ stats.active }}</div>
        <div class="stat-extra">已批准 (APPROVED)</div>
      </div>
      <div class="stat-card stat-archived">
        <div class="stat-label">历史基线数</div>
        <div class="stat-value">{{ stats.archived }}</div>
        <div class="stat-extra">已取代 (SUPERSEDED)</div>
      </div>
      <div class="stat-card stat-tasks">
        <div class="stat-label">总任务数</div>
        <div class="stat-value">{{ stats.totalTasks }}</div>
        <div class="stat-extra">所有基线快照合计</div>
      </div>
      <div class="stat-card stat-pending">
        <div class="stat-label">待审批基线数</div>
        <div class="stat-value">{{ stats.draft }}</div>
        <div class="stat-extra">草稿 (DRAFT)</div>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select
        v-model="query.projectId"
        placeholder="选择项目"
        clearable
        filterable
        style="width: 220px"
      >
        <el-option v-for="p in projectOptions" :key="p.id" :label="p.projectName" :value="p.id!" />
      </el-select>
      <el-select
        v-model="query.status"
        placeholder="状态"
        clearable
        style="width: 140px"
      >
        <el-option
          v-for="s in statusOptions"
          :key="s.value"
          :label="s.label"
          :value="s.value"
        />
      </el-select>
      <el-input
        v-model="query.keyword"
        placeholder="搜索基线名"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 加载骨架屏 -->
    <SkeletonCard v-if="loading" :loading="true" :rows="6">
      <div />
    </SkeletonCard>

    <template v-else>
      <!-- 空状态 -->
      <EmptyState
        v-if="filteredBaselines.length === 0"
        title="暂无基线"
        description="当前筛选条件下没有基线数据，可调整筛选或点击「新建基线」生成项目计划快照"
        icon="Files"
      >
        <template #action>
          <el-button type="primary" @click="openCreate">新建基线</el-button>
        </template>
      </EmptyState>

      <!-- 列表视图 -->
      <el-card v-else-if="viewMode === 'list'" shadow="never" class="table-card">
        <el-table :data="pagedBaselines" border stripe>
          <el-table-column label="编号" width="80" align="center">
            <template #default="{ row }">#{{ row.id }}</template>
          </el-table-column>
          <el-table-column label="基线名称" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">{{ row.baselineName }}</template>
          </el-table-column>
          <el-table-column label="项目" width="160" show-overflow-tooltip>
            <template #default="{ row }">{{ projectName(row.projectId) }}</template>
          </el-table-column>
          <el-table-column label="版本" width="80" align="center">
            <template #default="{ row }">v{{ row.version ?? 1 }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMeta(row.status).tagType" size="small" effect="dark">
                {{ statusMeta(row.status).label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建人" width="110" align="center">
            <template #default="{ row }">{{ row.approvedBy ?? '-' }}</template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="任务数" width="90" align="center">
            <template #default="{ row }">{{ snapshotCount(row) }}</template>
          </el-table-column>
          <el-table-column label="偏差数" width="90" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="goDiff(row)">查看</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="goDiff(row)">偏差分析</el-button>
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button
                link
                type="warning"
                :disabled="row.status === 'SUPERSEDED'"
                @click="handleArchive(row)"
              >
                归档
              </el-button>
              <el-button
                link
                type="danger"
                :disabled="row.status !== 'DRAFT'"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-bar">
          <el-pagination
            v-model:current-page="page.current"
            v-model:page-size="page.size"
            :total="filteredBaselines.length"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </div>
      </el-card>

      <!-- 时间轴视图 -->
      <el-card v-else shadow="never" class="timeline-card">
        <el-timeline>
          <el-timeline-item
            v-for="item in timelineBaselines"
            :key="item.id"
            :timestamp="formatDateTime(item.createTime)"
            placement="top"
            :type="statusMeta(item.status).tagType === 'success'
              ? 'success'
              : statusMeta(item.status).tagType === 'info'
                ? 'info'
                : 'warning'"
          >
            <div class="timeline-node" @click="goDiff(item)">
              <div class="timeline-node-header">
                <span class="timeline-name">{{ item.baselineName }}</span>
                <el-tag :type="statusMeta(item.status).tagType" size="small" effect="dark">
                  {{ statusMeta(item.status).label }}
                </el-tag>
                <span class="timeline-version">v{{ item.version ?? 1 }}</span>
              </div>
              <div class="timeline-meta">
                <span>项目：{{ projectName(item.projectId) }}</span>
                <span>任务数：{{ snapshotCount(item) }}</span>
                <span v-if="item.approvedAt">批准：{{ formatDateTime(item.approvedAt) }}</span>
              </div>
              <div v-if="item.changeReason" class="timeline-reason">
                变更原因：{{ item.changeReason }}
              </div>
              <div class="timeline-actions">
                <el-button link type="primary" size="small" @click.stop="goDiff(item)">
                  查看偏差分析
                </el-button>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </template>

    <!-- 新建基线对话框 -->
    <el-dialog v-model="createVisible" title="新建基线" width="520px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="项目" required>
          <el-select
            v-model="createForm.projectId"
            placeholder="选择项目"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.projectName"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="基线名称" required>
          <el-input v-model="createForm.baselineName" placeholder="如：初始基线" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="基线说明（可选）"
          />
        </el-form-item>
        <el-form-item label="触发审批">
          <el-switch v-model="createForm.triggerApproval" />
          <span class="form-hint">开启后，若偏差超双阈值（天数/百分比）将提示发起变更审批</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.baseline-list-page {
  padding: 16px 24px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}
.stat-card {
  background: var(--pms-color-bg-card, #fff);
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
  border-radius: var(--pms-radius-lg, 8px);
  padding: 16px;
  box-shadow: var(--pms-shadow-card, 0 1px 2px rgba(0, 0, 0, 0.04));
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}
.stat-active::before {
  background: #10b981;
}
.stat-archived::before {
  background: #909399;
}
.stat-tasks::before {
  background: #3b82f6;
}
.stat-pending::before {
  background: #f59e0b;
}
.stat-label {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2937);
  line-height: 1.2;
}
.stat-extra {
  font-size: 11px;
  color: var(--pms-color-text-placeholder, #9ca3af);
  margin-top: 4px;
}

/* 筛选区 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  align-items: center;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 16px;
}
.pagination-bar {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

/* 时间轴卡片 */
.timeline-card {
  padding: 8px 16px;
}
.timeline-node {
  cursor: pointer;
  padding: 8px 12px;
  border: 1px solid var(--pms-color-border-light, #e5e7eb);
  border-radius: var(--pms-radius-md, 6px);
  transition: all 0.2s;
}
.timeline-node:hover {
  border-color: var(--pms-color-primary, #3b82f6);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}
.timeline-node-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.timeline-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2937);
}
.timeline-version {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  background: var(--pms-color-bg-page, #f3f4f6);
  padding: 2px 6px;
  border-radius: 4px;
}
.timeline-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 6px;
}
.timeline-reason {
  font-size: 12px;
  color: var(--pms-color-text-regular, #4b5563);
  padding: 6px 8px;
  background: var(--pms-color-bg-page, #f9fafb);
  border-radius: 4px;
  margin-bottom: 6px;
}
.timeline-actions {
  display: flex;
  gap: 8px;
}

.form-hint {
  margin-left: 8px;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
}
</style>
