<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="转包名称"><el-input v-model="queryForm.subcontractName" clearable /></el-form-item>
        <el-form-item label="办事处"><el-input v-model="queryForm.officeCode" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>转包列表</span><el-button type="primary" @click="handleAdd">新增</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="subcontractCode" label="转包编码" width="140" />
        <el-table-column prop="subcontractName" label="转包名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectName" label="关联项目" width="180" show-overflow-tooltip />
        <el-table-column prop="facilitatorName" label="服务商" width="140" />
        <el-table-column prop="contractAmount" label="合同金额" width="120" />
        <el-table-column prop="officeCode" label="办事处" width="100" />
        <el-table-column prop="state" label="状态" width="80"><template #default="{ row }"><el-tag :type="row.state===1?'success':'info'">{{ row.state===1?'有效':'无效' }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="formData.id?'编辑':'新增'" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="转包名称"><el-input v-model="formData.subcontractName" /></el-form-item>
        <el-form-item label="转包编码"><el-input v-model="formData.subcontractCode" /></el-form-item>
        <el-form-item label="关联项目"><el-input v-model="formData.projectName" /></el-form-item>
        <el-form-item label="服务商"><el-input v-model="formData.facilitatorName" /></el-form-item>
        <el-form-item label="合同金额"><el-input-number v-model="formData.contractAmount" :min="0" /></el-form-item>
        <el-form-item label="办事处"><el-input v-model="formData.officeCode" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSubcontractList, addSubcontract, updateSubcontract, deleteSubcontract } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false)
const queryForm = reactive({ pageNum: 1, pageSize: 10, subcontractName: '', officeCode: '' })
const formData = reactive({ id: null, subcontractCode: '', subcontractName: '', projectName: '', facilitatorName: '', contractAmount: 0, officeCode: '' })
const fetchData = async () => { loading.value = true; try { const r = await getSubcontractList(queryForm); tableData.value = r.data.records; total.value = r.data.total } finally { loading.value = false } }
const resetQuery = () => { Object.assign(queryForm, { subcontractName: '', officeCode: '' }); fetchData() }
const handleAdd = () => { Object.assign(formData, { id: null, subcontractCode: '', subcontractName: '', projectName: '', facilitatorName: '', contractAmount: 0, officeCode: '' }); dialogVisible.value = true }
const handleEdit = row => { Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => { if (formData.id) { await updateSubcontract(formData) } else { await addSubcontract(formData) }; ElMessage.success('保存成功'); dialogVisible.value = false; fetchData() }
const handleDelete = row => { ElMessageBox.confirm('确认删除？').then(async () => { await deleteSubcontract(row.id); ElMessage.success('删除成功'); fetchData() }).catch(()=>{}) }
onMounted(fetchData)
</script>
