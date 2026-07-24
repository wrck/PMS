<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createTenantPackage,
  deleteTenantPackage,
  getTenantPackagePage,
  updateTenantPackage,
  type TenantPackagePageReqVO,
  type TenantPackageRespVO,
  type TenantPackageSaveReqVO
} from '@/api/yudao-system'
import { getMenuTree, type SysMenu } from '@/api/system'

interface MenuTreeNode extends SysMenu {
  children?: MenuTreeNode[]
}

const loading = ref(false)
const tableData = ref<TenantPackageRespVO[]>([])
const total = ref(0)
const menuTree = ref<MenuTreeNode[]>([])

const query = reactive<{
  pageNo: number
  pageSize: number
  name: string
  status: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  status: undefined,
  createTime: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const menuTreeRef = ref()
const submitting = ref(false)
const form = reactive<TenantPackageSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入套餐名', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): TenantPackageSaveReqVO {
  return {
    name: '',
    status: 0,
    remark: '',
    menuIds: []
  }
}

/** 将后端返回的平铺菜单列表按 parentId 构建为树形结构 */
function buildMenuTree(list: SysMenu[]): MenuTreeNode[] {
  const nodeMap = new Map<number, MenuTreeNode>()
  const tree: MenuTreeNode[] = []
  list.forEach((item) => {
    nodeMap.set(item.id!, { ...item, children: [] })
  })
  list.forEach((item) => {
    const node = nodeMap.get(item.id!)
    if (!node) return
    if (!item.parentId || item.parentId === 0 || !nodeMap.has(item.parentId)) {
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
    const params: TenantPackagePageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.name) params.name = query.name
    if (query.status !== undefined && query.status !== null) params.status = query.status
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getTenantPackagePage(params)
    tableData.value = res.list
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadMenuTree() {
  try {
    const list = await getMenuTree()
    menuTree.value = buildMenuTree(list)
  } catch {
    /* handled by interceptor */
  }
}

function handleSearch() {
  query.pageNo = 1
  loadData()
}

function handleReset() {
  query.name = ''
  query.status = undefined
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增租户套餐'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
  nextTick(() => {
    menuTreeRef.value?.setCheckedKeys([])
  })
}

function handleEdit(row: TenantPackageRespVO) {
  dialogTitle.value = '编辑租户套餐'
  Object.assign(form, createEmptyForm(), {
    id: row.id,
    name: row.name,
    status: row.status,
    remark: row.remark,
    menuIds: row.menuIds ? [...row.menuIds] : []
  })
  dialogVisible.value = true
  nextTick(() => {
    menuTreeRef.value?.setCheckedKeys(row.menuIds || [])
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      // 收集树勾选的菜单 id（包含半选父节点）
      const checked = menuTreeRef.value?.getCheckedKeys() || []
      const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() || []
      form.menuIds = [...halfChecked, ...checked]
      if (form.id) {
        await updateTenantPackage(form)
        ElMessage.success('更新成功')
      } else {
        await createTenantPackage(form)
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

function handleDelete(row: TenantPackageRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除租户套餐「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTenantPackage(row.id)
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

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  loadData()
  loadMenuTree()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>租户套餐</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="套餐名">
          <el-input v-model="query.name" placeholder="套餐名" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增套餐</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="套餐名" min-width="160" />
        <el-table-column label="菜单权限" min-width="200">
          <template #default="{ row }">
            <span v-if="row.menuIds && row.menuIds.length">共 {{ row.menuIds.length }} 项</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="套餐名" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单权限">
          <div class="menu-tree-wrap">
            <el-tree
              ref="menuTreeRef"
              :data="menuTree"
              :props="{ label: 'name', children: 'children' }"
              node-key="id"
              show-checkbox
              default-expand-all
              check-strictly
            />
          </div>
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
.menu-tree-wrap {
  max-height: 320px;
  overflow: auto;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 8px;
  width: 100%;
}
</style>
