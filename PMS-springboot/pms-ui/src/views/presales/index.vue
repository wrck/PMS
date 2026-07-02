<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="售前编码"><el-input v-model="queryForm.presalesCode" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="项目名称"><el-input v-model="queryForm.presalesName" clearable placeholder="支持模糊搜索" /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="办事处">
              <el-select v-model="queryForm.officeCode" clearable filterable>
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="时间类型">
              <el-select v-model="queryForm.searchTimeType" style="width:100%">
                <el-option label="申请时间" value="applyTime" /><el-option label="结束时间" value="endTime" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item>
              <el-date-picker v-model="queryForm.searchStartTime" type="date" placeholder="开始" style="width:45%" value-format="YYYY-MM-DD" />
              <span style="margin:0 2%">-</span>
              <el-date-picker v-model="queryForm.searchEndTime" type="date" placeholder="结束" style="width:45%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="6"><el-form-item label="项目经理"><el-autocomplete v-model="queryForm.programManagerName" :fetch-suggestions="queryPMUser" clearable placeholder="支持模糊搜索" /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="售前状态">
              <el-select v-model="queryForm.presalesState" clearable>
                <el-option label="草稿" value="0" /><el-option label="审批中" value="1" /><el-option label="已通过" value="2" /><el-option label="已驳回" value="3" /><el-option label="已关闭" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="是否有转销">
              <el-select v-model="queryForm.hasTransfer" clearable>
                <el-option label="是" value="1" /><el-option label="否" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="是否有核销">
              <el-select v-model="queryForm.hasRma" clearable>
                <el-option label="是" value="1" /><el-option label="否" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="售前类型">
              <el-select v-model="queryForm.presalesType" clearable>
                <el-option v-for="t in presalesTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>售前项目列表</span>
          <div>
            <el-button type="primary" @click="$router.push('/presales/apply')">发起售前流程</el-button>
            <el-dropdown @command="handleExport" style="margin-left:8px">
              <el-button>导出 <el-icon><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="normal">基于项目</el-dropdown-item>
                  <el-dropdown-item command="detail">基于设备明细</el-dropdown-item>
                  <el-dropdown-item command="callback">基于回访</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" style="margin-left:8px" :disabled="!selectedRows.length" @click="handleTerminateBatch">终止并关闭</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="presalesCode" label="售前编码" width="140" fixed />
        <el-table-column prop="presalesName" label="项目名称" min-width="200" show-overflow-tooltip>
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/presales/detail/${row.id}`)">{{ row.presalesName }}</el-link></template>
        </el-table-column>
        <el-table-column prop="presalesType" label="售前类型" width="100" />
        <el-table-column prop="officeName" label="办事处" width="100" />
        <el-table-column prop="programManagerName" label="项目经理" width="100" />
        <el-table-column prop="presalesState" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStateType(row.presalesState)" size="small">{{ getStateText(row.presalesState) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170"><template #default="{ row }">{{ formatDate(row.applyTime) }}</template></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="170"><template #default="{ row }">{{ formatDate(row.endTime) }}</template></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/presales/detail/${row.id}`)">详情</el-button>
            <el-button v-if="row.presalesState === 1" size="small" type="success" link @click="$router.push(`/presales/audit/${row.id}`)">办理</el-button>
            <el-button v-if="row.presalesState === 0 || row.presalesState === 3" size="small" link @click="$router.push(`/presales/apply?id=${row.id}`)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { listPresales, exportPresales, terminatePresales } from '@/api/presales'
import { listDepts } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const deptList = ref([])
const presalesTypeList = ref([])
const queryForm = reactive({
  pageNum: 1, pageSize: 20,
  presalesCode: '', presalesName: '', officeCode: '',
  searchTimeType: 'applyTime', searchStartTime: '', searchEndTime: '',
  programManagerName: '', presalesState: '', hasTransfer: '', hasRma: '', presalesType: ''
})
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const getStateType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }[s] || 'info')
const getStateText = (s) => ({ 0: '草稿', 1: '审批中', 2: '已通过', 3: '已驳回', 4: '已关闭' }[s] || '未知')
const queryPMUser = (q, cb) => { cb([]) }
const fetchData = async () => { loading.value = true; try { const r = await listPresales(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); queryForm.searchTimeType = 'applyTime'; handleQuery() }
const handleSelectionChange = (rows) => { selectedRows.value = rows }
const handleExport = async (type) => {
  try {
    const res = await exportPresales({ ...queryForm, exportDetail: type })
    const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = `售前项目_${type}.xlsx`; a.click(); URL.revokeObjectURL(url)
  } catch (e) { ElMessage.error('导出失败') }
}
const handleTerminateBatch = () => {
  ElMessageBox.confirm(`确认终止并关闭选中的 ${selectedRows.value.length} 个售前项目？`, '提示', { type: 'warning' }).then(async () => {
    await terminatePresales(selectedRows.value.map(r => r.id))
    ElMessage.success('操作成功'); fetchData()
  }).catch(() => {})
}
onMounted(async () => { fetchData(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
