<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getMailLogPage,
  getMailLog,
  type MailLogPageReqVO,
  type MailLogRespVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<MailLogRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  accountId: number | undefined
  templateId: number | undefined
  toMail: string
  sendStatus: number | undefined
  sendTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  accountId: undefined,
  templateId: undefined,
  toMail: '',
  sendStatus: undefined,
  sendTime: undefined
})

const sendStatusOptions = [
  { value: 0, label: '初始化' },
  { value: 10, label: '发送成功' },
  { value: 20, label: '发送失败' },
  { value: 30, label: '不发送' }
]

const detailVisible = ref(false)
const detailData = ref<MailLogRespVO | null>(null)
const detailLoading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params: MailLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.accountId !== undefined && query.accountId !== null) params.accountId = query.accountId
    if (query.templateId !== undefined && query.templateId !== null) params.templateId = query.templateId
    if (query.toMail) params.toMail = query.toMail
    if (query.sendStatus !== undefined && query.sendStatus !== null) params.sendStatus = query.sendStatus
    if (query.sendTime && query.sendTime.length === 2) params.sendTime = query.sendTime
    const res = await getMailLogPage(params)
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
  query.accountId = undefined
  query.templateId = undefined
  query.toMail = ''
  query.sendStatus = undefined
  query.sendTime = undefined
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

async function handleViewDetail(row: MailLogRespVO) {
  detailVisible.value = true
  detailLoading.value = true
  detailData.value = null
  try {
    detailData.value = await getMailLog(row.id)
  } catch {
    detailData.value = row
  } finally {
    detailLoading.value = false
  }
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

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

function formatMails(mails?: string[]): string {
  if (!mails || !mails.length) return '-'
  return mails.join(', ')
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>邮件日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="邮箱账号">
          <el-input v-model.number="query.accountId" placeholder="账号 ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模板编号">
          <el-input v-model.number="query.templateId" placeholder="模板 ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="收件邮箱">
          <el-input v-model="query.toMail" placeholder="收件邮箱" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="发送状态">
          <el-select v-model="query.sendStatus" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in sendStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间">
          <el-date-picker
            v-model="query.sendTime"
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
        <el-table-column label="收件邮箱" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ formatMails(row.toMails) }}</template>
        </el-table-column>
        <el-table-column prop="fromMail" label="发件邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="templateCode" label="模板编码" min-width="140" />
        <el-table-column prop="templateTitle" label="邮件标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="发送状态" width="100">
          <template #default="{ row }">
            <el-tag :type="sendStatusTagType(row.sendStatus)">{{ sendStatusLabel(row.sendStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.sendTime) }}</template>
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

    <el-dialog v-model="detailVisible" title="邮件日志详情" width="680px" destroy-on-close>
      <el-descriptions v-loading="detailLoading" v-if="detailData" :column="2" border>
        <el-descriptions-item label="日志编号">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="邮箱账号 ID">{{ detailData.accountId }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ detailData.userType }}</el-descriptions-item>
        <el-descriptions-item label="发件邮箱">{{ detailData.fromMail }}</el-descriptions-item>
        <el-descriptions-item label="发件人名称">{{ detailData.fromNickname }}</el-descriptions-item>
        <el-descriptions-item label="收件邮箱" :span="2">{{ formatMails(detailData.toMails) }}</el-descriptions-item>
        <el-descriptions-item label="抄送" :span="2">{{ formatMails(detailData.ccMails) }}</el-descriptions-item>
        <el-descriptions-item label="密送" :span="2">{{ formatMails(detailData.bccMails) }}</el-descriptions-item>
        <el-descriptions-item label="模板 ID">{{ detailData.templateId }}</el-descriptions-item>
        <el-descriptions-item label="模板编码">{{ detailData.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="模板发件人">{{ detailData.templateNickname }}</el-descriptions-item>
        <el-descriptions-item label="模板标题">{{ detailData.templateTitle }}</el-descriptions-item>
        <el-descriptions-item label="模板内容" :span="2">{{ detailData.templateContent }}</el-descriptions-item>
        <el-descriptions-item label="模板参数" :span="2">{{ detailData.templateParams }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">
          <el-tag :type="sendStatusTagType(detailData.sendStatus)">{{ sendStatusLabel(detailData.sendStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ formatDateTime(detailData.sendTime) }}</el-descriptions-item>
        <el-descriptions-item label="发送消息 ID">{{ detailData.sendMessageId }}</el-descriptions-item>
        <el-descriptions-item label="发送异常" :span="2">{{ detailData.sendException }}</el-descriptions-item>
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
