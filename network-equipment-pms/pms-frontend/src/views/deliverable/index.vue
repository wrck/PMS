<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  DELIVERABLE_STATUS_LABELS,
  loadDeliverableTypes,
  listFullDeliverables,
  translateDeliverableType,
  type Deliverable,
  type DeliverableStatus
} from '@/api/deliverable'
import type { SysDictItem } from '@/api/system'
import PageHeader from '@/components/common/PageHeader.vue'
import type { EpTagType } from '@/types'

// 工作区以 props 传入项目 ID 时，自动加载该项目的交付件并隐藏手动输入区。
// 独立路由进入时（无 props.projectId）保留手动输入项目 ID 的查询方式。
// 双视图：模板配置（templateInherited=true，只读预设）+ 实际交付（全部记录，含产物状态）。
const props = defineProps<{ projectId?: number }>()

const router = useRouter()

const loading = ref(false)
const tableData = ref<Deliverable[]>([])
const localProjectId = ref<number | undefined>(undefined)
/** 当前视图：template=模板预设配置，actual=实际交付产物 */
const viewMode = ref<'template' | 'actual'>('template')

// 实际生效的项目 ID：优先用 props 传入，其次用手动输入
const effectiveProjectId = computed(() => props.projectId ?? localProjectId.value)

// 是否嵌入工作区（隐藏项目 ID 输入框）
const embedded = computed(() => typeof props.projectId === 'number')

const headerDesc = computed(() =>
  embedded.value
    ? `项目 ${effectiveProjectId.value} 的交付件管理`
    : '查看项目交付件配置与实际交付产物，文件上传与完善请在阶段/任务过程中进行'
)

// 交付件类型字典项（用于表格列显示翻译）
const typeDictItems = ref<SysDictItem[]>([])

// 已就绪状态：PUBLISHED 及之后（PUBLISHED / REFERENCED / ARCHIVED）
const READY_STATUSES: ReadonlySet<DeliverableStatus> = new Set([
  'PUBLISHED',
  'REFERENCED',
  'ARCHIVED'
])

function isReady(status?: string): boolean {
  return !!status && (READY_STATUSES as ReadonlySet<string>).has(status)
}

// 状态标签 + 颜色
function statusMeta(status?: string): { tagType: EpTagType; label: string } {
  if (!status) return { tagType: 'info', label: '-' }
  const label = (DELIVERABLE_STATUS_LABELS as Record<string, string>)[status] ?? status
  let tagType: EpTagType = 'info'
  switch (status) {
    case 'SUBMITTED':
      tagType = 'warning'
      break
    case 'REVIEWED':
    case 'SIGNED':
      tagType = 'primary'
      break
    case 'PUBLISHED':
    case 'REFERENCED':
      tagType = 'success'
      break
    default:
      tagType = 'info'
  }
  return { tagType, label }
}

// 顶部统计：总数 / 必需 / 已就绪
const stats = computed(() => {
  const list = tableData.value
  return {
    total: list.length,
    mandatory: list.filter((d) => d.mandatory).length,
    ready: list.filter((d) => isReady(d.status)).length
  }
})

// ============== 数据加载 ==============
async function loadData() {
  if (!effectiveProjectId.value) {
    ElMessage.warning('请输入项目 ID')
    return
  }
  loading.value = true
  try {
    // 模板配置视图只查 templateInherited=true，实际交付视图查全部
    const params =
      viewMode.value === 'template'
        ? { projectId: effectiveProjectId.value, templateInherited: true }
        : { projectId: effectiveProjectId.value }
    const res = await listFullDeliverables(params)
    tableData.value = res ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadData()
}

function goDetail(id?: number) {
  if (id != null) router.push({ name: 'DeliverableDetail', params: { id: String(id) } })
}

// 视图切换时重新加载
watch(viewMode, () => {
  if (effectiveProjectId.value) loadData()
})

// 嵌入工作区时：项目 ID 变化或首次进入自动加载
watch(
  () => props.projectId,
  (val) => {
    if (typeof val === 'number') loadData()
  }
)

onMounted(async () => {
  await loadDeliverableTypes().then((items) => {
    typeDictItems.value = items
  })
  if (embedded.value) loadData()
})
</script>

<template>
  <div class="page-container">
    <PageHeader title="项目交付件" :description="headerDesc">
      <template #actions>
        <el-button :icon="'Refresh'" :loading="loading" @click="loadData">刷新</el-button>
      </template>
    </PageHeader>

    <!-- 嵌入工作区时不显示手动输入区；独立路由进入时保留 -->
    <el-form v-if="!embedded" :inline="true" @submit.prevent>
      <el-form-item label="项目 ID">
        <el-input-number
          v-model="localProjectId"
          :min="1"
          :controls="false"
          placeholder="请输入项目 ID"
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 双视图切换 -->
    <el-tabs v-model="viewMode" class="view-tabs">
      <el-tab-pane label="模板配置" name="template">
        <template #label>
          <span>模板配置</span>
          <el-tag size="small" type="info" class="tab-count">{{ stats.total }}</el-tag>
        </template>
      </el-tab-pane>
      <el-tab-pane label="实际交付" name="actual">
        <template #label>
          <span>实际交付</span>
          <el-tag size="small" type="success" class="tab-count">{{ stats.ready }}</el-tag>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card stat-total">
        <div class="stat-label">
          {{ viewMode === 'template' ? '模板预设交付件' : '交付件总数' }}
        </div>
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-extra">
          {{ viewMode === 'template' ? '从项目模板继承' : '含模板预设 + 过程新增' }}
        </div>
      </div>
      <div class="stat-card stat-mandatory">
        <div class="stat-label">必需交付件</div>
        <div class="stat-value">{{ stats.mandatory }}</div>
        <div class="stat-extra">mandatory = true</div>
      </div>
      <div class="stat-card stat-ready">
        <div class="stat-label">已就绪</div>
        <div class="stat-value">{{ stats.ready }}</div>
        <div class="stat-extra">PUBLISHED 及以上</div>
      </div>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column type="index" label="#" width="55" align="center" />
      <el-table-column label="交付件名称" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <el-link type="primary" @click="goDetail(row.id)">{{ row.deliverableName }}</el-link>
          <el-tag v-if="row.templateInherited" size="small" type="info" class="inherited-tag">模板预设</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="类型" min-width="120">
        <template #default="{ row }">
          {{ translateDeliverableType(row.deliverableType) }}
        </template>
      </el-table-column>
      <el-table-column v-if="viewMode === 'actual'" label="引用实体" min-width="140">
        <template #default="{ row }">
          <span v-if="row.refEntityType && row.refEntityId">
            {{ typeDictItems.find((it) => it.itemValue === row.refEntityType)?.itemText ?? row.refEntityType }}
            #{{ row.refEntityId }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="所属阶段" width="100" align="center">
        <template #default="{ row }">{{ row.phaseId ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="必需" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.mandatory ? 'warning' : 'info'" size="small">
            {{ row.mandatory ? '必需' : '可选' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="statusMeta(row.status).tagType" size="small">
            {{ statusMeta(row.status).label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="版本" width="90" align="center">
        <template #default="{ row }">
          {{ row.currentVersion ? `v${row.currentVersion}` : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">查看详情</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty
          :description="
            viewMode === 'template'
              ? '当前项目暂无模板预设交付件'
              : embedded
                ? '当前项目暂无交付件'
                : '请输入项目 ID 并查询'
          "
        />
      </template>
    </el-table>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.view-tabs :deep(.tab-count) {
  margin-left: 6px;
}

.inherited-tag {
  margin-left: 8px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
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
.stat-total::before {
  background: #3b82f6;
}
.stat-mandatory::before {
  background: #f59e0b;
}
.stat-ready::before {
  background: #10b981;
}
.stat-label {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #6b7280);
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #111827);
  line-height: 1.2;
}
.stat-extra {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #9ca3af);
  margin-top: 6px;
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
