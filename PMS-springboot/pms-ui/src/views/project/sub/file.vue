<template>
  <div class="file-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目文件</span>
          <el-upload action="/api/file/upload" :on-success="handleUploadSuccess" :show-file-list="false">
            <el-button type="primary" size="small">上传文件</el-button>
          </el-upload>
        </div>
      </template>
      <el-table :data="fileList" stripe>
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column prop="fileSize" label="大小" />
        <el-table-column prop="uploadPerson" label="上传人" />
        <el-table-column prop="uploadTime" label="上传时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleDownload(row)">下载</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const fileList = ref([])
const handleUploadSuccess = (res) => {}
const handleDownload = (row) => {}
const handleDelete = (row) => {}
</script>
