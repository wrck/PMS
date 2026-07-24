<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createDept,
  deleteDept,
  getDeptList,
  updateDept,
  type DeptListReqVO,
  type DeptRespVO,
  type DeptSaveReqVO
} from '@/api/yudao-system'

interface DeptTreeNode extends DeptRespVO {
  children?: DeptTreeNode[]
}

const loading = ref(false)
const treeData = ref<DeptTreeNode[]>([])

const query = reactive<{ name: string; status: number | undefined }>({ name: '', status: undefined })

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '关闭' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<DeptSaveReqVO>(createEmptyForm())

/** 树形下拉数据：包含虚拟根节点（id=0） */
const deptTreeWithRoot = computed(() => [
  { id: 0, name: '顶级部门', children: treeData.value }
])

const rules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }]
}

function createEmptyForm(): DeptSaveReqVO {
  return {
    name: '',
    parentId: 0,
    sort: 0,
    leaderUserId: undefined,
    phone: '',
    email: '',
    status: 0
  }
}

/** 将后端返回的平铺列表按 parentId 构建为树形结构 */
function buildDeptTree(list: DeptRespVO[]): DeptTreeNode[] {
  const nodeMap = new Map<number, DeptTreeNode>()
  const tree: DeptTreeNode[] = []
  list.forEach((item) => {
    nodeMap.set(item.id, { ...item, children: [] })
  })
  list.forEach((item) => {
    const node = nodeMap.get(item.id)
    if (!node) return
    if (item.parentId === 0 || !nodeMap.has(item.parentId)) {
      tree.push(node)
    } else {
      const parent = nodeMap.get(item.parentId)
      if (parent) {
        if (!parent.children) parent.children = []
        parent.children.push(node)
      } else {
        tree.push(node)
      }
    }
  })
  return tree
}

async function loadData() {
  loading.value = true
  try {
    const params: DeptListReqVO = {}
    if (query.name) params.name = query.name
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const list = await getDeptList(params)
    treeData.value = buildDeptTree(list)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadData()
}

function handleReset() {
  query.name = ''
  query.status = undefined
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增部门'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: DeptRespVO) {
  dialogTitle.value = '编辑部门'
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
        await updateDept(form)
        ElMessage.success('更新成功')
      } else {
        await createDept(form)
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

function handleDelete(row: DeptRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除部门「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDept(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
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
        <span>部门管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="部门名称">
          <el-input v-model="query.name" placeholder="部门名称" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增部门</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="treeData"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        stripe
        default-expand-all
      >
        <el-table-column prop="name" label="部门名称" min-width="180" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">
            {{ row.leaderUserId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
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
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="deptTreeWithRoot"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            node-key="id"
            check-strictly
            default-expand-all
            placeholder="请选择上级部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
