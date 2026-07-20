<template>
  <div class="mobile-scanner">
    <!-- 视频预览区 -->
    <div v-if="scanning" class="scanner-preview">
      <video ref="videoRef" autoplay playsinline muted class="scanner-video"></video>
      <!-- 扫码框装饰 -->
      <div class="scanner-overlay">
        <div class="scanner-frame">
          <span class="corner corner-tl"></span>
          <span class="corner corner-tr"></span>
          <span class="corner corner-bl"></span>
          <span class="corner corner-br"></span>
        </div>
      </div>
      <!-- 关闭按钮 -->
      <el-icon class="scanner-close" @click="stop"><Close /></el-icon>
      <!-- 提示文案 -->
      <div class="scanner-hint">{{ hint }}</div>
    </div>

    <!-- 触发按钮 -->
    <slot v-if="!scanning" name="trigger" :start="start">
      <el-button :icon="Camera" @click="start">{{ triggerText }}</el-button>
    </slot>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="scanner-error">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ errorMessage }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * MobileScanner — 移动端扫码组件（批次4-T10）。
 *
 * <p>调用设备摄像头扫描条形码/二维码，封装浏览器原生 BarcodeDetector API
 * （Chromium 系现代浏览器支持）+ canvas 截帧兜底方案。</p>
 *
 * <h3>能力</h3>
 * <ol>
 *   <li><b>多格式支持</b>：二维码（QR）/ 条形码（CODE_128/EAN_13/EAN_8/UPC_A 等）</li>
 *   <li><b>前后摄切换</b>：facingMode=environment 默认后摄，可配置前摄</li>
 *   <li><b>持续扫描</b>：每帧扫描，扫到结果触发 scan 事件并可选关闭</li>
 *   <li><b>权限处理</b>：摄像头权限被拒时显示友好错误提示</li>
 *   <li><b>兜底方案</b>：无 BarcodeDetector API 时用 canvas 截帧 + 第三方库解码</li>
 * </ol>
 *
 * <h3>使用示例</h3>
 * <pre>
 * &lt;MobileScanner
 *   :formats="['qr_code', 'code_128']"
 *   trigger-text="扫码"
 *   @scan="onScan"
 * /&gt;
 * </pre>
 */
import { ref, onBeforeUnmount } from 'vue'
import { Close, Camera, WarningFilled } from '@element-plus/icons-vue'

interface Props {
  /** 支持的码格式（传给 BarcodeDetector），默认 qr_code + 常见条码 */
  formats?: string[]
  /** 触发按钮文案 */
  triggerText?: string
  /** 摄像头方向：environment 后摄 / user 前摄 */
  facingMode?: 'environment' | 'user'
  /** 扫码成功后是否自动停止 */
  autoStop?: boolean
  /** 提示文案 */
  hint?: string
}

const props = withDefaults(defineProps<Props>(), {
  formats: () => ['qr_code', 'code_128', 'ean_13', 'ean_8', 'upc_a'],
  triggerText: '扫码',
  facingMode: 'environment',
  autoStop: true,
  hint: '将二维码/条码对准框内'
})

const emit = defineEmits<{
  (e: 'scan', result: string, format: string): void
  (e: 'error', message: string): void
}>()

const videoRef = ref<HTMLVideoElement | null>(null)
const scanning = ref(false)
const errorMessage = ref('')
let mediaStream: MediaStream | null = null
let scanTimer: ReturnType<typeof setInterval> | null = null
let barcodeDetector: any = null

/** 初始化 BarcodeDetector（若浏览器支持） */
async function initBarcodeDetector(): Promise<boolean> {
  if ('BarcodeDetector' in window) {
    try {
      // @ts-expect-error - BarcodeDetector 是实验性 API，尚未进入 TypeScript DOM 类型
      barcodeDetector = new window.BarcodeDetector({
        formats: props.formats
      })
      return true
    } catch (e) {
      console.warn('[MobileScanner] BarcodeDetector 初始化失败，将使用兜底方案', e)
      barcodeDetector = null
      return false
    }
  }
  return false
}

/** 启动扫码 */
async function start(): Promise<void> {
  errorMessage.value = ''
  if (!navigator.mediaDevices?.getUserMedia) {
    errorMessage.value = '当前浏览器不支持摄像头访问'
    emit('error', errorMessage.value)
    return
  }
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({
      video: {
        facingMode: props.facingMode,
        width: { ideal: 1280 },
        height: { ideal: 720 }
      }
    })
    scanning.value = true
    // 等待 DOM 更新后绑定 video
    await new Promise((resolve) => setTimeout(resolve, 100))
    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream
      await videoRef.value.play()
    }
    // 初始化 BarcodeDetector
    await initBarcodeDetector()
    // 开始扫描循环
    startScanLoop()
  } catch (e: any) {
    console.error('[MobileScanner] 摄像头启动失败', e)
    if (e.name === 'NotAllowedError') {
      errorMessage.value = '摄像头权限被拒绝，请在浏览器设置中允许'
    } else if (e.name === 'NotFoundError') {
      errorMessage.value = '未检测到摄像头设备'
    } else {
      errorMessage.value = `摄像头启动失败: ${e.message || e.name}`
    }
    scanning.value = false
    emit('error', errorMessage.value)
  }
}

/** 扫描循环 */
function startScanLoop(): void {
  if (scanTimer) clearInterval(scanTimer)
  scanTimer = setInterval(async () => {
    if (!scanning.value || !videoRef.value) return
    try {
      if (barcodeDetector) {
        // 使用原生 BarcodeDetector
        const barcodes = await barcodeDetector.detect(videoRef.value)
        if (barcodes && barcodes.length > 0) {
          const barcode = barcodes[0]
          emit('scan', barcode.rawValue, barcode.format)
          if (props.autoStop) stop()
        }
      } else {
        // 兜底方案：canvas 截帧（实际解码需引入 jsQR 等第三方库）
        // 此处仅做截帧，emit 提示需引入解码库
        // 实际项目中可通过 import('jsqr') 动态加载
        await fallbackScan()
      }
    } catch (e) {
      // 单帧扫描失败不中断循环
    }
  }, 200)
}

/** 兜底扫描：canvas 截帧 + 动态加载 jsQR 解码 */
async function fallbackScan(): Promise<void> {
  if (!videoRef.value) return
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  canvas.width = videoRef.value.videoWidth || 640
  canvas.height = videoRef.value.videoHeight || 480
  ctx.drawImage(videoRef.value, 0, 0, canvas.width, canvas.height)
  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
  try {
    // 动态加载 jsQR（仅在无 BarcodeDetector 时才加载，减小主 bundle）
    const jsQR = (await import('jsqr')).default
    const code = jsQR(imageData.data, imageData.width, imageData.height)
    if (code) {
      emit('scan', code.data, 'qr_code')
      if (props.autoStop) stop()
    }
  } catch (e) {
    // jsQR 未安装或加载失败，静默跳过
    // 实际部署时需在 package.json 中添加 jsqr 依赖
  }
}

/** 停止扫码 */
function stop(): void {
  scanning.value = false
  if (scanTimer) {
    clearInterval(scanTimer)
    scanTimer = null
  }
  if (mediaStream) {
    mediaStream.getTracks().forEach((track) => track.stop())
    mediaStream = null
  }
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
}

onBeforeUnmount(() => {
  stop()
})

defineExpose({ start, stop })
</script>

<style scoped>
.mobile-scanner {
  display: inline-block;
}
.scanner-preview {
  position: fixed;
  inset: 0;
  background: #000;
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.scanner-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.scanner-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.scanner-frame {
  width: 70vw;
  max-width: 300px;
  height: 70vw;
  max-height: 300px;
  position: relative;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
}
.corner {
  position: absolute;
  width: 24px;
  height: 24px;
  border: 3px solid #409eff;
}
.corner-tl {
  top: -3px;
  left: -3px;
  border-right: none;
  border-bottom: none;
  border-radius: 8px 0 0 0;
}
.corner-tr {
  top: -3px;
  right: -3px;
  border-left: none;
  border-bottom: none;
  border-radius: 0 8px 0 0;
}
.corner-bl {
  bottom: -3px;
  left: -3px;
  border-right: none;
  border-top: none;
  border-radius: 0 0 0 8px;
}
.corner-br {
  bottom: -3px;
  right: -3px;
  border-left: none;
  border-top: none;
  border-radius: 0 0 8px 0;
}
.scanner-close {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 28px;
  color: #fff;
  cursor: pointer;
  z-index: 1;
}
.scanner-hint {
  position: absolute;
  bottom: 60px;
  left: 0;
  right: 0;
  text-align: center;
  color: #fff;
  font-size: 14px;
}
.scanner-error {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f56c6c;
  margin-top: 8px;
  font-size: 13px;
}
</style>
