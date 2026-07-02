<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="公告编号"><el-input v-model="queryForm.probNum" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="主题"><el-input v-model="queryForm.theme" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="公告类型">
              <el-select v-model="queryForm.probType" clearable>
                <el-option v-for="t in probTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="公告状态">
              <el-select v-model="queryForm.probState" clearable>
                <el-option v-for="s in probStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="跟踪人"><el-autocomplete v-model="queryForm.trackingUserName" :fetch-suggestions="queryUser" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="产品类型"><el-input v-model="queryForm.productType" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="描述"><el-input v-model="queryForm.desc" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="影响类型">
              <el-select v-model="queryForm.affectedType" clearable>
                <el-option label="盒式系列" value="1" /><el-option label="框式系列" value="2" /><el-option label="其它系列" value="-1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="产品">
              <el-select v-model="queryForm.probProducts" multiple clearable filterable placeholder="可多选">
                <el-option v-for="p in productList" :key="p" :label="p" :value="p" />
              </el-select>
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
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>技术公告列表</span>
          <div>
            <el-button type="primary" @click="$router.push('/prob/apply')">新建公告</el-button>
            <el-button @click="handleExport">导出Excel</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="probNum" label="公告编号" width="140" fixed />
        <el-table-column prop="theme" label="主题" min-width="220" show-overflow-tooltip>
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/prob/detail/${row.id}`)">{{ row.theme }}</el-link></template>
        </el-table-column>
        <el-table-column prop="probTypeName" label="类型" width="100" />
        <el-table-column prop="probStateName" label="状态" width="100">
          <template #default="{ row }"><el-tag size="small" :type="row.probState === 1 ? 'success' : 'info'">{{ row.probStateName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="trackingUserName" label="跟踪人" width="100" />
        <el-table-column prop="productType" label="产品类型" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/prob/detail/${row.id}`)">详情</el-button>
            <el-button size="small" link @click="$router.push(`/prob/task?probId=${row.id}`)">任务</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listProbs, exportProbs } from '@/api/prob'
import { ElMessage } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const probTypeList = ref([])
const probStateList = ref([])
const productList = ref([])
const queryForm = reactive({
  pageNum: 1, pageSize: 20,
  probNum: '', theme: '', probType: '', probState: '',
  trackingUserName: '', productType: '', desc: '', affectedType: '', probProducts: []
})
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const queryUser = (q, cb) => { cb([]) }
const fetchData = async () => { loading.value = true; try { const r = await listProbs(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = Array.isArray(queryForm[k]) ? [] : '' }); handleQuery() }
const handleExport = async () => { try { const res = await exportProbs(queryForm); const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = '技术公告.xlsx'; a.click(); URL.revokeObjectURL(url) } catch (e) { ElMessage.error('导出失败') } }
onMounted(fetchData)
</script>
