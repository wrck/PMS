<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="条码"><el-input v-model="queryForm.barcode" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button @click="queryForm.barcode='';fetchData()">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>合格证列表</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="barcode" label="条码" width="180" />
        <el-table-column prop="oqcNo" label="OQC编号" width="150" />
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column prop="sealName" label="印章名称" width="150" />
        <el-table-column prop="sealCode" label="印章编码" width="150" />
        <el-table-column prop="createBy" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCertificateList } from '@/api'
const loading = ref(false); const tableData = ref([]); const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 10, barcode: '' })
const fetchData = async () => { loading.value = true; try { const r = await getCertificateList(queryForm); tableData.value = r.data.records; total.value = r.data.total } finally { loading.value = false } }
onMounted(fetchData)
</script>
