<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createNotice,
  deleteNotice,
  getNoticePage,
  updateNotice,
  type NoticePageReqVO,
  type NoticeRespVO,
  type NoticeSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<NoticeRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; title: string; status: number | undefined }>({
  pageNo: 1,
  pageSize: 10,
  title: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '关闭' }
]

const typeOptions = [
  { value: 1, label: '通知' },
  { value: 2, label: '公告' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<NoticeSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

function createEmptyForm(): NoticeSaveReqVO {
  return {
    title: '',
    type: 1,
    content: '',
    status: 0
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: NoticePageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.title) params.title = query.title
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getNoticePage(params)
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
  query.title = ''
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增公告'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: NoticeRespVO) {
  dialogTitle.value = '编辑公告'
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
        await updateNotice(form)
        ElMessage.success('更新成功')
      } else {
        await createNotice(form)
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

function handleDelete(row: NoticeRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除公告「${row.title}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteNotice(row.id)
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
  return status === 0 ? '开启' : '关闭'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'info'
}

function typeLabel(type: number) {
  return type === 1 ? '通知' : '公告'
}

function typeTagType(type: number) {
  return type === 1 ? 'primary' : 'success'
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>通知公告</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="公告标题">
          <el-input v-model="query.title" placeholder="公告标题" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增公告</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="title" label="公告标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="公告类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
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
