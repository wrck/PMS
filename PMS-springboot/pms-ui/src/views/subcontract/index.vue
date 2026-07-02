<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="转包编码"><el-input v-model="queryForm.subcontractCode" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="合同号"><el-input v-model="queryForm.contractNo" clearable placeholder="合同号/转包合同号" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="采购订单号"><el-input v-model="queryForm.purchaseOrderNo" clearable /></el-form-item></el-col>
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
            <el-form-item label="时间类型">
              <el-select v-model="queryForm.searchTimeType" style="width:100%">
                <el-option label="申请时间" value="createTime" /><el-option label="主任审批通过时间" value="zrApproverTime" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-date-picker v-model="queryForm.searchStartTime" type="date" placeholder="开始" style="width:45%" value-format="YYYY-MM-DD" />
              <span style="margin:0 2%">-</span>
              <el-date-picker v-model="queryForm.searchEndTime" type="date" placeholder="结束" style="width:45%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="6"><el-form-item label="服务商"><el-input v-model="queryForm.facilitatorName" clearable placeholder="支持模糊查询" /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="转包状态">
              <el-select v-model="queryForm.subcontractState" clearable>
                <el-option v-for="s in subcontractStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="利润部门">
              <el-select v-model="queryForm.profitDepCode" clearable filterable>
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="转包类型">
              <el-select v-model="queryForm.subcontractType" clearable>
                <el-option v-for="t in subcontractTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款状态">
              <el-select v-model="queryForm.paymentStatus" clearable>
                <el-option v-for="s in paymentStatusList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="已付款">
              <el-select v-model="queryForm.paid" clearable>
                <el-option label="待付款" value="0" /><el-option label="已付款" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="公司">
              <el-select v-model="queryForm.orgId" clearable filterable>
                <el-option v-for="c in companyList" :key="c.id" :label="c.abbr" :value="c.id" />
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
          <span>转包项目列表</span>
          <div>
            <el-button type="primary" @click="$router.push('/subcontract/apply')">申请转包</el-button>
            <el-button @click="handleExport">导出Excel</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="subcontractNo" label="转包编号" width="140" fixed />
        <el-table-column prop="subcontractName" label="转包名称" min-width="220" show-overflow-tooltip>
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/subcontract/detail/${row.id}`)">{{ row.subcontractName }}</el-link></template>
        </el-table-column>
        <el-table-column prop="contractNo" label="合同号" width="140" />
        <el-table-column prop="facilitatorName" label="服务商" width="120" />
        <el-table-column prop="subcontractType" label="转包类型" width="100" />
        <el-table-column prop="subcontractStateName" label="状态" width="100">
          <template #default="{ row }"><el-tag size="small" :type="row.subcontractState === 1 ? 'success' : 'info'">{{ row.subcontractStateName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
        <el-table-column prop="zrApproverTime" label="主任审批时间" width="170"><template #default="{ row }">{{ formatDate(row.zrApproverTime) }}</template></el-table-column>
        <el-table-column prop="confirmTime" label="确认时间" width="170"><template #default="{ row }">{{ formatDate(row.confirmTime) }}</template></el-table-column>
        <el-table-column prop="paymentTime" label="付款时间" width="170"><template #default="{ row }">{{ formatDate(row.paymentTime) }}</template></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/subcontract/detail/${row.id}`)">详情</el-button>
            <el-button v-if="row.subcontractState === 0" size="small" type="success" link @click="$router.push(`/subcontract/audit/${row.id}`)">办理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listSubcontracts, exportSubcontracts } from '@/api/subcontract'
import { listDepts } from '@/api/system'
import { ElMessage } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const deptList = ref([])
const subcontractStateList = ref([])
const subcontractTypeList = ref([])
const paymentStatusList = ref([])
const companyList = ref([])
const queryForm = reactive({
  pageNum: 1, pageSize: 20,
  subcontractCode: '', contractNo: '', purchaseOrderNo: '', officeCode: '',
  searchTimeType: 'createTime', searchStartTime: '', searchEndTime: '',
  facilitatorName: '', subcontractState: '', profitDepCode: '',
  subcontractType: '', paymentStatus: '', paid: '', orgId: ''
})
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const fetchData = async () => { loading.value = true; try { const r = await listSubcontracts(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); queryForm.searchTimeType = 'createTime'; handleQuery() }
const handleExport = async () => { try { const res = await exportSubcontracts(queryForm); const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = '转包项目.xlsx'; a.click(); URL.revokeObjectURL(url) } catch (e) { ElMessage.error('导出失败') } }
onMounted(async () => { fetchData(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
