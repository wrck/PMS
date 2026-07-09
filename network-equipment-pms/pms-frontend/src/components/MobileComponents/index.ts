/**
 * 移动端组件统一导出（批次4-T10）。
 *
 * <p>聚合 B4-T10 新增的 4 个移动端组件，便于按需引入：
 * <ul>
 *   <li>BottomSheet：底部弹出层（菜单/表单/详情）</li>
 *   <li>MobileDrawer：侧边抽屉（导航/筛选）</li>
 *   <li>SwipeActions：左滑操作（列表项快捷操作）</li>
 *   <li>MobileScanner：扫码（摄像头二维码/条码扫描）</li>
 * </ul>
 * </p>
 *
 * <p>使用方式：
 * <pre>
 * import { BottomSheet, MobileDrawer, SwipeActions, MobileScanner } from '@/components/MobileComponents'
 * </pre>
 * </p>
 */
export { default as BottomSheet } from './BottomSheet.vue'
export { default as MobileDrawer } from './MobileDrawer.vue'
export { default as SwipeActions } from './SwipeActions.vue'
export { default as MobileScanner } from './MobileScanner.vue'
export type { SwipeAction } from './SwipeActions.vue'
