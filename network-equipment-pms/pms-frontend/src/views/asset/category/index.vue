<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createCategory,
  deleteCategory,
  getCategoryTree,
  updateCategory,
  type AssetCategory
} from '@/api/asset'

const loading = ref(false)
const treeData = ref<AssetCategory[]>([])
const treeRef = ref()
const currentNodeId = ref<number | undefined>(undefined)
const filterText = ref('')

// Form panel state
const formRef = ref<FormInstance>()
const submitting = ref(false)
const isEdit = ref(false)
const form = reactive<AssetCategory>(createEmptyForm())

// Parent options for the el-tree-select (virtual root: parentId = 0)
const treeWithRoot = computed(() => [
  { id: 0, name: '顶级分类', children: treeData.value }
])

const rules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): AssetCategory {
  return {
    parentId: 0,
    code: '',
    name: '',
    sort: 0,
    status: 1
  }
}

const treeProps = { label: 'name', children: 'children' }

async function loadData() {
  loading.value = true
  try {
    treeData.value = (await getCategoryTree()) || []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function filterNode(value: string, data: AssetCategory) {
  if (!value) return true
  return data.name?.includes(value) ?? false
}

function handleFilterText() {
  treeRef.value?.filter(filterText.value)
}

function handleNodeClick(data: AssetCategory) {
  currentNodeId.value = data.id
  isEdit.value = true
  Object.assign(form, createEmptyForm(), data)
}

function handleAddTop() {
  resetForm()
  isEdit.value = false
  form.parentId = 0
}

function handleAddChild(data: AssetCategory) {
  if (!data.id) return
  resetForm()
  isEdit.value = false
  form.parentId = data.id ?? 0
}

function resetForm() {
  Object.assign(form, createEmptyForm())
  currentNodeId.value = undefined
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value && form.id) {
        await updateCategory({ ...form })
        ElMessage.success('更新成功')
      } else {
        const created = await createCategory({ ...form })
        ElMessage.success('新增成功')
        if (created?.id) currentNodeId.value = created.id
      }
      await loadData()
      await nextTick()
      if (currentNodeId.value) {
        treeRef.value?.setCurrentKey(currentNodeId.value)
      }
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

function handleDelete(data: AssetCategory) {
  if (!data.id) return
  const hasChildren = data.children && data.children.length > 0
  const tip = hasChildren
    ? `分类「${data.name}」包含子分类，删除后子分类也将被删除，确定继续吗？`
    : `确定删除分类「${data.name}」吗？`
  ElMessageBox.confirm(tip, '提示', { type: 'warning' })
    .then(async () => {
      await deleteCategory(data.id!)
      ElMessage.success('删除成功')
      if (currentNodeId.value === data.id) {
        resetForm()
        isEdit.value = false
      }
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleReset() {
  resetForm()
  isEdit.value = false
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-title">设备分类管理</div>

    <el-row :gutter="12" class="content-row">
      <!-- Left panel: category tree -->
      <el-col :span="9">
        <el-card shadow="never" class="tree-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>分类树</span>
              <el-button type="primary" :icon="'Plus'" size="small" @click="handleAddTop">
                新增顶级分类
              </el-button>
            </div>
          </template>

          <el-input
            v-model="filterText"
            placeholder="输入分类名称过滤"
            clearable
            :prefix-icon="'Search'"
            class="filter-input"
            @input="handleFilterText"
          />

          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            highlight-current
            default-expand-all
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <div class="tree-node">
                <span class="tree-node-label">
                  <el-tag
                    :type="data.status === 0 ? 'info' : 'success'"
                    size="small"
                    class="status-dot"
                  >
                    {{ data.status === 0 ? '禁用' : '启用' }}
                  </el-tag>
                  {{ data.name }}
                  <span class="tree-node-code">（{{ data.code }}）</span>
                </span>
                <span class="tree-node-actions" @click.stop>
                  <el-button link type="primary" size="small" @click="handleAddChild(data)">
                    新增子级
                  </el-button>
                  <el-button link type="danger" size="small" @click="handleDelete(data)">
                    删除
                  </el-button>
                </span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- Right panel: detail/edit form -->
      <el-col :span="15">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>{{ isEdit ? '编辑分类' : '新增分类' }}</span>
              <el-button v-if="isEdit" :icon="'Plus'" size="small" @click="handleAddTop">
                切换为新增
              </el-button>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            style="max-width: 560px"
          >
            <el-form-item label="上级分类" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="treeWithRoot"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                node-key="id"
                check-strictly
                default-expand-all
                placeholder="请选择上级分类"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="50" />
            </el-form-item>
            <el-form-item label="分类编码" prop="code">
              <el-input
                v-model="form.code"
                placeholder="请输入分类编码"
                maxlength="50"
                :disabled="isEdit"
              />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="9999" />
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
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmit">
                {{ isEdit ? '保存' : '新增' }}
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
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
.content-row {
  width: 100%;
}
.tree-card {
  min-height: 480px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.filter-input {
  margin-bottom: 12px;
}
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}
.tree-node-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.status-dot {
  margin-right: 4px;
}
.tree-node-code {
  color: #909399;
  font-size: 12px;
}
.tree-node-actions {
  display: none;
}
:deep(.el-tree-node__content:hover) .tree-node-actions {
  display: inline-flex;
}
:deep(.el-tree-node.is-current > .el-tree-node__content) .tree-node-actions {
  display: inline-flex;
}
</style>
