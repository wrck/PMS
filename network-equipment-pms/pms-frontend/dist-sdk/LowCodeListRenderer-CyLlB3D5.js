import { d as useRouter, f as axios, m as __exportAll } from "./request-BQrAOfxW.js";
import { n as triggerBlobDownload } from "./excel-BtLU3Vmp.js";
import { a as FilterType, r as ColumnType, t as ActionType } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { d as getDictPage, u as getDictItems } from "./system-CuVYDpvc.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createTextVNode, createVNode, defineComponent, normalizeStyle, onMounted, openBlock, reactive, ref, renderList, renderSlot, resolveComponent, resolveDirective, toDisplayString, unref, watch, withCtx, withDirectives, withKeys } from "vue";
//#region src/components/LowCodeListRenderer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "low-code-list-renderer" };
var _hoisted_2 = {
	key: 1,
	class: "list-toolbar"
};
var _hoisted_3 = { key: 4 };
/** Props 定义 */
var index_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ defineComponent({
	__name: "index",
	props: {
		config: {},
		data: {},
		loading: {
			type: Boolean,
			default: false
		},
		pageSize: { default: 20 },
		dictMap: { default: () => ({}) },
		autoFetch: {
			type: Boolean,
			default: true
		}
	},
	emits: [
		"selection-change",
		"operation-click",
		"toolbar-click",
		"page-change",
		"filter-change",
		"data-loaded"
	],
	setup(__props, { expose: __expose, emit: __emit }) {
		/**
		* 低代码列表渲染引擎。
		*
		* <p>根据传入的 {@link ListConfig} 动态渲染 Element Plus 表格，支持：</p>
		* <ul>
		*   <li>10 种列类型（text/image/tag/date/datetime/currency/percent/link/dict/custom）</li>
		*   <li>5 种筛选类型（input/select/date/daterange/cascader）</li>
		*   <li>行操作按钮（edit/view/delete/custom）+ 显示条件 + 权限指令</li>
		*   <li>工具栏按钮（create/custom）+ 权限指令</li>
		*   <li>分页（el-pagination，可关闭）+ 多选 + 序号 + 排序</li>
		*   <li>字典翻译（dictMap 注入 / 内部按 dictCode 异步加载缓存）</li>
		*   <li>导出（调用 config.export.api）</li>
		*   <li>自动请求（无 data 时按 searchApi 拉取数据）</li>
		* </ul>
		*
		* <p>对外暴露 selection-change / operation-click / page-change / filter-change
		* 事件，方便业务层介入。同时通过 defineExpose 暴露 refresh / getSelection /
		* getFilters / exportData 方法供父组件通过 ref 调用。</p>
		*/
		const props = __props;
		/** Emits 定义 */
		const emit = __emit;
		const router = useRouter();
		/** 内部维护的表格数据（autoFetch=true 时由 searchApi 拉取） */
		const innerData = ref([]);
		/** 内部加载状态 */
		const innerLoading = ref(false);
		/** 内部分页总条数 */
		const innerTotal = ref(0);
		/** 当前页码 */
		const currentPage = ref(1);
		/** 每页条数 */
		const pageSizeRef = ref(props.pageSize || props.config.pageSize || 20);
		/** 当前选中行 */
		const selection = ref([]);
		/**
		* 实际渲染数据：优先使用父组件传入的 data，否则使用内部 innerData。
		*/
		const tableData = computed(() => {
			var _props$data;
			return (_props$data = props.data) !== null && _props$data !== void 0 ? _props$data : innerData.value;
		});
		/** 实际加载状态 */
		const loading = computed(() => props.loading || innerLoading.value);
		/** 实际分页总条数：父组件 data 模式下用 data.length，自动模式下用 innerTotal */
		const total = computed(() => props.data ? props.data.length : innerTotal.value);
		/** 实际使用的 pageSizes */
		const pageSizes = computed(() => {
			var _props$config$pageSiz;
			return (_props$config$pageSiz = props.config.pageSizes) !== null && _props$config$pageSiz !== void 0 ? _props$config$pageSiz : [
				10,
				20,
				50,
				100
			];
		});
		/** 筛选表单数据（按 filter.prop 为 key） */
		const filterForm = reactive({});
		/**
		* 将筛选项的 span 解析为单个宽度数字（1-24），用于内联百分比宽度。
		*
		* <p>向后兼容：span 为数字时直接使用；span 为响应式断点对象时取 md 优先，
		* 依次回退 sm/lg/xs/xl，全缺省时返回 undefined（不设宽度）。</p>
		*/
		function resolveFilterSpan(span) {
			var _ref, _ref2, _ref3, _span$md;
			if (span === void 0) return void 0;
			if (typeof span === "number") return span;
			return (_ref = (_ref2 = (_ref3 = (_span$md = span.md) !== null && _span$md !== void 0 ? _span$md : span.sm) !== null && _ref3 !== void 0 ? _ref3 : span.lg) !== null && _ref2 !== void 0 ? _ref2 : span.xs) !== null && _ref !== void 0 ? _ref : span.xl;
		}
		/** 计算筛选项的内联样式（按 span 设置百分比宽度） */
		function filterStyle(f) {
			const spanNum = resolveFilterSpan(f.span);
			return spanNum ? {
				width: `${spanNum / 24 * 100}%`,
				flex: "0 0 auto"
			} : void 0;
		}
		/** 初始化筛选项默认值 */
		function initFilterDefaults() {
			var _props$config$filters;
			for (const f of (_props$config$filters = props.config.filters) !== null && _props$config$filters !== void 0 ? _props$config$filters : []) if (f.defaultValue !== void 0) filterForm[f.prop] = f.defaultValue;
			else if (f.type === FilterType.DATERANGE) filterForm[f.prop] = [];
			else if (f.type === FilterType.SELECT && f.multiple) filterForm[f.prop] = [];
			else filterForm[f.prop] = "";
		}
		watch(() => props.config.filters, () => initFilterDefaults(), {
			immediate: true,
			deep: true
		});
		/** 查询 */
		function handleSearch() {
			currentPage.value = 1;
			emit("filter-change", { ...filterForm });
			if (props.autoFetch && props.config.searchApi) fetchData();
		}
		/** 重置筛选 */
		function handleReset() {
			var _props$config$filters2;
			for (const f of (_props$config$filters2 = props.config.filters) !== null && _props$config$filters2 !== void 0 ? _props$config$filters2 : []) if (f.defaultValue !== void 0) filterForm[f.prop] = f.defaultValue;
			else if (f.type === FilterType.DATERANGE) filterForm[f.prop] = [];
			else if (f.type === FilterType.SELECT && f.multiple) filterForm[f.prop] = [];
			else filterForm[f.prop] = "";
			handleSearch();
		}
		/** 筛选 select 选项（优先 options，否则从 dictMap 取） */
		function getFilterOptions(f) {
			if (f.options && f.options.length > 0) return f.options;
			if (f.dictCode && props.dictMap[f.dictCode]) return props.dictMap[f.dictCode];
			return [];
		}
		/** 内部字典缓存（按 dictCode 索引） */
		const dictCache = reactive({ ...props.dictMap });
		/** 标记正在加载的 dictCode，避免重复请求 */
		const loadingDictCodes = reactive({});
		/** 需要字典翻译的列 */
		const dictColumns = computed(() => (props.config.columns || []).filter((c) => c.type === ColumnType.DICT && c.dictCode && !c.hidden));
		/** 需要字典翻译的筛选 */
		const dictFilters = computed(() => (props.config.filters || []).filter((f) => f.type === FilterType.SELECT && f.dictCode && (!f.options || f.options.length === 0)));
		/** 加载指定 dictCode 的字典项（异步，带缓存） */
		async function loadDict(dictCode) {
			if (!dictCode || dictCache[dictCode] || loadingDictCodes[dictCode]) return;
			loadingDictCodes[dictCode] = true;
			try {
				const dict = ((await getDictPage({
					keyword: dictCode,
					page: 1,
					size: 50
				})).records || []).find((d) => d.code === dictCode);
				if (!dict || !dict.id) {
					dictCache[dictCode] = [];
					return;
				}
				const items = await getDictItems(dictCode);
				dictCache[dictCode] = items.map((it) => ({
					label: it.label,
					value: it.value
				}));
			} catch (_unused) {
				dictCache[dictCode] = [];
			} finally {
				loadingDictCodes[dictCode] = false;
			}
		}
		/** 字典翻译：根据 dictCode + value 返回 label */
		function translateDict(dictCode, value) {
			if (value === null || value === void 0 || value === "") return "";
			const items = dictCache[dictCode];
			if (!items) return String(value);
			const item = items.find((it) => String(it.value) === String(value));
			return item ? item.label : String(value);
		}
		/** 初始化所有字典（dictMap 已传入的不再加载） */
		function initDicts() {
			for (const col of dictColumns.value) if (col.dictCode) loadDict(col.dictCode);
			for (const f of dictFilters.value) if (f.dictCode) loadDict(f.dictCode);
		}
		watch(() => [props.config.columns, props.config.filters], () => initDicts(), {
			immediate: true,
			deep: true
		});
		/** 可见列（过滤 hidden） */
		const visibleColumns = computed(() => (props.config.columns || []).filter((c) => !c.hidden));
		/** 解析 fixed 属性：字符串 "false" 或布尔 false 均视为不固定 */
		function resolveFixed(fixed) {
			if (fixed === void 0 || fixed === false || fixed === "false") return false;
			return fixed;
		}
		/** 单元格值 */
		function getCellValue(row, col) {
			return row[col.prop];
		}
		/** 格式化日期 */
		function formatDate(val, fmt) {
			if (val === null || val === void 0 || val === "") return "";
			const s = String(val);
			const d = new Date(s);
			if (isNaN(d.getTime())) return s;
			const pad = (n) => String(n).padStart(2, "0");
			const map = {
				YYYY: String(d.getFullYear()),
				MM: pad(d.getMonth() + 1),
				DD: pad(d.getDate()),
				HH: pad(d.getHours()),
				mm: pad(d.getMinutes()),
				ss: pad(d.getSeconds())
			};
			return fmt.replace(/YYYY|MM|DD|HH|mm|ss/g, (m) => map[m]);
		}
		/** 格式化货币：千分位 + 货币符号 */
		function formatCurrency(val, symbol = "¥") {
			if (val === null || val === void 0 || val === "") return "";
			const n = Number(val);
			if (isNaN(n)) return String(val);
			return symbol + n.toLocaleString("zh-CN", {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2
			});
		}
		/** 格式化百分比：值 × 100 + % */
		function formatPercent(val, decimals = 2) {
			if (val === null || val === void 0 || val === "") return "";
			const n = Number(val);
			if (isNaN(n)) return String(val);
			return (n * 100).toFixed(decimals) + "%";
		}
		/** 解析 formatter 字符串：返回 { kind, args } */
		function parseFormatter(formatter) {
			if (!formatter) return null;
			const idx = formatter.indexOf(":");
			if (idx < 0) return {
				kind: formatter,
				args: ""
			};
			return {
				kind: formatter.slice(0, idx),
				args: formatter.slice(idx + 1)
			};
		}
		/** 综合渲染单元格内容（用于非 custom 列） */
		function renderCell(row, col) {
			const val = getCellValue(row, col);
			switch (col.type || ColumnType.TEXT) {
				case ColumnType.DATE: return formatDate(val, "YYYY-MM-DD");
				case ColumnType.DATETIME: return formatDate(val, "YYYY-MM-DD HH:mm:ss");
				case ColumnType.CURRENCY: return formatCurrency(val);
				case ColumnType.PERCENT: return formatPercent(val);
				case ColumnType.DICT: return col.dictCode ? translateDict(col.dictCode, val) : String(val !== null && val !== void 0 ? val : "");
				default: return val === null || val === void 0 ? "" : String(val);
			}
		}
		/** 处理 formatter 覆盖（如 dateFormat:YYYY-MM/DD） */
		function renderWithFormatter(row, col) {
			const fmt = parseFormatter(col.formatter);
			if (!fmt) return renderCell(row, col);
			const val = getCellValue(row, col);
			switch (fmt.kind) {
				case "dateFormat": return formatDate(val, fmt.args || "YYYY-MM-DD");
				case "datetimeFormat": return formatDate(val, fmt.args || "YYYY-MM-DD HH:mm:ss");
				case "currency": return formatCurrency(val, fmt.args || "¥");
				case "percent": return formatPercent(val, fmt.args ? Number(fmt.args) : 2);
				default: return renderCell(row, col);
			}
		}
		/** 链接跳转：替换 {prop} 占位符 */
		function resolveLinkUrl(url, row) {
			return url.replace(/\{(\w+)\}/g, (_m, key) => {
				var _row$key;
				return String((_row$key = row[key]) !== null && _row$key !== void 0 ? _row$key : "");
			});
		}
		/** 处理链接点击 */
		function handleLinkClick(col, row) {
			if (!col.linkUrl) return;
			const url = resolveLinkUrl(col.linkUrl, row);
			if (url.startsWith("http")) window.location.href = url;
			else router.push(url);
		}
		/** 操作列宽度估算：每按钮约 60px + padding */
		const operationsWidth = computed(() => {
			const ops = visibleRowOps.value;
			if (!ops.length) return 0;
			return Math.max(ops.length * 70 + 20, 100);
		});
		/** 可见行操作（不考虑 row 维度 visible） */
		const visibleRowOps = computed(() => props.config.operations || []);
		/** 计算行维度可见操作（visible 表达式求值） */
		function getVisibleRowOps(row) {
			return visibleRowOps.value.filter((op) => {
				if (!op.visible) return true;
				try {
					return !!new Function("row", `return (${op.visible})`)(row);
				} catch (_unused2) {
					return true;
				}
			});
		}
		/** 行操作点击处理 */
		async function handleRowClick(op, row) {
			if (op.confirm) try {
				await ElMessageBox.confirm(op.confirm, "确认", { type: "warning" });
			} catch (_unused3) {
				return;
			}
			switch (op.action) {
				case ActionType.EDIT:
				case ActionType.VIEW:
					if (op.url) router.push(resolveLinkUrl(op.url, row));
					break;
				case ActionType.DELETE:
					if (op.api) try {
						const url = resolveLinkUrl(op.api, row);
						const method = (op.method || "DELETE").toUpperCase();
						await axios.request({
							url,
							method
						});
						ElMessage.success("删除成功");
						fetchData();
					} catch (_unused4) {}
					break;
				case ActionType.CUSTOM:
				default: emit("operation-click", op, row);
			}
		}
		/** 工具栏点击处理 */
		function handleToolbarClick(op) {
			if (op.confirm) {
				ElMessageBox.confirm(op.confirm, "确认", { type: "warning" }).then(() => execToolbar(op)).catch(() => {});
				return;
			}
			execToolbar(op);
		}
		function execToolbar(op) {
			switch (op.action) {
				case ActionType.CREATE:
				case ActionType.EDIT:
				case ActionType.VIEW:
					if (op.url) router.push(op.url);
					else emit("toolbar-click", op);
					break;
				case ActionType.CUSTOM:
				default: emit("toolbar-click", op);
			}
		}
		/** 行操作按钮类型映射（默认 text 模式更紧凑） */
		function rowButtonType(op) {
			return op.type || "";
		}
		/** 多选变化 */
		function handleSelectionChange(sel) {
			selection.value = sel;
			emit("selection-change", sel);
		}
		/** 页码变化 */
		function handleCurrentChange(page) {
			currentPage.value = page;
			emit("page-change", page, pageSizeRef.value);
			if (props.autoFetch && props.config.searchApi) fetchData();
		}
		/** 每页条数变化 */
		function handleSizeChange(size) {
			pageSizeRef.value = size;
			currentPage.value = 1;
			emit("page-change", 1, size);
			if (props.autoFetch && props.config.searchApi) fetchData();
		}
		/** 排序变化（透传到查询参数） */
		function handleSortChange(_payload) {
			if (props.autoFetch && props.config.searchApi) fetchData();
		}
		/** 构造查询参数（含分页 + 筛选） */
		function buildQuery() {
			var _props$config$filters3;
			const q = {
				current: currentPage.value,
				size: pageSizeRef.value
			};
			for (const f of (_props$config$filters3 = props.config.filters) !== null && _props$config$filters3 !== void 0 ? _props$config$filters3 : []) {
				const v = filterForm[f.prop];
				if (v !== "" && v !== null && v !== void 0 && !(Array.isArray(v) && v.length === 0)) q[f.prop] = v;
			}
			return q;
		}
		/** 按 searchApi 拉取数据 */
		async function fetchData() {
			const api = props.config.searchApi;
			if (!api) return;
			innerLoading.value = true;
			try {
				var _payload$data, _ref4, _page$records, _page$total;
				const method = (props.config.method || "GET").toUpperCase();
				const query = buildQuery();
				const headers = { Authorization: `Bearer ${localStorage.getItem("pms_token") || ""}` };
				let response;
				if (method === "POST") response = await axios.post(api, query, { headers });
				else response = await axios.get(api, {
					params: query,
					headers
				});
				const payload = response.data;
				const page = (_payload$data = payload === null || payload === void 0 ? void 0 : payload.data) !== null && _payload$data !== void 0 ? _payload$data : payload;
				innerData.value = (_ref4 = (_page$records = page === null || page === void 0 ? void 0 : page.records) !== null && _page$records !== void 0 ? _page$records : page === null || page === void 0 ? void 0 : page.list) !== null && _ref4 !== void 0 ? _ref4 : Array.isArray(page) ? page : [];
				innerTotal.value = (_page$total = page === null || page === void 0 ? void 0 : page.total) !== null && _page$total !== void 0 ? _page$total : innerData.value.length;
				emit("data-loaded", innerData.value, innerTotal.value);
			} catch (e) {
				innerData.value = [];
				innerTotal.value = 0;
				console.warn("[LowCodeListRenderer] 加载数据失败", e);
			} finally {
				innerLoading.value = false;
			}
		}
		/** 触发导出（调用 config.export.api） */
		async function exportData() {
			const exp = props.config.export;
			if (!(exp === null || exp === void 0 ? void 0 : exp.enabled)) {
				ElMessage.warning("未启用导出");
				return;
			}
			if (!exp.api) {
				ElMessage.warning("未配置导出接口");
				return;
			}
			try {
				const token = localStorage.getItem("pms_token") || "";
				const query = exp.withFilter === false ? {} : buildQuery();
				const response = await axios.get(exp.api, {
					params: query,
					responseType: "blob",
					headers: { Authorization: `Bearer ${token}` }
				});
				const fileName = exp.fileName ? `${exp.fileName}-${Date.now()}.xlsx` : `export-${Date.now()}.xlsx`;
				triggerBlobDownload(response.data, fileName);
				ElMessage.success("导出成功");
			} catch (e) {
				console.warn("[LowCodeListRenderer] 导出失败", e);
			}
		}
		__expose({
			/** 重新加载数据 */
			refresh: fetchData,
			/** 获取当前选中行 */
			getSelection: () => selection.value,
			/** 获取当前筛选条件 */
			getFilters: () => ({ ...filterForm }),
			/** 触发导出 */
			exportData,
			/** 重置筛选并加载 */
			resetAndFetch: handleReset
		});
		onMounted(() => {
			if (props.autoFetch && props.config.searchApi) fetchData();
		});
		watch(() => props.config.searchApi, (api) => {
			if (props.autoFetch && api) {
				currentPage.value = 1;
				fetchData();
			}
		});
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_cascader = resolveComponent("el-cascader");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_image = resolveComponent("el-image");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_link = resolveComponent("el-link");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _directive_permission = resolveDirective("permission");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				__props.config.filters && __props.config.filters.length ? (openBlock(), createBlock(_component_el_form, {
					key: 0,
					inline: "",
					model: filterForm,
					class: "list-filter-form"
				}, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.config.filters, (f) => {
						return openBlock(), createBlock(_component_el_form_item, {
							key: f.id,
							label: f.label,
							style: normalizeStyle(filterStyle(f))
						}, {
							default: withCtx(() => [f.type === unref(FilterType).INPUT ? (openBlock(), createBlock(_component_el_input, {
								key: 0,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								placeholder: f.placeholder || `请输入${f.label}`,
								clearable: f.clearable !== false,
								onKeyup: withKeys(handleSearch, ["enter"])
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"placeholder",
								"clearable"
							])) : f.type === unref(FilterType).SELECT ? (openBlock(), createBlock(_component_el_select, {
								key: 1,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								placeholder: f.placeholder || `请选择${f.label}`,
								clearable: f.clearable !== false,
								multiple: f.multiple === true,
								style: { "min-width": "160px" }
							}, {
								default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(getFilterOptions(f), (opt) => {
									return openBlock(), createBlock(_component_el_option, {
										key: String(opt.value),
										label: opt.label,
										value: opt.value
									}, null, 8, ["label", "value"]);
								}), 128))]),
								_: 2
							}, 1032, [
								"modelValue",
								"onUpdate:modelValue",
								"placeholder",
								"clearable",
								"multiple"
							])) : f.type === unref(FilterType).DATE ? (openBlock(), createBlock(_component_el_date_picker, {
								key: 2,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								type: "date",
								placeholder: f.placeholder || `请选择${f.label}`,
								clearable: f.clearable !== false,
								"value-format": "YYYY-MM-DD"
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"placeholder",
								"clearable"
							])) : f.type === unref(FilterType).DATERANGE ? (openBlock(), createBlock(_component_el_date_picker, {
								key: 3,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								type: "daterange",
								"range-separator": "至",
								"start-placeholder": "开始日期",
								"end-placeholder": "结束日期",
								clearable: f.clearable !== false,
								"value-format": "YYYY-MM-DD"
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"clearable"
							])) : f.type === unref(FilterType).CASCADER ? (openBlock(), createBlock(_component_el_cascader, {
								key: 4,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								options: f.options,
								placeholder: f.placeholder || `请选择${f.label}`,
								clearable: f.clearable !== false
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"options",
								"placeholder",
								"clearable"
							])) : (openBlock(), createBlock(_component_el_input, {
								key: 5,
								modelValue: filterForm[f.prop],
								"onUpdate:modelValue": ($event) => filterForm[f.prop] = $event,
								placeholder: f.placeholder || `请输入${f.label}`,
								clearable: f.clearable !== false
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"placeholder",
								"clearable"
							]))]),
							_: 2
						}, 1032, ["label", "style"]);
					}), 128)), createVNode(_component_el_form_item, null, {
						default: withCtx(() => [createVNode(_component_el_button, {
							type: "primary",
							icon: "Search",
							onClick: handleSearch
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("查询", -1)])]),
							_: 1
						}), createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: handleReset
						}, {
							default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("重置", -1)])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["model"])) : createCommentVNode("", true),
				__props.config.toolbar && __props.config.toolbar.length ? (openBlock(), createElementBlock("div", _hoisted_2, [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.config.toolbar, (op) => {
					return withDirectives((openBlock(), createBlock(_component_el_button, {
						key: op.id,
						type: op.type || "default",
						icon: op.icon,
						onClick: ($event) => handleToolbarClick(op)
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(op.label), 1)]),
						_: 2
					}, 1032, [
						"type",
						"icon",
						"onClick"
					])), [[_directive_permission, op.permission]]);
				}), 128)), __props.config.export && __props.config.export.enabled ? (openBlock(), createBlock(_component_el_button, {
					key: 0,
					icon: "Download",
					onClick: exportData
				}, {
					default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode(" 导出 ", -1)])]),
					_: 1
				})) : createCommentVNode("", true)])) : createCommentVNode("", true),
				withDirectives((openBlock(), createBlock(_component_el_table, {
					data: tableData.value,
					stripe: __props.config.stripe !== false,
					border: __props.config.border !== false,
					style: { width: "100%" },
					onSelectionChange: handleSelectionChange,
					onSortChange: handleSortChange
				}, {
					default: withCtx(() => [
						__props.config.showSelection ? (openBlock(), createBlock(_component_el_table_column, {
							key: 0,
							type: "selection",
							width: "50",
							fixed: "left"
						})) : createCommentVNode("", true),
						__props.config.showIndex ? (openBlock(), createBlock(_component_el_table_column, {
							key: 1,
							type: "index",
							width: "50",
							label: "序号",
							fixed: "left",
							index: (i) => (currentPage.value - 1) * pageSizeRef.value + i + 1
						}, null, 8, ["index"])) : createCommentVNode("", true),
						(openBlock(true), createElementBlock(Fragment, null, renderList(visibleColumns.value, (col) => {
							return openBlock(), createBlock(_component_el_table_column, {
								key: col.id,
								prop: col.prop,
								label: col.label,
								width: col.width,
								"min-width": col.minWidth,
								fixed: resolveFixed(col.fixed),
								sortable: col.sortable ? "custom" : false,
								align: col.align || "left",
								"show-overflow-tooltip": ""
							}, {
								default: withCtx(({ row }) => [col.type === unref(ColumnType).IMAGE ? (openBlock(), createBlock(_component_el_image, {
									key: 0,
									src: String(row[col.prop] || ""),
									style: normalizeStyle({
										width: (col.imageWidth || 60) + "px",
										height: (col.imageHeight || 60) + "px"
									}),
									fit: "cover",
									"preview-src-list": row[col.prop] ? [String(row[col.prop])] : [],
									"preview-teleported": ""
								}, null, 8, [
									"src",
									"style",
									"preview-src-list"
								])) : col.type === unref(ColumnType).TAG ? (openBlock(), createBlock(_component_el_tag, {
									key: 1,
									type: col.tagType || "primary"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(renderWithFormatter(row, col)), 1)]),
									_: 2
								}, 1032, ["type"])) : col.type === unref(ColumnType).LINK ? (openBlock(), createBlock(_component_el_link, {
									key: 2,
									type: "primary",
									underline: false,
									onClick: ($event) => handleLinkClick(col, row)
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(renderWithFormatter(row, col)), 1)]),
									_: 2
								}, 1032, ["onClick"])) : col.type === unref(ColumnType).CUSTOM ? renderSlot(_ctx.$slots, col.prop, {
									key: 3,
									row,
									column: col
								}, () => [createTextVNode(toDisplayString(renderWithFormatter(row, col)), 1)], true) : (openBlock(), createElementBlock("span", _hoisted_3, toDisplayString(renderWithFormatter(row, col)), 1))]),
								_: 2
							}, 1032, [
								"prop",
								"label",
								"width",
								"min-width",
								"fixed",
								"sortable",
								"align"
							]);
						}), 128)),
						visibleRowOps.value.length ? (openBlock(), createBlock(_component_el_table_column, {
							key: 2,
							label: "操作",
							width: operationsWidth.value,
							fixed: "right"
						}, {
							default: withCtx(({ row }) => [(openBlock(true), createElementBlock(Fragment, null, renderList(getVisibleRowOps(row), (op) => {
								return withDirectives((openBlock(), createBlock(_component_el_button, {
									key: op.id,
									type: rowButtonType(op),
									icon: op.icon,
									size: "small",
									link: "",
									onClick: ($event) => handleRowClick(op, row)
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(op.label), 1)]),
									_: 2
								}, 1032, [
									"type",
									"icon",
									"onClick"
								])), [[_directive_permission, op.permission]]);
							}), 128))]),
							_: 1
						}, 8, ["width"])) : createCommentVNode("", true)
					]),
					_: 3
				}, 8, [
					"data",
					"stripe",
					"border"
				])), [[_directive_loading, loading.value]]),
				__props.config.showPagination !== false ? (openBlock(), createBlock(_component_el_pagination, {
					key: 2,
					"current-page": currentPage.value,
					"onUpdate:currentPage": _cache[0] || (_cache[0] = ($event) => currentPage.value = $event),
					"page-size": pageSizeRef.value,
					"onUpdate:pageSize": _cache[1] || (_cache[1] = ($event) => pageSizeRef.value = $event),
					total: total.value,
					"page-sizes": pageSizes.value,
					layout: "total, sizes, prev, pager, next, jumper",
					style: {
						"margin-top": "12px",
						"justify-content": "flex-end"
					},
					onCurrentChange: handleCurrentChange,
					onSizeChange: handleSizeChange
				}, null, 8, [
					"current-page",
					"page-size",
					"total",
					"page-sizes"
				])) : createCommentVNode("", true)
			]);
		};
	}
});
//#endregion
//#region src/components/LowCodeListRenderer/index.vue
var LowCodeListRenderer_exports = /* @__PURE__ */ __exportAll({ default: () => LowCodeListRenderer_default });
var LowCodeListRenderer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(index_vue_vue_type_script_setup_true_lang_default, [["__scopeId", "data-v-129565a8"]]);
//#endregion
export { LowCodeListRenderer_exports as n, LowCodeListRenderer_default as t };

//# sourceMappingURL=LowCodeListRenderer-CyLlB3D5.js.map