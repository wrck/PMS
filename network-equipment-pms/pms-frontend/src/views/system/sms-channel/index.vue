<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createSmsChannel,
  deleteSmsChannel,
  getSmsChannelPage,
  updateSmsChannel,
  type SmsChannelPageReqVO,
  type SmsChannelRespVO,
  type SmsChannelSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<SmsChannelRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  signature: string
  code: string
  status: number | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  signature: '',
  code: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const codeOptions = [
  { value: 'ALIYUN', label: '阿里云' },
  { value: 'DINGTALK', label: '钉钉' },
  { value: 'HUAWEI', label: '华为云' },
  { value: 'TENCENT', label: '腾讯云' },
  { value: 'DEBUG_DING_TALK', label: '调试（钉钉）' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<SmsChannelSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  signature: [{ required: true, message: '请输入短信签名', trigger: 'blur' }],
  code: [{ required: true, message: '请选择短信渠道编码', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  apiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }]
}

function createEmptyForm(): SmsChannelSaveReqVO {
  return {
    code: 'ALIYUN',
    status: 0,
    signature: '',
    remark: '',
    apiKey: '',
    apiSecret: '',
    callbackUrl: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: SmsChannelPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.signature) params.signature = query.signature
    if (query.code) params.code = query.code
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getSmsChannelPage(params)
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
  query.signature = ''
  query.code = ''
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增短信渠道'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: SmsChannelRespVO) {
  dialogTitle.value = '编辑短信渠道'
  Object.assign(form, createEmptyForm(), row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateSmsChannel(form)
        ElMessage.success('更新成功')
      } else {
        await createSmsChannel(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

function handleDelete(row: SmsChannelRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除短信渠道「${row.signature}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteSmsChannel(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
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

function statusLabel(status: number) {
  return status === 0 ? '开启' : '停用'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'danger'
}

function codeLabel(code: string) {
  return codeOptions.find((o) => o.value === code)?.label || code
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
        <span>短信渠道</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="短信签名">
          <el-input v-model="query.signature" placeholder="短信签名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="渠道编码">
          <el-select v-model="query.code" placeholder="全部" clearable filterable style="width: 160px">
            <el-option v-for="o in codeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增短信渠道</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="signature" label="短信签名" min-width="140" />
        <el-table-column label="渠道编码" min-width="140">
          <template #default="{ row }">{{ codeLabel(row.code) }}（{{ row.code }}）</template>
        </el-table-column>
        <el-table-column prop="callbackUrl" label="回调 URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="短信签名" prop="signature">
          <el-input v-model="form.signature" placeholder="请输入短信签名" />
        </el-form-item>
        <el-form-item label="渠道编码" prop="code">
          <el-select v-model="form.code" placeholder="请选择渠道编码" style="width: 100%">
            <el-option v-for="o in codeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="API Key" prop="apiKey">
          <el-input v-model="form.apiKey" placeholder="请输入 API Key" />
        </el-form-item>
        <el-form-item label="API Secret" prop="apiSecret">
          <el-input v-model="form.apiSecret" placeholder="请输入 API Secret" show-password />
        </el-form-item>
        <el-form-item label="回调 URL" prop="callbackUrl">
          <el-input v-model="form.callbackUrl" placeholder="请输入回调 URL" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
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
