<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addComment, deleteComment, getComments, type LowCodeComment } from '@/api/lowcode-comment'

const props = defineProps<{ configType: string; configId: number; userId: number; userName?: string }>()
const emit = defineEmits<{ (e: 'mention', userId: number, userName: string): void }>()

const comments = ref<LowCodeComment[]>([])
const newComment = ref('')
const loading = ref(false)

async function load() {
  if (!props.configId) return
  loading.value = true
  try {
    comments.value = await getComments(props.configType, props.configId)
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (!newComment.value.trim()) return
  await addComment({
    configType: props.configType,
    configId: props.configId,
    userId: props.userId,
    userName: props.userName,
    content: newComment.value
  })
  newComment.value = ''
  ElMessage.success('评论成功')
  await load()
}

async function remove(id: number) {
  await deleteComment(id)
  ElMessage.success('删除成功')
  await load()
}

// 简单 @提及：用户输入 @[用户名](123) 格式自动识别
watch(newComment, (v) => {
  const matches = v.match(/@\[([^\]]+)\]\((\d+)\)/g)
  if (matches) {
    matches.forEach((m) => {
      const match = m.match(/@\[([^\]]+)\]\((\d+)\)/)
      if (match) emit('mention', Number(match[2]), match[1])
    })
  }
})

onMounted(load)
watch(() => props.configId, load)
</script>

<template>
  <div class="comment-panel">
    <div class="panel-header">评论 ({{ comments.length }})</div>
    <div v-loading="loading" class="comment-list">
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-meta">
          <strong>{{ c.userName || '匿名' }}</strong>
          <span class="comment-time">{{ c.createTime?.replace('T', ' ').slice(0, 16) }}</span>
          <el-button size="small" type="danger" link @click="remove(c.id!)">删除</el-button>
        </div>
        <div class="comment-content">{{ c.content }}</div>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无评论" :image-size="40" />
    </div>
    <div class="comment-input">
      <el-input v-model="newComment" type="textarea" :rows="2" placeholder="输入评论，使用 @[用户名](ID) 提及他人" />
      <el-button type="primary" size="small" style="margin-top: 8px" @click="submit">发送</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.comment-panel {
  padding: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.panel-header {
  font-weight: 600;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}
.comment-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}
.comment-item {
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;

  .comment-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #909399;
  }
  .comment-time {
    flex: 1;
  }
  .comment-content {
    margin-top: 4px;
    font-size: 13px;
    color: #303133;
    white-space: pre-wrap;
  }
}
.comment-input {
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}
</style>
