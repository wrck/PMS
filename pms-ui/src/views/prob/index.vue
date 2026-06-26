<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="标题"><el-input v-model="queryForm.probTitle" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.probState" clearable placeholder="全部">
            <el-option label="草稿" :value="1" /><el-option label="已发布" :value="2" /><el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>技术公告</span><el-button type="primary" @click="handleAdd">新增</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="probCode" label="编号" width="160" />
        <el-table-column prop="probTitle" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="probType" label="类型" width="100" />
        <el-table-column prop="probLevel" label="级别" width="80" />
        <el-table-column prop="probState" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.probState===2?'success':row.probState===3?'info':'warning'">{{ {1:'草稿',2:'已发布',3:'已关闭'}[row.probState]||'未知' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" width="100" />
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
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题"><el-input v-model="formData.probTitle" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="formData.probType" /></el-form-item>
        <el-form-item label="级别"><el-input v-model="formData.probLevel" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="formData.probContent" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProbList, addProb, updateProb, deleteProb } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false)
const queryForm = reactive({ pageNum: 1, pageSize: 10, probTitle: '', probState: null })
const formData = reactive({ id: null, probTitle: '', probType: null, probLevel: null, probContent: '' })
const fetchData = async () => { loading.value = true; try { const r = await getProbList(queryForm); tableData.value = r.data.records; total.value = r.data.total } finally { loading.value = false } }
const resetQuery = () => { Object.assign(queryForm, { probTitle: '', probState: null }); fetchData() }
const handleAdd = () => { Object.assign(formData, { id: null, probTitle: '', probType: null, probLevel: null, probContent: '' }); dialogVisible.value = true }
const handleEdit = row => { Object.assign(formData, row); dialogVisible.value = true }
const handleSubmit = async () => { if (formData.id) { await updateProb(formData) } else { await addProb(formData) }; ElMessage.success('保存成功'); dialogVisible.value = false; fetchData() }
const handleDelete = row => { ElMessageBox.confirm('确认删除？').then(async () => { await deleteProb(row.id); ElMessage.success('删除成功'); fetchData() }).catch(()=>{}) }
onMounted(fetchData)
</script>
