<template>
  <div class="presales-audit">
    <el-card>
      <template #header><span>售前audit</span></template>
      <el-form v-if="'audit' !== 'detail'" :model="form" label-width="120px">
        <el-form-item label="售前项目名称"><el-input v-model="form.presalesName" /></el-form-item>
        <el-form-item label="项目类型"><el-input v-model="form.presalesType" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">提交</el-button></el-form-item>
      </el-form>
      <div v-else>售前项目详情页面</div>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const form = ref({})
const handleSubmit = () => {}
</script>
