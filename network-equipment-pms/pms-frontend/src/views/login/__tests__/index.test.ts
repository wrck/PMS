import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// --- Mocks ---

// Mock only ElMessage from element-plus. Element Plus components are stubbed
// below to keep the test deterministic in jsdom.
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}))

const mocks = vi.hoisted(() => ({
  login: vi.fn(),
  routerPush: vi.fn()
}))

// Mock the user store so the component never touches the real Pinia store.
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    login: mocks.login,
    token: '',
    userInfo: null,
    permissions: []
  })
}))

// Mock vue-router — the component uses useRouter/useRoute.
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mocks.routerPush }),
  useRoute: () => ({ query: {} as Record<string, any> })
}))

import Login from '@/views/login/index.vue'
import { ElMessage } from 'element-plus'

// --- Custom Element Plus stubs ---

/**
 * Build an el-form stub whose `validate` callback reports the given validity.
 * The component calls `loginFormRef.value.validate(callback)`, so we expose
 * `validate` and control whether the form is considered valid.
 *
 * NOTE: Per-field validation (required, min-length) is driven by Element Plus
 * async-validator inside the real ElForm. Those rules are exercised through
 * the `validate` callback here: when `valid=false`, login is blocked.
 */
function createElFormStub(valid: boolean) {
  return defineComponent({
    name: 'ElForm',
    setup(_, { expose, slots }) {
      const validate = vi.fn((cb?: (v: boolean) => void) => {
        if (typeof cb === 'function') cb(valid)
        return Promise.resolve(valid)
      })
      expose({ validate })
      return () => h('form', { class: 'el-form' }, slots.default?.())
    }
  })
}

const ElFormItemStub = defineComponent({
  name: 'ElFormItem',
  setup(_, { slots }) {
    return () => h('div', { class: 'el-form-item' }, slots.default?.())
  }
})

const ElInputStub = defineComponent({
  name: 'ElInput',
  props: [
    'modelValue',
    'type',
    'placeholder',
    'prefixIcon',
    'clearable',
    'showPassword'
  ],
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    return () =>
      h('input', {
        class: 'el-input__inner',
        type: props.type === 'password' ? 'password' : 'text',
        value: props.modelValue ?? '',
        placeholder: props.placeholder,
        onInput: (e: Event) =>
          emit('update:modelValue', (e.target as HTMLInputElement).value)
      })
  }
})

const ElButtonStub = defineComponent({
  name: 'ElButton',
  props: ['type', 'loading', 'disabled'],
  emits: ['click'],
  setup(props, { emit, slots }) {
    return () =>
      h(
        'button',
        {
          class: ['el-button', 'login-btn'],
          disabled: props.disabled,
          onClick: (e: Event) => emit('click', e)
        },
        slots.default?.()
      )
  }
})

const ElIconStub = defineComponent({
  name: 'ElIcon',
  setup(_, { slots }) {
    return () => h('i', { class: 'el-icon' }, slots.default?.())
  }
})

function mountLogin(valid: boolean) {
  return mount(Login, {
    global: {
      stubs: {
        ElForm: createElFormStub(valid),
        ElFormItem: ElFormItemStub,
        ElInput: ElInputStub,
        ElButton: ElButtonStub,
        ElIcon: ElIconStub
      }
    }
  })
}

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('rendering', () => {
    it('renders username and password inputs and a login button', () => {
      const wrapper = mountLogin(true)

      const inputs = wrapper.findAll('input')
      expect(inputs.length).toBe(2)
      // Username input is type=text, password input is type=password
      expect(wrapper.find('input[type=password]').exists()).toBe(true)

      const button = wrapper.find('button.login-btn')
      expect(button.exists()).toBe(true)
      expect(button.text()).toContain('登')
      expect(button.text()).toContain('录')
    })

    it('renders the system title', () => {
      const wrapper = mountLogin(true)
      expect(wrapper.text()).toContain('网络设备工程项目管理系统')
    })
  })

  describe('form validation gate', () => {
    it('does not call login when the form is invalid', async () => {
      const wrapper = mountLogin(false)

      // Set values so the only thing blocking login is `valid=false`
      const inputs = wrapper.findAll('input')
      await inputs[0].setValue('admin')
      await inputs[1].setValue('123456')

      await wrapper.find('button.login-btn').trigger('click')
      await flushPromises()

      expect(mocks.login).not.toHaveBeenCalled()
      expect(mocks.routerPush).not.toHaveBeenCalled()
    })
  })

  describe('successful login', () => {
    it('calls store.login, shows a success message and redirects to /dashboard', async () => {
      mocks.login.mockResolvedValue({
        token: 'tok',
        userInfo: { id: 1, username: 'admin', nickname: 'Admin' }
      })
      const wrapper = mountLogin(true)

      const inputs = wrapper.findAll('input')
      await inputs[0].setValue('admin')
      await inputs[1].setValue('123456')

      await wrapper.find('button.login-btn').trigger('click')
      await flushPromises()

      expect(mocks.login).toHaveBeenCalledWith({ username: 'admin', password: '123456' })
      expect(ElMessage.success).toHaveBeenCalledWith('登录成功')
      expect(mocks.routerPush).toHaveBeenCalledWith('/dashboard')
    })
  })

  describe('login failure', () => {
    it('does not redirect and resets the loading state when login rejects', async () => {
      mocks.login.mockRejectedValue(new Error('invalid credentials'))
      const wrapper = mountLogin(true)

      const inputs = wrapper.findAll('input')
      await inputs[0].setValue('admin')
      await inputs[1].setValue('wrong-password')

      await wrapper.find('button.login-btn').trigger('click')
      await flushPromises()

      expect(mocks.login).toHaveBeenCalled()
      // No redirect on failure (error message is shown by the request interceptor)
      expect(mocks.routerPush).not.toHaveBeenCalled()
      // Loading flag should be reset to false
      const button = wrapper.findComponent({ name: 'ElButton' })
      expect(button.props('loading')).toBe(false)
    })
  })
})
