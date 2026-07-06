<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatLineRound, Close } from '@element-plus/icons-vue'
import { createFeedback, type Feedback, type FeedbackCategory } from '@/api/feedback'

/**
 * 浮动反馈按钮组件。
 *
 * <p>右下角悬浮按钮，点击弹出反馈对话框：
 * <ul>
 *   <li>category 单选（BUG / SUGGESTION / QUESTION / OTHER）</li>
 *   <li>title 必填</li>
 *   <li>content 必填</li>
 *   <li>contact 选填</li>
 * </ul>
 * </p>
 *
 * <p>提交后调用 POST /api/system/feedback，配合后端 @RateLimit 防止滥用；
 * 前端提交成功后禁用按钮 60 秒，作为额外的客户端节流。</p>
 */

interface FeedbackForm {
  category: FeedbackCategory
  title: string
  content: string
  contact: string
}

const RATE_LIMIT_SECONDS = 60

const dialogVisible = ref(false)
const submitting = ref(false)
/** 剩余冷却秒数（提交成功后倒计时） */
const cooldown = ref(0)
let cooldownTimer: ReturnType<typeof setInterval> | null = null

const form = ref<FeedbackForm>({
  category: 'BUG',
  title: '',
  content: '',
  contact: ''
})

const categoryOptions: { label: string; value: FeedbackCategory }[] = [
  { label: '问题反馈 (BUG)', value: 'BUG' },
  { label: '功能建议', value: 'SUGGESTION' },
  { label: '使用咨询', value: 'QUESTION' },
  { label: '其他', value: 'OTHER' }
]

const canSubmit = computed(
  () =>
    !submitting.value &&
    cooldown.value === 0 &&
    form.value.title.trim().length > 0 &&
    form.value.content.trim().length > 0
)

function resetForm() {
  form.value = {
    category: 'BUG',
    title: '',
    content: '',
    contact: ''
  }
}

function openDialog() {
  if (cooldown.value > 0) {
    ElMessage.warning(`提交过于频繁，请 ${cooldown.value} 秒后再试`)
    return
  }
  resetForm()
  dialogVisible.value = true
}

function startCooldown() {
  cooldown.value = RATE_LIMIT_SECONDS
  if (cooldownTimer) clearInterval(cooldownTimer)
  cooldownTimer = setInterval(() => {
    cooldown.value--
    if (cooldown.value <= 0) {
      if (cooldownTimer) {
        clearInterval(cooldownTimer)
        cooldownTimer = null
      }
      cooldown.value = 0
    }
  }, 1000)
}

async function submit() {
  if (!canSubmit.value) return
  submitting.value = true
  try {
    const payload: Feedback = {
      category: form.value.category,
      title: form.value.title.trim(),
      content: form.value.content.trim(),
      contact: form.value.contact.trim() || undefined
    }
    await createFeedback(payload)
    ElMessage.success('反馈已提交，我们将尽快处理，感谢您的支持！')
    dialogVisible.value = false
    startCooldown()
  } catch {
    // 拦截器已弹出错误提示，此处无需重复
  } finally {
    submitting.value = false
  }
}

function handleClose() {
  if (submitting.value) return
  dialogVisible.value = false
}
</script>

<template>
  <div class="feedback-button">
    <!-- 悬浮按钮 -->
    <button
      class="feedback-fab"
      type="button"
      :title="cooldown > 0 ? `冷却中（${cooldown}s）` : '提交反馈'"
      @click="openDialog"
    >
      <el-icon :size="20"><ChatLineRound /></el-icon>
      <span class="feedback-fab__label">反馈</span>
      <span v-if="cooldown > 0" class="feedback-fab__cooldown">{{ cooldown }}s</span>
    </button>

    <!-- 反馈对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="提交反馈"
      width="520"
      :close-on-click-modal="false"
      :before-close="handleClose"
    >
      <el-form :model="form" label-width="80px" label-position="right">
        <el-form-item label="类型" required>
          <el-radio-group v-model="form.category">
            <el-radio
              v-for="opt in categoryOptions"
              :key="opt.value"
              :value="opt.value"
            >
              {{ opt.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input
            v-model="form.title"
            placeholder="请简短描述您的问题或建议"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述问题现象、复现步骤或建议内容"
            maxlength="4000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input
            v-model="form.contact"
            placeholder="选填，电话或邮箱，便于我们回复"
            maxlength="100"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button :icon="Close" @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="!canSubmit"
          @click="submit"
        >
          提交反馈
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.feedback-button {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1500;
}

.feedback-fab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border: none;
  border-radius: 28px;
  background-color: #409eff;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.2s ease;
}

.feedback-fab:hover {
  background-color: #337ecc;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
  transform: translateY(-1px);
}

.feedback-fab:disabled,
.feedback-fab[disabled] {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.feedback-fab__label {
  font-weight: 500;
}

.feedback-fab__cooldown {
  display: inline-block;
  padding: 0 6px;
  background-color: rgba(255, 255, 255, 0.25);
  border-radius: 10px;
  font-size: 12px;
}
</style>
