import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'

// FeedbackButton 浮动反馈按钮组件的单元测试。
//
// 测试策略：
//   - mock element-plus 的 ElMessage，验证提示调用
//   - mock @element-plus/icons-vue，避免引入真实图标渲染
//   - mock @/api/feedback 的 createFeedback，不依赖真实后端
//   - 自定义 stub 替换 el-dialog / el-form / el-input / el-button 等组件，
//     保证 jsdom 下的确定性渲染
//   - 使用 vi.useFakeTimers 验证 60 秒冷却倒计时

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}))

vi.mock('@element-plus/icons-vue', () => ({
  ChatLineRound: defineComponent({ name: 'ChatLineRound', setup: () => () => h('i') }),
  Close: defineComponent({ name: 'Close', setup: () => () => h('i') })
}))

const feedbackMock = vi.hoisted(() => ({
  createFeedback: vi.fn()
}))

vi.mock('@/api/feedback', () => ({
  createFeedback: feedbackMock.createFeedback
}))

import FeedbackButton from '@/components/FeedbackButton/index.vue'
import { ElMessage } from 'element-plus'

// --- Element Plus 组件 stub ---

const ElDialogStub = defineComponent({
  name: 'ElDialog',
  props: ['modelValue', 'title', 'width'],
  setup(props, { slots }) {
    return () =>
      props.modelValue
        ? h('div', { class: 'el-dialog' }, [
            h('div', { class: 'el-dialog__title' }, props.title ?? ''),
            slots.default?.(),
            h('div', { class: 'el-dialog__footer' }, slots.footer?.())
          ])
        : null
  }
})

const ElFormStub = defineComponent({
  name: 'ElForm',
  setup(_, { slots }) {
    return () => h('form', { class: 'el-form' }, slots.default?.())
  }
})

const ElFormItemStub = defineComponent({
  name: 'ElFormItem',
  props: ['label', 'required'],
  setup(_, { slots }) {
    return () => h('div', { class: 'el-form-item' }, slots.default?.())
  }
})

const ElInputStub = defineComponent({
  name: 'ElInput',
  props: ['modelValue', 'type', 'placeholder', 'rows', 'maxlength', 'showWordLimit'],
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    return () =>
      props.type === 'textarea'
        ? h('textarea', {
            class: 'el-input__inner el-textarea__inner',
            value: props.modelValue ?? '',
            onInput: (e: Event) =>
              emit('update:modelValue', (e.target as HTMLTextAreaElement).value)
          })
        : h('input', {
            class: 'el-input__inner',
            type: 'text',
            value: props.modelValue ?? '',
            placeholder: props.placeholder,
            onInput: (e: Event) =>
              emit('update:modelValue', (e.target as HTMLInputElement).value)
          })
  }
})

const ElRadioGroupStub = defineComponent({
  name: 'ElRadioGroup',
  props: ['modelValue'],
  setup(_, { slots }) {
    return () => h('div', { class: 'el-radio-group' }, slots.default?.())
  }
})

const ElRadioStub = defineComponent({
  name: 'ElRadio',
  props: ['value', 'label'],
  setup(_, { slots }) {
    return () => h('label', { class: 'el-radio' }, slots.default?.())
  }
})

const ElButtonStub = defineComponent({
  name: 'ElButton',
  props: ['type', 'loading', 'disabled', 'icon', 'size', 'link'],
  emits: ['click'],
  setup(props, { emit, slots }) {
    return () =>
      h(
        'button',
        {
          class: 'el-button',
          disabled: props.disabled || false,
          onClick: (e: Event) => {
            if (!props.disabled) emit('click', e)
          }
        },
        slots.default?.()
      )
  }
})

const ElIconStub = defineComponent({
  name: 'ElIcon',
  setup(_, { slots }) {
    return () => h('span', { class: 'el-icon' }, slots.default?.())
  }
})

function mountFeedback() {
  return mount(FeedbackButton, {
    global: {
      stubs: {
        ElDialog: ElDialogStub,
        ElForm: ElFormStub,
        ElFormItem: ElFormItemStub,
        ElInput: ElInputStub,
        ElRadioGroup: ElRadioGroupStub,
        ElRadio: ElRadioStub,
        ElButton: ElButtonStub,
        ElIcon: ElIconStub
      }
    }
  })
}

/** 在已打开的对话框中填写标题与内容 */
async function fillForm(wrapper: ReturnType<typeof mountFeedback>, title: string, content: string) {
  const inputs = wrapper.findAll('input.el-input__inner')
  await inputs[0].setValue(title)
  await wrapper.find('textarea.el-input__inner').setValue(content)
}

/** 查找提交按钮（footer 中文本含「提交反馈」的 el-button） */
function findSubmitButton(wrapper: ReturnType<typeof mountFeedback>) {
  return wrapper.findAll('button.el-button').find((b) => b.text().includes('提交反馈'))
}

describe('FeedbackButton', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.clearAllMocks()
    feedbackMock.createFeedback.mockReset()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('渲染悬浮按钮并显示「反馈」标签', () => {
    const wrapper = mountFeedback()
    const fab = wrapper.find('.feedback-fab')
    expect(fab.exists()).toBe(true)
    expect(fab.text()).toContain('反馈')
    // 冷却倒计时元素初始不存在
    expect(wrapper.find('.feedback-fab__cooldown').exists()).toBe(false)
  })

  it('点击悬浮按钮打开反馈对话框', async () => {
    const wrapper = mountFeedback()
    expect(wrapper.find('.el-dialog').exists()).toBe(false)

    await wrapper.find('.feedback-fab').trigger('click')

    expect(wrapper.find('.el-dialog').exists()).toBe(true)
    expect(wrapper.find('.el-dialog__title').text()).toBe('提交反馈')
  })

  it('标题或内容为空时提交按钮禁用', async () => {
    const wrapper = mountFeedback()
    await wrapper.find('.feedback-fab').trigger('click')

    // 未填写任何内容
    const submitBtn = findSubmitButton(wrapper)
    expect(submitBtn).toBeTruthy()
    expect(submitBtn!.attributes('disabled')).toBeDefined()

    // 仅填写标题
    await fillForm(wrapper, '标题', '')
    expect(submitBtn!.attributes('disabled')).toBeDefined()

    // 标题与内容均填写后启用
    await fillForm(wrapper, '标题', '内容详情')
    expect(submitBtn!.attributes('disabled')).toBeUndefined()
  })

  it('填写必填项后提交调用 createFeedback 并关闭对话框', async () => {
    feedbackMock.createFeedback.mockResolvedValue(true)
    const wrapper = mountFeedback()
    await wrapper.find('.feedback-fab').trigger('click')
    await fillForm(wrapper, '页面卡顿', '打开项目列表时卡顿约 5 秒')

    const submitBtn = findSubmitButton(wrapper)!
    await submitBtn.trigger('click')
    await flushPromises()

    expect(feedbackMock.createFeedback).toHaveBeenCalledTimes(1)
    const payload = feedbackMock.createFeedback.mock.calls[0][0]
    expect(payload.title).toBe('页面卡顿')
    expect(payload.content).toBe('打开项目列表时卡顿约 5 秒')
    expect(payload.category).toBe('BUG')
    // 提交成功后对话框关闭
    expect(wrapper.find('.el-dialog').exists()).toBe(false)
    // 成功提示
    expect(ElMessage.success).toHaveBeenCalled()
  })

  it('提交成功后进入 60 秒冷却倒计时', async () => {
    feedbackMock.createFeedback.mockResolvedValue(true)
    const wrapper = mountFeedback()
    await wrapper.find('.feedback-fab').trigger('click')
    await fillForm(wrapper, '标题', '内容')
    await findSubmitButton(wrapper)!.trigger('click')
    await flushPromises()

    // 立即进入冷却：60s
    expect(wrapper.find('.feedback-fab__cooldown').text()).toBe('60s')

    // 推进 15 秒，剩余 45 秒
    vi.advanceTimersByTime(15_000)
    // 等待 Vue 重新渲染倒计时文本
    await flushPromises()
    expect(wrapper.find('.feedback-fab__cooldown').text()).toBe('45s')
  })

  it('冷却中点击悬浮按钮显示警告且不打开对话框', async () => {
    feedbackMock.createFeedback.mockResolvedValue(true)
    const wrapper = mountFeedback()
    await wrapper.find('.feedback-fab').trigger('click')
    await fillForm(wrapper, '标题', '内容')
    await findSubmitButton(wrapper)!.trigger('click')
    await flushPromises()

    // 此时已进入冷却，对话框已关闭
    expect(wrapper.find('.el-dialog').exists()).toBe(false)

    await wrapper.find('.feedback-fab').trigger('click')

    expect(ElMessage.warning).toHaveBeenCalled()
    // 冷却期间不重新打开对话框
    expect(wrapper.find('.el-dialog').exists()).toBe(false)
  })

  it('提交失败时不进入冷却且对话框保持打开', async () => {
    feedbackMock.createFeedback.mockRejectedValue(new Error('网络异常'))
    const wrapper = mountFeedback()
    await wrapper.find('.feedback-fab').trigger('click')
    await fillForm(wrapper, '标题', '内容')

    await findSubmitButton(wrapper)!.trigger('click')
    await flushPromises()

    expect(feedbackMock.createFeedback).toHaveBeenCalled()
    // 失败时对话框保持打开
    expect(wrapper.find('.el-dialog').exists()).toBe(true)
    // 失败时不进入冷却
    expect(wrapper.find('.feedback-fab__cooldown').exists()).toBe(false)
    // 不弹成功提示
    expect(ElMessage.success).not.toHaveBeenCalled()
  })
})
