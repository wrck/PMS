import { a as put, f as axios, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
import { n as triggerBlobDownload } from "./excel-BtLU3Vmp.js";
//#region src/api/lowcode.ts
/** 字段类型常量（与后端 FormConfigSchema 保持一致） */
var FieldType = {
	INPUT: "input",
	TEXTAREA: "textarea",
	NUMBER: "number",
	PASSWORD: "password",
	SELECT: "select",
	RADIO: "radio",
	CHECKBOX: "checkbox",
	DATE: "date",
	DATETIME: "datetime",
	DATERANGE: "daterange",
	SWITCH: "switch",
	RATE: "rate",
	SLIDER: "slider",
	CASCADER: "cascader",
	UPLOAD: "upload",
	DIVIDER: "divider",
	TITLE: "title",
	CUSTOM: "custom"
};
/** 布局类型常量 */
var LayoutType = {
	GRID: "grid",
	TABS: "tabs",
	COLLAPSE: "collapse"
};
/** 分页查询表单配置（后端参数为 current/size） */
function listForms(query) {
	const { page, size, ...rest } = query;
	return get("/api/lowcode/form", {
		current: page !== null && page !== void 0 ? page : 1,
		size: size !== null && size !== void 0 ? size : 10,
		...rest
	});
}
/** 根据 ID 查询表单配置 */
function getForm(id) {
	return get(`/api/lowcode/form/${id}`);
}
/** 根据编码查询已发布（PUBLISHED）的表单配置 */
function getFormByCode(code) {
	return get(`/api/lowcode/form/code/${code}`);
}
/** 创建表单配置 */
function createForm(data) {
	return post("/api/lowcode/form", data);
}
/** 更新表单配置 */
function updateForm(id, data) {
	return put(`/api/lowcode/form/${id}`, data);
}
/** 删除表单配置 */
function deleteForm(id) {
	return del(`/api/lowcode/form/${id}`);
}
/** 发布表单配置：DRAFT → PUBLISHED */
function publishForm(id) {
	return post(`/api/lowcode/form/${id}/publish`);
}
/** 归档表单配置：PUBLISHED → ARCHIVED */
function archiveForm(id) {
	return post(`/api/lowcode/form/${id}/archive`);
}
/**
* 导出指定编码的表单配置为 JSON 文件并触发浏览器下载。
*
* <p>此接口返回二进制流（非统一 envelope），故绕过统一的 axios 拦截器，
* 直接使用原始 axios 注入 token 并以 blob 形式接收，然后触发下载。</p>
*
* @param code     表单编码
* @param fileName 下载文件名（可选，默认 form-{code}.json）
*/
async function exportForm(code, fileName) {
	const token = localStorage.getItem("pms_token") || "";
	triggerBlobDownload((await axios.get(`/api/lowcode/form/${code}/export`, {
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	})).data, fileName !== null && fileName !== void 0 ? fileName : `form-${code}.json`);
}
/**
* 从 JSON 字符串导入表单配置。
* 若 code 冲突，后端会自动追加数字后缀。
*
* <p>后端 {@code @RequestBody String json} 直接接收原始 JSON 字符串，
* 因此将 string 转为 unknown 再到 object 以满足 axios 的类型签名，
* 实际请求体仍是原始字符串。</p>
*/
function importForm(json) {
	return post("/api/lowcode/form/import", json, { headers: { "Content-Type": "application/json" } });
}
/** 列类型常量（与后端 ListConfigSchema 保持一致） */
var ColumnType = {
	TEXT: "text",
	IMAGE: "image",
	TAG: "tag",
	DATE: "date",
	DATETIME: "datetime",
	CURRENCY: "currency",
	PERCENT: "percent",
	LINK: "link",
	DICT: "dict",
	CUSTOM: "custom"
};
/** 筛选类型常量 */
var FilterType = {
	INPUT: "input",
	SELECT: "select",
	DATE: "date",
	DATERANGE: "daterange",
	CASCADER: "cascader"
};
/** 动作类型常量 */
var ActionType = {
	CREATE: "create",
	EDIT: "edit",
	VIEW: "view",
	DELETE: "delete",
	CUSTOM: "custom"
};
/** 按钮类型常量（与 Element Plus 一致） */
var ButtonType = {
	PRIMARY: "primary",
	SUCCESS: "success",
	WARNING: "warning",
	DANGER: "danger",
	INFO: "info",
	TEXT: "text"
};
/** 列表布局常量 */
var ListLayout = {
	TABLE: "table",
	CARD: "card"
};
/** 分页查询列表配置（后端参数为 current/size） */
function listLists(query) {
	const { page, size, ...rest } = query;
	return get("/api/lowcode/list", {
		current: page !== null && page !== void 0 ? page : 1,
		size: size !== null && size !== void 0 ? size : 10,
		...rest
	});
}
/** 根据 ID 查询列表配置 */
function getList(id) {
	return get(`/api/lowcode/list/${id}`);
}
/** 根据编码查询已发布（PUBLISHED）的列表配置 */
function getListByCode(code) {
	return get(`/api/lowcode/list/code/${code}`);
}
/** 创建列表配置 */
function createList(data) {
	return post("/api/lowcode/list", data);
}
/** 更新列表配置 */
function updateList(id, data) {
	return put(`/api/lowcode/list/${id}`, data);
}
/** 删除列表配置 */
function deleteList(id) {
	return del(`/api/lowcode/list/${id}`);
}
/** 发布列表配置：DRAFT → PUBLISHED */
function publishList(id) {
	return post(`/api/lowcode/list/${id}/publish`);
}
/** 归档列表配置：PUBLISHED → ARCHIVED */
function archiveList(id) {
	return post(`/api/lowcode/list/${id}/archive`);
}
/**
* 导出指定编码的列表配置为 JSON 文件并触发浏览器下载。
*
* <p>此接口返回二进制流（非统一 envelope），故绕过统一的 axios 拦截器，
* 直接使用原始 axios 注入 token 并以 blob 形式接收，然后触发下载。</p>
*
* @param code     列表编码
* @param fileName 下载文件名（可选，默认 list-{code}.json）
*/
async function exportList(code, fileName) {
	const token = localStorage.getItem("pms_token") || "";
	triggerBlobDownload((await axios.get(`/api/lowcode/list/${code}/export`, {
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	})).data, fileName !== null && fileName !== void 0 ? fileName : `list-${code}.json`);
}
/**
* 从 JSON 字符串导入列表配置。
* 若 code 冲突，后端会自动追加数字后缀。
*
* <p>后端 {@code @RequestBody String json} 直接接收原始 JSON 字符串，
* 因此将 string 转为 unknown 再到 object 以满足 axios 的类型签名，
* 实际请求体仍是原始字符串。</p>
*/
function importList(json) {
	return post("/api/lowcode/list/import", json, { headers: { "Content-Type": "application/json" } });
}
/** 页面类型常量（与后端 TabConfigSchema 保持一致） */
var TabPageType = {
	FORM: "form",
	LIST: "list",
	RELATED_PAGE: "related-page",
	CUSTOM: "custom"
};
/** el-tabs type 常量 */
var TabsType = {
	CARD: "card",
	BORDER_CARD: "border-card",
	PLAIN: "plain"
};
/** 标签位置常量 */
var TabPosition = {
	TOP: "top",
	RIGHT: "right",
	BOTTOM: "bottom",
	LEFT: "left"
};
/** 分页查询标签页配置（后端参数为 current/size） */
function listTabs(query) {
	const { page, size, ...rest } = query;
	return get("/api/lowcode/tab", {
		current: page !== null && page !== void 0 ? page : 1,
		size: size !== null && size !== void 0 ? size : 10,
		...rest
	});
}
/** 根据 ID 查询标签页配置 */
function getTab(id) {
	return get(`/api/lowcode/tab/${id}`);
}
/** 根据编码查询已发布（PUBLISHED）的标签页配置 */
function getTabByCode(code) {
	return get(`/api/lowcode/tab/code/${code}`);
}
/** 创建标签页配置 */
function createTab(data) {
	return post("/api/lowcode/tab", data);
}
/** 更新标签页配置 */
function updateTab(id, data) {
	return put(`/api/lowcode/tab/${id}`, data);
}
/** 删除标签页配置 */
function deleteTab(id) {
	return del(`/api/lowcode/tab/${id}`);
}
/** 发布标签页配置：DRAFT → PUBLISHED */
function publishTab(id) {
	return post(`/api/lowcode/tab/${id}/publish`);
}
/** 归档标签页配置：PUBLISHED → ARCHIVED */
function archiveTab(id) {
	return post(`/api/lowcode/tab/${id}/archive`);
}
/**
* 导出指定编码的标签页配置为 JSON 文件并触发浏览器下载。
*
* <p>此接口返回二进制流（非统一 envelope），故绕过统一的 axios 拦截器，
* 直接使用原始 axios 注入 token 并以 blob 形式接收，然后触发下载。</p>
*
* @param code     标签页编码
* @param fileName 下载文件名（可选，默认 tab-{code}.json）
*/
async function exportTab(code, fileName) {
	const token = localStorage.getItem("pms_token") || "";
	triggerBlobDownload((await axios.get(`/api/lowcode/tab/${code}/export`, {
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	})).data, fileName !== null && fileName !== void 0 ? fileName : `tab-${code}.json`);
}
/**
* 从 JSON 字符串导入标签页配置。
* 若 code 冲突，后端会自动追加数字后缀。
*/
function importTab(json) {
	return post("/api/lowcode/tab/import", json, { headers: { "Content-Type": "application/json" } });
}
/** 区块类型常量（与后端 RelatedPageConfigSchema 保持一致） */
var SectionType = {
	FORM: "form",
	LIST: "list",
	TAB: "tab",
	CUSTOM: "custom"
};
/** 关联页布局类型常量 */
var RelatedPageLayout = {
	GRID: "grid",
	TABS: "tabs",
	COLLAPSE: "collapse"
};
/** 分页查询关联页配置（后端参数为 current/size） */
function listRelatedPages(query) {
	const { page, size, ...rest } = query;
	return get("/api/lowcode/related-page", {
		current: page !== null && page !== void 0 ? page : 1,
		size: size !== null && size !== void 0 ? size : 10,
		...rest
	});
}
/** 根据 ID 查询关联页配置 */
function getRelatedPage(id) {
	return get(`/api/lowcode/related-page/${id}`);
}
/** 根据编码查询已发布（PUBLISHED）的关联页配置 */
function getRelatedPageByCode(code) {
	return get(`/api/lowcode/related-page/code/${code}`);
}
/** 创建关联页配置 */
function createRelatedPage(data) {
	return post("/api/lowcode/related-page", data);
}
/** 更新关联页配置 */
function updateRelatedPage(id, data) {
	return put(`/api/lowcode/related-page/${id}`, data);
}
/** 删除关联页配置 */
function deleteRelatedPage(id) {
	return del(`/api/lowcode/related-page/${id}`);
}
/** 发布关联页配置：DRAFT → PUBLISHED */
function publishRelatedPage(id) {
	return post(`/api/lowcode/related-page/${id}/publish`);
}
/** 归档关联页配置：PUBLISHED → ARCHIVED */
function archiveRelatedPage(id) {
	return post(`/api/lowcode/related-page/${id}/archive`);
}
/**
* 导出指定编码的关联页配置为 JSON 文件并触发浏览器下载。
*
* <p>此接口返回二进制流（非统一 envelope），故绕过统一的 axios 拦截器，
* 直接使用原始 axios 注入 token 并以 blob 形式接收，然后触发下载。</p>
*
* @param code     关联页编码
* @param fileName 下载文件名（可选，默认 related-page-{code}.json）
*/
async function exportRelatedPage(code, fileName) {
	const token = localStorage.getItem("pms_token") || "";
	triggerBlobDownload((await axios.get(`/api/lowcode/related-page/${code}/export`, {
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	})).data, fileName !== null && fileName !== void 0 ? fileName : `related-page-${code}.json`);
}
/**
* 从 JSON 字符串导入关联页配置。
* 若 code 冲突，后端会自动追加数字后缀。
*/
function importRelatedPage(json) {
	return post("/api/lowcode/related-page/import", json, { headers: { "Content-Type": "application/json" } });
}
/**
* 校验当前用户是否有权访问指定低代码页面。
*
* <p>渲染入口在 onMounted 中调用此接口，无权限时显示提示并阻止渲染。</p>
*
* @param pageType 页面类型 form/list/tab/related-page
* @param pageCode 低代码配置编码
*/
function checkLowCodePermission(pageType, pageCode) {
	return get("/api/lowcode/permission/check", {
		pageType,
		pageCode
	});
}
//#endregion
export { updateTab as $, getForm as A, importRelatedPage as B, deleteList as C, exportList as D, exportForm as E, getRelatedPageByCode as F, listTabs as G, listForms as H, getTab as I, publishRelatedPage as J, publishForm as K, getTabByCode as L, getList as M, getListByCode as N, exportRelatedPage as O, getRelatedPage as P, updateRelatedPage as Q, importForm as R, deleteForm as S, deleteTab as T, listLists as U, importTab as V, listRelatedPages as W, updateForm as X, publishTab as Y, updateList as Z, checkLowCodePermission as _, FilterType as a, createRelatedPage as b, RelatedPageLayout as c, TabPosition as d, TabsType as f, archiveTab as g, archiveRelatedPage as h, FieldType as i, getFormByCode as j, exportTab as k, SectionType as l, archiveList as m, ButtonType as n, LayoutType as o, archiveForm as p, publishList as q, ColumnType as r, ListLayout as s, ActionType as t, TabPageType as u, createForm as v, deleteRelatedPage as w, createTab as x, createList as y, importList as z };

//# sourceMappingURL=lowcode-F-suzo7c.js.map