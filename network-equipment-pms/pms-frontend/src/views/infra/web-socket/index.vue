<script setup lang="ts">
import { computed, onBeforeUnmount, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'InfraWebSocket' })

const serverUrl = ref('ws://localhost:48080/admin-api/infra/ws')
const sendText = ref('')
const status = ref<'CLOSED' | 'CONNECTING' | 'OPEN'>('CLOSED')

let ws: WebSocket | null = null

interface MsgRecord {
  time: string
  text: string
  type: 'sent' | 'received' | 'system'
}
const messageList = ref<MsgRecord[]>([])

const statusTagType = computed(() => {
  if (status.value === 'OPEN') return 'success'
  if (status.value === 'CONNECTING') return 'warning'
  return 'info'
})

const statusLabel = computed(() => {
  if (status.value === 'OPEN') return '已连接'
  if (status.value === 'CONNECTING') return '连接中'
  return '未连接'
})

const isConnected = computed(() => status.value === 'OPEN')

function nowStr(): string {
  return new Date().toLocaleString('zh-CN', { hour12: false })
}

function pushMsg(text: string, type: MsgRecord['type']) {
  messageList.value.push({ time: nowStr(), text, type })
}

function handleConnect() {
  if (isConnected.value) {
    handleClose()
    return
  }
  if (!serverUrl.value.trim()) {
    ElMessage.warning('请输入 WebSocket 服务地址')
    return
  }
  status.value = 'CONNECTING'
  try {
    ws = new WebSocket(serverUrl.value)
  } catch (err) {
    status.value = 'CLOSED'
    ElMessage.error('WebSocket 地址不合法')
    return
  }

  ws.onopen = () => {
    status.value = 'OPEN'
    pushMsg('连接已建立', 'system')
  }

  ws.onmessage = (event) => {
    pushMsg(event.data, 'received')
  }

  ws.onerror = () => {
    pushMsg('连接发生错误', 'system')
  }

  ws.onclose = () => {
    status.value = 'CLOSED'
    pushMsg('连接已关闭', 'system')
    ws = null
  }
}

function handleClose() {
  if (ws) {
    ws.close()
    ws = null
  }
  status.value = 'CLOSED'
}

function handleSend() {
  if (!isConnected.value || !ws) {
    ElMessage.warning('请先建立 WebSocket 连接')
    return
  }
  if (!sendText.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  ws.send(sendText.value)
  pushMsg(sendText.value, 'sent')
  sendText.value = ''
}

function handleClear() {
  messageList.value = []
}

onBeforeUnmount(() => {
  handleClose()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>WebSocket 测试</template>

      <el-row :gutter="12">
        <!-- 左侧：连接 + 发送 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>连接</span>
                <el-tag :type="statusTagType" size="small">{{ statusLabel }}</el-tag>
              </div>
            </template>

            <el-form label-width="100px" @submit.prevent>
              <el-form-item label="服务地址">
                <el-input v-model="serverUrl" placeholder="请输入 WebSocket 服务地址" clearable />
              </el-form-item>
              <el-form-item label="操作">
                <el-button
                  :type="isConnected ? 'danger' : 'primary'"
                  @click="handleConnect"
                >
                  {{ isConnected ? '关闭连接' : '开启连接' }}
                </el-button>
                <el-button :disabled="!isConnected" @click="handleClose">断开</el-button>
              </el-form-item>
            </el-form>

            <el-divider content-position="left">发送消息</el-divider>

            <el-input
              v-model="sendText"
              type="textarea"
              :rows="5"
              :disabled="!isConnected"
              placeholder="请输入要发送的消息内容"
            />
            <div class="send-bar">
              <el-button
                type="primary"
                :disabled="!isConnected"
                @click="handleSend"
              >
                发送
              </el-button>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：消息记录 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>消息记录</span>
                <el-button link type="danger" size="small" @click="handleClear">清空</el-button>
              </div>
            </template>

            <div class="msg-list">
              <ul v-if="messageList.length > 0">
                <li v-for="(msg, idx) in messageList" :key="idx" class="msg-item">
                  <div class="msg-meta">
                    <el-tag
                      size="small"
                      :type="msg.type === 'sent' ? 'primary' : msg.type === 'received' ? 'success' : 'info'"
                    >
                      {{ msg.type === 'sent' ? '发送' : msg.type === 'received' ? '收到' : '系统' }}
                    </el-tag>
                    <span class="msg-time">{{ msg.time }}</span>
                  </div>
                  <div class="msg-text">{{ msg.text }}</div>
                </li>
              </ul>
              <el-empty v-else description="暂无消息记录" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.send-bar {
  margin-top: 12px;
  text-align: right;
}
.msg-list {
  max-height: 480px;
  overflow: auto;
}
.msg-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.msg-item {
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}
.msg-item:last-child {
  border-bottom: none;
}
.msg-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.msg-time {
  font-size: 12px;
  color: #909399;
}
.msg-text {
  font-size: 13px;
  color: #303133;
  word-break: break-all;
  white-space: pre-wrap;
}
</style>
