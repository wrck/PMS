<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="roleCode" label="角色编码" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="170" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="角色名称"><el-input v-model="formData.roleName" /></el-form-item>
        <el-form-item label="角色编码"><el-input v-model="formData.roleCode" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="formData.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRoleList, addRole, updateRole, deleteRole } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = reactive({ id: null, roleName: '', roleCode: '', status: 1 })

const fetchData = async () => {
  loading.value = true
  try { const res = await getRoleList({ pageNum: pageNum.value, pageSize: pageSize.value }); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
const handleAdd = () => { dialogTitle.value = '新增角色'; Object.assign(formData, { id: null, roleName: '', roleCode: '', status: 1 }); dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑角色'; Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => {
  if (formData.id) { await updateRole(formData); ElMessage.success('更新成功') }
  else { await addRole(formData); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetchData()
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' }).then(async () => {
    await deleteRole(row.id); ElMessage.success('删除成功'); fetchData()
  }).catch(() => {})
}
onMounted(fetchData)
</script>
