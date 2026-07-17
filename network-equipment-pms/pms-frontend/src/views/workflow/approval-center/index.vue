<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getApprovalsByProject,
  getPendingApprovals,
  getSubmittedApprovals,
  type ApprovalRecord
} from '@/api/approval-center'

type TabName = 'pending' | 'submitted' | 'project'

const router = useRouter()
const activeTab = ref<TabName>('pending')
const loading = ref(false)

const pendingData = ref<ApprovalRecord[]>([])
const submittedData = ref<ApprovalRecord[]>([])
const projectData = ref<ApprovalRecord[]>([])
const projectIdInput = ref<number | undefined>(undefined)

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'pending') {
      pendingData.value = await getPendingApprovals()
    } else if (activeTab.value === 'submitted') {
      submittedData.value = await getSubmittedApprovals()
    } else {
      // 项目维度
      if (!projectIdInput.value) {
        projectData.value = []
      } else {
        projectData.value = await getApprovalsByProject(projectIdInput.value)
      }
    }
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  loadData()
}

function viewDetail(row: ApprovalRecord) {
  if (!row.id) return
  router.push(`/workflow/approval-detail/${row.id}`)
}

function viewHistory(row: ApprovalRecord) {
  if (!row.id) return
  router.push(`/workflow/approval-history/${row.id}`)
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

function approvalTypeLabel(type?: string) {
  const map: Record<string, string> = {
    PROJECT: '项目',
    TASK: '任务',
    DELIVERABLE: '交付件',
    RISK: '风险',
    ISSUE: '问题',
    CHANGE: '变更',
    RESOURCE: '资源',
    COST: '成本',
    PHASE_EXIT: '阶段退出',
    BASELINE_CHANGE: '基线变更'
  }
  return map[type || ''] || type || '-'
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">统一审批中心</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的待办" name="pending" />
        <el-tab-pane label="我提交的" name="submitted" />
        <el-tab-pane label="项目维度" name="project" />
      </el-tabs>

      <!-- 待办 / 已提交 -->
      <template v-if="activeTab === 'pending' || activeTab === 'submitted'">
        <div class="toolbar">
          <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
        </div>
        <el-table v-loading="loading" :data="activeTab === 'pending' ? pendingData : submittedData" border stripe>
          <el-table-column prop="title" label="审批标题" min-width="200" show-overflow-tooltip />
          <el-table-column label="类型" width="110" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ approvalTypeLabel(row.approvalType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterName" label="提交人" min-width="110" show-overflow-tooltip />
          <el-table-column prop="currentNodeName" label="当前节点" min-width="120" show-overflow-tooltip />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small">
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="轮次" width="70" align="center">
            <template #default="{ row }">第 {{ row.round || 1 }} 轮</template>
          </el-table-column>
          <el-table-column prop="submittedAt" label="提交时间" min-width="160" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
              <el-button link type="info" @click="viewHistory(row)">历史</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty :description="activeTab === 'pending' ? '暂无待办审批' : '暂无已提交审批'" />
          </template>
        </el-table>
      </template>

      <!-- 项目维度 -->
      <template v-else>
        <div class="toolbar">
          <el-input-number
            v-model="projectIdInput"
            :min="1"
            placeholder="请输入项目ID"
            style="width: 220px"
            controls-position="right"
          />
          <el-button type="primary" :icon="'Search'" @click="loadData">查询</el-button>
          <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
        </div>
        <el-table v-loading="loading" :data="projectData" border stripe>
          <el-table-column prop="title" label="审批标题" min-width="200" show-overflow-tooltip />
          <el-table-column label="类型" width="110" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ approvalTypeLabel(row.approvalType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterName" label="提交人" min-width="110" show-overflow-tooltip />
          <el-table-column prop="currentNodeName" label="当前节点" min-width="120" show-overflow-tooltip />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small">
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="轮次" width="70" align="center">
            <template #default="{ row }">第 {{ row.round || 1 }} 轮</template>
          </el-table-column>
          <el-table-column prop="submittedAt" label="提交时间" min-width="160" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
              <el-button link type="info" @click="viewHistory(row)">历史</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="请输入项目ID查询，或该项目暂无审批记录" />
          </template>
        </el-table>
      </template>
    </el-card>
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
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
}
</style>
