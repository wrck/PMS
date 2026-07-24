<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createNotifyTemplate,
  deleteNotifyTemplate,
  getNotifyTemplatePage,
  sendNotify,
  updateNotifyTemplate,
  type NotifySendReqVO,
  type NotifyTemplatePageReqVO,
  type NotifyTemplateRespVO,
  type NotifyTemplateSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<NotifyTemplateRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  code: string
  name: string
  status: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  code: '',
  name: '',
  status: undefined,
  createTime: undefined
})

const typeOptions = [
  { value: 1, label: '通知' },
  { value: 2, label: '公告' }
]

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<NotifyTemplateSaveReqVO>(createEmptyForm())

const sendDialogVisible = ref(false)
const sendFormRef = ref<FormInstance>()
const sendSubmitting = ref(false)
const sendForm = reactive<{ templateCode: string; userId: number; templateParams: string }>({
  templateCode: '',
  userId: 0,
  templateParams: '{}'
})

const rules: FormRules = {
  code: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入发件人名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入模板内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const sendRules: FormRules = {
  userId: [{ required: true, message: '请输入接收用户 ID', trigger: 'blur' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }]
}

function createEmptyForm(): NotifyTemplateSaveReqVO {
  return {
    name: '',
    nickname: '',
    code: '',
    content: '',
    type: 1,
    status: 0,
    remark: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: NotifyTemplatePageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.code) params.code = query.code
    if (query.name) params.name = query.name
    if (query.status !== undefined && query.status !== null) params.status = query.status
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getNotifyTemplatePage(params)
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
  query.code = ''
  query.name = ''
  query.status = undefined
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增站内信模板'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: NotifyTemplateRespVO) {
  dialogTitle.value = '编辑站内信模板'
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
        await updateNotifyTemplate(form)
        ElMessage.success('更新成功')
      } else {
        await createNotifyTemplate(form)
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

function handleDelete(row: NotifyTemplateRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除站内信模板「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteNotifyTemplate(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleSendTest(row: NotifyTemplateRespVO) {
  sendForm.templateCode = row.code
  sendForm.userId = 0
  sendForm.templateParams = '{}'
  sendDialogVisible.value = true
}

async function handleSendSubmit() {
  if (!sendFormRef.value) return
  await sendFormRef.value.validate(async (valid) => {
    if (!valid) return
    sendSubmitting.value = true
    try {
      let params: Record<string, unknown> = {}
      try {
        params = JSON.parse(sendForm.templateParams || '{}')
      } catch {
        ElMessage.error('模板参数必须为合法 JSON')
        sendSubmitting.value = false
        return
      }
      const payload: NotifySendReqVO = {
        userId: sendForm.userId,
        templateCode: sendForm.templateCode,
        templateParams: params
      }
      await sendNotify(payload)
      ElMessage.success('发送成功')
      sendDialogVisible.value = false
    } catch {
      /* handled by interceptor */
    } finally {
      sendSubmitting.value = false
    }
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

function typeLabel(type: number) {
  return typeOptions.find((o) => o.value === type)?.label || String(type)
}

function typeTagType(type: number): 'primary' | 'success' {
  return type === 1 ? 'primary' : 'success'
}

function statusLabel(status: number) {
  return status === 0 ? '开启' : '停用'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'danger'
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
        <span>站内信模板</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="模板编码">
          <el-input v-model="query.code" placeholder="模板编码" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模板名称">
          <el-input v-model="query.name" placeholder="模板名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
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

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增模板</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column label="模板类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="模板编码" min-width="160" />
        <el-table-column prop="name" label="模板名称" min-width="140" />
        <el-table-column prop="nickname" label="发件人名称" min-width="120" />
        <el-table-column prop="content" label="模板内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleSendTest(row)">测试</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="模板编码" prop="code">
          <el-input v-model="form.code" :disabled="!!form.id" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="发件人名称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入发件人名称" />
        </el-form-item>
        <el-form-item label="模板类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择模板类型" style="width: 100%">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入模板内容，使用 {var} 占位参数" />
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

    <el-dialog v-model="sendDialogVisible" title="发送测试站内信" width="560px" destroy-on-close>
      <el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="110px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="sendForm.templateCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="接收用户 ID" prop="userId">
          <el-input-number v-model="sendForm.userId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="模板参数" prop="templateParams">
          <el-input v-model="sendForm.templateParams" type="textarea" :rows="6" placeholder='请输入 JSON 格式参数，例如 {"code":"1234"}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sendSubmitting" @click="handleSendSubmit">发送</el-button>
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
