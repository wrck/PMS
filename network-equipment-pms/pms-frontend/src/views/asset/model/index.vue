<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createModel,
  deleteModel,
  getCategoryTree,
  listModels,
  updateModel,
  type AssetCategory,
  type AssetModel,
  type ModelPageQuery
} from '@/api/asset'

const loading = ref(false)
const tableData = ref<AssetModel[]>([])
const total = ref(0)
const categoryTree = ref<AssetCategory[]>([])

const query = reactive<ModelPageQuery>({ categoryId: undefined, name: '', page: 1, size: 10 })

// Dialog state
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const isEdit = ref(false)
const form = reactive<AssetModel>(createEmptyForm())

const categoryTreeProps = { label: 'name', value: 'id', children: 'children' }

const rules: FormRules = {
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入型号名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入型号编码', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): AssetModel {
  return {
    categoryId: 0,
    code: '',
    name: '',
    brand: '',
    spec: '',
    standardPrice: 0,
    unit: '',
    status: 1,
    remark: ''
  }
}

async function loadCategories() {
  try {
    categoryTree.value = (await getCategoryTree()) || []
  } catch {
    /* handled by interceptor */
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await listModels({
      categoryId: query.categoryId,
      name: query.name || undefined,
      page: query.page,
      size: query.size
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.categoryId = undefined
  query.name = ''
  query.page = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增型号'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: AssetModel) {
  isEdit.value = true
  dialogTitle.value = '编辑型号'
  Object.assign(form, createEmptyForm(), row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value && form.id) {
        await updateModel({ ...form })
        ElMessage.success('更新成功')
      } else {
        await createModel({ ...form })
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

function handleDelete(row: AssetModel) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除型号「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteModel(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handlePageChange(p: number) {
  query.page = p
  loadData()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadData()
}

function formatPrice(val?: number) {
  if (val === undefined || val === null) return '-'
  return `¥${val.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function statusTagType(status?: number) {
  return status === 1 ? 'success' : 'info'
}

function statusLabel(status?: number) {
  return status === 1 ? '启用' : '禁用'
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title">设备型号管理</div>

    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="分类">
          <el-tree-select
            v-model="query.categoryId"
            :data="categoryTree"
            :props="categoryTreeProps"
            node-key="id"
            check-strictly
            clearable
            default-expand-all
            placeholder="请选择分类"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="型号名称">
          <el-input
            v-model="query.name"
            placeholder="型号名称/编码"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增型号</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="型号名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="code" label="型号编码" min-width="140" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" min-width="120" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" min-width="140" show-overflow-tooltip />
        <el-table-column prop="spec" label="规格参数" min-width="180" show-overflow-tooltip />
        <el-table-column label="标准单价" width="130" align="right">
          <template #default="{ row }">
            {{ formatPrice(row.standardPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="90" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类" prop="categoryId">
          <el-tree-select
            v-model="form.categoryId"
            :data="categoryTree"
            :props="categoryTreeProps"
            node-key="id"
            check-strictly
            default-expand-all
            placeholder="请选择所属分类"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="型号名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入型号名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="型号编码" prop="code">
          <el-input
            v-model="form.code"
            placeholder="请输入型号编码"
            maxlength="50"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" maxlength="50" />
        </el-form-item>
        <el-form-item label="规格参数" prop="spec">
          <el-input
            v-model="form.spec"
            type="textarea"
            :rows="3"
            placeholder="请输入规格参数"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="标准单价" prop="standardPrice">
          <el-input-number
            v-model="form.standardPrice"
            :min="0"
            :precision="2"
            :step="100"
            style="width: 200px"
          />
          <span class="form-tip">元</span>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：台/套/个" maxlength="20" style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="备注信息"
            maxlength="200"
          />
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
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.form-tip {
  margin-left: 8px;
  color: #909399;
}
</style>
