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
  // sortablejs 预构建，避免新增依赖后 dev 服务器报 "Failed to resolve import"
  optimizeDeps: {
    include: [
      'monaco-editor/esm/vs/editor/editor.worker',
      'sortablejs'
    ]
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
