<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="queryForm.module" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realname" label="姓名" width="120" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operation" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" width="170" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end"
        v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
        :page-sizes="[10,20,50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperateLogList } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 10, username: '', module: '' })

const fetchData = async () => {
  loading.value = true
  try { const res = await getOperateLogList(queryForm); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
const resetQuery = () => { Object.assign(queryForm, { username: '', module: '' }); fetchData() }
onMounted(fetchData)
</script>
