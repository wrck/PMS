<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="字典名称"><el-input v-model="queryForm.dictName" clearable /></el-form-item>
        <el-form-item label="字典编码"><el-input v-model="queryForm.dictCode" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>字典管理</span>
          <el-button type="primary" @click="showDialog()">新增字典</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="dictName" label="字典名称" width="200" />
        <el-table-column prop="dictCode" label="字典编码" width="200" />
        <el-table-column prop="dictValue" label="字典值" width="150" />
        <el-table-column prop="dictSort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑字典' : '新增字典'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="字典名称" prop="dictName"><el-input v-model="form.dictName" /></el-form-item>
        <el-form-item label="字典编码" prop="dictCode"><el-input v-model="form.dictCode" /></el-form-item>
        <el-form-item label="字典值" prop="dictValue"><el-input v-model="form.dictValue" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.dictSort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
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
import { listDict, addDict, updateDict, deleteDict } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const queryForm = reactive({ pageNum: 1, pageSize: 20, dictName: '', dictCode: '' })
const form = reactive({ id: null, dictName: '', dictCode: '', dictValue: '', dictSort: 0, status: 1, remark: '' })
const rules = { dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }], dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }] }
const fetchData = async () => { loading.value = true; try { const r = await listDict(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { dictName: '', dictCode: '' }); handleQuery() }
const showDialog = (row) => { if (row) Object.assign(form, row); else Object.assign(form, { id: null, dictName: '', dictCode: '', dictValue: '', dictSort: 0, status: 1, remark: '' }); dialogVisible.value = true }
const handleSave = async () => { await formRef.value.validate(); if (form.id) await updateDict(form); else await addDict(form); ElMessage.success('保存成功'); dialogVisible.value = false; fetchData() }
const handleDelete = (row) => { ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' }).then(async () => { await deleteDict(row.id); ElMessage.success('已删除'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
