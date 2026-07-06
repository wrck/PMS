<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FileUploader from '@/components/FileUploader/index.vue'
import {
  cancelUploaded,
  downloadAttachment,
  initChecklist,
  listDeliverables,
  markUploaded,
  type DeliverableChecklist,
  type DeliverableType
} from '@/api/deliverable'
import type { EpTagType } from '@/types'

const loading = ref(false)
const tableData = ref<DeliverableChecklist[]>([])
const projectId = ref<number | undefined>(undefined)

// 8 种交付物类型中文映射
const typeLabels: Record<DeliverableType, string> = {
  AS_BUILT: '竣工图',
  TEST_REPORT: '测试报告',
  ACCEPTANCE_CERT: '验收证书',
  TRAINING_RECORD: '培训记录',
  OPERATION_MANUAL: '操作手册',
  ASSET_REGISTER: '资产清单',
  WARRANTY_CERT: '质保证书',
  SPARE_PARTS_LIST: '备件清单'
}

function typeLabel(type?: string): string {
  return (typeLabels as Record<string, string>)[type ?? ''] ?? type ?? '-'
}

// 时间格式化：去 T 并截取到秒
function formatDateTime(val?: string): string {
  return val?.replace('T', ' ').slice(0, 19) ?? '-'
}

// 已上传状态标签
function uploadedMeta(row: DeliverableChecklist): { tagType: EpTagType; label: string } {
  if (row.uploaded) return { tagType: 'success', label: '已上传' }
  if (row.required) return { tagType: 'danger', label: '未上传' }
  return { tagType: 'info', label: '无需上传' }
}

// 是否必需标签
function requiredMeta(row: DeliverableChecklist): { tagType: EpTagType; label: string } {
  return row.required
    ? { tagType: 'warning', label: '必需' }
    : { tagType: 'info', label: '可选' }
}

// 未上传的必需项数量
const pendingCount = computed(() => {
  return tableData.value.filter((r) => r.required && !r.uploaded).length
})

// ============== 上传弹窗 ==============
const uploadVisible = ref(false)
const currentRow = ref<DeliverableChecklist | null>(null)

// ============== 数据加载 ==============
async function loadData() {
  if (!projectId.value) {
    ElMessage.warning('请输入项目 ID')
    return
  }
  loading.value = true
  try {
    const res = await listDeliverables(projectId.value)
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

// 初始化清单
function handleInit() {
  if (!projectId.value) {
    ElMessage.warning('请输入项目 ID')
    return
  }
  ElMessageBox.confirm(`确认为项目「${projectId.value}」初始化终验交付物清单吗？`, '初始化清单', {
    type: 'warning'
  })
    .then(async () => {
      await initChecklist(projectId.value!)
      ElMessage.success('清单已初始化')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============== 上传 / 下载 / 取消 ==============
function handleUpload(row: DeliverableChecklist) {
  currentRow.value = row
  uploadVisible.value = true
}

// FileUploader 上传成功回调：可能直接返回附件 ID，也可能返回 { id } 对象
async function handleUploaded(payload: number | { id?: number }) {
  const row = currentRow.value
  if (!row?.id) return
  const attachmentId = typeof payload === 'number' ? payload : payload?.id
  if (typeof attachmentId !== 'number') {
    ElMessage.warning('未获取到附件 ID')
    return
  }
  try {
    await markUploaded(row.id, attachmentId)
    ElMessage.success('已标记上传')
    uploadVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  }
}

function handleDownload(row: DeliverableChecklist) {
  if (!row.attachmentId) return
  window.open(downloadAttachment(row.attachmentId), '_blank')
}

function handleCancelUpload(row: DeliverableChecklist) {
  if (!row.id) return
  ElMessageBox.confirm(`确认取消「${typeLabel(row.deliverableType)}」的上传标记吗？`, '取消上传', {
    type: 'warning'
  })
    .then(async () => {
      await cancelUploaded(row.id!)
      ElMessage.success('已取消上传')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">终验交付物清单</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目 ID">
          <el-input-number
            v-model="projectId"
            :min="1"
            :controls="false"
            placeholder="请输入项目 ID"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button type="success" :icon="'Files'" @click="handleInit">初始化清单</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="交付物类型" min-width="140">
          <template #default="{ row }">{{ typeLabel(row.deliverableType) }}</template>
        </el-table-column>
        <el-table-column label="是否必需" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="requiredMeta(row).tagType" size="small">
              {{ requiredMeta(row).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上传状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="uploadedMeta(row).tagType" size="small">
              {{ uploadedMeta(row).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="attachmentId" label="附件 ID" width="100" align="center">
          <template #default="{ row }">{{ row.attachmentId ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="确认时间" width="160" align="center">
          <template #default="{ row }">{{ formatDateTime(row.checkedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleUpload(row)">上传附件</el-button>
            <el-button
              v-if="row.attachmentId"
              link
              type="primary"
              @click="handleDownload(row)"
            >
              下载
            </el-button>
            <el-button
              v-if="row.uploaded"
              link
              type="danger"
              @click="handleCancelUpload(row)"
            >
              取消上传
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="请输入项目 ID 并查询，或点击「初始化清单」" />
        </template>
      </el-table>

      <!-- 底部校验状态 -->
      <div v-if="tableData.length > 0" class="validate-bar">
        <el-tag
          :type="pendingCount === 0 ? 'success' : 'warning'"
          size="large"
        >
          {{ pendingCount === 0 ? '✓ 全部必需项已上传，可以提交终验' : `还有 ${pendingCount} 项未上传` }}
        </el-tag>
      </div>
    </el-card>

    <!-- 上传弹窗 -->
    <el-dialog v-model="uploadVisible" title="上传交付物" width="520px" destroy-on-close>
      <div v-if="currentRow" class="upload-tip">
        当前交付物：<strong>{{ typeLabel(currentRow.deliverableType) }}</strong>
      </div>
      <FileUploader biz-type="DELIVERABLE" @uploaded="handleUploaded" />
      <template #footer>
        <el-button @click="uploadVisible = false">关闭</el-button>
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
.upload-tip {
  margin-bottom: 12px;
  color: #606266;
}
.validate-bar {
  margin-top: 16px;
  text-align: center;
}
</style>
