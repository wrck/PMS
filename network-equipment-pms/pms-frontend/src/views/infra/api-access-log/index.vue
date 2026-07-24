<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getApiAccessLogPage,
  type ApiAccessLogPageReqVO,
  type ApiAccessLogRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraApiAccessLog' })

const loading = ref(false)
const tableData = ref<ApiAccessLogRespVO[]>([])
const total = ref(0)

const query = reactive<ApiAccessLogPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  applicationName: '',
  requestUrl: '',
  resultCode: undefined
})

async function loadData() {
  loading.value = true
  try {
    const params: ApiAccessLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.applicationName) params.applicationName = query.applicationName
    if (query.requestUrl) params.requestUrl = query.requestUrl
    if (query.resultCode !== undefined && query.resultCode !== null) {
      params.resultCode = query.resultCode
    }
    const res = await getApiAccessLogPage(params)
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
  query.resultCode = undefined
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

// 结果码：0=成功
function resultCodeTagType(code: number): 'success' | 'danger' {
  return code === 0 ? 'success' : 'danger'
}

function resultCodeLabel(code: number): string {
  return code === 0 ? '成功' : String(code)
}

// 详情弹窗
const detailVisible = ref(false)
const currentRow = ref<ApiAccessLogRespVO | null>(null)

function handleViewDetail(row: ApiAccessLogRespVO) {
  currentRow.value = row
  detailVisible.value = true
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
        <span class="page-title">API访问日志</span>
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
        <el-form-item label="结果码">
          <el-input-number
            v-model="query.resultCode"
            :controls="false"
            placeholder="0=成功"
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="日志编号" width="100" />
        <el-table-column
          prop="traceId"
          label="链路追踪"
          min-width="200"
          show-overflow-tooltip
        />
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
        <el-table-column prop="userIp" label="用户 IP" min-width="140" />
        <el-table-column label="执行时长" width="110" align="center">
          <template #default="{ row }">{{ row.duration }}ms</template>
        </el-table-column>
        <el-table-column label="结果码" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="resultCodeTagType(row.resultCode)" size="small">
              {{ resultCodeLabel(row.resultCode) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.beginTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无访问日志" />
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
    <el-dialog v-model="detailVisible" title="访问日志详情" width="780px" destroy-on-close>
      <div v-if="currentRow">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="日志编号">{{ currentRow.id }}</el-descriptions-item>
          <el-descriptions-item label="链路追踪">{{ currentRow.traceId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="应用名">{{ currentRow.applicationName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ currentRow.requestMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求地址" :span="2">{{ currentRow.requestUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户 IP">{{ currentRow.userIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行时长">{{ currentRow.duration }}ms</el-descriptions-item>
          <el-descriptions-item label="结果码">{{ currentRow.resultCode }}</el-descriptions-item>
          <el-descriptions-item label="结果消息">{{ currentRow.resultMsg || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentRow.beginTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentRow.endTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section-title">请求参数</div>
        <pre class="detail-pre">{{ currentRow.requestParams || '无' }}</pre>

        <div class="detail-section-title">响应结果</div>
        <pre class="detail-pre">{{ currentRow.responseBody || '无' }}</pre>
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
  max-height: 280px;
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
