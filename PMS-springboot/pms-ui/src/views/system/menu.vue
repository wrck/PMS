<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>菜单管理</span>
          <el-button type="primary" @click="showDialog()">新增菜单</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" min-width="200" />
        <el-table-column prop="menuCode" label="菜单编码" width="150" />
        <el-table-column prop="menuType" label="菜单类型" width="100">
          <template #default="{ row }"><el-tag :type="row.menuType==='M'?'':row.menuType==='C'?'success':'warning'" size="small">{{ {M:'目录',C:'菜单',B:'按钮'}[row.menuType] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="200" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="showDialog(row)">编辑</el-button>
            <el-button v-if="row.menuType !== 'B'" size="small" type="success" link @click="showDialog(null, row.id)">新增子菜单</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜单' : '新增菜单'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="上级菜单">
          <el-tree-select v-model="form.parentId" :data="menuTree" :props="{ label: 'menuName', value: 'id' }" check-strictly clearable style="width:100%" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio-button value="M">目录</el-radio-button>
            <el-radio-button value="C">菜单</el-radio-button>
            <el-radio-button value="B">按钮</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName"><el-input v-model="form.menuName" /></el-form-item>
        <el-form-item label="菜单编码" prop="menuCode"><el-input v-model="form.menuCode" /></el-form-item>
        <el-form-item v-if="form.menuType !== 'B'" label="路由路径"><el-input v-model="form.path" /></el-form-item>
        <el-form-item v-if="form.menuType !== 'B'" label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.menuType === 'B'" label="权限标识"><el-input v-model="form.perms" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMenus, addMenu, updateMenu, deleteMenu } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const menuTree = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({ id: null, parentId: null, menuType: 'M', menuName: '', menuCode: '', path: '', icon: '', sortOrder: 0, status: 1, perms: '' })
const rules = { menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }], menuCode: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }] }
const fetchData = async () => { loading.value = true; try { const r = await listMenus(); tableData.value = r.data || []; menuTree.value = r.data || [] } finally { loading.value = false } }
const showDialog = (row, parentId) => { if (row) Object.assign(form, row); else Object.assign(form, { id: null, parentId: parentId || null, menuType: 'M', menuName: '', menuCode: '', path: '', icon: '', sortOrder: 0, status: 1, perms: '' }); dialogVisible.value = true }
const handleSave = async () => { await formRef.value.validate(); if (form.id) await updateMenu(form); else await addMenu(form); ElMessage.success('保存成功'); dialogVisible.value = false; fetchData() }
const handleDelete = (row) => { ElMessageBox.confirm('确认删除该菜单？', '提示', { type: 'warning' }).then(async () => { await deleteMenu(row.id); ElMessage.success('已删除'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
