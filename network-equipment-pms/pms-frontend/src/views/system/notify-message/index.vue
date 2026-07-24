<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotifyMessagePage,
  updateNotifyMessageRead,
  type NotifyMessagePageReqVO,
  type NotifyMessageRespVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<NotifyMessageRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  userId: number | undefined
  templateCode: string
  templateType: number | undefined
  readStatus: boolean | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  templateCode: '',
  templateType: undefined,
  readStatus: undefined,
  createTime: undefined
})

const templateTypeOptions = [
  { value: 1, label: '通知' },
  { value: 2, label: '公告' }
]

const readStatusOptions = [
  { value: true, label: '已读' },
  { value: false, label: '未读' }
]

const detailVisible = ref(false)
const detailData = ref<NotifyMessageRespVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    const params: NotifyMessagePageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.userId !== undefined && query.userId !== null) params.userId = query.userId
    if (query.templateCode) params.templateCode = query.templateCode
    if (query.templateType !== undefined && query.templateType !== null) params.templateType = query.templateType
    if (query.readStatus !== undefined && query.readStatus !== null) params.readStatus = query.readStatus
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getNotifyMessagePage(params)
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
  query.userId = undefined
  query.templateCode = ''
  query.templateType = undefined
  query.readStatus = undefined
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

function handleViewDetail(row: NotifyMessageRespVO) {
  detailData.value = row
  detailVisible.value = true
}

function handleMarkRead(row: NotifyMessageRespVO) {
  if (!row.id || row.readStatus) return
  ElMessageBox.confirm(`确定将站内信「${row.templateCode}」标记为已读吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await updateNotifyMessageRead([row.id])
      ElMessage.success('标记已读成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function templateTypeLabel(type: number) {
  return templateTypeOptions.find((o) => o.value === type)?.label || String(type)
}

function templateTypeTagType(type: number): 'primary' | 'success' {
  return type === 1 ? 'primary' : 'success'
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
      <template #header>
        <span>站内信消息</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户 ID">
          <el-input v-model.number="query.userId" placeholder="用户 ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模板编码">
          <el-input v-model="query.templateCode" placeholder="模板编码" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模板类型">
          <el-select v-model="query.templateType" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in templateTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="阅读状态">
          <el-select v-model="query.readStatus" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in readStatusOptions" :key="String(o.value)" :label="o.label" :value="o.value" />
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
        <el-table-column prop="userId" label="用户 ID" width="100" />
        <el-table-column prop="templateCode" label="模板编码" min-width="140" />
        <el-table-column prop="templateNickname" label="模板发件人" min-width="120" />
        <el-table-column prop="templateContent" label="消息内容" min-width="240" show-overflow-tooltip />
        <el-table-column label="模板类型" width="100">
          <template #default="{ row }">
            <el-tag :type="templateTypeTagType(row.templateType)">{{ templateTypeLabel(row.templateType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阅读状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.readStatus ? 'success' : 'warning'">{{ row.readStatus ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阅读时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.readTime) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">详情</el-button>
            <el-button v-if="!row.readStatus" link type="success" @click="handleMarkRead(row)">标记已读</el-button>
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

    <el-dialog v-model="detailVisible" title="站内信详情" width="640px" destroy-on-close>
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="消息编号">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ detailData.userType }}</el-descriptions-item>
        <el-descriptions-item label="模板 ID">{{ detailData.templateId }}</el-descriptions-item>
        <el-descriptions-item label="模板编码">{{ detailData.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="模板发件人">{{ detailData.templateNickname }}</el-descriptions-item>
        <el-descriptions-item label="模板类型">
          <el-tag :type="templateTypeTagType(detailData.templateType)">{{ templateTypeLabel(detailData.templateType) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="阅读状态">
          <el-tag :type="detailData.readStatus ? 'success' : 'warning'">{{ detailData.readStatus ? '已读' : '未读' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息内容" :span="2">{{ detailData.templateContent }}</el-descriptions-item>
        <el-descriptions-item label="模板参数" :span="2">{{ detailData.templateParams }}</el-descriptions-item>
        <el-descriptions-item label="阅读时间">{{ formatDateTime(detailData.readTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
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
