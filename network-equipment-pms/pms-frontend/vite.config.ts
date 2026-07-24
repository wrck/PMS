import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // monaco-editor worker 预打包，避免 dev 模式下 worker 加载报错
  optimizeDeps: {
    include: ['monaco-editor/esm/vs/editor/editor.worker']
  },
  server: {
    port: 5000,
    proxy: {
      // yudao 底座原生接口（/admin-api/system/*、/admin-api/infra/*）
      '/admin-api': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      // PMS 业务接口
      '/api': {
        target: 'http://localhost:9080',
        changeOrigin: true
      }
    }
  }
})
