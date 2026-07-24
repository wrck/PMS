/**
 * yudao 下载工具适配层。
 *
 * <p>提供 yudao 原生页面所需的 {@code download.excel / download.zip / download.html / download.doc} 等。</p>
 *
 * <p>对接 pms-frontend 的 axios 实例，请求时设置 {@code responseType: 'blob'}，
 * 响应结果直接作为 Blob 处理。</p>
 */
import service from '@/utils/request'

/**
 * 通用下载：从指定 URL 下载文件，触发浏览器保存。
 *
 * @param url 请求 URL（完整路径，如 '/admin-api/system/user/export-excel'）
 * @param params 查询参数
 * @returns Blob 数据
 */
export async function downloadBlob(
  url: string,
  params?: Record<string, unknown>
): Promise<Blob> {
  const res = await service.get<Blob>(url, {
    params,
    responseType: 'blob'
  })
  return res as unknown as Blob
}

/**
 * 触发浏览器下载文件。
 *
 * @param blob Blob 数据
 * @param fileName 文件名（含扩展名）
 */
export function saveAs(blob: Blob, fileName: string): void {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/**
 * 下载 Excel 文件。
 *
 * @param url 请求 URL
 * @param params 查询参数
 * @param fileName 文件名（含扩展名，如 '用户列表.xls'）
 */
export async function excel(
  url: string,
  params: Record<string, unknown>,
  fileName: string
): Promise<void> {
  const blob = await downloadBlob(url, params)
  saveAs(blob, fileName)
}

/**
 * 下载 ZIP 文件。
 *
 * @param url 请求 URL
 * @param params 查询参数
 * @param fileName 文件名（含扩展名，如 '代码.zip'）
 */
export async function zip(
  url: string,
  params: Record<string, unknown>,
  fileName: string
): Promise<void> {
  const blob = await downloadBlob(url, params)
  saveAs(blob, fileName)
}

/**
 * 下载 HTML 文件。
 *
 * @param url 请求 URL
 * @param params 查询参数
 * @param fileName 文件名（含扩展名）
 */
export async function html(
  url: string,
  params: Record<string, unknown>,
  fileName: string
): Promise<void> {
  const blob = await downloadBlob(url, params)
  saveAs(blob, fileName)
}

/**
 * 下载 Word 文件。
 *
 * @param url 请求 URL
 * @param params 查询参数
 * @param fileName 文件名（含扩展名）
 */
export async function doc(
  url: string,
  params: Record<string, unknown>,
  fileName: string
): Promise<void> {
  const blob = await downloadBlob(url, params)
  saveAs(blob, fileName)
}

/** 文件大小格式化（字节 → KB/MB/GB） */
export function fileSizeFormatter(bytes: number): string {
  if (bytes === 0 || bytes == null) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`
}

export default { excel, zip, html, doc, saveAs, downloadBlob, fileSizeFormatter }
