<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createDict,
  createDictItem,
  deleteDict,
  deleteDictItem,
  getDictItems,
  getDictPage,
  updateDict,
  updateDictItem,
  type SysDict,
  type SysDictItem
} from '@/api/system'

const loading = ref(false)
const tableData = ref<SysDict[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; dictName: string }>({ page: 1, size: 10, dictName: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<SysDict>(createEmptyForm())

const rules: FormRules = {
  dictType: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

function createEmptyForm(): SysDict {
  return { dictType: '', dictName: '', status: '0' }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getDictPage(query)
    tableData.value = res.records
    total.value = res.total
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
  query.dictName = ''
  query.page = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增字典'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: SysDict) {
  dialogTitle.value = '编辑字典'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateDict(form)
        ElMessage.success('更新成功')
      } else {
        await createDict(form)
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

function handleDelete(row: SysDict) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除字典「${row.dictName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDict(row.id!)
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

// ============ 字典项管理 ============

const itemDialogVisible = ref(false)
const itemDialogTitle = ref('')
const itemLoading = ref(false)
const itemList = ref<SysDictItem[]>([])
const itemFormRef = ref<FormInstance>()
const itemSubmitting = ref(false)
const itemForm = reactive<SysDictItem>(createEmptyItemForm())
const currentDict = ref<SysDict | null>(null)

const itemRules: FormRules = {
  itemText: [{ required: true, message: '请输入字典项文本', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入字典项值', trigger: 'blur' }]
}

function createEmptyItemForm(): SysDictItem {
  return { dictId: 0, itemText: '', itemValue: '', sortOrder: 0, status: '0' }
}

async function handleManageItems(row: SysDict) {
  currentDict.value = row
  itemDialogTitle.value = `字典项管理 - ${row.dictName}`
  Object.assign(itemForm, createEmptyItemForm())
  itemDialogVisible.value = true
  await loadItems(row)
}

async function loadItems(row: SysDict) {
  if (!row.dictType) return
  itemLoading.value = true
  try {
    itemList.value = await getDictItems(row.dictType)
  } catch {
    /* handled by interceptor */
  } finally {
    itemLoading.value = false
  }
}

function handleAddItem() {
  Object.assign(itemForm, createEmptyItemForm())
  if (currentDict.value?.id) {
    itemForm.dictId = currentDict.value.id
  }
  itemDialogTitle.value = `新增字典项 - ${currentDict.value?.dictName ?? ''}`
}

function handleEditItem(row: SysDictItem) {
  Object.assign(itemForm, row)
  itemDialogTitle.value = `编辑字典项 - ${currentDict.value?.dictName ?? ''}`
}

async function handleItemSubmit() {
  if (!itemFormRef.value) return
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    itemSubmitting.value = true
    try {
      if (itemForm.id) {
        await updateDictItem(itemForm)
        ElMessage.success('更新成功')
      } else {
        await createDictItem(itemForm)
        ElMessage.success('新增成功')
      }
      Object.assign(itemForm, createEmptyItemForm())
      if (currentDict.value) await loadItems(currentDict.value)
    } catch {
      /* handled by interceptor */
    } finally {
      itemSubmitting.value = false
    }
  })
}

function handleDeleteItem(row: SysDictItem) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除字典项「${row.itemText}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDictItem(row.id!)
      ElMessage.success('删除成功')
      if (currentDict.value) await loadItems(currentDict.value)
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="字典名称">
          <el-input v-model="query.dictName" placeholder="字典名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增字典</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="dictType" label="字典编码" min-width="160" />
        <el-table-column prop="dictName" label="字典名称" min-width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'">
              {{ row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleManageItems(row)">字典项</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典编码" prop="dictType">
          <el-input v-model="form.dictType" :disabled="!!form.id" placeholder="请输入字典编码" />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="'0'" :inactive-value="'1'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemDialogVisible" :title="itemDialogTitle" width="720px" destroy-on-close>
      <div class="item-toolbar">
        <el-button type="primary" size="small" :icon="'Plus'" @click="handleAddItem">新增字典项</el-button>
      </div>
      <el-table v-loading="itemLoading" :data="itemList" border stripe size="small">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="itemText" label="字典项文本" min-width="140" />
        <el-table-column prop="itemValue" label="字典项值" min-width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'" size="small">
              {{ row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEditItem(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left">字典项表单</el-divider>

      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px" inline>
        <el-form-item label="字典项文本" prop="itemText">
          <el-input v-model="itemForm.itemText" placeholder="请输入字典项文本" />
        </el-form-item>
        <el-form-item label="字典项值" prop="itemValue">
          <el-input v-model="itemForm.itemValue" placeholder="请输入字典项值" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="itemForm.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="itemForm.status" :active-value="'0'" :inactive-value="'1'" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="itemSubmitting" @click="handleItemSubmit">
            {{ itemForm.id ? '更新' : '新增' }}
          </el-button>
        </el-form-item>
      </el-form>
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
.item-toolbar {
  margin-bottom: 12px;
}
</style>
