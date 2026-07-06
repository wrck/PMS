<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { createMenu, deleteMenu, getMenuTree, updateMenu, type SysMenu } from '@/api/system'

const loading = ref(false)
const treeData = ref<SysMenu[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<SysMenu>(createEmptyForm())

// Tree-select options including a virtual root node (parentId = 0)
const menuTreeWithRoot = computed(() => [
  { id: 0, name: '顶级菜单', children: treeData.value }
])

const menuTypeOptions = [
  { value: 0, label: '目录' },
  { value: 1, label: '菜单' },
  { value: 2, label: '按钮' },
  { value: 3, label: '低代码页面' }
]

/** 低代码页面类型选项 */
const lowcodePageTypeOptions = [
  { value: 'form', label: '表单' },
  { value: 'list', label: '列表' },
  { value: 'tab', label: '标签页' },
  { value: 'related-page', label: '关联页' }
]

const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  pageType: [{ required: true, message: '请选择低代码页面类型', trigger: 'change' }],
  pageCode: [{ required: true, message: '请输入低代码配置编码', trigger: 'blur' }]
}

function createEmptyForm(): SysMenu {
  return {
    parentId: 0,
    name: '',
    path: '',
    component: '',
    icon: '',
    type: 1,
    permission: '',
    sort: 0,
    visible: 1,
    status: 1,
    pageType: 'form',
    pageCode: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    treeData.value = await getMenuTree()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleAdd(row?: SysMenu) {
  dialogTitle.value = '新增菜单'
  Object.assign(form, createEmptyForm())
  if (row?.id) form.parentId = row.id
  dialogVisible.value = true
}

function handleEdit(row: SysMenu) {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, createEmptyForm(), row)
  dialogVisible.value = true
}

/**
 * 当低代码页面类型或编码变化时，自动生成路由 path 为 /lowcode/{pageType}/{pageCode}。
 * 同时为权限标识生成默认值 lowcode:page:{pageType}:{pageCode}（仅在用户未自定义时）。
 */
watch(
  () => [form.type, form.pageType, form.pageCode],
  ([type, pageType, pageCode]) => {
    if (type === 3 && pageType && pageCode) {
      form.path = `/lowcode/${pageType}/${pageCode}`
      // 权限标识为空或匹配自动生成规则时，跟随 pageType/pageCode 更新
      const autoPerm = `lowcode:page:${pageType}:${pageCode}`
      if (!form.permission || /^lowcode:page:[a-z-]+:.+$/.test(form.permission)) {
        form.permission = autoPerm
      }
    }
  }
)

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateMenu(form)
        ElMessage.success('更新成功')
      } else {
        await createMenu(form)
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

function handleDelete(row: SysMenu) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除菜单「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteMenu(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function typeLabel(type: number) {
  return menuTypeOptions.find((o) => o.value === type)?.label ?? ''
}

function typeTagType(type: number) {
  if (type === 0) return 'primary'
  if (type === 1) return 'success'
  if (type === 3) return 'danger'
  return 'warning'
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd()">新增菜单</el-button>
        <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
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
        <el-table-column prop="name" label="菜单名称" min-width="180" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="path" label="路由路径" min-width="140" />
        <el-table-column prop="component" label="组件" min-width="180" />
        <el-table-column prop="permission" label="权限标识" min-width="160" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAdd(row)">新增</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeWithRoot"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            node-key="id"
            check-strictly
            default-expand-all
            placeholder="请选择上级菜单"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio v-for="o in menuTypeOptions" :key="o.value" :value="o.value">{{ o.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名称，如 User" />
        </el-form-item>
        <!-- 低代码页面类型（type=3 时显示） -->
        <el-form-item v-if="form.type === 3" label="页面类型" prop="pageType">
          <el-select v-model="form.pageType" placeholder="请选择低代码页面类型" style="width: 100%">
            <el-option
              v-for="o in lowcodePageTypeOptions"
              :key="o.value"
              :label="o.label"
              :value="o.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 3" label="页面编码" prop="pageCode">
          <el-input
            v-model="form.pageCode"
            placeholder="低代码配置编码，如 tpl_project_create"
          />
        </el-form-item>
        <el-form-item v-if="form.type !== 2" label="路由路径" prop="path">
          <el-input
            v-model="form.path"
            :placeholder="form.type === 3 ? '由页面类型和编码自动生成' : '如 /system/user'"
            :disabled="form.type === 3"
          />
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item v-if="form.type !== 0" label="权限标识" prop="permission">
          <el-input
            v-model="form.permission"
            :placeholder="form.type === 3 ? '如 lowcode:page:form:myCode（可自定义）' : '如 system:user:list'"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否显示" prop="visible">
          <el-switch v-model="form.visible" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
