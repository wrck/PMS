<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getApiErrorLogPage,
  updateApiErrorLogStatus,
  type ApiErrorLogPageReqVO,
  type ApiErrorLogRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraApiErrorLog' })

const loading = ref(false)
const tableData = ref<ApiErrorLogRespVO[]>([])
const total = ref(0)

const query = reactive<ApiErrorLogPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  applicationName: '',
  requestUrl: '',
  processStatus: undefined
})

async function loadData() {
  loading.value = true
  try {
    const params: ApiErrorLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.applicationName) params.applicationName = query.applicationName
    if (query.requestUrl) params.requestUrl = query.requestUrl
    if (query.processStatus !== undefined && query.processStatus !== null) {
      params.processStatus = query.processStatus
    }
    const res = await getApiErrorLogPage(params)
    tableData.value = res?.list ?? []
    total.value = res?.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNo = 1
  loadData()
}

function handleReset() {
  query.applicationName = ''
  query.requestUrl = ''
  query.processStatus = undefined
  query.pageNo = 1
  loadData()
}

function handlePageChange(p: number) {
  query.pageNo = p
  loadData()
}

function handleSizeChange(s: number) {
  query.pageSize = s
  query.pageNo = 1
  loadData()
}

// 请求方法 tag 类型
function methodTagType(method: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  const m = (method || '').toUpperCase()
  if (m === 'GET') return 'success'
  if (m === 'POST') return 'primary'
  if (m === 'PUT' || m === 'PATCH') return 'warning'
  if (m === 'DELETE') return 'danger'
  return 'info'
}

// 处理状态：0=未处理，1=已处理，2=已忽略
function processStatusTagType(status: number): 'warning' | 'success' | 'info' {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  return 'info'
}

function processStatusLabel(status: number): string {
  if (status === 0) return '未处理'
  if (status === 1) return '已处理'
  if (status === 2) return '已忽略'
  return String(status)
}

const statusOptions = [
  { value: 0, label: '未处理' },
  { value: 1, label: '已处理' },
  { value: 2, label: '已忽略' }
]

// 详情弹窗
const detailVisible = ref(false)
const currentRow = ref<ApiErrorLogRespVO | null>(null)

function handleViewDetail(row: ApiErrorLogRespVO) {
  currentRow.value = row
  detailVisible.value = true
}

// 更新状态
const updatingId = ref<number | null>(null)

async function handleUpdateStatus(row: ApiErrorLogRespVO, processStatus: number) {
  updatingId.value = row.id
  try {
    await updateApiErrorLogStatus(row.id, processStatus)
    ElMessage.success('状态更新成功')
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    updatingId.value = null
  }
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">API错误日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="应用名">
          <el-input
            v-model="query.applicationName"
            placeholder="请输入应用名"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="请求地址">
          <el-input
            v-model="query.requestUrl"
            placeholder="请输入请求地址"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select
            v-model="query.processStatus"
            placeholder="全部"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="opt in statusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column
          prop="applicationName"
          label="应用名"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column label="请求方法" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="methodTagType(row.requestMethod)" size="small">
              {{ row.requestMethod || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="requestUrl"
          label="请求地址"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          prop="exceptionName"
          label="异常名"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          prop="exceptionMessage"
          label="异常消息"
          min-width="240"
          show-overflow-tooltip
        />
        <el-table-column prop="userIp" label="用户 IP" min-width="140" />
        <el-table-column label="异常时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.exceptionTime) }}</template>
        </el-table-column>
        <el-table-column label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="processStatusTagType(row.processStatus)" size="small">
              {{ processStatusLabel(row.processStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button
              link
              type="success"
              :loading="updatingId === row.id"
              @click="handleUpdateStatus(row, 1)"
            >
              标记已处理
            </el-button>
            <el-button
              link
              type="warning"
              :loading="updatingId === row.id"
              @click="handleUpdateStatus(row, 2)"
            >
              标记已忽略
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无错误日志" />
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.pageNo"
        :page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="错误日志详情" width="820px" destroy-on-close>
      <div v-if="currentRow">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="编号">{{ currentRow.id }}</el-descriptions-item>
          <el-descriptions-item label="链路追踪">{{ currentRow.traceId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="应用名">{{ currentRow.applicationName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ currentRow.requestMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求地址" :span="2">{{ currentRow.requestUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户 IP">{{ currentRow.userIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="异常时间">{{ formatDateTime(currentRow.exceptionTime) }}</el-descriptions-item>
          <el-descriptions-item label="异常名" :span="2">{{ currentRow.exceptionName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="异常消息" :span="2">{{ currentRow.exceptionMessage || '-' }}</el-descriptions-item>
          <el-descriptions-item label="根因消息" :span="2">{{ currentRow.exceptionRootCauseMessage || '-' }}</el-descriptions-item>
          <el-descriptions-item label="异常类">{{ currentRow.exceptionClassName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="文件:行号">
            {{ currentRow.exceptionFileName || '-' }}:{{ currentRow.exceptionLineNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="处理状态">{{ processStatusLabel(currentRow.processStatus) }}</el-descriptions-item>
          <el-descriptions-item label="处理时间">{{ formatDateTime(currentRow.processTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section-title">异常堆栈</div>
        <pre class="detail-pre">{{ currentRow.exceptionStackTrace || '无堆栈信息' }}</pre>
      </div>
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
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.detail-section-title {
  margin: 16px 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}
.detail-pre {
  max-height: 360px;
  overflow: auto;
  padding: 12px;
  margin: 0;
  background-color: #1e1e1e;
  color: #e0e0e0;
  border-radius: 4px;
  font-family: 'Menlo', 'Consolas', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
