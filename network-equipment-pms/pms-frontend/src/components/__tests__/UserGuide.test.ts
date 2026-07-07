import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'

// UserGuide 用户引导组件的单元测试。
//
// 该组件包含 5 个引导步骤，并在完成 / 跳过时调用 useFirstLogin().markCompleted。
// 组件使用 <Teleport to="body"> 渲染，因此测试通过 document.body 查询 DOM。
//
// 测试策略：
//   - mock @/composables/useFirstLogin，控制 isFirstLogin 初始值并断言 markCompleted 调用
//   - stub ElButton，避免依赖真实 Element Plus
//   - 通过步骤计数器与按钮文案验证 5 步引导流程

// 提升可变状态：当前是否首次登录、markCompleted spy
const guideMock = vi.hoisted(() => ({
  firstLoginValue: true,
  markCompleted: vi.fn()
}))

vi.mock('@/composables/useFirstLogin', async () => {
  const { ref } = await import('vue')
  return {
    useFirstLogin: () => ({
      // 每次调用读取最新值，保证测试间可重置
      isFirstLogin: ref(guideMock.firstLoginValue),
      markCompleted: guideMock.markCompleted
    })
  }
})

import UserGuide from '@/components/UserGuide/index.vue'

// ElButton stub：渲染为普通 button，点击时触发 click 事件
const ElButtonStub = defineComponent({
  name: 'ElButton',
  props: ['type', 'loading', 'disabled', 'size', 'link'],
  emits: ['click'],
  setup(props, { emit, slots }) {
    return () =>
      h(
        'button',
        {
          class: 'el-button',
          onClick: (e: Event) => emit('click', e)
        },
        slots.default?.()
      )
  }
})

function mountGuide(props?: { visible?: boolean }) {
  return mount(UserGuide, {
    props,
    global: {
      stubs: {
        ElButton: ElButtonStub
      }
    }
  })
}

/** 引导气泡元素（teleport 到 body） */
function popover(): HTMLElement | null {
  return document.body.querySelector('.guide-popover')
}

/** 步骤计数器文本，例如 "1 / 5" */
function stepText(): string {
  return document.body.querySelector('.guide-popover__step')?.textContent?.trim() ?? ''
}

/** 在气泡中按文案查找 el-button */
function findGuideButton(text: string): HTMLElement | null {
  const btns = document.body.querySelectorAll('button.el-button')
  for (const b of Array.from(btns)) {
    if ((b as HTMLElement).textContent?.includes(text)) return b as HTMLElement
  }
  return null
}

describe('UserGuide', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
    guideMock.firstLoginValue = true
    guideMock.markCompleted = vi.fn()
  })

  afterEach(() => {
    document.body.innerHTML = ''
  })

  it('首次登录时自动显示引导并展示第 1 步', async () => {
    guideMock.firstLoginValue = true
    const wrapper = mountGuide()
    await flushPromises()

    expect(document.body.querySelector('.user-guide')).not.toBeNull()
    expect(popover()).not.toBeNull()
    expect(stepText()).toBe('1 / 5')
    // 第 1 步标题为「欢迎使用...」
    expect(document.body.querySelector('.guide-popover__title')?.textContent).toContain(
      '欢迎使用'
    )
    // 第 1 步不显示「上一步」
    expect(findGuideButton('上一步')).toBeNull()
    // 显示「下一步」
    expect(findGuideButton('下一步')).not.toBeNull()
    wrapper.unmount()
  })

  it('引导共 5 步，逐步点击下一步直到显示「完成」', async () => {
    guideMock.firstLoginValue = true
    const wrapper = mountGuide()
    await flushPromises()

    // 第 1 步 → 第 2 步
    findGuideButton('下一步')!.click()
    await flushPromises()
    expect(stepText()).toBe('2 / 5')
    expect(findGuideButton('上一步')).not.toBeNull()

    // → 第 3 步
    findGuideButton('下一步')!.click()
    await flushPromises()
    expect(stepText()).toBe('3 / 5')

    // → 第 4 步
    findGuideButton('下一步')!.click()
    await flushPromises()
    expect(stepText()).toBe('4 / 5')

    // → 第 5 步：按钮文案变为「完成」
    findGuideButton('下一步')!.click()
    await flushPromises()
    expect(stepText()).toBe('5 / 5')
    expect(findGuideButton('完成')).not.toBeNull()
    expect(findGuideButton('下一步')).toBeNull()

    wrapper.unmount()
  })

  it('点击「跳过」按钮隐藏引导并调用 markCompleted', async () => {
    guideMock.firstLoginValue = true
    const wrapper = mountGuide()
    await flushPromises()
    expect(document.body.querySelector('.user-guide')).not.toBeNull()

    findGuideButton('跳过')!.click()
    await flushPromises()

    // 引导隐藏
    expect(document.body.querySelector('.user-guide')).toBeNull()
    // 标记已完成
    expect(guideMock.markCompleted).toHaveBeenCalled()
    // 触发 skip 事件
    expect(wrapper.emitted('skip')).toBeTruthy()
    wrapper.unmount()
  })

  it('完成最后一步时触发 finish 事件并调用 markCompleted', async () => {
    guideMock.firstLoginValue = true
    const wrapper = mountGuide()
    await flushPromises()

    // 连续点击下一步直至第 5 步
    for (let i = 0; i < 4; i++) {
      findGuideButton('下一步')!.click()
      await flushPromises()
    }
    expect(stepText()).toBe('5 / 5')

    // 点击「完成」
    findGuideButton('完成')!.click()
    await flushPromises()

    // 引导隐藏
    expect(document.body.querySelector('.user-guide')).toBeNull()
    // 标记已完成
    expect(guideMock.markCompleted).toHaveBeenCalled()
    // 触发 finish 事件
    expect(wrapper.emitted('finish')).toBeTruthy()
    wrapper.unmount()
  })

  it('非首次登录时不自动显示引导', async () => {
    guideMock.firstLoginValue = false
    const wrapper = mountGuide()
    await flushPromises()

    expect(document.body.querySelector('.user-guide')).toBeNull()
    wrapper.unmount()
  })

  it('通过 props.visible 强制显示引导（覆盖首次登录检测）', async () => {
    guideMock.firstLoginValue = false
    const wrapper = mountGuide({ visible: true })
    await flushPromises()

    expect(document.body.querySelector('.user-guide')).not.toBeNull()
    expect(stepText()).toBe('1 / 5')
    wrapper.unmount()
  })

  it('暴露 start 方法可手动启动引导', async () => {
    guideMock.firstLoginValue = false
    const wrapper = mountGuide()
    await flushPromises()
    // 初始不显示
    expect(document.body.querySelector('.user-guide')).toBeNull()

    // 通过 defineExpose 暴露的 start 方法手动启动
    ;(wrapper.vm as unknown as { start: () => void }).start()
    await flushPromises()

    expect(document.body.querySelector('.user-guide')).not.toBeNull()
    expect(stepText()).toBe('1 / 5')
    wrapper.unmount()
  })
})
