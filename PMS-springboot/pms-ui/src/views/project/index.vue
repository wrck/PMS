<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="90px">
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable placeholder="支持模糊搜索" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="项目编码/合同号"><el-input v-model="queryForm.projectCode" clearable /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="办事处">
              <el-select v-model="queryForm.officeCode" clearable filterable placeholder="--请选择--">
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="销售代表">
              <el-autocomplete v-model="queryForm.salesManName" :fetch-suggestions="querySalesUser" placeholder="支持模糊搜索" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="项目状态">
              <el-select v-model="queryForm.projectState" clearable>
                <el-option v-for="s in projectStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="实施状态">
              <el-select v-model="queryForm.executionState" clearable>
                <el-option v-for="s in executionStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="闭环流程状态">
              <el-select v-model="queryForm.closeProcessState" clearable>
                <el-option v-for="s in closeProcessStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="当前任务">
              <el-select v-model="queryForm.projectPlanState" clearable>
                <el-option v-for="s in planStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="发货状态">
              <el-select v-model="queryForm.shipmentState" clearable>
                <el-option v-for="s in shipmentStateList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="市场部">
              <div style="display:flex;gap:2px">
                <el-select v-model="queryForm.column004" clearable style="width:25%" @change="onMarketChange">
                  <el-option v-for="m in marketList" :key="m" :label="m" :value="m" />
                </el-select>
                <el-select v-model="queryForm.column005" clearable style="width:25%">
                  <el-option v-for="s in systemList" :key="s" :label="s" :value="s" />
                </el-select>
                <el-select v-model="queryForm.column006" clearable style="width:25%">
                  <el-option v-for="e in expandList" :key="e" :label="e" :value="e" />
                </el-select>
                <el-select v-model="queryForm.column007" clearable style="width:25%">
                  <el-option v-for="i in industryList" :key="i" :label="i" :value="i" />
                </el-select>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="项目分类">
              <el-select v-model="queryForm.column011" clearable>
                <el-option label="直签" value="10" /><el-option label="非直签" value="20" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="项目类型">
              <el-select v-model="queryForm.column010" clearable>
                <el-option label="普通类" value="10" /><el-option label="工程类" value="20" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="重大项目级别">
              <el-select v-model="queryForm.majorProjectLevel" clearable>
                <el-option v-for="l in majorLevelList" :key="l" :label="l" :value="l" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="服务经理">
              <el-autocomplete v-model="queryForm.serviceManagerCodeforjson" :fetch-suggestions="querySMUser" placeholder="支持模糊搜索" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="项目经理">
              <el-autocomplete v-model="queryForm.programManagerCodeforjson" :fetch-suggestions="queryPMUser" placeholder="支持模糊搜索" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="时间类型">
              <el-select v-model="queryForm.projectTimeType" style="width:100%">
                <el-option label="创建时间" value="10" /><el-option label="刷新时间" value="20" /><el-option label="关闭时间" value="30" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-date-picker v-model="queryForm.startTime" type="date" placeholder="开始时间" style="width:45%" value-format="YYYY-MM-DD" />
              <span style="margin:0 2%">-</span>
              <el-date-picker v-model="queryForm.endTime" type="date" placeholder="结束时间" style="width:45%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="实施方式">
              <el-select v-model="queryForm.column012" clearable>
                <el-option v-for="s in ssfsList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="设备型号"><el-input v-model="queryForm.itemModel" clearable /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="公司">
              <el-select v-model="queryForm.compId" clearable filterable>
                <el-option v-for="c in companyList" :key="c.id" :label="c.abbr" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="合作伙伴渠道"><el-input v-model="queryForm.partnerChannel" clearable /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="序列号"><el-input v-model="queryForm.barCode" clearable /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="维保状态">
              <el-select v-model="queryForm.warrantyStatus" clearable>
                <el-option v-for="s in warrantyStatusList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="维保等级">
              <el-select v-model="queryForm.warrantyGrade" clearable>
                <el-option v-for="s in warrantyGradeList" :key="s.basicDataId" :label="s.basicDataName" :value="s.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="WAF服务">
              <el-select v-model="queryForm.wafService" clearable>
                <el-option label="是" value="1" /><el-option label="否" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" style="text-align:center">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>项目列表</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增项目</el-button>
            <el-button @click="handleExport">导出Excel</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%" @selection-change="handleSelectionChange" :max-height="600">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="projectCode" label="项目编码" width="130" fixed />
        <el-table-column prop="projectName" label="项目名称" min-width="220" show-overflow-tooltip>
          <template #default="{ row }"><el-link type="primary" @click="handleDetail(row)">{{ row.projectName }}</el-link></template>
        </el-table-column>
        <el-table-column prop="contractNo" label="合同号" width="140" show-overflow-tooltip />
        <el-table-column prop="compName" label="公司" width="80" />
        <el-table-column prop="officeName" label="办事处" width="100" />
        <el-table-column prop="projectStateName" label="项目状态" width="100">
          <template #default="{ row }"><el-tag size="small" :type="getStateType(row.projectState)">{{ row.projectStateName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="ssfsName" label="实施方式" width="80" />
        <el-table-column prop="projectTypeName" label="项目类型" width="80" />
        <el-table-column prop="majorProjectLevel" label="重大项目级别" width="100" />
        <el-table-column prop="salesManName" label="销售代表" width="80" />
        <el-table-column prop="serviceManagerName" label="服务经理" width="80" />
        <el-table-column prop="projectManagerName" label="项目经理" width="80" />
        <el-table-column prop="projectStartTime" label="订单创建时间" width="110"><template #default="{ row }">{{ formatDate(row.projectStartTime) }}</template></el-table-column>
        <el-table-column prop="projectRefreshTime" label="刷新时间" width="110"><template #default="{ row }">{{ formatDate(row.projectRefreshTime) }}</template></el-table-column>
        <el-table-column prop="executionStateName" label="实施状态" width="100" />
        <el-table-column prop="closeProcessStateName" label="闭环流程状态" width="100" />
        <el-table-column prop="shipmentStateName" label="发货状态" width="80" />
        <el-table-column prop="agentChannel" label="代理商渠道" width="100" show-overflow-tooltip />
        <el-table-column prop="serviceChannel" label="服务提供商" width="100" show-overflow-tooltip />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :page-sizes="[20,50,100,200]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listProjects, exportSpotCheck } from '@/api/project'
import { listDepts } from '@/api/system'
import { ElMessage } from 'element-plus'
const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const deptList = ref([])
const projectStateList = ref([])
const executionStateList = ref([])
const closeProcessStateList = ref([])
const planStateList = ref([])
const shipmentStateList = ref([])
const ssfsList = ref([])
const majorLevelList = ref([])
const companyList = ref([])
const marketList = ref([])
const systemList = ref([])
const expandList = ref([])
const industryList = ref([])
const warrantyStatusList = ref([])
const warrantyGradeList = ref([])
const allUsers = ref([])
const allSMUsers = ref([])
const allPMUsers = ref([])
const queryForm = reactive({
  pageNum: 1, pageSize: 20,
  projectName: '', projectCode: '', contractNo: '',
  officeCode: '', salesManName: '', salesManCode: '',
  projectState: '', executionState: '', closeProcessState: '',
  projectPlanState: '', shipmentState: '',
  column004: '', column005: '', column006: '', column007: '',
  column011: '', column010: '', majorProjectLevel: '',
  serviceManagerCodeforjson: '', serviceManagerCode: '',
  programManagerCodeforjson: '', programManagerCode: '',
  projectTimeType: '10', startTime: '', endTime: '',
  column012: '', itemModel: '', compId: '', partnerChannel: '',
  barCode: '', warrantyStatus: '', warrantyGrade: '', wafService: ''
})
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const getStateType = (s) => { if (s == 20) return 'danger'; if (s == 100) return 'success'; if (s >= 30 && s <= 50) return 'warning'; return 'info' }
const querySalesUser = (q, cb) => { cb(q ? allUsers.value.filter(u => u.value.includes(q)) : allUsers.value) }
const querySMUser = (q, cb) => { cb(q ? allSMUsers.value.filter(u => u.value.includes(q)) : allSMUsers.value) }
const queryPMUser = (q, cb) => { cb(q ? allPMUsers.value.filter(u => u.value.includes(q)) : allPMUsers.value) }
const onMarketChange = () => {}
const fetchData = async () => { loading.value = true; try { const res = await listProjects(queryForm); tableData.value = res.data?.records || []; total.value = res.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); queryForm.projectTimeType = '10'; handleQuery() }
const handleSelectionChange = (rows) => { selectedRows.value = rows }
const handleAdd = () => { router.push('/project/detail/new') }
const handleDetail = (row) => { router.push(`/project/detail/${row.id}`) }
const handleExport = async () => { try { const res = await exportSpotCheck(queryForm); const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = '项目清单.xlsx'; a.click(); URL.revokeObjectURL(url) } catch (e) { ElMessage.error('导出失败') } }
onMounted(async () => { fetchData(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
