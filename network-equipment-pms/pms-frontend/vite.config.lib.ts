/**
 * Vite Library 模式打包配置（批次4-T6 组件 SDK 独立打包）。
 *
 * <p>用途：将 src/sdk/ 目录打包为独立可发布的组件 SDK 库，借鉴 Power Apps PCF 的
 * 独立组件包与 ToolJet Component SDK 的 npm 分发模式。</p>
 *
 * <p>产物：
 * <ul>
 *   <li>dist-sdk/pms-lowcode-sdk.es.js — ES Module 格式（推荐）</li>
 *   <li>dist-sdk/pms-lowcode-sdk.umd.js — UMD 格式（兼容 CDN script 标签）</li>
 *   <li>dist-sdk/pms-lowcode-sdk.d.ts — 类型声明（由 vite-plugin-dts 生成）</li>
 * </ul>
 * </p>
 *
 * <p>外部依赖：vue / element-plus 不打包进 SDK，由宿主应用提供（peerDependencies），
 * 避免重复加载与版本冲突。</p>
 *
 * <p>使用方式：
 * <pre>
 * # 构建命令（在 pms-frontend 目录）
 * npx vite build --config vite.config.lib.ts
 * # 或在 package.json 中配置 scripts:
 * # "build:sdk": "vite build --config vite.config.lib.ts"
 * </pre>
 * </p>
 */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    // 产物输出目录（与主应用 dist 隔离，避免冲突）
    outDir: 'dist-sdk',
    // 清空旧产物
    emptyOutDir: true,
    // 关闭 CSS 代码分割，CSS 内联到 JS（lib 模式推荐）
    cssCodeSplit: false,
    // sourcemap 便于调试
    sourcemap: true,
    // 库模式核心配置
    lib: {
      // SDK 入口
      entry: path.resolve(__dirname, 'src/sdk/index.ts'),
      // 产物名（UMD 全局变量名）
      name: 'PmsLowCodeSDK',
      // 产物文件名模板
      fileName: (format) => `pms-lowcode-sdk.${format}.js`,
      // 输出格式：ES + UMD（兼容现代打包与 CDN script 标签）
      formats: ['es', 'umd']
    },
    rollupOptions: {
      // 外部化 peer dependencies，不打包进 SDK
      external: [
        'vue',
        'element-plus',
        '@element-plus/icons-vue',
        'monaco-editor',
        // @/ 别名解析后的内部模块（ LowCodeComponentRegistry 等）需打包进 SDK
        // 此处仅排除真正的 npm 包依赖
      ],
      output: {
        // UMD 模式下提供全局变量名映射
        globals: {
          vue: 'Vue',
          'element-plus': 'ElementPlus',
          '@element-plus/icons-vue': 'ElementPlusIconsVue',
          'monaco-editor': 'monaco'
        },
        // 产物命名（lib.fileName 已控制 JS，此处补充 CSS/资源）
        assetFileNames: (assetInfo) => {
          if (assetInfo.name && assetInfo.name.endsWith('.css')) {
            return 'pms-lowcode-sdk.css'
          }
          return 'assets/[name][extname]'
        }
      }
    },
    // 库模式下 minify 会增加体积，开发阶段关闭，发布时手动开启
    minify: false,
    target: 'es2018'
  }
})
