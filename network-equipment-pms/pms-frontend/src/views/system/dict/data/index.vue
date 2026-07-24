<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createDictData,
  deleteDictData,
  getDictDataPage,
  getSimpleDictTypeList,
  updateDictData,
  type DictDataPageReqVO,
  type DictDataRespVO,
  type DictDataSaveReqVO,
  type DictTypeSimpleRespVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<DictDataRespVO[]>([])
const total = ref(0)
const dictTypeList = ref<DictTypeSimpleRespVO[]>([])

const currentDictType = ref<string>('')

const query = reactive<{
  pageNo: number
  pageSize: number
  dictType: string
  label: string
  status: number | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  dictType: '',
  label: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const colorTypeOptions = [
  { value: 'default', label: '默认' },
  { value: 'primary', label: 'Primary' },
  { value: 'success', label: 'Success' },
  { value: 'info', label: 'Info' },
  { value: 'warning', label: 'Warning' },
  { value: 'danger', label: 'Danger' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<DictDataSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  dictType: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  label: [{ required: true, message: '请输入数据标签', trigger: 'blur' }],
  value: [{ required: true, message: '请输入数据键值', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }]
}

function createEmptyForm(): DictDataSaveReqVO {
  return {
    sort: 0,
    label: '',
    value: '',
    dictType: '',
    status: 0,
    colorType: 'default',
    cssClass: '',
    remark: ''
  }
}

async function loadDictTypeList() {
  try {
    dictTypeList.value = await getSimpleDictTypeList()
    if (dictTypeList.value.length && !currentDictType.value) {
      currentDictType.value = dictTypeList.value[0].type
      query.dictType = currentDictType.value
    }
  } catch {
    /* handled by interceptor */
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: DictDataPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.dictType) params.dictType = query.dictType
    if (query.label) params.label = query.label
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getDictDataPage(params)
    tableData.value = res.list
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

watch(currentDictType, (val) => {
  query.dictType = val
  query.pageNo = 1
  loadData()
})

function handleSearch() {
  query.pageNo = 1
  loadData()
}

function handleReset() {
  query.label = ''
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增字典数据'
  Object.assign(form, createEmptyForm())
  form.dictType = currentDictType.value
  dialogVisible.value = true
}

function handleEdit(row: DictDataRespVO) {
  dialogTitle.value = '编辑字典数据'
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
        await updateDictData(form)
        ElMessage.success('更新成功')
      } else {
        await createDictData(form)
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

function handleDelete(row: DictDataRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除字典数据「${row.label}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDictData(row.id)
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

function colorTagType(colorType: string) {
  if (colorType === 'primary' || colorType === 'default') return '' as const
  return colorType as 'success' | 'info' | 'warning' | 'danger'
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  loadDictTypeList().then(() => {
    loadData()
  })
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>字典数据</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="字典类型">
          <el-select
            v-model="currentDictType"
            placeholder="请选择字典类型"
            filterable
            style="width: 240px"
          >
            <el-option
              v-for="t in dictTypeList"
              :key="t.type"
              :label="`${t.name}（${t.type}）`"
              :value="t.type"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据标签">
          <el-input v-model="query.label" placeholder="数据标签" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增字典数据</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="label" label="数据标签" min-width="140" />
        <el-table-column prop="value" label="数据键值" min-width="120" />
        <el-table-column prop="dictType" label="字典类型" min-width="160" show-overflow-tooltip />
        <el-table-column label="样式" width="110">
          <template #default="{ row }">
            <el-tag :type="colorTagType(row.colorType)">{{ row.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
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
        <el-form-item label="字典类型" prop="dictType">
          <el-select v-model="form.dictType" placeholder="请选择字典类型" filterable style="width: 100%">
            <el-option
              v-for="t in dictTypeList"
              :key="t.type"
              :label="`${t.name}（${t.type}）`"
              :value="t.type"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据标签" prop="label">
          <el-input v-model="form.label" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="value">
          <el-input v-model="form.value" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="样式类型" prop="colorType">
          <el-select v-model="form.colorType" placeholder="请选择样式" style="width: 100%">
            <el-option v-for="o in colorTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="样式类名" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入样式类名（可选）" />
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
