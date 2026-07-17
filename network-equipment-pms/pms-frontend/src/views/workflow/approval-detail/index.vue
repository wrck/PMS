<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveApproval,
  getApprovalDetail,
  rejectApproval,
  resubmitApproval,
  withdrawApproval,
  type ApprovalDetailVO,
  type ApprovalRecord
} from '@/api/approval-center'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'
import SensitiveFieldDisplay from '@/components/SensitiveFieldDisplay.vue'

const route = useRoute()
const router = useRouter()

const detail = ref<ApprovalDetailVO | null>(null)
const loading = ref(false)
const submitting = ref(false)

// 操作对话框
const actionDialogVisible = ref(false)
const actionType = ref<'approve' | 'reject' | 'resubmit'>('approve')
const actionComment = ref('')

const record = computed<ApprovalRecord | null>(() => detail.value?.record ?? null)
const recordId = computed(() => Number(route.params.id))

const canApprove = computed(() => record.value?.status === 'PENDING')
const canReject = computed(() => record.value?.status === 'PENDING')
const canWithdraw = computed(() => record.value?.status === 'PENDING')
const canResubmit = computed(
  () => record.value?.status === 'REJECTED' || record.value?.status === 'WITHDRAWN'
)

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getApprovalDetail(recordId.value)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

function openActionDialog(type: 'approve' | 'reject' | 'resubmit') {
  actionType.value = type
  actionComment.value = ''
  actionDialogVisible.value = true
}

async function handleAction() {
  if (!record.value?.id) return
  submitting.value = true
  try {
    const id = record.value.id
    if (actionType.value === 'approve') {
      await approveApproval(id, actionComment.value)
      ElMessage.success('审批通过')
    } else if (actionType.value === 'reject') {
      await rejectApproval(id, actionComment.value)
      ElMessage.success('已退回')
    } else if (actionType.value === 'resubmit') {
      await resubmitApproval(id, actionComment.value)
      ElMessage.success('已重新提交')
    }
    actionDialogVisible.value = false
    await loadDetail()
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

function handleWithdraw() {
  if (!record.value?.id) return
  ElMessageBox.confirm('确定撤回该审批吗？', '撤回确认', { type: 'warning' })
    .then(async () => {
      submitting.value = true
      try {
        await withdrawApproval(record.value!.id!)
        ElMessage.success('已撤回')
        await loadDetail()
      } catch {
        /* handled by interceptor */
      } finally {
        submitting.value = false
      }
    })
    .catch(() => {
      /* cancelled */
    })
}

function backToList() {
  router.push('/workflow/approval-center')
}

function statusTagType(status?: string) {
  switch (status) {
    case 'PENDING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'WITHDRAWN':
      return 'info'
    case 'TIMEOUT':
      return 'danger'
    default:
      return 'info'
  }
}

function statusLabel(status?: string) {
  const map: Record<string, string> = {
    PENDING: '待审批',
    APPROVED: '已通过',
    REJECTED: '已退回',
    WITHDRAWN: '已撤回',
    TIMEOUT: '已超时'
  }
  return map[status || ''] || status || '-'
}

function actionTitle() {
  return actionType.value === 'approve'
    ? '通过审批'
    : actionType.value === 'reject'
      ? '退回审批'
      : '重新提交'
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header :icon="'ArrowLeft'" :title="'返回'" @back="backToList">
      <template #content>
        <span class="header-title">审批详情</span>
      </template>
    </el-page-header>

    <template v-if="record">
      <!-- 审批基本信息 -->
      <el-card shadow="never">
        <template #header>
          <div class="card-header">
            <span class="page-title">{{ record.title }}</span>
            <el-tag :type="statusTagType(record.status)" size="large">
              {{ statusLabel(record.status) }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border size="default">
          <el-descriptions-item label="审批标题">{{ record.title }}</el-descriptions-item>
          <el-descriptions-item label="审批类型">{{ record.approvalType }}</el-descriptions-item>
          <el-descriptions-item label="业务ID">{{ record.businessId }}</el-descriptions-item>
          <el-descriptions-item label="业务编码">{{ record.businessCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ record.submitterName || record.submitterId }}</el-descriptions-item>
          <el-descriptions-item label="当前节点">{{ record.currentNodeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="轮次">第 {{ record.round || 1 }} 轮</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ record.submittedAt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ record.completedAt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="流程实例ID">{{ record.processInstanceId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="是否升级">
            <el-tag :type="record.escalated ? 'danger' : 'info'" size="small">
              {{ record.escalated ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 操作按钮 -->
        <div class="actions">
          <el-button type="success" :disabled="!canApprove" :loading="submitting" @click="openActionDialog('approve')">
            通过
          </el-button>
          <el-button type="danger" :disabled="!canReject" :loading="submitting" @click="openActionDialog('reject')">
            退回
          </el-button>
          <el-button type="warning" :disabled="!canWithdraw" :loading="submitting" @click="handleWithdraw">
            撤回
          </el-button>
          <el-button type="primary" :disabled="!canResubmit" :loading="submitting" @click="openActionDialog('resubmit')">
            重新提交
          </el-button>
        </div>
      </el-card>

      <!-- 业务数据（含脱敏） -->
      <el-card shadow="never">
        <template #header>
          <span class="page-title">业务数据</span>
        </template>
        <el-descriptions :column="2" border size="default" v-if="detail?.businessData">
          <el-descriptions-item
            v-for="(value, key) in detail.businessData"
            :key="String(key)"
            :label="String(key)"
          >
            <SensitiveFieldDisplay
              :field-name="String(key)"
              :value="value"
              :masked-fields="detail.maskedFields"
            />
          </el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="无业务数据" />
      </el-card>

      <!-- 审批历史时间轴 -->
      <el-card shadow="never">
        <template #header>
          <span class="page-title">审批历史</span>
        </template>
        <ApprovalTimeline :history="detail?.history || []" />
      </el-card>
    </template>

    <el-empty v-else-if="!loading" description="审批记录不存在或已加载" />

    <!-- 操作对话框 -->
    <el-dialog v-model="actionDialogVisible" :title="actionTitle()" width="500px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item :label="actionType === 'approve' ? '审批意见' : actionType === 'reject' ? '退回意见' : '重新提交说明'">
          <el-input
            v-model="actionComment"
            type="textarea"
            :rows="4"
            :placeholder="actionType === 'approve' ? '请输入审批意见' : actionType === 'reject' ? '请输入退回原因' : '请输入重新提交说明'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionDialogVisible = false">取消</el-button>
        <el-button
          :type="actionType === 'approve' ? 'success' : actionType === 'reject' ? 'danger' : 'primary'"
          :loading="submitting"
          @click="handleAction"
        >
          确定
        </el-button>
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
.header-title {
  font-size: 16px;
  font-weight: 600;
  margin-left: 8px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}
</style>
