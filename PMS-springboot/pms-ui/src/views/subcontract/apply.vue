<template>
  <div>
    <el-card>
      <template #header><span>申请转包</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="转包编码"><el-input v-model="form.subcontractCode" disabled /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="转包名称" prop="subcontractName"><el-input v-model="form.subcontractName" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="转包类型" prop="subcontractType">
              <el-select v-model="form.subcontractType" style="width:100%">
                <el-option v-for="t in subcontractTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="合同号" prop="contractNo"><el-input v-model="form.contractNo" @change="onContractChange" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="服务商" prop="facilitatorId">
              <el-select v-model="form.facilitatorId" filterable style="width:100%">
                <el-option v-for="f in facilitatorList" :key="f.id" :label="f.facilitatorName" :value="f.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8"><el-form-item label="总金额"><el-input-number v-model="form.totalAmount" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>

        <!-- 转包行项目 -->
        <el-divider content-position="left">转包行项目</el-divider>
        <el-table :data="form.lines" stripe size="small" style="margin-bottom:12px">
          <el-table-column prop="itemCode" label="物料编码" width="150">
            <template #default="{ row }"><el-input v-model="row.itemCode" size="small" /></template>
          </el-table-column>
          <el-table-column prop="itemName" label="物料名称" min-width="200">
            <template #default="{ row }"><el-input v-model="row.itemName" size="small" /></template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{ row }"><el-input-number v-model="row.quantity" size="small" :min="1" /></template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }"><el-input-number v-model="row.price" size="small" :precision="2" /></template>
          </el-table-column>
          <el-table-column width="80">
            <template #default="{ $index }"><el-button size="small" type="danger" :icon="Delete" circle @click="form.lines.splice($index, 1)" /></template>
          </el-table-column>
        </el-table>
        <el-button size="small" @click="form.lines.push({ itemCode: '', itemName: '', quantity: 1, price: 0 })">+ 添加行项目</el-button>

        <!-- 付款计划 -->
        <el-divider content-position="left">付款计划</el-divider>
        <el-table :data="form.payments" stripe size="small" style="margin-bottom:12px">
          <el-table-column prop="paymentName" label="付款节点" width="200">
            <template #default="{ row }"><el-input v-model="row.paymentName" size="small" /></template>
          </el-table-column>
          <el-table-column prop="paymentAmount" label="金额" width="150">
            <template #default="{ row }"><el-input-number v-model="row.paymentAmount" size="small" :precision="2" /></template>
          </el-table-column>
          <el-table-column prop="paymentCondition" label="付款条件" min-width="200">
            <template #default="{ row }"><el-input v-model="row.paymentCondition" size="small" /></template>
          </el-table-column>
          <el-table-column width="80">
            <template #default="{ $index }"><el-button size="small" type="danger" :icon="Delete" circle @click="form.payments.splice($index, 1)" /></template>
          </el-table-column>
        </el-table>
        <el-button size="small" @click="form.payments.push({ paymentName: '', paymentAmount: 0, paymentCondition: '' })">+ 添加付款节点</el-button>

        <!-- 附件 -->
        <el-divider />
        <el-form-item label="附件">
          <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleUpload" :file-list="form.attachments" multiple>
            <el-button size="small">上传附件</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交申请</el-button>
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
import { createSubcontract, getSubcontract } from '@/api/subcontract'
import { getFacilitators } from '@/api/subcontract'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.query.id
const formRef = ref(null)
const subcontractTypeList = ref([])
const facilitatorList = ref([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const form = reactive({
  id: null, subcontractCode: '', subcontractName: '', subcontractType: '', contractNo: '',
  facilitatorId: '', totalAmount: 0, remark: '',
  lines: [{ itemCode: '', itemName: '', quantity: 1, price: 0 }],
  payments: [{ paymentName: '', paymentAmount: 0, paymentCondition: '' }],
  attachments: []
})
const rules = {
  subcontractName: [{ required: true, message: '请输入转包名称', trigger: 'blur' }],
  subcontractType: [{ required: true, message: '请选择转包类型', trigger: 'change' }],
  contractNo: [{ required: true, message: '请输入合同号', trigger: 'blur' }],
  facilitatorId: [{ required: true, message: '请选择服务商', trigger: 'change' }]
}
const onContractChange = () => { /* 根据合同号查询项目信息 */ }
const handleUpload = (res) => { if (res.code === 200) form.attachments.push(res.data) }
const handleSubmit = async () => { await formRef.value.validate(); await createSubcontract(form); ElMessage.success('申请已提交'); router.push('/subcontract') }
onMounted(async () => {
  try { const r = await getFacilitators(); facilitatorList.value = r.data || [] } catch (e) {}
  if (id) { const r = await getSubcontract(id); Object.assign(form, r.data || {}) }
})
</script>
