<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  addDeliverableReference,
  addDeliverableSignature,
  DELIVERABLE_STATUS_LABELS,
  DELIVERABLE_STATUS_ORDER,
  getDeliverable,
  listDeliverableReferences,
  listDeliverableSignatures,
  listDeliverableVersions,
  type Deliverable,
  type DeliverableReference,
  type DeliverableSignature,
  type DeliverableStatus,
  type DeliverableVersion
} from '@/api/deliverable'
import type { EpTagType } from '@/types'

defineOptions({ name: 'DeliverableDetail' })

const route = useRoute()
const router = useRouter()

const deliverableId = computed(() => Number(route.params.id))
const loading = ref(false)
const deliverable = ref<Deliverable | null>(null)

// 4 个 tab 的数据
const versions = ref<DeliverableVersion[]>([])
const signatures = ref<DeliverableSignature[]>([])
const references = ref<DeliverableReference[]>([])
const activeTab = ref<'basic' | 'versions' | 'signatures' | 'references'>('basic')

// ============ 状态元数据 ============
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

// 当前状态在 7 态顺序中的索引（用于状态流可视化）
const currentIndex = computed(() => {
  const s = deliverable.value?.status as DeliverableStatus | undefined
  if (!s) return -1
  return DELIVERABLE_STATUS_ORDER.indexOf(s)
})

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 19)
}

// ============ 数据加载 ============
async function loadDetail() {
  if (!deliverableId.value) return
  loading.value = true
  try {
    deliverable.value = await getDeliverable(deliverableId.value)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadVersions() {
  if (!deliverableId.value) return
  try {
    versions.value = (await listDeliverableVersions(deliverableId.value)) ?? []
  } catch {
    /* handled by interceptor */
  }
}

async function loadSignatures() {
  if (!deliverableId.value) return
  try {
    signatures.value = (await listDeliverableSignatures(deliverableId.value)) ?? []
  } catch {
    /* handled by interceptor */
  }
}

async function loadReferences() {
  if (!deliverableId.value) return
  try {
    references.value = (await listDeliverableReferences(deliverableId.value)) ?? []
  } catch {
    /* handled by interceptor */
  }
}

async function onTabChange(tab: string) {
  if (tab === 'versions' && versions.value.length === 0) await loadVersions()
  else if (tab === 'signatures' && signatures.value.length === 0) await loadSignatures()
  else if (tab === 'references' && references.value.length === 0) await loadReferences()
}

// ============ 新增签名 ============
const sigDialogVisible = ref(false)
const sigForm = ref<Partial<DeliverableSignature>>({
  signerId: 0,
  signerName: '',
  signerRole: '',
  signatureType: 'ELECTRONIC',
  signatureData: ''
})

function openAddSignature() {
  sigForm.value = {
    signerId: 0,
    signerName: '',
    signerRole: '',
    signatureType: 'ELECTRONIC',
    signatureData: ''
  }
  sigDialogVisible.value = true
}

async function handleAddSignature() {
  if (!sigForm.value.signerId) {
    ElMessage.warning('请填写签核人 ID')
    return
  }
  try {
    await addDeliverableSignature(deliverableId.value, sigForm.value)
    ElMessage.success('签名记录已新增')
    sigDialogVisible.value = false
    await loadSignatures()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 新增引用 ============
const refDialogVisible = ref(false)
const refForm = ref<Partial<DeliverableReference>>({
  referenceType: 'TASK',
  referencedById: 0,
  referencedByName: '',
  targetDeliverableId: undefined
})

function openAddReference() {
  refForm.value = {
    referenceType: 'TASK',
    referencedById: 0,
    referencedByName: '',
    targetDeliverableId: undefined
  }
  refDialogVisible.value = true
}

async function handleAddReference() {
  if (!refForm.value.referencedById) {
    ElMessage.warning('请填写引用方业务 ID')
    return
  }
  try {
    await addDeliverableReference(deliverableId.value, refForm.value)
    ElMessage.success('引用关系已新增（若源为 PUBLISHED 将自动流转为 REFERENCED）')
    refDialogVisible.value = false
    await loadReferences()
    await loadDetail() // 状态可能已流转为 REFERENCED
  } catch {
    /* handled by interceptor */
  }
}

// ============ 版本下载 ============
function handleDownloadVersion(v: DeliverableVersion) {
  if (!v.filePath) {
    ElMessage.warning('该版本无文件路径')
    return
  }
  // 简单实现：新窗口打开文件路径（实际可对接附件下载接口）
  window.open(v.filePath, '_blank')
}

function goBack() {
  router.back()
}

onMounted(loadDetail)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header @back="goBack">
      <template #content>
        <span class="page-title">
          交付件详情：{{ deliverable?.deliverableName ?? '-' }}
        </span>
      </template>
    </el-page-header>

    <el-card v-if="deliverable" shadow="never">
      <!-- 7 态状态流可视化 -->
      <div class="status-flow">
        <div
          v-for="(s, idx) in DELIVERABLE_STATUS_ORDER"
          :key="s"
          class="flow-node"
          :class="{
            'is-current': idx === currentIndex,
            'is-passed': idx < currentIndex,
            'is-pending': idx > currentIndex
          }"
        >
          <div class="node-circle">{{ idx < currentIndex ? '✓' : idx + 1 }}</div>
          <div class="node-label">{{ DELIVERABLE_STATUS_LABELS[s] }}</div>
          <div v-if="idx < DELIVERABLE_STATUS_ORDER.length - 1" class="node-arrow">→</div>
        </div>
      </div>

      <el-tabs v-model="activeTab" @tab-change="onTabChange" class="detail-tabs">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="交付件 ID">{{ deliverable.id }}</el-descriptions-item>
            <el-descriptions-item label="交付件名称">{{ deliverable.deliverableName }}</el-descriptions-item>
            <el-descriptions-item label="项目 ID">{{ deliverable.projectId }}</el-descriptions-item>
            <el-descriptions-item label="阶段 ID">{{ deliverable.phaseId ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ deliverable.deliverableType ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusMeta(deliverable.status).tagType" size="small">
                {{ statusMeta(deliverable.status).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前版本">v{{ deliverable.currentVersion ?? 1 }}</el-descriptions-item>
            <el-descriptions-item label="必需交付件">
              <el-tag v-if="deliverable.mandatory" type="warning" size="small">必需</el-tag>
              <el-tag v-else type="info" size="small">可选</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="签核角色">{{ deliverable.approverRole ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="文件路径">{{ deliverable.filePath ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{ formatDateTime(deliverable.publishedAt) }}</el-descriptions-item>
            <el-descriptions-item label="归档时间">{{ formatDateTime(deliverable.archivedAt) }}</el-descriptions-item>
            <el-descriptions-item label="创建人">{{ deliverable.createBy ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(deliverable.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新人">{{ deliverable.updateBy ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDateTime(deliverable.updateTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- Tab 2: 版本历史 -->
        <el-tab-pane :label="`版本历史 (${versions.length})`" name="versions">
          <el-table :data="versions" border stripe>
            <el-table-column prop="versionNo" label="版本号" width="90" align="center">
              <template #default="{ row }">v{{ row.versionNo }}</template>
            </el-table-column>
            <el-table-column prop="filePath" label="文件路径" min-width="200" />
            <el-table-column prop="fileChecksum" label="SHA256" width="160" align="center">
              <template #default="{ row }">{{ row.fileChecksum ?? '-' }}</template>
            </el-table-column>
            <el-table-column prop="uploadedBy" label="上传人" width="100" align="center">
              <template #default="{ row }">{{ row.uploadedBy ?? '-' }}</template>
            </el-table-column>
            <el-table-column label="上传时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.uploadedAt) }}</template>
            </el-table-column>
            <el-table-column prop="changeLog" label="变更说明" min-width="160" />
            <el-table-column label="版本状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusMeta(row.status).tagType" size="small">
                  {{ statusMeta(row.status).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDownloadVersion(row)">下载</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无版本记录" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- Tab 3: 签名记录 -->
        <el-tab-pane :label="`签名记录 (${signatures.length})`" name="signatures">
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="openAddSignature">新增签名</el-button>
          </div>
          <el-table :data="signatures" border stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="versionNo" label="版本" width="80" align="center">
              <template #default="{ row }">v{{ row.versionNo ?? '-' }}</template>
            </el-table-column>
            <el-table-column prop="signerName" label="签核人" width="120" />
            <el-table-column prop="signerRole" label="角色" width="120">
              <template #default="{ row }">{{ row.signerRole ?? '-' }}</template>
            </el-table-column>
            <el-table-column prop="signatureType" label="类型" width="110" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ row.signatureType ?? 'ELECTRONIC' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="signatureData" label="签名数据" min-width="160" />
            <el-table-column label="签核时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.signedAt) }}</template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无签名记录" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- Tab 4: 引用关系 -->
        <el-tab-pane :label="`引用关系 (${references.length})`" name="references">
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="openAddReference">新增引用</el-button>
          </div>
          <el-table :data="references" border stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="referenceType" label="引用方类型" width="120" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ row.referenceType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="referencedById" label="引用方 ID" width="110" align="center" />
            <el-table-column prop="referencedByName" label="引用方名称" min-width="160">
              <template #default="{ row }">{{ row.referencedByName ?? '-' }}</template>
            </el-table-column>
            <el-table-column prop="targetDeliverableId" label="目标交付件 ID" width="140" align="center">
              <template #default="{ row }">{{ row.targetDeliverableId ?? '-' }}</template>
            </el-table-column>
            <el-table-column label="创建时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无引用记录" />
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增签名弹窗 -->
    <el-dialog v-model="sigDialogVisible" title="新增签名记录" width="520px" destroy-on-close>
      <el-form :model="sigForm" label-width="100px">
        <el-form-item label="签核人 ID" required>
          <el-input-number v-model="sigForm.signerId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="签核人姓名">
          <el-input v-model="sigForm.signerName" placeholder="冗余字段，便于展示" />
        </el-form-item>
        <el-form-item label="签核角色">
          <el-input v-model="sigForm.signerRole" placeholder="如：技术负责人" />
        </el-form-item>
        <el-form-item label="签名类型">
          <el-select v-model="sigForm.signatureType" style="width: 100%">
            <el-option label="电子签名" value="ELECTRONIC" />
            <el-option label="印章" value="STAMP" />
            <el-option label="数字签名" value="DIGITAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="签名数据">
          <el-input v-model="sigForm.signatureData" placeholder="证书指纹/印章图URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sigDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSignature">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增引用弹窗 -->
    <el-dialog v-model="refDialogVisible" title="新增引用关系" width="520px" destroy-on-close>
      <div class="ref-tip">
        注：仅 PUBLISHED 或 REFERENCED 状态的交付件可被引用。新增引用后，若源为
        PUBLISHED 将自动流转为 REFERENCED。
      </div>
      <el-form :model="refForm" label-width="110px">
        <el-form-item label="引用方类型" required>
          <el-select v-model="refForm.referenceType" style="width: 100%">
            <el-option label="任务" value="TASK" />
            <el-option label="阶段" value="PHASE" />
            <el-option label="项目" value="PROJECT" />
            <el-option label="交付件" value="DELIVERABLE" />
            <el-option label="报告" value="REPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="引用方 ID" required>
          <el-input-number v-model="refForm.referencedById" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="引用方名称">
          <el-input v-model="refForm.referencedByName" placeholder="冗余字段，便于展示" />
        </el-form-item>
        <el-form-item label="目标交付件 ID">
          <el-input-number v-model="refForm.targetDeliverableId" :min="1" :controls="false" style="width: 100%" placeholder="引用方为交付件时填写" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddReference">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.detail-tabs {
  margin-top: 16px;
}
.tab-toolbar {
  margin-bottom: 12px;
  text-align: right;
}
.ref-tip {
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fdf6ec;
  border-radius: 4px;
  color: #e6a23c;
  font-size: 13px;
  line-height: 1.6;
}

/* 7 态状态流可视化 */
.status-flow {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
  gap: 0;
}
.flow-node {
  display: flex;
  align-items: center;
  position: relative;
}
.node-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  border: 2px solid #dcdfe6;
  background: #fff;
  color: #909399;
  margin-right: 6px;
}
.node-label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
}
.node-arrow {
  color: #dcdfe6;
  margin-right: 8px;
  font-size: 14px;
}
.flow-node.is-passed .node-circle {
  background: #67c23a;
  border-color: #67c23a;
  color: #fff;
}
.flow-node.is-passed .node-label {
  color: #67c23a;
}
.flow-node.is-current .node-circle {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.2);
}
.flow-node.is-current .node-label {
  color: #409eff;
  font-weight: 600;
}
</style>
