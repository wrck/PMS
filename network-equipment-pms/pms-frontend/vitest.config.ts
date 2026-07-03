/// <reference types="vitest" />
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// Vitest configuration. Kept separate from vite.config.ts so the dev-server
// proxy / build pipeline is not affected by test-only options.
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    include: ['src/**/__tests__/**/*.test.ts', 'e2e/**/*.test.ts'],
    coverage: {
      provider: 'v8'
    }
  }
})
