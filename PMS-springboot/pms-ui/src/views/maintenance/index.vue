<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="维护类型">
              <el-select v-model="queryForm.maintenanceType" clearable>
                <el-option v-for="t in maintenanceTypeList" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="维护分类">
              <el-select v-model="queryForm.maintenanceCategory" clearable @change="onCategoryChange">
                <el-option v-for="c in categoryList" :key="c.category" :label="c.categoryName" :value="c.category" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="子分类">
              <el-select v-model="queryForm.maintenanceSubCategory" clearable>
                <el-option v-for="s in subCategoryList" :key="s.subCategory" :label="s.subCategoryName" :value="s.subCategory" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="项目编码"><el-input v-model="queryForm.projectCode" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="办事处">
              <el-select v-model="queryForm.officeCode" clearable filterable>
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="维护人"><el-autocomplete v-model="queryForm.maintenancePerson" :fetch-suggestions="queryUser" clearable /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-date-picker v-model="queryForm.startTime" type="date" placeholder="开始" style="width:45%" value-format="YYYY-MM-DD" />
              <span style="margin:0 2%">-</span>
              <el-date-picker v-model="queryForm.endTime" type="date" placeholder="结束" style="width:45%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>维保管理</span>
          <div>
            <el-button type="primary" @click="$router.push('/maintenance/detail/new')">新建维保</el-button>
            <el-button @click="$router.push('/maintenance/daily')">维保日报</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="maintenanceType" label="维护类型" width="100" />
        <el-table-column prop="maintenanceCategoryName" label="维护分类" width="120" />
        <el-table-column prop="maintenanceContent" label="维护内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="maintenancePerson" label="维护人" width="100" />
        <el-table-column prop="maintenanceTime" label="维护时间" width="170"><template #default="{ row }">{{ formatDate(row.maintenanceTime) }}</template></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/maintenance/detail/${row.id}`)">详情</el-button>
            <el-button size="small" link @click="$router.push(`/maintenance/detail/${row.id}?edit=true`)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMaintenance } from '@/api/maintenance'
import { listDepts } from '@/api/system'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const deptList = ref([])
const maintenanceTypeList = ref([])
const categoryList = ref([])
const subCategoryList = ref([])
const queryForm = reactive({
  pageNum: 1, pageSize: 20,
  projectName: '', projectCode: '', maintenanceType: '',
  maintenanceCategory: '', maintenanceSubCategory: '',
  officeCode: '', maintenancePerson: '', startTime: '', endTime: ''
})
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const queryUser = (q, cb) => { cb([]) }
const onCategoryChange = () => { queryForm.maintenanceSubCategory = ''; /* load subcategories */ }
const fetchData = async () => { loading.value = true; try { const r = await listMaintenance(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); handleQuery() }
onMounted(async () => { fetchData(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
