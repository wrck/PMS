import { f as axios } from "./request-BQrAOfxW.js";
//#region src/api/excel.ts
var TOKEN_KEY = "pms_token";
/**
* 通用的 Excel 文件下载函数：以 blob 形式请求并通过浏览器 a 标签触发下载。
*
* @param url     下载地址（相对路径，例如 /api/asset/template）
* @param params  查询参数（可选）
* @param fileName 下载到本地的文件名（含扩展名，例如 asset-template.xlsx）
*/
async function downloadExcel(url, params, fileName) {
	const token = localStorage.getItem(TOKEN_KEY) || "";
	const response = await axios.get(url, {
		params,
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	});
	triggerBlobDownload(response.data, fileName !== null && fileName !== void 0 ? fileName : extractFileNameFromResponse(response.headers, url));
}
/**
* 通用的 Excel 上传导入函数：以 FormData 形式上传文件并返回后端聚合的导入结果。
*
* @param url   上传地址（例如 /api/asset/import）
* @param file  上传的 .xlsx 文件
*/
async function uploadExcel(url, file) {
	const token = localStorage.getItem(TOKEN_KEY) || "";
	const formData = new FormData();
	formData.append("file", file);
	const payload = (await axios.post(url, formData, { headers: {
		Authorization: `Bearer ${token}`,
		"Content-Type": "multipart/form-data"
	} })).data;
	if (payload && typeof payload === "object" && "data" in payload && payload.data) return payload.data;
	return payload;
}
/**
* 触发浏览器下载一个 Blob。
*
* @param blob     待下载的 Blob 数据
* @param fileName 文件名（含扩展名）
*/
function triggerBlobDownload(blob, fileName) {
	const url = URL.createObjectURL(blob);
	const link = document.createElement("a");
	link.href = url;
	link.download = fileName;
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
	setTimeout(() => URL.revokeObjectURL(url), 0);
}
/**
* 从响应头 Content-Disposition 中提取文件名，失败时回退到 URL 的最后一段。
*
* @param headers 响应头
* @param url     请求地址（用于回退）
*/
function extractFileNameFromResponse(headers, url) {
	var _headers$contentDisp, _url$split$filter$pop;
	const raw = (_headers$contentDisp = headers === null || headers === void 0 ? void 0 : headers["content-disposition"]) !== null && _headers$contentDisp !== void 0 ? _headers$contentDisp : headers === null || headers === void 0 ? void 0 : headers["Content-Disposition"];
	const disposition = typeof raw === "string" ? raw : "";
	if (disposition) {
		const match = /filename\*?=([^;]+)/i.exec(disposition);
		if (match && match[1]) {
			let name = match[1].trim().replace(/^["']|["']$/g, "");
			if (name.startsWith("UTF-8''") || name.startsWith("utf-8''")) name = decodeURIComponent(name.split("''")[1]);
			return name;
		}
	}
	const seg = (_url$split$filter$pop = url.split("/").filter(Boolean).pop()) !== null && _url$split$filter$pop !== void 0 ? _url$split$filter$pop : "download";
	return seg.endsWith(".xlsx") ? seg : `${seg}.xlsx`;
}
//#endregion
export { triggerBlobDownload as n, uploadExcel as r, downloadExcel as t };

//# sourceMappingURL=excel-BtLU3Vmp.js.map