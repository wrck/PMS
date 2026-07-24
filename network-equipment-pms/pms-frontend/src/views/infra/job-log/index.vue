<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getJobLog,
  getJobLogPage,
  type JobLogPageReqVO,
  type JobLogRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraJobLog' })

const loading = ref(false)
const tableData = ref<JobLogRespVO[]>([])
const total = ref(0)

const query = reactive<JobLogPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  jobId: undefined,
  handlerName: '',
  beginTime: undefined,
  status: undefined
})

const statusOptions = [
  { value: 0, label: '成功' },
  { value: 1, label: '失败' }
]

function statusTagType(status: number): 'success' | 'danger' {
  return status === 0 ? 'success' : 'danger'
}

function statusLabel(status: number): string {
  return status === 0 ? '成功' : '失败'
}

async function loadData() {
  loading.value = true
  try {
    const params: JobLogPageReqVO = { pageNo: query.pageNo, pageSize: query.pageSize }
    if (query.jobId !== undefined && query.jobId !== null) params.jobId = query.jobId
    if (query.handlerName) params.handlerName = query.handlerName
    if (query.beginTime && query.beginTime.length === 2) params.beginTime = query.beginTime
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getJobLogPage(params)
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
  query.jobId = undefined
  query.handlerName = ''
  query.beginTime = undefined
  query.status = undefined
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

// 详情弹窗
const detailVisible = ref(false)
const detailLoading = ref(false)
const currentRow = ref<JobLogRespVO | null>(null)

async function handleViewDetail(row: JobLogRespVO) {
  detailVisible.value = true
  detailLoading.value = true
  try {
    currentRow.value = await getJobLog(row.id)
  } catch {
    currentRow.value = row
  } finally {
    detailLoading.value = false
  }
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>定时任务日志</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="任务编号">
          <el-input-number
            v-model="query.jobId"
            :controls="false"
            placeholder="请输入任务编号"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="处理器名称">
          <el-input
            v-model="query.handlerName"
            placeholder="请输入处理器名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="query.beginTime"
            type="daterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
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
        <el-table-column prop="id" label="日志编号" width="100" />
        <el-table-column prop="jobId" label="任务编号" width="100" />
        <el-table-column prop="handlerName" label="处理器名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="executeIndex" label="第几次执行" width="110" align="center" />
        <el-table-column label="执行时间" width="320">
          <template #default="{ row }">
            {{ formatDateTime(row.beginTime) }} ~ {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="执行时长" width="110" align="center">
          <template #default="{ row }">{{ row.duration }} 毫秒</template>
        </el-table-column>
        <el-table-column label="任务状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">详细</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无任务日志" />
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
    <el-dialog v-model="detailVisible" title="任务日志详情" width="720px" destroy-on-close>
      <div v-loading="detailLoading">
        <el-descriptions v-if="currentRow" :column="2" border size="small">
          <el-descriptions-item label="日志编号">{{ currentRow.id }}</el-descriptions-item>
          <el-descriptions-item label="任务编号">{{ currentRow.jobId }}</el-descriptions-item>
          <el-descriptions-item label="处理器名称">{{ currentRow.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理器参数">{{ currentRow.handlerParam || '-' }}</el-descriptions-item>
          <el-descriptions-item label="CRON 表达式">{{ currentRow.cronExpression || '-' }}</el-descriptions-item>
          <el-descriptions-item label="第几次执行">{{ currentRow.executeIndex }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentRow.beginTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentRow.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="执行时长">{{ currentRow.duration }} 毫秒</el-descriptions-item>
          <el-descriptions-item label="任务状态">
            <el-tag :type="statusTagType(currentRow.status)" size="small">
              {{ statusLabel(currentRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(currentRow.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <template v-if="currentRow && currentRow.result">
          <div class="detail-section-title">执行结果</div>
          <pre class="detail-pre">{{ currentRow.result }}</pre>
        </template>
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
