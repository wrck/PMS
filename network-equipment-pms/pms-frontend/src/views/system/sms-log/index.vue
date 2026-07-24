<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getSmsLogPage,
  type SmsLogPageReqVO,
  type SmsLogRespVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<SmsLogRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  channelId: number | undefined
  templateId: number | undefined
  mobile: string
  sendStatus: number | undefined
  receiveStatus: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  channelId: undefined,
  templateId: undefined,
  mobile: '',
  sendStatus: undefined,
  receiveStatus: undefined,
  createTime: undefined
})

const sendStatusOptions = [
  { value: 0, label: '初始化' },
  { value: 10, label: '发送成功' },
  { value: 20, label: '发送失败' },
  { value: 30, label: '不发送' }
]

const receiveStatusOptions = [
  { value: 0, label: '未接收' },
  { value: 10, label: '接收成功' },
  { value: 20, label: '接收失败' }
]

const detailVisible = ref(false)
const detailData = ref<SmsLogRespVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    const params: SmsLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.channelId !== undefined && query.channelId !== null) params.channelId = query.channelId
    if (query.templateId !== undefined && query.templateId !== null) params.templateId = query.templateId
    if (query.mobile) params.mobile = query.mobile
    if (query.sendStatus !== undefined && query.sendStatus !== null) params.sendStatus = query.sendStatus
    if (query.receiveStatus !== undefined && query.receiveStatus !== null) {
      params.receiveStatus = query.receiveStatus
    }
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getSmsLogPage(params)
    tableData.value = res.list
    total.value = res.total
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
  query.channelId = undefined
  query.templateId = undefined
  query.mobile = ''
  query.sendStatus = undefined
  query.receiveStatus = undefined
  query.createTime = undefined
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

function handleViewDetail(row: SmsLogRespVO) {
  detailData.value = row
  detailVisible.value = true
}

function sendStatusLabel(status: number) {
  return sendStatusOptions.find((o) => o.value === status)?.label || String(status)
}

function sendStatusTagType(status: number): 'info' | 'success' | 'danger' | 'warning' {
  if (status === 10) return 'success'
  if (status === 20) return 'danger'
  if (status === 30) return 'warning'
  return 'info'
}

function receiveStatusLabel(status: number) {
  return receiveStatusOptions.find((o) => o.value === status)?.label || String(status)
}

function receiveStatusTagType(status: number): 'info' | 'success' | 'danger' {
  if (status === 10) return 'success'
  if (status === 20) return 'danger'
  return 'info'
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

function formatParams(params?: Record<string, unknown>): string {
  if (!params) return '-'
  try {
    return JSON.stringify(params)
  } catch {
    return String(params)
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>短信日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="短信渠道">
          <el-input v-model.number="query.channelId" placeholder="渠道 ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模板编号">
          <el-input v-model.number="query.templateId" placeholder="模板 ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.mobile" placeholder="手机号" clearable style="width: 160px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="发送状态">
          <el-select v-model="query.sendStatus" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in sendStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收状态">
          <el-select v-model="query.receiveStatus" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in receiveStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="query.createTime"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="channelCode" label="渠道编码" min-width="120" />
        <el-table-column prop="templateCode" label="模板编码" min-width="140" />
        <el-table-column prop="mobile" label="手机号" min-width="130" />
        <el-table-column prop="templateContent" label="短信内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="发送状态" width="100">
          <template #default="{ row }">
            <el-tag :type="sendStatusTagType(row.sendStatus)">{{ sendStatusLabel(row.sendStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接收状态" width="100">
          <template #default="{ row }">
            <el-tag :type="receiveStatusTagType(row.receiveStatus)">{{ receiveStatusLabel(row.receiveStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.sendTime) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="detailVisible" title="短信日志详情" width="640px" destroy-on-close>
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="日志编号">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="渠道 ID">{{ detailData.channelId }}</el-descriptions-item>
        <el-descriptions-item label="渠道编码">{{ detailData.channelCode }}</el-descriptions-item>
        <el-descriptions-item label="模板 ID">{{ detailData.templateId }}</el-descriptions-item>
        <el-descriptions-item label="模板编码">{{ detailData.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="API 模板编号">{{ detailData.apiTemplateId }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.mobile }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ detailData.userType }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">
          <el-tag :type="sendStatusTagType(detailData.sendStatus)">{{ sendStatusLabel(detailData.sendStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收状态">
          <el-tag :type="receiveStatusTagType(detailData.receiveStatus)">{{ receiveStatusLabel(detailData.receiveStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ formatDateTime(detailData.sendTime) }}</el-descriptions-item>
        <el-descriptions-item label="接收时间">{{ formatDateTime(detailData.receiveTime) }}</el-descriptions-item>
        <el-descriptions-item label="API 发送编码">{{ detailData.apiSendCode }}</el-descriptions-item>
        <el-descriptions-item label="API 发送消息" :span="2">{{ detailData.apiSendMsg }}</el-descriptions-item>
        <el-descriptions-item label="API 接收编码">{{ detailData.apiReceiveCode }}</el-descriptions-item>
        <el-descriptions-item label="API 接收消息">{{ detailData.apiReceiveMsg }}</el-descriptions-item>
        <el-descriptions-item label="API 请求 ID">{{ detailData.apiRequestId }}</el-descriptions-item>
        <el-descriptions-item label="API 序列号">{{ detailData.apiSerialNo }}</el-descriptions-item>
        <el-descriptions-item label="模板内容" :span="2">{{ detailData.templateContent }}</el-descriptions-item>
        <el-descriptions-item label="模板参数" :span="2">{{ formatParams(detailData.templateParams) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
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
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
