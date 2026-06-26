<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="维保类型"><el-input v-model="queryForm.maintenanceType" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button @click="queryForm.maintenanceType='';fetchData()">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>维保记录</span><el-button type="primary" @click="handleAdd">新增</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="maintenanceType" label="维保类型" width="120" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="maintenanceDate" label="维保日期" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }"><el-button type="primary" link @click="handleEdit(row)">编辑</el-button><el-button type="danger" link @click="handleDelete(row)">删除</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="formData.id?'编辑':'新增'" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="项目编码"><el-input v-model="formData.projectCode" /></el-form-item>
        <el-form-item label="维保类型"><el-input v-model="formData.maintenanceType" /></el-form-item>
        <el-form-item label="操作人"><el-input v-model="formData.operator" /></el-form-item>
        <el-form-item label="维保内容"><el-input v-model="formData.maintenanceContent" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMaintenanceList, addMaintenance, updateMaintenance, deleteMaintenance } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false)
const queryForm = reactive({ pageNum: 1, pageSize: 10, maintenanceType: '' })
const formData = reactive({ id: null, projectCode: '', maintenanceType: '', operator: '', maintenanceContent: '' })
const fetchData = async () => { loading.value = true; try { const r = await getMaintenanceList(queryForm); tableData.value = r.data.records; total.value = r.data.total } finally { loading.value = false } }
const handleAdd = () => { Object.assign(formData, { id: null, projectCode: '', maintenanceType: '', operator: '', maintenanceContent: '' }); dialogVisible.value = true }
const handleEdit = row => { Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => { if (formData.id) { await updateMaintenance(formData) } else { await addMaintenance(formData) }; ElMessage.success('保存成功'); dialogVisible.value = false; fetchData() }
const handleDelete = row => { ElMessageBox.confirm('确认删除？').then(async () => { await deleteMaintenance(row.id); ElMessage.success('删除成功'); fetchData() }).catch(()=>{}) }
onMounted(fetchData)
</script>
