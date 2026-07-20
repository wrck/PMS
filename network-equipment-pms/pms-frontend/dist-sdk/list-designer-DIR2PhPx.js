import { d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { n as initBuiltinComponents, t as LowCodeComponentRegistry_default } from "./LowCodeComponentRegistry-BhIrM3BV.js";
import { D as exportList, M as getList, Z as updateList, a as FilterType, m as archiveList, n as ButtonType, q as publishList, r as ColumnType, s as ListLayout, t as ActionType, y as createList, z as importList } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as LowCodeListRenderer_default } from "./LowCodeListRenderer-CyLlB3D5.js";
import { t as useUndoRedo } from "./useUndoRedo-C9SCn4rB.js";
import { n as BREAKPOINT_ORDER } from "./breakpoints-CjTxpeuh.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDynamicComponent, toDisplayString, unref, watch, withCtx, withModifiers } from "vue";
//#region src/views/lowcode/list-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "list-designer" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "toolbar-left" };
var _hoisted_4 = { class: "toolbar-right" };
var _hoisted_5 = { class: "designer-body" };
var _hoisted_6 = { class: "comp-group-title" };
var _hoisted_7 = { class: "comp-items" };
var _hoisted_8 = ["onDragstart", "onClick"];
var _hoisted_9 = { class: "canvas-header" };
var _hoisted_10 = { class: "canvas-switches" };
var _hoisted_11 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_12 = {
	key: 1,
	class: "item-list"
};
var _hoisted_13 = [
	"onDragstart",
	"onDrop",
	"onClick"
];
var _hoisted_14 = { class: "item-card-header" };
var _hoisted_15 = { class: "item-label" };
var _hoisted_16 = {
	key: 0,
	class: "item-prop"
};
var _hoisted_17 = { class: "item-actions" };
var _hoisted_18 = {
	key: 0,
	class: "empty-prop"
};
var _hoisted_19 = { class: "bp-row" };
/** 组件库分组 */
var HISTORY_DEBOUNCE_MS = 400;
//#endregion
//#region src/views/lowcode/list-designer/index.vue
var list_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		/**
		* 低代码列表设计器。
		*
		* <p>三栏布局：</p>
		* <ul>
		*   <li>左侧：组件库面板（列组件 / 筛选组件 / 操作组件 三大类）</li>
		*   <li>中间：画布（4 个 Tab：列配置 / 筛选配置 / 操作配置 / 工具栏配置），
		*       每个 Tab 内字段可拖拽排序、点击选中编辑</li>
		*   <li>右侧：属性面板（按当前 Tab 与选中项展示对应属性编辑表单）</li>
		* </ul>
		*
		* <p>顶部操作栏：保存草稿 / 发布 / 导入 / 导出 / 预览 / 重置，以及列表元信息编辑。</p>
		*
		* <p>使用原生 HTML5 拖拽 API（draggable + dragstart/dragover/drop）实现，
		* 支持组件库 → 画布拖拽、画布内排序拖拽两种交互。字段 ID 自动生成
		* （col_N / filter_N / op_N），点击"预览"打开 LowCodeListRenderer 弹窗。</p>
		*/
		const route = useRoute();
		const router = useRouter();
		const componentGroups = ref([
			{
				title: "列组件",
				items: [
					{
						type: ColumnType.TEXT,
						label: "文本",
						icon: "Document",
						category: "column",
						defaultProps: {}
					},
					{
						type: ColumnType.IMAGE,
						label: "图片",
						icon: "Picture",
						category: "column",
						defaultProps: {
							imageWidth: 60,
							imageHeight: 60
						}
					},
					{
						type: ColumnType.TAG,
						label: "标签",
						icon: "PriceTag",
						category: "column",
						defaultProps: { tagType: "primary" }
					},
					{
						type: ColumnType.DATE,
						label: "日期",
						icon: "Calendar",
						category: "column",
						defaultProps: {}
					},
					{
						type: ColumnType.DATETIME,
						label: "日期时间",
						icon: "Clock",
						category: "column",
						defaultProps: {}
					},
					{
						type: ColumnType.CURRENCY,
						label: "货币",
						icon: "Money",
						category: "column",
						defaultProps: {}
					},
					{
						type: ColumnType.PERCENT,
						label: "百分比",
						icon: "DataLine",
						category: "column",
						defaultProps: {}
					},
					{
						type: ColumnType.LINK,
						label: "链接",
						icon: "Link",
						category: "column",
						defaultProps: { linkUrl: "" }
					},
					{
						type: ColumnType.DICT,
						label: "字典",
						icon: "Collection",
						category: "column",
						defaultProps: { dictCode: "" }
					},
					{
						type: ColumnType.CUSTOM,
						label: "自定义",
						icon: "Setting",
						category: "column",
						defaultProps: {}
					}
				]
			},
			{
				title: "筛选组件",
				items: [
					{
						type: FilterType.INPUT,
						label: "输入框",
						icon: "EditPen",
						category: "filter",
						defaultProps: { span: 6 }
					},
					{
						type: FilterType.SELECT,
						label: "下拉选择",
						icon: "ArrowDown",
						category: "filter",
						defaultProps: {
							span: 6,
							options: []
						}
					},
					{
						type: FilterType.DATE,
						label: "日期",
						icon: "Calendar",
						category: "filter",
						defaultProps: { span: 6 }
					},
					{
						type: FilterType.DATERANGE,
						label: "日期范围",
						icon: "Calendar",
						category: "filter",
						defaultProps: { span: 12 }
					},
					{
						type: FilterType.CASCADER,
						label: "级联选择",
						icon: "Share",
						category: "filter",
						defaultProps: {
							span: 6,
							options: []
						}
					}
				]
			},
			{
				title: "操作组件",
				items: [
					{
						type: ActionType.EDIT,
						label: "编辑",
						icon: "Edit",
						category: "operation",
						defaultProps: {
							type: ButtonType.PRIMARY,
							action: ActionType.EDIT,
							url: ""
						}
					},
					{
						type: ActionType.VIEW,
						label: "查看",
						icon: "View",
						category: "operation",
						defaultProps: {
							type: ButtonType.TEXT,
							action: ActionType.VIEW,
							url: ""
						}
					},
					{
						type: ActionType.DELETE,
						label: "删除",
						icon: "Delete",
						category: "operation",
						defaultProps: {
							type: ButtonType.DANGER,
							action: ActionType.DELETE,
							api: "",
							method: "DELETE",
							confirm: "确认删除？"
						}
					},
					{
						type: ActionType.CUSTOM,
						label: "自定义按钮",
						icon: "Operation",
						category: "operation",
						defaultProps: {
							type: ButtonType.PRIMARY,
							action: ActionType.CUSTOM
						}
					},
					{
						type: ActionType.CREATE,
						label: "新增（工具栏）",
						icon: "Plus",
						category: "operation",
						defaultProps: {
							type: ButtonType.PRIMARY,
							action: ActionType.CREATE,
							url: ""
						}
					}
				]
			}
		]);
		/** 注册中心业务组件（供"自定义列"绑定 componentName） */
		const registryComponents = ref([]);
		/** 列表元信息（对应 LowCodeListConfig 的非 listConfig 字段） */
		const metaForm = reactive({
			code: "",
			name: "",
			description: "",
			listConfig: "",
			status: "DRAFT",
			bizType: "",
			version: 1
		});
		/** 设计器内部维护的 ListConfig 对象 */
		const listConfig = reactive({
			title: "",
			description: "",
			searchApi: "",
			method: "GET",
			pageSize: 20,
			pageSizes: [
				10,
				20,
				50,
				100
			],
			layout: ListLayout.TABLE,
			stripe: true,
			border: true,
			showSelection: true,
			showIndex: true,
			showPagination: true,
			columns: [],
			filters: [],
			operations: [],
			toolbar: [],
			export: { enabled: false }
		});
		/** 当前 Tab */
		const activeTab = ref("column");
		/** 当前选中项 id（列/筛选/操作的 id） */
		const selectedId = ref("");
		/** 各类型 ID 计数器 */
		let colSeq = 0;
		let filterSeq = 0;
		let opSeq = 0;
		/** 元信息表单 ref */
		const metaFormRef = ref();
		const metaRules = {
			code: [{
				required: true,
				message: "请输入列表编码",
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入列表名称",
				trigger: "blur"
			}]
		};
		const loading = ref(false);
		const previewVisible = ref(false);
		/** 当前选中的列 */
		const selectedColumn = computed(() => listConfig.columns.find((c) => c.id === selectedId.value) || null);
		/** 当前选中的筛选项 */
		const selectedFilter = computed(() => {
			var _listConfig$filters;
			return ((_listConfig$filters = listConfig.filters) === null || _listConfig$filters === void 0 ? void 0 : _listConfig$filters.find((f) => f.id === selectedId.value)) || null;
		});
		const filterResponsiveCollapse = ref(["resp"]);
		/** 当前选中筛选项是否启用响应式断点（span 为对象） */
		const isFilterResponsive = computed({
			get: () => !!selectedFilter.value && typeof selectedFilter.value.span === "object",
			set: (val) => {
				const filter = selectedFilter.value;
				if (!filter) return;
				if (val) {
					const cur = typeof filter.span === "number" ? filter.span : 6;
					filter.span = {
						xs: cur,
						sm: cur,
						md: cur,
						lg: cur,
						xl: cur
					};
				} else {
					var _obj$md;
					const obj = filter.span;
					filter.span = typeof obj === "object" && obj ? (_obj$md = obj.md) !== null && _obj$md !== void 0 ? _obj$md : 6 : 6;
				}
			}
		});
		/** 非响应式模式下的栅格宽度（数字） */
		const filterSpan = computed({
			get: () => {
				var _selectedFilter$value;
				return typeof ((_selectedFilter$value = selectedFilter.value) === null || _selectedFilter$value === void 0 ? void 0 : _selectedFilter$value.span) === "number" ? selectedFilter.value.span : 6;
			},
			set: (v) => {
				if (selectedFilter.value) selectedFilter.value.span = v;
			}
		});
		/** 读取指定断点值（undefined 表示留空继承更小断点，与 form-designer 对齐） */
		function getFilterBreakpoint(k) {
			var _selectedFilter$value2;
			const s = (_selectedFilter$value2 = selectedFilter.value) === null || _selectedFilter$value2 === void 0 ? void 0 : _selectedFilter$value2.span;
			if (typeof s !== "object" || !s) return void 0;
			const v = s[k];
			return typeof v === "number" && !Number.isNaN(v) ? v : void 0;
		}
		/** 设置指定断点值（v=undefined 时删除该 key，体现"留空即继承"语义） */
		function setFilterBreakpoint(k, v) {
			const filter = selectedFilter.value;
			if (!filter) return;
			const s = filter.span;
			const obj = typeof s === "object" && s ? { ...s } : {};
			if (v === void 0 || v === null || Number.isNaN(v)) delete obj[k];
			else obj[k] = v;
			filter.span = [
				"xs",
				"sm",
				"md",
				"lg",
				"xl"
			].some((bp) => obj[bp] !== void 0) ? obj : 6;
		}
		/** 当前选中的行操作 */
		const selectedOperation = computed(() => {
			var _listConfig$operation;
			return ((_listConfig$operation = listConfig.operations) === null || _listConfig$operation === void 0 ? void 0 : _listConfig$operation.find((o) => o.id === selectedId.value)) || null;
		});
		/** 当前选中的工具栏操作 */
		const selectedToolbar = computed(() => {
			var _listConfig$toolbar;
			return ((_listConfig$toolbar = listConfig.toolbar) === null || _listConfig$toolbar === void 0 ? void 0 : _listConfig$toolbar.find((o) => o.id === selectedId.value)) || null;
		});
		/** 当前 Tab 对应的选中项 */
		const selectedItem = computed(() => {
			switch (activeTab.value) {
				case "column": return selectedColumn.value;
				case "filter": return selectedFilter.value;
				case "operation": return selectedOperation.value;
				case "toolbar": return selectedToolbar.value;
				default: return null;
			}
		});
		/** 生成列对象 */
		function createColumn(type, label, extraProps = {}) {
			colSeq++;
			return {
				id: `col_${colSeq}`,
				prop: `col${colSeq}`,
				label,
				type,
				width: 120,
				align: "left",
				hidden: false,
				editable: false,
				...extraProps
			};
		}
		/** 生成筛选项对象 */
		function createFilter(type, label, extraProps = {}) {
			filterSeq++;
			return {
				id: `filter_${filterSeq}`,
				prop: `filter${filterSeq}`,
				label,
				type,
				placeholder: `请输入${label}`,
				clearable: true,
				span: 6,
				...extraProps
			};
		}
		/** 生成操作按钮对象 */
		function createOperation(extraProps = {}) {
			opSeq++;
			return {
				id: `op_${opSeq}`,
				label: "操作",
				type: ButtonType.PRIMARY,
				action: ActionType.CUSTOM,
				...extraProps
			};
		}
		/** 添加项（点击组件库或拖拽放置） */
		function addComponent(comp) {
			if (comp.category === "column") {
				const col = createColumn(comp.type, comp.label, comp.defaultProps || {});
				listConfig.columns.push(col);
				selectedId.value = col.id;
			} else if (comp.category === "filter") {
				const f = createFilter(comp.type, comp.label, comp.defaultProps || {});
				listConfig.filters = listConfig.filters || [];
				listConfig.filters.push(f);
				selectedId.value = f.id;
			} else if (comp.category === "operation") {
				const op = createOperation(comp.defaultProps || {});
				if (comp.type === ActionType.CREATE) {
					listConfig.toolbar = listConfig.toolbar || [];
					listConfig.toolbar.push(op);
					activeTab.value = "toolbar";
				} else {
					listConfig.operations = listConfig.operations || [];
					listConfig.operations.push(op);
					activeTab.value = "operation";
				}
				selectedId.value = op.id;
			}
		}
		/** 通用删除 */
		function removeItem(id) {
			listConfig.columns = listConfig.columns.filter((c) => c.id !== id);
			listConfig.filters = (listConfig.filters || []).filter((f) => f.id !== id);
			listConfig.operations = (listConfig.operations || []).filter((o) => o.id !== id);
			listConfig.toolbar = (listConfig.toolbar || []).filter((o) => o.id !== id);
			if (selectedId.value === id) selectedId.value = "";
		}
		/** 复制项（按 id 在对应 Tab 中复制） */
		function duplicateItem(id) {
			var _listConfig$filters2, _listConfig$operation2, _listConfig$toolbar2;
			const col = listConfig.columns.find((c) => c.id === id);
			if (col) {
				colSeq++;
				const copy = JSON.parse(JSON.stringify(col));
				copy.id = `col_${colSeq}`;
				copy.prop = `${col.prop}_copy`;
				copy.label = `${col.label}_副本`;
				const idx = listConfig.columns.findIndex((c) => c.id === id);
				listConfig.columns.splice(idx + 1, 0, copy);
				selectedId.value = copy.id;
				return;
			}
			const f = (_listConfig$filters2 = listConfig.filters) === null || _listConfig$filters2 === void 0 ? void 0 : _listConfig$filters2.find((x) => x.id === id);
			if (f) {
				filterSeq++;
				const copy = JSON.parse(JSON.stringify(f));
				copy.id = `filter_${filterSeq}`;
				copy.prop = `${f.prop}_copy`;
				copy.label = `${f.label}_副本`;
				const idx = listConfig.filters.findIndex((x) => x.id === id);
				listConfig.filters.splice(idx + 1, 0, copy);
				selectedId.value = copy.id;
				return;
			}
			const op = (_listConfig$operation2 = listConfig.operations) === null || _listConfig$operation2 === void 0 ? void 0 : _listConfig$operation2.find((x) => x.id === id);
			if (op) {
				opSeq++;
				const copy = JSON.parse(JSON.stringify(op));
				copy.id = `op_${opSeq}`;
				copy.label = `${op.label}_副本`;
				const idx = listConfig.operations.findIndex((x) => x.id === id);
				listConfig.operations.splice(idx + 1, 0, copy);
				selectedId.value = copy.id;
				return;
			}
			const tb = (_listConfig$toolbar2 = listConfig.toolbar) === null || _listConfig$toolbar2 === void 0 ? void 0 : _listConfig$toolbar2.find((x) => x.id === id);
			if (tb) {
				opSeq++;
				const copy = JSON.parse(JSON.stringify(tb));
				copy.id = `op_${opSeq}`;
				copy.label = `${tb.label}_副本`;
				const idx = listConfig.toolbar.findIndex((x) => x.id === id);
				listConfig.toolbar.splice(idx + 1, 0, copy);
				selectedId.value = copy.id;
			}
		}
		/** 通用上下移动（按当前 Tab 操作） */
		function moveItem(id, direction) {
			let arr;
			switch (activeTab.value) {
				case "column":
					arr = listConfig.columns;
					break;
				case "filter":
					arr = listConfig.filters;
					break;
				case "operation":
					arr = listConfig.operations;
					break;
				case "toolbar":
					arr = listConfig.toolbar;
					break;
			}
			if (!arr) return;
			const idx = arr.findIndex((x) => x.id === id);
			if (idx < 0) return;
			const newIdx = idx + direction;
			if (newIdx < 0 || newIdx >= arr.length) return;
			const tmp = arr[idx];
			arr[idx] = arr[newIdx];
			arr[newIdx] = tmp;
		}
		/** 选中项 */
		function selectItem(id) {
			selectedId.value = id;
		}
		let dragType = "";
		let dragItemId = "";
		/** 组件库 dragstart */
		function onCompDragStart(event, comp) {
			dragType = comp.type;
			dragItemId = "";
			if (event.dataTransfer) {
				event.dataTransfer.effectAllowed = "copy";
				event.dataTransfer.setData("text/plain", `comp:${comp.type}`);
			}
		}
		/** 画布项 dragstart（用于排序） */
		function onItemDragStart(event, id) {
			dragItemId = id;
			dragType = "";
			if (event.dataTransfer) {
				event.dataTransfer.effectAllowed = "move";
				event.dataTransfer.setData("text/plain", `item:${id}`);
			}
		}
		/** 画布 dragover */
		function onCanvasDragOver(event) {
			if (event.dataTransfer) event.dataTransfer.dropEffect = dragType ? "copy" : "move";
			event.preventDefault();
		}
		/** 画布 drop：组件库 → 添加；画布项 → 不变（排序通过 drop 到目标 item 实现） */
		function onCanvasDrop(event) {
			var _event$dataTransfer;
			event.preventDefault();
			const raw = ((_event$dataTransfer = event.dataTransfer) === null || _event$dataTransfer === void 0 ? void 0 : _event$dataTransfer.getData("text/plain")) || "";
			if (raw.startsWith("comp:")) {
				const type = raw.slice(5);
				for (const g of componentGroups.value) {
					const comp = g.items.find((c) => c.type === type);
					if (comp) {
						addComponent(comp);
						return;
					}
				}
			}
		}
		/** 项 drop：将 dragItemId 移动到 targetId 之前 */
		function onItemDrop(event, targetId) {
			event.preventDefault();
			if (!dragItemId || dragItemId === targetId) return;
			let arr;
			switch (activeTab.value) {
				case "column":
					arr = listConfig.columns;
					break;
				case "filter":
					arr = listConfig.filters;
					break;
				case "operation":
					arr = listConfig.operations;
					break;
				case "toolbar":
					arr = listConfig.toolbar;
					break;
			}
			if (!arr) return;
			const fromIdx = arr.findIndex((x) => x.id === dragItemId);
			const toIdx = arr.findIndex((x) => x.id === targetId);
			if (fromIdx < 0 || toIdx < 0) return;
			const [moved] = arr.splice(fromIdx, 1);
			arr.splice(toIdx, 0, moved);
			dragItemId = "";
		}
		function addFilterOption(f) {
			if (!f.options) f.options = [];
			f.options.push({
				label: "新选项",
				value: `option_${Date.now()}`
			});
		}
		function removeFilterOption(f, idx) {
			if (f.options) f.options.splice(idx, 1);
		}
		function syncListConfigToStr() {
			metaForm.listConfig = JSON.stringify(listConfig, null, 2);
		}
		function parseListConfigFromStr() {
			try {
				var _parsed$title, _parsed$description, _parsed$searchApi, _parsed$method, _parsed$pageSize, _parsed$pageSizes, _parsed$layout, _parsed$stripe, _parsed$border, _parsed$showSelection, _parsed$showIndex, _parsed$showPaginatio;
				if (!metaForm.listConfig) {
					listConfig.columns = [];
					listConfig.filters = [];
					listConfig.operations = [];
					listConfig.toolbar = [];
					return;
				}
				const parsed = JSON.parse(metaForm.listConfig);
				listConfig.title = (_parsed$title = parsed.title) !== null && _parsed$title !== void 0 ? _parsed$title : "";
				listConfig.description = (_parsed$description = parsed.description) !== null && _parsed$description !== void 0 ? _parsed$description : "";
				listConfig.searchApi = (_parsed$searchApi = parsed.searchApi) !== null && _parsed$searchApi !== void 0 ? _parsed$searchApi : "";
				listConfig.method = (_parsed$method = parsed.method) !== null && _parsed$method !== void 0 ? _parsed$method : "GET";
				listConfig.pageSize = (_parsed$pageSize = parsed.pageSize) !== null && _parsed$pageSize !== void 0 ? _parsed$pageSize : 20;
				listConfig.pageSizes = (_parsed$pageSizes = parsed.pageSizes) !== null && _parsed$pageSizes !== void 0 ? _parsed$pageSizes : [
					10,
					20,
					50,
					100
				];
				listConfig.layout = (_parsed$layout = parsed.layout) !== null && _parsed$layout !== void 0 ? _parsed$layout : ListLayout.TABLE;
				listConfig.stripe = (_parsed$stripe = parsed.stripe) !== null && _parsed$stripe !== void 0 ? _parsed$stripe : true;
				listConfig.border = (_parsed$border = parsed.border) !== null && _parsed$border !== void 0 ? _parsed$border : true;
				listConfig.showSelection = (_parsed$showSelection = parsed.showSelection) !== null && _parsed$showSelection !== void 0 ? _parsed$showSelection : true;
				listConfig.showIndex = (_parsed$showIndex = parsed.showIndex) !== null && _parsed$showIndex !== void 0 ? _parsed$showIndex : true;
				listConfig.showPagination = (_parsed$showPaginatio = parsed.showPagination) !== null && _parsed$showPaginatio !== void 0 ? _parsed$showPaginatio : true;
				listConfig.columns = parsed.columns || [];
				listConfig.filters = parsed.filters || [];
				listConfig.operations = parsed.operations || [];
				listConfig.toolbar = parsed.toolbar || [];
				listConfig.export = parsed.export || { enabled: false };
				colSeq = 0;
				filterSeq = 0;
				opSeq = 0;
				const bump = (prefix, seqRef) => (item) => {
					const m = new RegExp(`${prefix}_(\\d+)`).exec(item.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > seqRef.v) seqRef.v = n;
					}
				};
				listConfig.columns.forEach(bump("col", {
					get v() {
						return colSeq;
					},
					set v(n) {
						colSeq = n;
					}
				}));
				(listConfig.filters || []).forEach(bump("filter", {
					get v() {
						return filterSeq;
					},
					set v(n) {
						filterSeq = n;
					}
				}));
				(listConfig.operations || []).forEach(bump("op", {
					get v() {
						return opSeq;
					},
					set v(n) {
						opSeq = n;
					}
				}));
				(listConfig.toolbar || []).forEach(bump("op", {
					get v() {
						return opSeq;
					},
					set v(n) {
						opSeq = n;
					}
				}));
			} catch (e) {
				ElMessage.error("列表配置 JSON 解析失败：" + e.message);
			}
		}
		/**
		* 撤销/重做历史栈：对整个 listConfig 做 JSON 快照。
		*
		* <p>采用 watch 深度监听 listConfig 自动推历史（400ms 防抖合并连续输入，
		* 避免 50 步栈被逐字符吞掉）；undo/redo 时反向同步快照回 reactive，
		* 保持 listConfig 引用不变以兼容现有 UI 双向绑定。</p>
		*/
		const history = useUndoRedo(JSON.parse(JSON.stringify(listConfig)));
		const { present: historyPresent, canUndo, canRedo } = history;
		/** 抑制标志：undo/redo 同步快照回 listConfig 时关闭 watch 推历史，避免循环 */
		let suppressHistory = false;
		/** 防抖计时器：连续输入合并为一次历史入栈 */
		let historyDebounce = null;
		function commitPendingHistory() {
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(listConfig)));
			}
		}
		watch(listConfig, () => {
			if (suppressHistory) return;
			if (historyDebounce) clearTimeout(historyDebounce);
			historyDebounce = setTimeout(() => {
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(listConfig)));
			}, HISTORY_DEBOUNCE_MS);
		}, {
			deep: true,
			flush: "sync"
		});
		/** 将历史当前快照同步回 reactive listConfig（保持引用不变，UI 自动更新） */
		function applyHistoryToListConfig() {
			const snap = historyPresent.value;
			suppressHistory = true;
			try {
				const target = listConfig;
				const src = snap;
				for (const key of Object.keys(target)) if (!(key in src)) delete target[key];
				for (const key of Object.keys(src)) target[key] = JSON.parse(JSON.stringify(src[key]));
				colSeq = 0;
				filterSeq = 0;
				opSeq = 0;
				const bump = (prefix, getSeq, setSeq) => (item) => {
					const m = new RegExp(`${prefix}_(\\d+)`).exec(item.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > getSeq()) setSeq(n);
					}
				};
				listConfig.columns.forEach(bump("col", () => colSeq, (n) => colSeq = n));
				(listConfig.filters || []).forEach(bump("filter", () => filterSeq, (n) => filterSeq = n));
				(listConfig.operations || []).forEach(bump("op", () => opSeq, (n) => opSeq = n));
				(listConfig.toolbar || []).forEach(bump("op", () => opSeq, (n) => opSeq = n));
			} finally {
				nextTick(() => {
					suppressHistory = false;
				});
			}
		}
		/** 撤销 */
		function undo() {
			commitPendingHistory();
			if (!canUndo.value) return;
			history.undo();
			applyHistoryToListConfig();
		}
		/** 重做 */
		function redo() {
			commitPendingHistory();
			if (!canRedo.value) return;
			history.redo();
			applyHistoryToListConfig();
		}
		/** 键盘快捷键：Ctrl/Cmd+Z 撤销，Ctrl+Y 或 Ctrl/Cmd+Shift+Z 重做 */
		function onUndoRedoKeydown(event) {
			if (!(typeof navigator !== "undefined" && navigator.platform.toUpperCase().indexOf("MAC") >= 0 ? event.metaKey : event.ctrlKey)) return;
			const key = event.key.toLowerCase();
			if (key === "z" && !event.shiftKey) {
				event.preventDefault();
				undo();
			} else if (key === "z" && event.shiftKey || key === "y") {
				event.preventDefault();
				redo();
			}
		}
		async function loadList(id) {
			loading.value = true;
			try {
				const data = await getList(id);
				Object.assign(metaForm, data);
				parseListConfigFromStr();
				if (historyDebounce) {
					clearTimeout(historyDebounce);
					historyDebounce = null;
				}
				history.reset(JSON.parse(JSON.stringify(listConfig)));
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function handleSave() {
			if (!metaFormRef.value) return;
			await metaFormRef.value.validate(async (valid) => {
				if (!valid) return;
				if (listConfig.columns.length === 0) {
					ElMessage.warning("请至少添加一列");
					return;
				}
				syncListConfigToStr();
				loading.value = true;
				try {
					if (metaForm.id) {
						await updateList(metaForm.id, metaForm);
						ElMessage.success("保存成功");
					} else {
						const created = await createList(metaForm);
						metaForm.id = created.id;
						metaForm.status = created.status;
						ElMessage.success("创建成功");
					}
				} catch (_unused2) {} finally {
					loading.value = false;
				}
			});
		}
		async function handlePublish() {
			if (!metaForm.id) {
				ElMessage.warning("请先保存草稿");
				return;
			}
			syncListConfigToStr();
			loading.value = true;
			try {
				await updateList(metaForm.id, metaForm);
				await publishList(metaForm.id);
				metaForm.status = "PUBLISHED";
				ElMessage.success("发布成功");
			} catch (_unused3) {} finally {
				loading.value = false;
			}
		}
		async function handleArchive() {
			if (!metaForm.id) return;
			try {
				await ElMessageBox.confirm("确认归档此列表？归档后不可再使用", "确认", { type: "warning" });
				await archiveList(metaForm.id);
				metaForm.status = "ARCHIVED";
				ElMessage.success("归档成功");
			} catch (_unused4) {}
		}
		async function handleExport() {
			if (!metaForm.code) {
				ElMessage.warning("请先填写列表编码");
				return;
			}
			syncListConfigToStr();
			if (metaForm.id) try {
				await exportList(metaForm.code);
				ElMessage.success("导出成功");
			} catch (_unused5) {}
			else {
				const blob = new Blob([JSON.stringify(metaForm, null, 2)], { type: "application/json" });
				const url = URL.createObjectURL(blob);
				const link = document.createElement("a");
				link.href = url;
				link.download = `list-${metaForm.code}.json`;
				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
				setTimeout(() => URL.revokeObjectURL(url), 0);
				ElMessage.success("本地导出成功");
			}
		}
		async function handleImport() {
			const input = document.createElement("input");
			input.type = "file";
			input.accept = ".json,application/json";
			input.onchange = async () => {
				var _input$files;
				const file = (_input$files = input.files) === null || _input$files === void 0 ? void 0 : _input$files[0];
				if (!file) return;
				const text = await file.text();
				try {
					const imported = await importList(text);
					ElMessage.success(`导入成功，编码：${imported.code}`);
					Object.assign(metaForm, imported);
					parseListConfigFromStr();
				} catch (_unused6) {
					try {
						const parsed = JSON.parse(text);
						if (parsed.listConfig && typeof parsed.listConfig === "string") {
							Object.assign(metaForm, parsed);
							parseListConfigFromStr();
							ElMessage.success("已加载到画布（本地解析，未提交后端）");
						} else if (Array.isArray(parsed.columns)) {
							metaForm.listConfig = text;
							parseListConfigFromStr();
							ElMessage.success("已加载到画布");
						} else ElMessage.error("无法识别的 JSON 结构");
					} catch (e) {
						ElMessage.error("JSON 解析失败：" + e.message);
					}
				}
			};
			input.click();
		}
		function handlePreview() {
			syncListConfigToStr();
			previewVisible.value = true;
		}
		function handleReset() {
			ElMessageBox.confirm("确认清空画布所有配置？此操作不可恢复", "确认", { type: "warning" }).then(() => {
				listConfig.columns = [];
				listConfig.filters = [];
				listConfig.operations = [];
				listConfig.toolbar = [];
				selectedId.value = "";
				colSeq = 0;
				filterSeq = 0;
				opSeq = 0;
				ElMessage.success("已重置画布");
			}).catch(() => {});
		}
		function goToList() {
			router.push("/lowcode/list-list");
		}
		const currentItems = computed(() => {
			switch (activeTab.value) {
				case "column": return listConfig.columns.map((c) => ({
					id: c.id,
					type: c.type,
					label: c.label,
					prop: c.prop
				}));
				case "filter": return (listConfig.filters || []).map((f) => ({
					id: f.id,
					type: f.type,
					label: f.label,
					prop: f.prop
				}));
				case "operation": return (listConfig.operations || []).map((o) => ({
					id: o.id,
					type: o.action,
					label: o.label
				}));
				case "toolbar": return (listConfig.toolbar || []).map((o) => ({
					id: o.id,
					type: o.action,
					label: o.label
				}));
				default: return [];
			}
		});
		const editId = route.query.id ? Number(route.query.id) : 0;
		if (editId > 0) loadList(editId);
		else {
			listConfig.title = "未命名列表";
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
			}
			history.reset(JSON.parse(JSON.stringify(listConfig)));
		}
		onMounted(async () => {
			window.addEventListener("keydown", onUndoRedoKeydown);
			try {
				await initBuiltinComponents();
				registryComponents.value = LowCodeComponentRegistry_default.list();
			} catch (_unused7) {}
		});
		onBeforeUnmount(() => {
			window.removeEventListener("keydown", onUndoRedoKeydown);
			if (historyDebounce) clearTimeout(historyDebounce);
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_checkbox = resolveComponent("el-checkbox");
			const _component_Plus = resolveComponent("Plus");
			const _component_el_button_group = resolveComponent("el-button-group");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_option_group = resolveComponent("el-option-group");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_slider = resolveComponent("el-slider");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					class: "toolbar-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [
						createVNode(_component_el_button, {
							type: "primary",
							icon: "Document",
							loading: loading.value,
							onClick: handleSave
						}, {
							default: withCtx(() => [..._cache[53] || (_cache[53] = [createTextVNode("保存草稿", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							type: "success",
							icon: "Promotion",
							onClick: handlePublish
						}, {
							default: withCtx(() => [..._cache[54] || (_cache[54] = [createTextVNode("发布", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Download",
							onClick: handleExport
						}, {
							default: withCtx(() => [..._cache[55] || (_cache[55] = [createTextVNode("导出", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Upload",
							onClick: handleImport
						}, {
							default: withCtx(() => [..._cache[56] || (_cache[56] = [createTextVNode("导入", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "View",
							onClick: handlePreview
						}, {
							default: withCtx(() => [..._cache[57] || (_cache[57] = [createTextVNode("预览", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "RefreshLeft",
							disabled: !unref(canUndo),
							onClick: undo
						}, {
							default: withCtx(() => [..._cache[58] || (_cache[58] = [createTextVNode("撤销", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "RefreshRight",
							disabled: !unref(canRedo),
							onClick: redo
						}, {
							default: withCtx(() => [..._cache[59] || (_cache[59] = [createTextVNode("重做", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: handleReset
						}, {
							default: withCtx(() => [..._cache[60] || (_cache[60] = [createTextVNode("重置", -1)])]),
							_: 1
						}),
						metaForm.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							icon: "FolderOpened",
							onClick: handleArchive
						}, {
							default: withCtx(() => [..._cache[61] || (_cache[61] = [createTextVNode("归档", -1)])]),
							_: 1
						})) : createCommentVNode("", true)
					]), createElementVNode("div", _hoisted_4, [createVNode(_component_el_tag, { type: metaForm.status === "PUBLISHED" ? "success" : metaForm.status === "ARCHIVED" ? "info" : "warning" }, {
						default: withCtx(() => [createTextVNode(toDisplayString(metaForm.status || "DRAFT"), 1)]),
						_: 1
					}, 8, ["type"]), createVNode(_component_el_button, {
						link: "",
						type: "primary",
						onClick: goToList
					}, {
						default: withCtx(() => [..._cache[62] || (_cache[62] = [createTextVNode("返回列表", -1)])]),
						_: 1
					})])])]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "meta-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "metaFormRef",
						ref: metaFormRef,
						model: metaForm,
						rules: metaRules,
						inline: "",
						"label-width": "90px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "列表编码",
								prop: "code"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.code,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => metaForm.code = $event),
									placeholder: "如：tpl_project_list",
									disabled: !!metaForm.id,
									style: { "width": "220px" }
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "列表名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.name,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => metaForm.name = $event),
									placeholder: "请输入列表名称",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "业务类型" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.bizType,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => metaForm.bizType = $event),
									placeholder: "如：PROJECT",
									style: { "width": "160px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "描述" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.description,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => metaForm.description = $event),
									placeholder: "列表描述",
									style: { "width": "320px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}),
				createElementVNode("div", _hoisted_5, [
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-left",
						"body-style": { padding: "8px" }
					}, {
						header: withCtx(() => [..._cache[63] || (_cache[63] = [createElementVNode("span", { class: "panel-title" }, "组件库", -1)])]),
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value, (group) => {
							return openBlock(), createElementBlock("div", {
								key: group.title,
								class: "comp-group"
							}, [createElementVNode("div", _hoisted_6, toDisplayString(group.title), 1), createElementVNode("div", _hoisted_7, [(openBlock(true), createElementBlock(Fragment, null, renderList(group.items, (comp) => {
								return openBlock(), createElementBlock("div", {
									key: comp.type + comp.category,
									class: "comp-item",
									draggable: "true",
									onDragstart: ($event) => onCompDragStart($event, comp),
									onClick: ($event) => addComponent(comp)
								}, [createVNode(_component_el_icon, null, {
									default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(comp.icon)))]),
									_: 2
								}, 1024), createElementVNode("span", null, toDisplayString(comp.label), 1)], 40, _hoisted_8);
							}), 128))])]);
						}), 128))]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-center",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [createElementVNode("div", _hoisted_9, [
							createVNode(_component_el_tabs, {
								modelValue: activeTab.value,
								"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => activeTab.value = $event),
								type: "border-card",
								class: "canvas-tabs"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_tab_pane, {
										label: "列配置",
										name: "column"
									}),
									createVNode(_component_el_tab_pane, {
										label: "筛选配置",
										name: "filter"
									}),
									createVNode(_component_el_tab_pane, {
										label: "操作配置",
										name: "operation"
									}),
									createVNode(_component_el_tab_pane, {
										label: "工具栏配置",
										name: "toolbar"
									})
								]),
								_: 1
							}, 8, ["modelValue"]),
							createVNode(_component_el_form, {
								inline: "",
								size: "small",
								class: "canvas-config"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_form_item, { label: "查询接口" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: listConfig.searchApi,
											"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => listConfig.searchApi = $event),
											placeholder: "/api/project",
											style: { "width": "200px" }
										}, null, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, { label: "方法" }, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: listConfig.method,
											"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => listConfig.method = $event),
											style: { "width": "90px" }
										}, {
											default: withCtx(() => [createVNode(_component_el_option, {
												label: "GET",
												value: "GET"
											}), createVNode(_component_el_option, {
												label: "POST",
												value: "POST"
											})]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, { label: "每页条数" }, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: listConfig.pageSize,
											"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => listConfig.pageSize = $event),
											min: 1,
											max: 500,
											style: { "width": "100px" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})
								]),
								_: 1
							}),
							createElementVNode("div", _hoisted_10, [
								createVNode(_component_el_checkbox, {
									modelValue: listConfig.showSelection,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => listConfig.showSelection = $event)
								}, {
									default: withCtx(() => [..._cache[64] || (_cache[64] = [createTextVNode("多选", -1)])]),
									_: 1
								}, 8, ["modelValue"]),
								createVNode(_component_el_checkbox, {
									modelValue: listConfig.showIndex,
									"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => listConfig.showIndex = $event)
								}, {
									default: withCtx(() => [..._cache[65] || (_cache[65] = [createTextVNode("序号", -1)])]),
									_: 1
								}, 8, ["modelValue"]),
								createVNode(_component_el_checkbox, {
									modelValue: listConfig.showPagination,
									"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => listConfig.showPagination = $event)
								}, {
									default: withCtx(() => [..._cache[66] || (_cache[66] = [createTextVNode("分页", -1)])]),
									_: 1
								}, 8, ["modelValue"]),
								createVNode(_component_el_checkbox, {
									modelValue: listConfig.stripe,
									"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => listConfig.stripe = $event)
								}, {
									default: withCtx(() => [..._cache[67] || (_cache[67] = [createTextVNode("斑马纹", -1)])]),
									_: 1
								}, 8, ["modelValue"]),
								createVNode(_component_el_checkbox, {
									modelValue: listConfig.border,
									"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => listConfig.border = $event)
								}, {
									default: withCtx(() => [..._cache[68] || (_cache[68] = [createTextVNode("边框", -1)])]),
									_: 1
								}, 8, ["modelValue"])
							])
						])]),
						default: withCtx(() => [createElementVNode("div", {
							class: normalizeClass(["canvas-dropzone", { empty: currentItems.value.length === 0 }]),
							onDragover: onCanvasDragOver,
							onDrop: onCanvasDrop
						}, [currentItems.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_11, [createVNode(_component_el_icon, { size: 40 }, {
							default: withCtx(() => [createVNode(_component_Plus)]),
							_: 1
						}), createElementVNode("p", null, "从左侧拖拽" + toDisplayString(activeTab.value === "column" ? "列" : activeTab.value === "filter" ? "筛选" : "操作") + "组件到此处", 1)])) : (openBlock(), createElementBlock("div", _hoisted_12, [(openBlock(true), createElementBlock(Fragment, null, renderList(currentItems.value, (item, idx) => {
							return openBlock(), createElementBlock("div", {
								key: item.id,
								class: normalizeClass(["item-card", { active: selectedId.value === item.id }]),
								draggable: "true",
								onDragstart: ($event) => onItemDragStart($event, item.id),
								onDragover: onCanvasDragOver,
								onDrop: ($event) => onItemDrop($event, item.id),
								onClick: ($event) => selectItem(item.id)
							}, [createElementVNode("div", _hoisted_14, [
								createVNode(_component_el_tag, {
									size: "small",
									type: "info"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(item.type), 1)]),
									_: 2
								}, 1024),
								createElementVNode("span", _hoisted_15, toDisplayString(item.label), 1),
								item.prop ? (openBlock(), createElementBlock("span", _hoisted_16, toDisplayString(item.prop), 1)) : createCommentVNode("", true),
								createElementVNode("div", _hoisted_17, [createVNode(_component_el_button_group, { size: "small" }, {
									default: withCtx(() => [
										createVNode(_component_el_button, {
											icon: "Top",
											disabled: idx === 0,
											onClick: withModifiers(($event) => moveItem(item.id, -1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "Bottom",
											disabled: idx === currentItems.value.length - 1,
											onClick: withModifiers(($event) => moveItem(item.id, 1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "CopyDocument",
											onClick: withModifiers(($event) => duplicateItem(item.id), ["stop"])
										}, null, 8, ["onClick"]),
										createVNode(_component_el_button, {
											icon: "Delete",
											type: "danger",
											onClick: withModifiers(($event) => removeItem(item.id), ["stop"])
										}, null, 8, ["onClick"])
									]),
									_: 2
								}, 1024)])
							])], 42, _hoisted_13);
						}), 128))]))], 34)]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-right",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [..._cache[69] || (_cache[69] = [createElementVNode("span", { class: "panel-title" }, "属性面板", -1)])]),
						default: withCtx(() => [!selectedItem.value ? (openBlock(), createElementBlock("div", _hoisted_18, [createVNode(_component_el_empty, {
							description: "请选择一个配置项",
							"image-size": 80
						})])) : activeTab.value === "column" && selectedColumn.value ? (openBlock(), createBlock(_component_el_form, {
							key: 1,
							model: selectedColumn.value,
							"label-width": "90px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[70] || (_cache[70] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "列类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedColumn.value.type,
										"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => selectedColumn.value.type = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [createVNode(_component_el_option_group, { label: "列组件" }, {
											default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value[0].items, (c) => {
												return openBlock(), createBlock(_component_el_option, {
													key: c.type,
													label: c.label,
													value: c.type
												}, null, 8, ["label", "value"]);
											}), 128))]),
											_: 1
										}), registryComponents.value.length ? (openBlock(), createBlock(_component_el_option_group, {
											key: 0,
											label: "业务组件"
										}, {
											default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(registryComponents.value, (meta) => {
												return openBlock(), createBlock(_component_el_option, {
													key: meta.name,
													label: meta.displayName,
													value: unref(ColumnType).CUSTOM
												}, null, 8, ["label", "value"]);
											}), 128))]),
											_: 1
										})) : createCommentVNode("", true)]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								selectedColumn.value.type === unref(ColumnType).CUSTOM ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "业务组件"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedColumn.value.componentName,
										"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => selectedColumn.value.componentName = $event),
										placeholder: "选择业务组件",
										clearable: "",
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(registryComponents.value, (meta) => {
											return openBlock(), createBlock(_component_el_option, {
												key: meta.name,
												label: `${meta.displayName} (${meta.name})`,
												value: meta.name
											}, null, 8, ["label", "value"]);
										}), 128))]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_form_item, { label: "列标题" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedColumn.value.label,
										"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => selectedColumn.value.label = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "字段名" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedColumn.value.prop,
										"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => selectedColumn.value.prop = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "列宽(px)" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: selectedColumn.value.width,
										"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => selectedColumn.value.width = $event),
										min: 50,
										max: 500,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "最小列宽" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: selectedColumn.value.minWidth,
										"onUpdate:modelValue": _cache[18] || (_cache[18] = ($event) => selectedColumn.value.minWidth = $event),
										min: 50,
										max: 500,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "对齐" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedColumn.value.align,
										"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => selectedColumn.value.align = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "左",
												value: "left"
											}),
											createVNode(_component_el_option, {
												label: "中",
												value: "center"
											}),
											createVNode(_component_el_option, {
												label: "右",
												value: "right"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "固定列" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedColumn.value.fixed,
										"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => selectedColumn.value.fixed = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "不固定",
												value: false
											}),
											createVNode(_component_el_option, {
												label: "左",
												value: "left"
											}),
											createVNode(_component_el_option, {
												label: "右",
												value: "right"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "可排序" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedColumn.value.sortable,
										"onUpdate:modelValue": _cache[21] || (_cache[21] = ($event) => selectedColumn.value.sortable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "隐藏列" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedColumn.value.hidden,
										"onUpdate:modelValue": _cache[22] || (_cache[22] = ($event) => selectedColumn.value.hidden = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[71] || (_cache[71] = [createTextVNode("类型属性", -1)])]),
									_: 1
								}),
								selectedColumn.value.type === unref(ColumnType).DICT ? (openBlock(), createBlock(_component_el_form_item, {
									key: 1,
									label: "字典编码"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedColumn.value.dictCode,
										"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => selectedColumn.value.dictCode = $event),
										placeholder: "如：project_status"
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								selectedColumn.value.type === unref(ColumnType).IMAGE ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [createVNode(_component_el_form_item, { label: "图片宽" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: selectedColumn.value.imageWidth,
										"onUpdate:modelValue": _cache[24] || (_cache[24] = ($event) => selectedColumn.value.imageWidth = $event),
										min: 20,
										max: 200,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}), createVNode(_component_el_form_item, { label: "图片高" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: selectedColumn.value.imageHeight,
										"onUpdate:modelValue": _cache[25] || (_cache[25] = ($event) => selectedColumn.value.imageHeight = $event),
										min: 20,
										max: 200,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})], 64)) : createCommentVNode("", true),
								selectedColumn.value.type === unref(ColumnType).LINK ? (openBlock(), createBlock(_component_el_form_item, {
									key: 3,
									label: "跳转地址"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedColumn.value.linkUrl,
										"onUpdate:modelValue": _cache[26] || (_cache[26] = ($event) => selectedColumn.value.linkUrl = $event),
										placeholder: "/project/detail/{id}"
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								selectedColumn.value.type === unref(ColumnType).TAG ? (openBlock(), createBlock(_component_el_form_item, {
									key: 4,
									label: "标签类型"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedColumn.value.tagType,
										"onUpdate:modelValue": _cache[27] || (_cache[27] = ($event) => selectedColumn.value.tagType = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "主要",
												value: "primary"
											}),
											createVNode(_component_el_option, {
												label: "成功",
												value: "success"
											}),
											createVNode(_component_el_option, {
												label: "警告",
												value: "warning"
											}),
											createVNode(_component_el_option, {
												label: "危险",
												value: "danger"
											}),
											createVNode(_component_el_option, {
												label: "信息",
												value: "info"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_form_item, { label: "格式化器" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedColumn.value.formatter,
										"onUpdate:modelValue": _cache[28] || (_cache[28] = ($event) => selectedColumn.value.formatter = $event),
										placeholder: "dateFormat:YYYY-MM-DD 或 currency:¥"
									}, null, 8, ["modelValue"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["model"])) : activeTab.value === "filter" && selectedFilter.value ? (openBlock(), createBlock(_component_el_form, {
							key: 2,
							model: selectedFilter.value,
							"label-width": "90px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[72] || (_cache[72] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "筛选类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedFilter.value.type,
										"onUpdate:modelValue": _cache[29] || (_cache[29] = ($event) => selectedFilter.value.type = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [createVNode(_component_el_option_group, { label: "筛选组件" }, {
											default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value[1].items, (c) => {
												return openBlock(), createBlock(_component_el_option, {
													key: c.type,
													label: c.label,
													value: c.type
												}, null, 8, ["label", "value"]);
											}), 128))]),
											_: 1
										})]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedFilter.value.label,
										"onUpdate:modelValue": _cache[30] || (_cache[30] = ($event) => selectedFilter.value.label = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "字段名" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedFilter.value.prop,
										"onUpdate:modelValue": _cache[31] || (_cache[31] = ($event) => selectedFilter.value.prop = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "占位符" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedFilter.value.placeholder,
										"onUpdate:modelValue": _cache[32] || (_cache[32] = ($event) => selectedFilter.value.placeholder = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "默认值" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedFilter.value.defaultValue,
										"onUpdate:modelValue": _cache[33] || (_cache[33] = ($event) => selectedFilter.value.defaultValue = $event),
										placeholder: "留空表示无默认值"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "响应式栅格" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: isFilterResponsive.value,
										"onUpdate:modelValue": _cache[34] || (_cache[34] = ($event) => isFilterResponsive.value = $event)
									}, null, 8, ["modelValue"]), _cache[73] || (_cache[73] = createElementVNode("span", { class: "form-tip" }, "开启后按 xs/sm/md/lg/xl 五档断点配置", -1))]),
									_: 1
								}),
								!isFilterResponsive.value ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "栅格宽度"
								}, {
									default: withCtx(() => [createVNode(_component_el_slider, {
										modelValue: filterSpan.value,
										"onUpdate:modelValue": _cache[35] || (_cache[35] = ($event) => filterSpan.value = $event),
										min: 1,
										max: 24,
										"show-input": "",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_collapse, {
									key: 1,
									modelValue: filterResponsiveCollapse.value,
									"onUpdate:modelValue": _cache[36] || (_cache[36] = ($event) => filterResponsiveCollapse.value = $event),
									class: "resp-collapse"
								}, {
									default: withCtx(() => [createVNode(_component_el_collapse_item, {
										title: "响应式断点（1-24，留空继承更小断点）",
										name: "resp"
									}, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(unref(BREAKPOINT_ORDER), (bp) => {
											return openBlock(), createBlock(_component_el_form_item, {
												key: bp,
												label: bp
											}, {
												default: withCtx(() => [createElementVNode("div", _hoisted_19, [createVNode(_component_el_input_number, {
													"model-value": getFilterBreakpoint(bp),
													min: 1,
													max: 24,
													"controls-position": "right",
													placeholder: "留空",
													style: { "flex": "1" },
													"onUpdate:modelValue": (v) => setFilterBreakpoint(bp, v !== null && v !== void 0 ? v : void 0)
												}, null, 8, ["model-value", "onUpdate:modelValue"]), getFilterBreakpoint(bp) !== void 0 ? (openBlock(), createBlock(_component_el_button, {
													key: 0,
													link: "",
													type: "primary",
													icon: "Close",
													title: "清除（留空，继承更小断点）",
													onClick: ($event) => setFilterBreakpoint(bp, void 0)
												}, null, 8, ["onClick"])) : createCommentVNode("", true)])]),
												_: 2
											}, 1032, ["label"]);
										}), 128))]),
										_: 1
									})]),
									_: 1
								}, 8, ["modelValue"])),
								createVNode(_component_el_form_item, { label: "可清空" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedFilter.value.clearable,
										"onUpdate:modelValue": _cache[37] || (_cache[37] = ($event) => selectedFilter.value.clearable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								selectedFilter.value.type === unref(FilterType).SELECT ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [
									createVNode(_component_el_divider, { "content-position": "left" }, {
										default: withCtx(() => [..._cache[74] || (_cache[74] = [createTextVNode("选项配置", -1)])]),
										_: 1
									}),
									createVNode(_component_el_form_item, { label: "字典编码" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: selectedFilter.value.dictCode,
											"onUpdate:modelValue": _cache[38] || (_cache[38] = ($event) => selectedFilter.value.dictCode = $event),
											placeholder: "留空时使用下方选项"
										}, null, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, { label: "多选" }, {
										default: withCtx(() => [createVNode(_component_el_switch, {
											modelValue: selectedFilter.value.multiple,
											"onUpdate:modelValue": _cache[39] || (_cache[39] = ($event) => selectedFilter.value.multiple = $event)
										}, null, 8, ["modelValue"])]),
										_: 1
									}),
									(openBlock(true), createElementBlock(Fragment, null, renderList(selectedFilter.value.options, (opt, idx) => {
										return openBlock(), createElementBlock("div", {
											key: idx,
											class: "option-row"
										}, [
											createVNode(_component_el_input, {
												modelValue: opt.label,
												"onUpdate:modelValue": ($event) => opt.label = $event,
												placeholder: "标签",
												size: "small",
												style: { "width": "40%" }
											}, null, 8, ["modelValue", "onUpdate:modelValue"]),
											createVNode(_component_el_input, {
												modelValue: opt.value,
												"onUpdate:modelValue": ($event) => opt.value = $event,
												placeholder: "值",
												size: "small",
												style: {
													"width": "40%",
													"margin-left": "4px"
												}
											}, null, 8, ["modelValue", "onUpdate:modelValue"]),
											createVNode(_component_el_button, {
												icon: "Delete",
												type: "danger",
												size: "small",
												style: { "margin-left": "4px" },
												onClick: ($event) => removeFilterOption(selectedFilter.value, idx)
											}, null, 8, ["onClick"])
										]);
									}), 128)),
									createVNode(_component_el_button, {
										icon: "Plus",
										size: "small",
										onClick: _cache[40] || (_cache[40] = ($event) => addFilterOption(selectedFilter.value))
									}, {
										default: withCtx(() => [..._cache[75] || (_cache[75] = [createTextVNode("添加选项", -1)])]),
										_: 1
									})
								], 64)) : createCommentVNode("", true)
							]),
							_: 1
						}, 8, ["model"])) : activeTab.value === "operation" && selectedOperation.value || activeTab.value === "toolbar" && selectedToolbar.value ? (openBlock(), createBlock(_component_el_form, {
							key: 3,
							model: activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value,
							"label-width": "90px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[76] || (_cache[76] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "按钮文本" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).label,
										"onUpdate:modelValue": _cache[41] || (_cache[41] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).label = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "按钮类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).type,
										"onUpdate:modelValue": _cache[42] || (_cache[42] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).type = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "主要",
												value: "primary"
											}),
											createVNode(_component_el_option, {
												label: "成功",
												value: "success"
											}),
											createVNode(_component_el_option, {
												label: "警告",
												value: "warning"
											}),
											createVNode(_component_el_option, {
												label: "危险",
												value: "danger"
											}),
											createVNode(_component_el_option, {
												label: "信息",
												value: "info"
											}),
											createVNode(_component_el_option, {
												label: "文本",
												value: "text"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "图标" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).icon,
										"onUpdate:modelValue": _cache[43] || (_cache[43] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).icon = $event),
										placeholder: "Element Plus 图标名"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "动作类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).action,
										"onUpdate:modelValue": _cache[44] || (_cache[44] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).action = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "新建",
												value: "create"
											}),
											createVNode(_component_el_option, {
												label: "编辑",
												value: "edit"
											}),
											createVNode(_component_el_option, {
												label: "查看",
												value: "view"
											}),
											createVNode(_component_el_option, {
												label: "删除",
												value: "delete"
											}),
											createVNode(_component_el_option, {
												label: "自定义",
												value: "custom"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "跳转地址" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).url,
										"onUpdate:modelValue": _cache[45] || (_cache[45] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).url = $event),
										placeholder: "/project/edit/{id}"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "调用接口" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).api,
										"onUpdate:modelValue": _cache[46] || (_cache[46] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).api = $event),
										placeholder: "/api/project/{id}"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "接口方法" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).method,
										"onUpdate:modelValue": _cache[47] || (_cache[47] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).method = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "GET",
												value: "GET"
											}),
											createVNode(_component_el_option, {
												label: "POST",
												value: "POST"
											}),
											createVNode(_component_el_option, {
												label: "PUT",
												value: "PUT"
											}),
											createVNode(_component_el_option, {
												label: "DELETE",
												value: "DELETE"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "确认提示" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).confirm,
										"onUpdate:modelValue": _cache[48] || (_cache[48] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).confirm = $event),
										placeholder: "如：确认删除？"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "权限标识" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).permission,
										"onUpdate:modelValue": _cache[49] || (_cache[49] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).permission = $event),
										placeholder: "如：project:update"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								activeTab.value === "operation" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "显示条件"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).visible,
										"onUpdate:modelValue": _cache[50] || (_cache[50] = ($event) => (activeTab.value === "operation" ? selectedOperation.value : selectedToolbar.value).visible = $event),
										placeholder: "如：row.status === 'DRAFT'"
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true)
							]),
							_: 1
						}, 8, ["model"])) : createCommentVNode("", true)]),
						_: 1
					})
				]),
				createVNode(_component_el_dialog, {
					modelValue: previewVisible.value,
					"onUpdate:modelValue": _cache[52] || (_cache[52] = ($event) => previewVisible.value = $event),
					title: "列表预览",
					width: "90%",
					top: "3vh",
					"close-on-click-modal": false,
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[51] || (_cache[51] = ($event) => previewVisible.value = false) }, {
						default: withCtx(() => [..._cache[77] || (_cache[77] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(LowCodeListRenderer_default, {
						config: listConfig,
						"auto-fetch": false,
						data: [{
							id: 1,
							projectCode: "P2025001",
							projectName: "示例项目",
							status: "IN_PROGRESS",
							amount: 1e5,
							progress: .65,
							createTime: "2025-01-01 10:00:00"
						}],
						"dict-map": {}
					}, null, 8, ["config"])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-5ee5f6fa"]]);
//#endregion
export { list_designer_default as default };

//# sourceMappingURL=list-designer-DIR2PhPx.js.map