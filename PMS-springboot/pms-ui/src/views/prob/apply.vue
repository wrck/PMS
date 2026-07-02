<template>
  <div>
    <el-card>
      <template #header><span>新建技术公告</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="公告编号"><el-input v-model="form.probNum" disabled /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="公告类型" prop="probType">
              <el-select v-model="form.probType" style="width:100%">
                <el-option v-for="t in probTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="影响类型" prop="affectedType">
              <el-select v-model="form.affectedType" style="width:100%">
                <el-option label="盒式系列" value="1" /><el-option label="框式系列" value="2" /><el-option label="其它系列" value="-1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="16"><el-form-item label="主题" prop="theme"><el-input v-model="form.theme" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="跟踪人"><el-autocomplete v-model="form.trackingUserName" :fetch-suggestions="queryUser" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="产品类型"><el-input v-model="form.productType" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="产品">
              <el-select v-model="form.probProducts" multiple filterable style="width:100%">
                <el-option v-for="p in productList" :key="p" :label="p" :value="p" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8"><el-form-item label="生效日期"><el-date-picker v-model="form.effectDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item label="解决方案">
          <el-input v-model="form.solution" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="影响范围">
          <el-input v-model="form.impactScope" type="textarea" :rows="3" />
        </el-form-item>
        <!-- 软件版本 -->
        <el-divider content-position="left">受影响软件版本</el-divider>
        <el-table :data="form.softVersions" stripe size="small" style="margin-bottom:12px">
          <el-table-column prop="itemCode" label="产品编码" width="150">
            <template #default="{ row }"><el-input v-model="row.itemCode" size="small" /></template>
          </el-table-column>
          <el-table-column prop="oldVersion" label="旧版本" width="150">
            <template #default="{ row }"><el-input v-model="row.oldVersion" size="small" /></template>
          </el-table-column>
          <el-table-column prop="newVersion" label="新版本" width="150">
            <template #default="{ row }"><el-input v-model="row.newVersion" size="small" /></template>
          </el-table-column>
          <el-table-column width="80">
            <template #default="{ $index }"><el-button size="small" type="danger" :icon="Delete" circle @click="form.softVersions.splice($index, 1)" /></template>
          </el-table-column>
        </el-table>
        <el-button size="small" @click="form.softVersions.push({ itemCode: '', oldVersion: '', newVersion: '' })">+ 添加版本</el-button>
        <el-divider />
        <el-form-item label="附件">
          <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleUpload" :file-list="form.attachments" multiple>
            <el-button size="small">上传附件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { createProb } from '@/api/prob'
import { ElMessage } from 'element-plus'
const router = useRouter()
const formRef = ref(null)
const probTypeList = ref([])
const productList = ref([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const form = reactive({
  probNum: '', probType: '', affectedType: '', theme: '', trackingUserName: '',
  productType: '', probProducts: [], effectDate: '', description: '', solution: '', impactScope: '',
  softVersions: [{ itemCode: '', oldVersion: '', newVersion: '' }],
  attachments: []
})
const rules = {
  probType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  affectedType: [{ required: true, message: '请选择影响类型', trigger: 'change' }],
  theme: [{ required: true, message: '请输入主题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
}
const queryUser = (q, cb) => { cb([]) }
const handleUpload = (res) => { if (res.code === 200) form.attachments.push(res.data) }
const handleSubmit = async () => { await formRef.value.validate(); await createProb(form); ElMessage.success('提交成功'); router.push('/prob') }
</script>
