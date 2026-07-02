<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>售前申请</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="售前编码"><el-input v-model="form.presalesCode" disabled /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="项目名称" prop="presalesName"><el-input v-model="form.presalesName" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="售前类型" prop="presalesType">
              <el-select v-model="form.presalesType" style="width:100%">
                <el-option v-for="t in presalesTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="办事处" prop="officeCode">
              <el-select v-model="form.officeCode" filterable style="width:100%">
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8"><el-form-item label="项目经理"><el-autocomplete v-model="form.programManagerName" :fetch-suggestions="queryPMUser" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="客户名称"><el-input v-model="form.customerName" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="合同号"><el-input v-model="form.contractNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="产品信息"><el-input v-model="form.productInfo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="预计金额"><el-input-number v-model="form.estimatedAmount" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24"><el-form-item label="申请说明"><el-input v-model="form.applyReason" type="textarea" :rows="4" /></el-form-item></el-col>
        </el-row>
        <!-- 产品信息表格 -->
        <el-divider content-position="left">产品明细</el-form-item></el-divider>
        <el-table :data="form.products" stripe size="small" style="margin-bottom:12px">
          <el-table-column prop="productCode" label="产品编码" width="150">
            <template #default="{ row }"><el-input v-model="row.productCode" size="small" /></template>
          </el-table-column>
          <el-table-column prop="productName" label="产品名称" min-width="200">
            <template #default="{ row }"><el-input v-model="row.productName" size="small" /></template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{ row }"><el-input-number v-model="row.quantity" size="small" :min="1" /></template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }"><el-input-number v-model="row.price" size="small" :precision="2" /></template>
          </el-table-column>
          <el-table-column width="80">
            <template #default="{ $index }"><el-button size="small" type="danger" :icon="Delete" circle @click="form.products.splice($index, 1)" /></template>
          </el-table-column>
        </el-table>
        <el-button size="small" @click="form.products.push({ productCode: '', productName: '', quantity: 1, price: 0 })">+ 添加产品</el-button>
        <el-divider />
        <el-form-item label="附件">
          <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleUpload" :file-list="form.attachments" multiple>
            <el-button size="small">上传附件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交申请</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { getPresales, applyPresales, savePresalesDraft } from '@/api/presales'
import { listDepts } from '@/api/system'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.query.id
const loading = ref(false)
const formRef = ref(null)
const deptList = ref([])
const presalesTypeList = ref([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const form = reactive({
  id: null, presalesCode: '', presalesName: '', presalesType: '', officeCode: '',
  programManagerName: '', customerName: '', contractNo: '', productInfo: '',
  estimatedAmount: 0, applyReason: '',
  products: [{ productCode: '', productName: '', quantity: 1, price: 0 }],
  attachments: []
})
const rules = {
  presalesName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  presalesType: [{ required: true, message: '请选择售前类型', trigger: 'change' }],
  officeCode: [{ required: true, message: '请选择办事处', trigger: 'change' }]
}
const queryPMUser = (q, cb) => { cb([]) }
onMounted(async () => {
  try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {}
  if (id) { loading.value = true; try { const r = await getPresales(id); Object.assign(form, r.data || {}) } finally { loading.value = false } }
})
const handleUpload = (res) => { if (res.code === 200) form.attachments.push(res.data) }
const handleSubmit = async () => { await formRef.value.validate(); await applyPresales(form); ElMessage.success('申请已提交'); router.push('/presales') }
const handleSaveDraft = async () => { await savePresalesDraft(form); ElMessage.success('已保存草稿') }
</script>
