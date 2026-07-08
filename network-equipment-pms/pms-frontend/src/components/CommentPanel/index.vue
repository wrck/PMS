<script setup lang="ts">
/**
 * 评论面板（线程化 + @提及自动补全）。
 *
 * <p>线程化：调用 /api/lowcode/comment/threaded 获取按 parent_id 构建的评论树，
 * 在前端按深度扁平化渲染，支持折叠/展开回复。</p>
 *
 * <p>@提及：输入框中输入 @ 触发用户选择器下拉，输入关键字后 debounce 300ms
 * 调用 /api/system/user/search 搜索用户，选中后插入 @[用户名](用户ID) 格式。
 * 支持 ↑/↓ 选择、Enter 确认、Esc 关闭。</p>
 */
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addComment,
  deleteComment,
  getThreadedComments,
  type CommentTreeNode
} from '@/api/lowcode-comment'
import { searchUsers, type MentionUser } from '@/api/system'

const props = defineProps<{ configType: string; configId: number; userId: number; userName?: string }>()
const emit = defineEmits<{ (e: 'mention', userId: number, userName: string): void }>()

/** 线程化评论树（根节点列表） */
const threads = ref<CommentTreeNode[]>([])
const newComment = ref('')
const loading = ref(false)

// ---------------- 线程化渲染（扁平化 + 折叠） ----------------
/** 折叠的评论 id 集合 */
const collapsedSet = ref<Set<number>>(new Set())

interface FlatItem {
  node: CommentTreeNode
  depth: number
  hasReplies: boolean
}

/** 将评论树按深度扁平化；折叠的节点不展开其子回复 */
function flatten(nodes: CommentTreeNode[], depth: number): FlatItem[] {
  const result: FlatItem[] = []
  for (const node of nodes) {
    const id = node.comment.id ?? 0
    const hasReplies = node.replies.length > 0
    result.push({ node, depth, hasReplies })
    if (!collapsedSet.value.has(id)) {
      result.push(...flatten(node.replies, depth + 1))
    }
  }
  return result
}

const flatList = computed<FlatItem[]>(() => flatten(threads.value, 0))

function toggleCollapse(id: number) {
  const s = new Set(collapsedSet.value)
  if (s.has(id)) s.delete(id)
  else s.add(id)
  collapsedSet.value = s
}

function isCollapsed(id: number): boolean {
  return collapsedSet.value.has(id)
}

// ---------------- 回复 ----------------
/** 当前正在回复的评论 id（同一时刻只展开一个回复框） */
const replyingTo = ref<number | null>(null)
const replyContent = ref('')

function openReply(id: number) {
  replyingTo.value = id
  replyContent.value = ''
  mentionVisible.value = false
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
  mentionVisible.value = false
}

// ---------------- @提及自动补全 ----------------
/** 触发提及的输入域：main=主评论框，reply=回复框 */
type MentionField = 'main' | 'reply'
const activeField = ref<MentionField>('main')
const mainTextareaRef = ref<HTMLTextAreaElement | null>(null)
const replyTextareaRef = ref<HTMLTextAreaElement | null>(null)

const mentionVisible = ref(false)
const mentionUsers = ref<MentionUser[]>([])
const mentionLoading = ref(false)
/** 当前 @ 关键字（@ 之后到光标的文本） */
const mentionQuery = ref('')
/** @ 符号在输入文本中的起始下标 */
const mentionStart = ref(0)
/** 键盘选中索引 */
const activeMentionIndex = ref(0)
/** debounce 定时器 */
let mentionTimer: ReturnType<typeof setTimeout> | null = null

function setReplyRef(el: Element | { $el?: Element } | null) {
  // 回复框在 v-for 内通过函数 ref 绑定，取底层 textarea 元素
  if (!el) {
    replyTextareaRef.value = null
    return
  }
  const dom = (el as { $el?: Element }).$el ?? el
  replyTextareaRef.value = dom as HTMLTextAreaElement
}

/**
 * 检测光标位置是否处于 "@关键字" 上下文中。
 *
 * <p>仅当 @ 位于文本起点或前置字符为空白时才触发，避免邮箱地址 a@b 误触发。
 * 匹配到则打开下拉并按关键字（debounce 300ms）搜索用户。</p>
 */
function detectMention(ta: HTMLTextAreaElement, field: MentionField) {
  activeField.value = field
  const value = ta.value
  const cursor = ta.selectionStart ?? value.length
  const before = value.slice(0, cursor)
  const m = before.match(/@([^\s@]*)$/)
  if (m) {
    const atIdx = before.length - m[0].length
    // @ 前必须是空白或文本起点
    const prevChar = atIdx > 0 ? before[atIdx - 1] : ''
    if (atIdx > 0 && !/\s/.test(prevChar)) {
      mentionVisible.value = false
      return
    }
    mentionStart.value = atIdx
    mentionQuery.value = m[1]
    mentionVisible.value = true
    scheduleMentionSearch(m[1])
  } else {
    mentionVisible.value = false
  }
}

function scheduleMentionSearch(keyword: string) {
  if (mentionTimer) clearTimeout(mentionTimer)
  mentionTimer = setTimeout(() => {
    void doSearchUsers(keyword)
  }, 300)
}

async function doSearchUsers(keyword: string) {
  mentionLoading.value = true
  try {
    mentionUsers.value = await searchUsers(keyword || undefined, 20)
    activeMentionIndex.value = mentionUsers.value.length > 0 ? 0 : -1
  } catch {
    mentionUsers.value = []
    activeMentionIndex.value = -1
  } finally {
    mentionLoading.value = false
  }
}

function mentionLabel(u: MentionUser): string {
  return u.realName ? `${u.realName}（${u.username}）` : u.username
}

/** 选中用户后，将 "@关键字" 替换为 @[用户名](用户ID) 并恢复光标 */
async function selectMention(user: MentionUser) {
  const ta = activeField.value === 'main' ? mainTextareaRef.value : replyTextareaRef.value
  const model = activeField.value === 'main' ? newComment : replyContent
  if (!ta) return
  const start = mentionStart.value
  const cursor = ta.selectionStart ?? model.value.length
  const insertText = `@[${user.realName || user.username}](${user.id})`
  model.value = model.value.slice(0, start) + insertText + model.value.slice(cursor)
  mentionVisible.value = false
  emit('mention', user.id, user.realName || user.username)
  await nextTick()
  const pos = start + insertText.length
  ta.focus()
  ta.setSelectionRange(pos, pos)
}

/** 输入框键盘事件：开启下拉时拦截方向键/回车/ESC */
function onKeydown(e: KeyboardEvent, _field: MentionField) {
  if (!mentionVisible.value || mentionUsers.value.length === 0) return
  const len = mentionUsers.value.length
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    activeMentionIndex.value = (activeMentionIndex.value + 1) % len
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    activeMentionIndex.value = (activeMentionIndex.value - 1 + len) % len
  } else if (e.key === 'Enter') {
    e.preventDefault()
    const u = mentionUsers.value[activeMentionIndex.value]
    if (u) void selectMention(u)
  } else if (e.key === 'Escape') {
    e.preventDefault()
    mentionVisible.value = false
  }
}

// 主评论框输入
function onMainInput(e: Event) {
  const ta = e.target as HTMLTextAreaElement
  newComment.value = ta.value
  detectMention(ta, 'main')
}

// 回复框输入
function onReplyInput(e: Event) {
  const ta = e.target as HTMLTextAreaElement
  replyContent.value = ta.value
  detectMention(ta, 'reply')
}

// ---------------- 提及内容解析 ----------------
/** 从评论内容中提取被 @ 的用户 ID，逗号分隔，写入 mentions 字段 */
function extractMentions(content: string): string {
  const re = /@\[([^\]]+)\]\((\d+)\)/g
  const ids: string[] = []
  let m: RegExpExecArray | null
  while ((m = re.exec(content)) !== null) {
    ids.push(m[2])
  }
  return ids.join(',')
}

// ---------------- 数据加载 / 提交 ----------------
async function load() {
  if (!props.configId) return
  loading.value = true
  try {
    threads.value = await getThreadedComments(props.configType, props.configId)
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
    content: newComment.value,
    mentions: extractMentions(newComment.value)
  })
  newComment.value = ''
  mentionVisible.value = false
  ElMessage.success('评论成功')
  await load()
}

async function submitReply() {
  if (!replyingTo.value || !replyContent.value.trim()) return
  await addComment({
    configType: props.configType,
    configId: props.configId,
    userId: props.userId,
    userName: props.userName,
    content: replyContent.value,
    parentId: replyingTo.value,
    mentions: extractMentions(replyContent.value)
  })
  replyContent.value = ''
  replyingTo.value = null
  mentionVisible.value = false
  ElMessage.success('回复成功')
  await load()
}

async function remove(id: number) {
  await deleteComment(id)
  ElMessage.success('删除成功')
  await load()
}

function formatTime(t?: string): string {
  return t ? t.replace('T', ' ').slice(0, 16) : ''
}

onMounted(load)
watch(() => props.configId, load)
</script>

<template>
  <div class="comment-panel">
    <div class="panel-header">评论 ({{ threads.length }})</div>
    <div v-loading="loading" class="comment-list">
      <div
        v-for="item in flatList"
        :key="item.node.comment.id"
        class="comment-item"
        :style="{ paddingLeft: item.depth * 20 + 'px' }"
      >
        <div class="comment-meta">
          <strong>{{ item.node.comment.userName || '匿名' }}</strong>
          <span class="comment-time">{{ formatTime(item.node.comment.createTime) }}</span>
          <el-button
            v-if="item.hasReplies"
            link
            size="small"
            @click="toggleCollapse(item.node.comment.id!)"
          >
            {{ isCollapsed(item.node.comment.id!) ? `展开(${item.node.replies.length})` : '收起' }}
          </el-button>
          <el-button link size="small" @click="openReply(item.node.comment.id!)">回复</el-button>
          <el-button link size="small" type="danger" @click="remove(item.node.comment.id!)">删除</el-button>
        </div>
        <div class="comment-content">{{ item.node.comment.content }}</div>

        <!-- 回复输入框（仅对当前选中的评论展开） -->
        <div v-if="replyingTo === item.node.comment.id" class="reply-box">
          <div class="mention-wrapper">
            <textarea
              :ref="setReplyRef"
              :value="replyContent"
              class="cp-textarea"
              rows="2"
              placeholder="回复评论，输入 @ 可提及他人"
              @input="onReplyInput"
              @keydown="onKeydown($event, 'reply')"
            ></textarea>
            <!-- @提及下拉（回复域） -->
            <div v-if="mentionVisible && activeField === 'reply'" class="mention-dropdown">
              <div v-if="mentionLoading" class="mention-tip">搜索中…</div>
              <div v-else-if="mentionUsers.length === 0" class="mention-tip">
                {{ mentionQuery ? '无匹配用户' : '请输入关键字' }}
              </div>
              <div
                v-for="(u, idx) in mentionUsers"
                v-else
                :key="u.id"
                class="mention-item"
                :class="{ active: idx === activeMentionIndex }"
                @mousedown.prevent
                @click="selectMention(u)"
              >
                {{ mentionLabel(u) }}
              </div>
            </div>
          </div>
          <div class="reply-actions">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button size="small" type="primary" @click="submitReply">回复</el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="flatList.length === 0" description="暂无评论" :image-size="40" />
    </div>

    <!-- 主评论输入框 -->
    <div class="comment-input">
      <div class="mention-wrapper">
        <textarea
          ref="mainTextareaRef"
          :value="newComment"
          class="cp-textarea"
          rows="2"
          placeholder="输入评论，使用 @ 可提及他人（@[用户名](ID)）"
          @input="onMainInput"
          @keydown="onKeydown($event, 'main')"
        ></textarea>
        <!-- @提及下拉（主域） -->
        <div v-if="mentionVisible && activeField === 'main'" class="mention-dropdown">
          <div v-if="mentionLoading" class="mention-tip">搜索中…</div>
          <div v-else-if="mentionUsers.length === 0" class="mention-tip">
            {{ mentionQuery ? '无匹配用户' : '请输入关键字' }}
          </div>
          <div
            v-for="(u, idx) in mentionUsers"
            v-else
            :key="u.id"
            class="mention-item"
            :class="{ active: idx === activeMentionIndex }"
            @mousedown.prevent
            @click="selectMention(u)"
          >
            {{ mentionLabel(u) }}
          </div>
        </div>
      </div>
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
    word-break: break-word;
  }
}
.reply-box {
  margin-top: 8px;
  padding: 8px;
  background: var(--el-fill-color-light);
  border-radius: 4px;

  .reply-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
  }
}
.comment-input {
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}

/* 原生 textarea，借用 Element Plus 输入框风格 */
.cp-textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 8px 12px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--el-input-text-color, var(--el-text-color-regular));
  background-color: var(--el-input-bg-color, #fff);
  border: 1px solid var(--el-input-border-color, #dcdfe6);
  border-radius: 4px;
  resize: vertical;
  outline: none;
  transition: border-color 0.2s;
  font-family: inherit;
}
.cp-textarea:focus {
  border-color: var(--el-color-primary);
}
.cp-textarea::placeholder {
  color: var(--el-text-color-placeholder);
}

.mention-wrapper {
  position: relative;
}
.mention-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 2000;
  min-width: 220px;
  max-height: 240px;
  overflow-y: auto;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .mention-tip {
    padding: 8px 12px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
  .mention-item {
    padding: 6px 12px;
    font-size: 13px;
    cursor: pointer;
    color: var(--el-text-color-regular);

    &:hover,
    &.active {
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
  }
}
</style>
