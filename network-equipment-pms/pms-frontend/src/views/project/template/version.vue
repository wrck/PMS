<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  View,
  Back,
  DCaret,
  Plus,
  Promotion
} from '@element-plus/icons-vue'
import {
  listTemplateVersions,
  publishVersion,
  getTemplate,
  type ProjectTemplate,
  type ProjectTemplateVersion,
  type TemplateSnapshot
} from '@/api/project-template'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

defineOptions({ name: 'ProjectTemplateVersion' })

const route = useRoute()
const router = useRouter()

const templateId = Number(route.params.id)
const loading = ref(false)
const publishing = ref(false)
const template = ref<ProjectTemplate>()

const allVersions = ref<ProjectTemplateVersion[]>([])

// 按版本号倒序（v1.10.0 > v1.2.0；简单按字符串拆分比较）
const sortedVersions = computed(() => {
  return [...allVersions.value].sort((a, b) => compareVersion(b.version, a.version))
})

function compareVersion(v1: string, v2: string): number {
  const a = v1.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
  const b = v2.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
  const len = Math.max(a.length, b.length)
  for (let i = 0; i < len; i++) {
    const ai = a[i] ?? 0
    const bi = b[i] ?? 0
    if (ai !== bi) return ai - bi
  }
  return 0
}

function getStatusType(status?: string) {
  const map: Record<string, 'info' | 'success' | 'warning' | 'danger'> = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return map[status ?? ''] ?? 'info'
}

function getStatusLabel(status?: string) {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已归档'
  }
  return map[status ?? ''] ?? status ?? '-'
}

// 发布对话框
const publishDialogVisible = ref(false)
const publishForm = reactive({ version: '', changeLog: '' })

// 详情抽屉
const detailVisible = ref(false)
const detailVersion = ref<ProjectTemplateVersion>()

// 对比对话框
const compareDialogVisible = ref(false)
const compareLeft = ref<string>('')
const compareRight = ref<string>('')

const versionOptions = computed(() =>
  sortedVersions.value.map((v) => ({ label: `v${v.version}`, value: v.version }))
)

interface DiffItem {
  category: string
  type: 'added' | 'removed' | 'modified'
  detail: string
}

const diffResult = ref<DiffItem[]>([])

// ============== 加载数据 ==============

async function loadData() {
  if (!templateId) return
  loading.value = true
  try {
    template.value = await getTemplate(templateId)
    const res = await listTemplateVersions(templateId, 1, 200)
    allVersions.value = res?.records ?? []
  } catch {
    allVersions.value = []
  } finally {
    loading.value = false
  }
}

// ============== 详情抽屉 ==============

function viewDetail(v: ProjectTemplateVersion) {
  detailVersion.value = v
  detailVisible.value = true
}

function snapshotSummary(snap?: TemplateSnapshot) {
  if (!snap) return []
  return [
    { label: '阶段', count: snap.phases?.length ?? 0 },
    { label: '任务', count: snap.tasks?.length ?? 0 },
    { label: '交付件', count: snap.deliverables?.length ?? 0 },
    { label: '依赖', count: snap.dependencies?.length ?? 0 },
    { label: '审批计划', count: snap.approvalPlans?.length ?? 0 },
    { label: '里程碑', count: snap.milestones?.length ?? 0 }
  ]
}

// ============== 回滚 ==============

function handleRollback(v: ProjectTemplateVersion) {
  ElMessageBox.confirm(
    `确认回滚到版本 v${v.version} 吗？该操作将以当前快照为基础发布一个新版本。`,
    '回滚确认',
    { type: 'warning' }
  )
    .then(async () => {
      if (!template.value) return
      publishing.value = true
      try {
        const newVersion = `v${nextVersionString()}`
        await publishVersion(templateId, {
          version: newVersion,
          snapshot: v.snapshotJson ?? {},
          changeLog: `回滚自 v${v.version}`
        })
        ElMessage.success(`已回滚到 v${v.version}（新版本 ${newVersion}）`)
        await loadData()
      } finally {
        publishing.value = false
      }
    })
    .catch(() => {})
}

function nextVersionString() {
  if (allVersions.value.length === 0) return '1.0.0'
  const latest = sortedVersions.value[0]
  if (!latest) return '1.0.0'
  const parts = latest.version.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
  parts[parts.length - 1] = (parts[parts.length - 1] ?? 0) + 1
  return parts.join('.')
}

// ============== 对比 ==============

function openCompare() {
  if (allVersions.value.length < 2) {
    ElMessage.warning('至少需要 2 个版本才能对比')
    return
  }
  compareLeft.value = sortedVersions.value[1]?.version ?? ''
  compareRight.value = sortedVersions.value[0]?.version ?? ''
  computeDiff()
  compareDialogVisible.value = true
}

function computeDiff() {
  const left = allVersions.value.find((v) => v.version === compareLeft.value)
  const right = allVersions.value.find((v) => v.version === compareRight.value)
  if (!left || !right) {
    diffResult.value = []
    return
  }
  const result: DiffItem[] = []
  const leftSnap = left.snapshotJson ?? {}
  const rightSnap = right.snapshotJson ?? {}

  const categories: { key: keyof TemplateSnapshot; label: string; idField: string }[] = [
    { key: 'phases', label: '阶段', idField: 'phaseCode' },
    { key: 'tasks', label: '任务', idField: 'taskCode' },
    { key: 'deliverables', label: '交付件', idField: 'name' },
    { key: 'dependencies', label: '依赖', idField: 'id' },
    { key: 'approvalPlans', label: '审批计划', idField: 'name' },
    { key: 'milestones', label: '里程碑', idField: 'name' }
  ]

  for (const cat of categories) {
    const leftList = (leftSnap[cat.key] as any[]) ?? []
    const rightList = (rightSnap[cat.key] as any[]) ?? []
    const leftIds = new Set(leftList.map((i: any) => i?.[cat.idField] ?? i?.id ?? ''))
    const rightIds = new Set(rightList.map((i: any) => i?.[cat.idField] ?? i?.id ?? ''))
    const added = rightList.filter(
      (i: any) => !leftIds.has(i?.[cat.idField] ?? i?.id ?? '')
    )
    const removed = leftList.filter(
      (i: any) => !rightIds.has(i?.[cat.idField] ?? i?.id ?? '')
    )
    const common = rightList.filter((i: any) => {
      const id = i?.[cat.idField] ?? i?.id ?? ''
      return leftIds.has(id)
    })
    added.forEach((i: any) =>
      result.push({
        category: cat.label,
        type: 'added',
        detail: `新增：${i?.[cat.idField] ?? i?.name ?? JSON.stringify(i)}`
      })
    )
    removed.forEach((i: any) =>
      result.push({
        category: cat.label,
        type: 'removed',
        detail: `删除：${i?.[cat.idField] ?? i?.name ?? JSON.stringify(i)}`
      })
    )
    common.forEach((i: any) => {
      const id = i?.[cat.idField] ?? i?.id ?? ''
      const leftItem = leftList.find((l: any) => (l?.[cat.idField] ?? l?.id) === id)
      if (leftItem && JSON.stringify(leftItem) !== JSON.stringify(i)) {
        result.push({
          category: cat.label,
          type: 'modified',
          detail: `修改：${id}`
        })
      }
    })
  }
  diffResult.value = result
}

function diffTypeTag(type: DiffItem['type']) {
  return { added: 'success', removed: 'danger', modified: 'warning' }[type] as
    | 'success'
    | 'danger'
    | 'warning'
}

function diffTypeLabel(type: DiffItem['type']) {
  return { added: '新增', removed: '删除', modified: '修改' }[type]
}

// ============== 发布 ==============

async function handlePublish() {
  if (!publishForm.version.trim()) {
    ElMessage.warning('请填写版本号')
    return
  }
  publishing.value = true
  try {
    // 发布当前模板（基础信息 + 空快照，实际快照由 form.vue 编辑产生）
    await publishVersion(templateId, {
      version: publishForm.version,
      snapshot: {},
      changeLog: publishForm.changeLog
    })
    ElMessage.success('版本已发布')
    publishDialogVisible.value = false
    publishForm.version = ''
    publishForm.changeLog = ''
    await loadData()
  } finally {
    publishing.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="template-version-page">
    <PageHeader
      :title="`版本管理${template ? ' · ' + template.templateName : ''}`"
      description="按版本时间轴展示，支持详情查看、回滚与对比"
    >
      <template #actions>
        <el-button :icon="ArrowLeft" @click="router.push('/project/template')">返回列表</el-button>
        <el-button :icon="DCaret" @click="openCompare">版本对比</el-button>
        <el-button
          type="primary"
          :icon="Plus"
          @click="publishDialogVisible = true"
        >
          发布新版本
        </el-button>
      </template>
    </PageHeader>

    <SkeletonCard v-if="loading" :loading="true" :rows="6" />

    <EmptyState
      v-else-if="sortedVersions.length === 0"
      title="暂无版本记录"
      description="该模板尚未发布任何版本"
    >
      <template #action>
        <el-button type="primary" :icon="Plus" @click="publishDialogVisible = true">
          发布第一个版本
        </el-button>
      </template>
    </EmptyState>

    <el-card v-else shadow="never" class="timeline-card">
      <el-timeline>
        <el-timeline-item
          v-for="v in sortedVersions"
          :key="v.id"
          :type="getStatusType(v.status)"
          :timestamp="v.publishedAt || v.createTime || ''"
          placement="top"
        >
          <el-card shadow="hover" class="version-card">
            <div class="version-header">
              <div class="version-title">
                <h3>v{{ v.version }}</h3>
                <el-tag :type="getStatusType(v.status)" size="small" effect="light" round>
                  {{ getStatusLabel(v.status) }}
                </el-tag>
              </div>
              <div class="version-meta">
                <span v-if="v.publishedBy">发布人 ID：{{ v.publishedBy }}</span>
              </div>
            </div>
            <p class="version-log">{{ v.changeLog || '暂无变更说明' }}</p>
            <div class="version-stats">
              <span
                v-for="item in snapshotSummary(v.snapshotJson)"
                :key="item.label"
                class="stat-chip"
              >
                {{ item.label }} <strong>{{ item.count }}</strong>
              </span>
            </div>
            <div class="version-actions">
              <el-button text size="small" :icon="View" @click="viewDetail(v)">
                查看详情
              </el-button>
              <el-button
                text
                size="small"
                :icon="Back"
                :disabled="v.status !== 'PUBLISHED'"
                :loading="publishing"
                @click="handleRollback(v)"
              >
                回滚到此版本
              </el-button>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="版本详情" size="50%">
      <template v-if="detailVersion">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="版本号">v{{ detailVersion.version }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailVersion.status)" size="small">
              {{ getStatusLabel(detailVersion.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ detailVersion.publishedAt ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="发布人 ID">
            {{ detailVersion.publishedBy ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ detailVersion.createTime ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="版本 ID">{{ detailVersion.id }}</el-descriptions-item>
          <el-descriptions-item label="变更说明" :span="2">
            {{ detailVersion.changeLog || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <h4 class="snapshot-title">快照内容</h4>
        <el-table :data="snapshotSummary(detailVersion.snapshotJson)" border stripe size="small">
          <el-table-column prop="label" label="类别" />
          <el-table-column prop="count" label="数量" width="120" align="center" />
        </el-table>

        <h4 class="snapshot-title">快照 JSON</h4>
        <pre class="snapshot-json">{{ JSON.stringify(detailVersion.snapshotJson, null, 2) }}</pre>
      </template>
    </el-drawer>

    <!-- 对比对话框 -->
    <el-dialog v-model="compareDialogVisible" title="版本对比" width="640px">
      <el-form :inline="true" label-width="80px">
        <el-form-item label="左版本">
          <el-select v-model="compareLeft" style="width: 180px" @change="computeDiff">
            <el-option v-for="o in versionOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="右版本">
          <el-select v-model="compareRight" style="width: 180px" @change="computeDiff">
            <el-option v-for="o in versionOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-empty v-if="diffResult.length === 0" description="两个版本无差异" />
      <el-table v-else :data="diffResult" border stripe size="small" max-height="400">
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="diffTypeTag(row.type)" size="small">{{ diffTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="detail" label="详情" min-width="240" show-overflow-tooltip />
      </el-table>

      <template #footer>
        <el-button @click="compareDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 发布对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布新版本" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="如 v1.0.0" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input
            v-model="publishForm.changeLog"
            type="textarea"
            :rows="4"
            placeholder="说明本次发布的变更内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="Promotion" :loading="publishing" @click="handlePublish">
          发布
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.template-version-page {
  padding: 16px 24px;
}

.timeline-card {
  border-radius: var(--pms-radius-lg);
}

.version-card {
  border-radius: var(--pms-radius-md);
  transition: all var(--pms-transition-fast);
}
.version-card:hover {
  box-shadow: var(--pms-shadow-card-hover);
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.version-title {
  display: flex;
  align-items: center;
  gap: 12px;
}
.version-title h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin: 0;
}
.version-meta {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}

.version-log {
  font-size: 13px;
  color: var(--pms-color-text-regular);
  margin: 0 0 12px;
  line-height: 1.6;
}

.version-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}
.stat-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-full);
  color: var(--pms-color-text-secondary);
}
.stat-chip strong {
  color: var(--pms-color-primary);
}

.version-actions {
  display: flex;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--pms-color-border-light);
}

.snapshot-title {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: var(--pms-color-text-primary);
}
.snapshot-json {
  background: var(--pms-color-bg-page);
  padding: 12px;
  border-radius: var(--pms-radius-md);
  font-size: 12px;
  font-family: var(--pms-font-family-mono);
  overflow: auto;
  max-height: 320px;
}
</style>
