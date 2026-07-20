<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Clock,
  CopyDocument,
  Delete,
  Edit,
  Files,
  Plus,
  Refresh,
  Search
} from '@element-plus/icons-vue'
import {
  listTemplates,
  deleteTemplate,
  type ProjectTemplate
} from '@/api/project-template'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()

const loading = ref(false)
const templates = ref<ProjectTemplate[]>([])

const query = reactive({
  templateName: '',
  category: '',
  status: ''
})

const categoryOptions = [
  { value: 'IMPLEMENT', label: '实施' },
  { value: 'MAINTENANCE', label: '维护' },
  { value: 'CONSULTING', label: '咨询' }
]

const statusOptions = [
  { value: 'DRAFT', label: '草稿', type: 'info' as const },
  { value: 'PUBLISHED', label: '已发布', type: 'success' as const },
  { value: 'DEPRECATED', label: '已废弃', type: 'danger' as const }
]

const filteredTemplates = computed(() => {
  return templates.value.filter((t) => {
    if (query.status && t.status !== query.status) return false
    if (query.category && t.category !== query.category) return false
    if (query.templateName) {
      const kw = query.templateName.trim().toLowerCase()
      if (
        !t.templateName?.toLowerCase().includes(kw) &&
        !t.templateCode?.toLowerCase().includes(kw)
      ) {
        return false
      }
    }
    return true
  })
})

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', type: 'info' as const }
}

function getCategoryLabel(category?: string) {
  return categoryOptions.find((c) => c.value === category)?.label ?? category ?? '-'
}

async function loadData() {
  loading.value = true
  try {
    const res = await listTemplates({ page: 1, size: 200 })
    templates.value = res?.records ?? []
  } catch {
    templates.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  // 本地筛选，无需重新拉取
}

function handleReset() {
  query.templateName = ''
  query.category = ''
  query.status = ''
}

function goCreate() {
  router.push('/project/template/form')
}

function goEdit(id?: number) {
  if (!id) return
  router.push(`/project/template/form/${id}`)
}

function goVersion(id?: number) {
  if (!id) return
  router.push(`/project/template/version/${id}`)
}

async function handleCopy(tpl: ProjectTemplate) {
  // 复制：基于当前模板打开新建表单（通过 router state 传递来源）
  router.push({
    path: '/project/template/form',
    state: { copyFrom: tpl.id }
  })
}

function handleDelete(tpl: ProjectTemplate) {
  if (!tpl.id) return
  ElMessageBox.confirm(
    `确认删除模板「${tpl.templateName}」吗？仅 DRAFT 状态可删除。`,
    '删除确认',
    { type: 'warning' }
  )
    .then(async () => {
      await deleteTemplate(tpl.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="template-list-page">
    <PageHeader title="项目模板" description="管理项目模板，支持版本化发布">
      <template #actions>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        <el-button v-permission="'project:template:add'" type="primary" :icon="Plus" @click="goCreate">新建模板</el-button>
      </template>
    </PageHeader>

    <div class="filter-bar">
      <el-input
        v-model="query.templateName"
        placeholder="按名称 / 编码搜索"
        clearable
        :prefix-icon="Search"
        style="width: 240px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="query.category"
        placeholder="全部类别"
        clearable
        style="width: 160px"
      >
        <el-option v-for="opt in categoryOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
      </el-select>
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 160px">
        <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
      </el-select>
      <el-button :icon="Refresh" link @click="handleReset">重置</el-button>
      <span class="filter-tip">共 {{ filteredTemplates.length }} 个模板</span>
    </div>

    <SkeletonCard v-if="loading" :loading="true" :rows="4" class="grid-skeleton" />

    <EmptyState
      v-else-if="filteredTemplates.length === 0"
      title="暂无模板"
      :description="templates.length === 0 ? '点击右上角「新建模板」开始创建' : '没有符合筛选条件的模板'"
    >
      <template #action>
        <el-button v-if="templates.length === 0" v-permission="'project:template:add'" type="primary" :icon="Plus" @click="goCreate">
          新建模板
        </el-button>
      </template>
    </EmptyState>

    <div v-else class="template-grid">
      <el-card
        v-for="tpl in filteredTemplates"
        :key="tpl.id"
        shadow="hover"
        class="template-card"
      >
        <div class="card-header">
          <div class="card-icon">
            <el-icon :size="22"><Files /></el-icon>
          </div>
          <el-tag :type="getStatusMeta(tpl.status).type" size="small" effect="light" round>
            {{ getStatusMeta(tpl.status).label }}
          </el-tag>
        </div>

        <h3 class="card-title" :title="tpl.templateName">{{ tpl.templateName }}</h3>

        <div class="card-meta">
          <span class="meta-item"><label>编码：</label>{{ tpl.templateCode || '-' }}</span>
          <span class="meta-item"><label>分类：</label>{{ getCategoryLabel(tpl.category) }}</span>
        </div>

        <p class="card-desc">{{ tpl.description || '暂无描述' }}</p>

        <div class="card-stats">
          <div class="stat-item">
            <span class="stat-label">创建时间</span>
            <span class="stat-value">{{ tpl.createTime?.substring(0, 10) ?? '-' }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">更新时间</span>
            <span class="stat-value">{{ tpl.updateTime?.substring(0, 10) ?? '-' }}</span>
          </div>
        </div>

        <div class="card-actions" @click.stop>
          <el-button v-permission="'project:template:add'" text size="small" :icon="Edit" @click="goEdit(tpl.id)">编辑</el-button>
          <el-button text size="small" :icon="Clock" @click="goVersion(tpl.id)">版本</el-button>
          <el-button v-permission="'project:template:add'" text size="small" :icon="CopyDocument" @click="handleCopy(tpl)">复制</el-button>
          <el-button v-permission="'project:template:add'" text size="small" type="danger" :icon="Delete" @click="handleDelete(tpl)">
            删除
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.template-list-page {
  padding: 16px 24px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.filter-tip {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-left: auto;
}

.grid-skeleton {
  padding: 24px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.template-card {
  cursor: pointer;
  transition: all var(--pms-transition-fast);
  border-radius: var(--pms-radius-lg);
}
.template-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--pms-shadow-card-hover);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--pms-radius-md);
  background: var(--pms-color-primary-light-9);
  color: var(--pms-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 10px;
  flex-wrap: wrap;
}
.card-meta label {
  color: var(--pms-color-text-placeholder);
  margin-right: 2px;
}

.card-desc {
  font-size: 13px;
  color: var(--pms-color-text-regular);
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 38px;
}

.card-stats {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-top: 1px dashed var(--pms-color-border-light);
  margin-bottom: 8px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.stat-label {
  font-size: 11px;
  color: var(--pms-color-text-placeholder);
}
.stat-value {
  font-size: 13px;
  color: var(--pms-color-text-regular);
}

.card-actions {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  padding-top: 4px;
  border-top: 1px solid var(--pms-color-border-light);
}
</style>
