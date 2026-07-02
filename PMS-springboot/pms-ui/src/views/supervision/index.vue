<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="项目编码"><el-input v-model="queryForm.projectCode" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="督查人"><el-input v-model="queryForm.createUser" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="办事处">
              <el-select v-model="queryForm.officeCode" clearable filterable>
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item>
              <el-date-picker v-model="queryForm.processStartTime" type="date" placeholder="开始时间" style="width:45%" value-format="YYYY-MM-DD" />
              <span style="margin:0 2%">-</span>
              <el-date-picker v-model="queryForm.processEndTime" type="date" placeholder="结束时间" style="width:45%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="18" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>项目督查</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip>
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/project/detail/${row.projectId}`)">{{ row.projectName }}</el-link></template>
        </el-table-column>
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="processTime" label="督查时间" width="120"><template #default="{ row }">{{ formatDate(row.processTime) }}</template></el-table-column>
        <el-table-column prop="createUser" label="督查人" width="100" />
        <el-table-column prop="supervisionContent" label="督查内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'warning'" size="small">{{ row.status===1?'已整改':'待整改' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/supervision/detail/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listSupervisions } from '@/api/supervision'
import { listDepts } from '@/api/system'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const deptList = ref([])
const queryForm = reactive({ pageNum: 1, pageSize: 20, projectName: '', projectCode: '', createUser: '', officeCode: '', processStartTime: '', processEndTime: '' })
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const fetchData = async () => { loading.value = true; try { const r = await listSupervisions(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); handleQuery() }
onMounted(async () => { fetchData(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
