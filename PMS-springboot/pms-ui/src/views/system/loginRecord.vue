<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="queryForm.username" clearable /></el-form-item>
        <el-form-item label="登录IP"><el-input v-model="queryForm.loginIp" clearable /></el-form-item>
        <el-form-item label="登录状态">
          <el-select v-model="queryForm.loginStatus" clearable>
            <el-option label="成功" value="1" /><el-option label="失败" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryForm.startTime" type="datetime" placeholder="开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:180px" />
          <span style="margin:0 4px">-</span>
          <el-date-picker v-model="queryForm.endTime" type="datetime" placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>登录记录</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="loginIp" label="登录IP" width="140" />
        <el-table-column prop="loginLocation" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="150" />
        <el-table-column prop="loginStatus" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.loginStatus===1?'success':'danger'" size="small">{{ row.loginStatus===1?'成功':'失败' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="170" />
        <el-table-column prop="msg" label="消息" min-width="200" show-overflow-tooltip />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listLoginRecords } from '@/api/system'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, username: '', loginIp: '', loginStatus: '', startTime: '', endTime: '' })
const fetchData = async () => { loading.value = true; try { const r = await listLoginRecords(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); handleQuery() }
onMounted(fetchData)
</script>
