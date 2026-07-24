<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createPost,
  deletePost,
  getPostPage,
  updatePost,
  type PostPageReqVO,
  type PostRespVO,
  type PostSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<PostRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; code: string; name: string; status: number | undefined }>({
  pageNo: 1,
  pageSize: 10,
  code: '',
  name: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '关闭' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<PostSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }]
}

function createEmptyForm(): PostSaveReqVO {
  return {
    name: '',
    code: '',
    sort: 0,
    status: 0,
    remark: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: PostPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.code) params.code = query.code
    if (query.name) params.name = query.name
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getPostPage(params)
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
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增岗位'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: PostRespVO) {
  dialogTitle.value = '编辑岗位'
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
        await updatePost(form)
        ElMessage.success('更新成功')
      } else {
        await createPost(form)
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

function handleDelete(row: PostRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除岗位「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deletePost(row.id)
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

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>岗位管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="岗位编码">
          <el-input v-model="query.code" placeholder="岗位编码" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input v-model="query.name" placeholder="岗位名称" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增岗位</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="岗位名称" min-width="140" />
        <el-table-column prop="code" label="岗位编码" min-width="140" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="岗位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="code">
          <el-input v-model="form.code" :disabled="!!form.id" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
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
