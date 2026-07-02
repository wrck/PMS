<template>
  <div class="prob-apply">
    <el-card>
      <template #header><span>技术公告apply</span></template>
      <el-form v-if="'apply' !== 'detail'" :model="form" label-width="120px">
        <el-form-item label="公告标题"><el-input v-model="form.probTitle" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">提交</el-button></el-form-item>
      </el-form>
      <div v-else>技术公告详情页面</div>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const form = ref({})
const handleSubmit = () => {}
</script>
