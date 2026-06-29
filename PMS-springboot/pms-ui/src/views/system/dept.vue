<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">新增部门</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all border>
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="deptCode" label="部门编码" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="部门名称"><el-input v-model="formData.deptName" /></el-form-item>
        <el-form-item label="部门编码"><el-input v-model="formData.deptCode" /></el-form-item>
        <el-form-item label="上级部门"><el-input v-model="formData.parentId" placeholder="0为顶级" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="formData.sort" :min="0" /></el-form-item>
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
import { getDeptList, addDept, updateDept, deleteDept } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = reactive({ id: null, deptName: '', deptCode: '', parentId: 0, sort: 0, status: 1 })

const fetchData = async () => {
  loading.value = true
  try { const res = await getDeptList(); tableData.value = res.data }
  finally { loading.value = false }
}
const handleAdd = () => { dialogTitle.value = '新增部门'; Object.assign(formData, { id: null, deptName: '', deptCode: '', parentId: 0, sort: 0, status: 1 }); dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑部门'; Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => {
  if (formData.id) { await updateDept(formData); ElMessage.success('更新成功') }
  else { await addDept(formData); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetchData()
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该部门？', '提示', { type: 'warning' }).then(async () => {
    await deleteDept(row.id); ElMessage.success('删除成功'); fetchData()
  }).catch(() => {})
}
onMounted(fetchData)
</script>
