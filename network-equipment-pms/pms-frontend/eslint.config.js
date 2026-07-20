/**
 * ESLint flat config —— TypeScript + Vue 3 项目统一规则。
 *
 * <p>核心规则：</p>
 * <ul>
 *   <li>{@code @typescript-eslint/no-explicit-any: warn} —— 禁止显式 any，已存在的
 *       动态表单数据等场景使用 {@code eslint-disable-next-line} 行内豁免。</li>
 *   <li>{@code @typescript-eslint/no-unused-vars: warn} —— 未使用变量告警，
 *       下划线前缀参数（_r / _e 等）豁免。</li>
 *   <li>{@code no-console: warn} —— 生产代码避免遗留 console。</li>
 * </ul>
 */
import js from '@eslint/js'
import tseslint from 'typescript-eslint'
import pluginVue from 'eslint-plugin-vue'
import vueParser from 'vue-eslint-parser'

export default tseslint.config(
  // ============ 基础推荐 ============
  js.configs.recommended,
  ...tseslint.configs.recommended,
  ...pluginVue.configs['flat/recommended'],

  // ============ Vue SFC 解析：模板内联 TS 表达式 ============
  {
    files: ['**/*.vue'],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        parser: tseslint.parser,
        extraFileExtensions: ['.vue'],
        sourceType: 'module'
      }
    }
  },

  // ============ TS / Vue 文件通用规则 ============
  {
    files: ['**/*.{ts,tsx,vue}'],
    rules: {
      // 禁止显式 any —— warn 级别，允许 eslint-disable 行内豁免
      '@typescript-eslint/no-explicit-any': 'warn',
      // 未使用变量告警；下划线前缀参数豁免（如 _r / _e）
      '@typescript-eslint/no-unused-vars': [
        'warn',
        { argsIgnorePattern: '^_', varsIgnorePattern: '^_', caughtErrorsIgnorePattern: '^_' }
      ],
      // 生产代码避免遗留 console
      'no-console': ['warn', { allow: ['warn', 'error'] }],
      // Vue 模板属性多行时强制每行一个属性（保持一致性）
      'vue/max-attributes-per-line': 'off',
      // 允许单词组件名（如 index.vue）
      'vue/multi-word-component-names': 'off'
    }
  },

  // ============ 测试文件放宽 ============
  {
    files: ['**/__tests__/**/*.{ts,tsx}', '**/*.test.{ts,tsx}', '**/*.spec.{ts,tsx}'],
    rules: {
      '@typescript-eslint/no-explicit-any': 'off',
      'no-console': 'off'
    }
  },

  // ============ 忽略路径 ============
  {
    ignores: ['dist/**', 'dist-sdk/**', 'node_modules/**', 'coverage/**', '*.config.{js,ts}']
  }
)
