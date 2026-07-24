<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createModel,
  deleteModel,
  deployModel,
  getCategorySimpleList,
  getFormSimpleList,
  getModelList,
  updateModel,
  type CategorySimpleRespVO,
  type FormSimpleRespVO,
  type ModelRespVO,
  type ModelSaveReqVO
} from '@/api/yudao-bpm'

const loading = ref(false)
const allData = ref<ModelRespVO[]>([])

// 搜索条件
const query = reactive<{ name: string; key: string; category: string | undefined; status: number | undefined }>({
  name: '',
  key: '',
  category: undefined,
  status: undefined
})

const statusOptions = [
  { value: 0, label: '已启用' },
  { value: 1, label: '已停用' }
]

const formTypeOptions = [
  { value: 10, label: '流程表单' },
  { value: 20, label: '业务表单' }
]

const categoryList = ref<CategorySimpleRespVO[]>([])
const formList = ref<FormSimpleRespVO[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<ModelSaveReqVO>(createEmptyForm())

// 客户端过滤（model 接口仅支持 name 服务端过滤）
const tableData = computed(() => {
  return allData.value.filter((item) => {
    if (query.name && !item.name?.includes(query.name)) return false
    if (query.key && !item.key?.includes(query.key)) return false
    if (query.category && item.category !== query.category) return false
    if (query.status !== undefined && query.status !== null && item.status !== query.status) return false
    return true
  })
})

const rules: FormRules = {
  key: [{ required: true, message: '请输入流程标识', trigger: 'blur' }],
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择流程分类', trigger: 'change' }],
  formType: [{ required: true, message: '请选择表单类型', trigger: 'change' }]
}

function createEmptyForm(): ModelSaveReqVO {
  return {
    key: '',
    name: '',
    description: '',
    category: '',
    formType: 10,
    formId: undefined,
    formCustomCreatePath: '',
    formCustomViewPath: '',
    status: 0
  }
}

async function loadData() {
  loading.value = true
  try {
    allData.value = await getModelList(query.name || undefined)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  try {
    const [cats, forms] = await Promise.all([getCategorySimpleList(), getFormSimpleList()])
    categoryList.value = cats
    formList.value = forms
  } catch {
    /* handled by interceptor */
  }
}

function handleSearch() {
  loadData()
}

function handleReset() {
  query.name = ''
  query.key = ''
  query.category = undefined
  query.status = undefined
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增流程模型'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: ModelRespVO) {
  dialogTitle.value = '编辑流程模型'
  Object.assign(form, createEmptyForm(), {
    id: row.id,
    key: row.key,
    name: row.name,
    description: row.description,
    category: row.category,
    formType: row.formType,
    formId: row.formId,
    formCustomCreatePath: row.formCustomCreatePath,
    formCustomViewPath: row.formCustomViewPath,
    status: row.status
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateModel(form)
        ElMessage.success('更新成功')
      } else {
        await createModel(form)
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

function handleDelete(row: ModelRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除模型「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteModel(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDeploy(row: ModelRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定部署模型「${row.name}」吗？`, '部署确认', { type: 'warning' })
    .then(async () => {
      await deployModel(row.id)
      ElMessage.success('部署成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function statusLabel(status: number) {
  return status === 0 ? '已启用' : '已停用'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'info'
}

function formTypeLabel(formType: number) {
  return formTypeOptions.find((o) => o.value === formType)?.label || '-'
}

function categoryName(code: string) {
  return categoryList.value.find((c) => c.code === code)?.name || code || '-'
}

onMounted(() => {
  loadOptions()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>流程模型</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="流程名称">
          <el-input v-model="query.name" placeholder="流程名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="流程标识">
          <el-input v-model="query.key" placeholder="流程标识" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-select v-model="query.category" placeholder="全部" clearable style="width: 160px">
            <el-option
              v-for="c in categoryList"
              :key="c.code"
              :label="c.name"
              :value="c.code"
            />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增模型</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="key" label="流程标识" min-width="140" />
        <el-table-column prop="name" label="流程名称" min-width="160" />
        <el-table-column label="流程分类" min-width="120">
          <template #default="{ row }">{{ categoryName(row.category) }}</template>
        </el-table-column>
        <el-table-column label="表单类型" width="110">
          <template #default="{ row }">{{ formTypeLabel(row.formType) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleDeploy(row)">部署</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="流程标识" prop="key">
          <el-input v-model="form.key" :disabled="!!form.id" placeholder="请输入流程标识" />
        </el-form-item>
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择流程分类" style="width: 100%">
            <el-option
              v-for="c in categoryList"
              :key="c.code"
              :label="c.name"
              :value="c.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表单类型" prop="formType">
          <el-select v-model="form.formType" placeholder="请选择表单类型" style="width: 100%">
            <el-option v-for="o in formTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.formType === 10" label="流程表单" prop="formId">
          <el-select v-model="form.formId" placeholder="请选择流程表单" style="width: 100%">
            <el-option v-for="f in formList" :key="f.id" :label="f.name" :value="f.id" />
          </el-select>
        </el-form-item>
        <template v-if="form.formType === 20">
          <el-form-item label="自定义表单路径" prop="formCustomCreatePath">
            <el-input v-model="form.formCustomCreatePath" placeholder="请输入表单提交路由" />
          </el-form-item>
          <el-form-item label="自定义详情路径" prop="formCustomViewPath">
            <el-input v-model="form.formCustomViewPath" placeholder="请输入表单查看路由" />
          </el-form-item>
        </template>
        <el-form-item label="流程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入流程描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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
</style>
