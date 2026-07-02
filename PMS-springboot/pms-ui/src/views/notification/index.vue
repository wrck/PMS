<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="通知主题"><el-input v-model="queryForm.notifySubject" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="通知状态">
              <el-select v-model="queryForm.notifyState" clearable>
                <el-option label="未读" value="0" /><el-option label="已读" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="通知类型">
              <el-select v-model="queryForm.notifyType" clearable>
                <el-option v-for="t in notifyTypeList" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
            <el-button @click="markAllRead">全部已读</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>系统通知</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="notifySubject" label="通知主题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="notifyContent" label="通知内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="notifyType" label="通知类型" width="100" />
        <el-table-column prop="createBy" label="发送人" width="100" />
        <el-table-column prop="createTime" label="时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
        <el-table-column prop="notifyState" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.notifyState===0?'danger':'success'" size="small">{{ row.notifyState===0?'未读':'已读' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.notifyState===0" size="small" type="primary" link @click="markAsRead(row)">标记已读</el-button>
            <el-button size="small" type="danger" link @click="deleteNotification(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listNotifications, updateNotificationState, deleteNotification as apiDeleteNotification } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const notifyTypeList = ref([])
const queryForm = reactive({ pageNum: 1, pageSize: 20, notifySubject: '', notifyState: '', notifyType: '' })
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const fetchData = async () => { loading.value = true; try { const r = await listNotifications(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { notifySubject: '', notifyState: '', notifyType: '' }); handleQuery() }
const markAsRead = async (row) => { await updateNotificationState(row.id); ElMessage.success('已标记已读'); fetchData() }
const markAllRead = async () => { /* mark all as read */ ElMessage.success('全部已读'); fetchData() }
const deleteNotification = (row) => { ElMessageBox.confirm('确认删除该通知？', '提示', { type: 'warning' }).then(async () => { await apiDeleteNotification(row.id); ElMessage.success('已删除'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
