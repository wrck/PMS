<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true">
        <el-form-item label="数据类型">
          <el-input v-model="dataType" placeholder="如: office, project_type" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button type="success" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dataType" label="数据类型" width="150" />
        <el-table-column prop="dataCode" label="编码" width="150" />
        <el-table-column prop="dataName" label="名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
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
        <el-form-item label="类型"><el-input v-model="formData.dataType" :disabled="!!formData.id" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="formData.dataCode" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="formData.dataName" /></el-form-item>
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
import { getBasicDataList, addBasicData, updateBasicData, deleteBasicData } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dataType = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = reactive({ id: null, dataType: '', dataCode: '', dataName: '', sort: 0, status: 1 })

const fetchData = async () => {
  if (!dataType.value) { tableData.value = []; return }
  loading.value = true
  try { const res = await getBasicDataList(dataType.value); tableData.value = res.data }
  finally { loading.value = false }
}
const handleAdd = () => { dialogTitle.value = '新增'; Object.assign(formData, { id: null, dataType: dataType.value, dataCode: '', dataName: '', sort: 0, status: 1 }); dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑'; Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => {
  if (formData.id) { await updateBasicData(formData); ElMessage.success('更新成功') }
  else { await addBasicData(formData); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetchData()
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' }).then(async () => {
    await deleteBasicData(row.id); ElMessage.success('删除成功'); fetchData()
  }).catch(() => {})
}
onMounted(() => { dataType.value = 'office'; fetchData() })
</script>
