import { d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { $ as updateTab, I as getTab, V as importTab, Y as publishTab, d as TabPosition, f as TabsType, g as archiveTab, k as exportTab, u as TabPageType, x as createTab } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as LowCodeTabRenderer_default } from "./LowCodeTabRenderer-Cgh2BbMb.js";
import { t as useUndoRedo } from "./useUndoRedo-C9SCn4rB.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDynamicComponent, toDisplayString, unref, watch, withCtx, withModifiers } from "vue";
//#region src/views/lowcode/tab-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "tab-designer" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "toolbar-left" };
var _hoisted_4 = { class: "toolbar-right" };
var _hoisted_5 = { class: "designer-body" };
var _hoisted_6 = { class: "comp-group-title" };
var _hoisted_7 = { class: "comp-items" };
var _hoisted_8 = ["onDragstart", "onClick"];
var _hoisted_9 = { class: "canvas-header" };
var _hoisted_10 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_11 = {
	key: 1,
	class: "item-list"
};
var _hoisted_12 = [
	"onDragstart",
	"onDrop",
	"onClick"
];
var _hoisted_13 = { class: "item-card-header" };
var _hoisted_14 = { class: "item-label" };
var _hoisted_15 = { class: "item-prop" };
var _hoisted_16 = {
	key: 0,
	class: "item-prop"
};
var _hoisted_17 = { class: "item-actions" };
var _hoisted_18 = { class: "item-card-meta" };
var _hoisted_19 = {
	key: 0,
	class: "empty-prop"
};
var _hoisted_20 = { class: "props-list" };
/** 组件库分组 */
var HISTORY_DEBOUNCE_MS = 400;
//#endregion
//#region src/views/lowcode/tab-designer/index.vue
var tab_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		/**
		* 低代码标签页设计器。
		*
		* <p>三栏布局：</p>
		* <ul>
		*   <li>左侧：组件库面板（Tab Item 组件，按 pageType 分组）</li>
		*   <li>中间：画布（Tab 顶层配置 + Tab 列表，可拖拽排序，
		*       每个卡片显示 title/name/pageCode/pageType）</li>
		*   <li>右侧：属性面板（选中 Tab 的 title/name/icon/pageCode/pageType/pageUrl/lazy/disabled/visible/props）</li>
		* </ul>
		*
		* <p>顶部操作栏：保存草稿 / 发布 / 归档 / 导入 / 导出 / 预览 / 重置，以及元信息编辑。</p>
		*
		* <p>使用原生 HTML5 拖拽 API（draggable + dragstart/dragover/drop）实现，
		* 支持组件库 → 画布拖拽、画布内 Tab 排序拖拽两种交互。Tab ID 自动生成
		* （tab_N），点击"预览"打开 LowCodeTabRenderer 弹窗。</p>
		*/
		const route = useRoute();
		const router = useRouter();
		const componentGroups = ref([{
			title: "标签页组件（按引用页面类型）",
			items: [
				{
					type: TabPageType.FORM,
					label: "表单 Tab",
					icon: "Document",
					defaultProps: {
						pageType: TabPageType.FORM,
						lazy: true
					}
				},
				{
					type: TabPageType.LIST,
					label: "列表 Tab",
					icon: "List",
					defaultProps: {
						pageType: TabPageType.LIST,
						lazy: true
					}
				},
				{
					type: TabPageType.RELATED_PAGE,
					label: "关联页 Tab",
					icon: "Share",
					defaultProps: {
						pageType: TabPageType.RELATED_PAGE,
						lazy: true
					}
				},
				{
					type: TabPageType.CUSTOM,
					label: "自定义 Tab",
					icon: "Setting",
					defaultProps: {
						pageType: TabPageType.CUSTOM,
						lazy: true,
						pageUrl: ""
					}
				}
			]
		}]);
		/** 标签页元信息（对应 LowCodeTabConfig 的非 tabConfig 字段） */
		const metaForm = reactive({
			code: "",
			name: "",
			description: "",
			tabConfig: "",
			status: "DRAFT",
			bizType: "",
			version: 1
		});
		/** 设计器内部维护的 TabConfig 对象 */
		const tabConfig = reactive({
			title: "",
			description: "",
			type: TabsType.BORDER_CARD,
			tabPosition: TabPosition.TOP,
			closable: false,
			addable: false,
			editable: false,
			tabs: []
		});
		/** 当前选中项 id（tab 的 id） */
		const selectedId = ref("");
		/** 各类型 ID 计数器 */
		let tabSeq = 0;
		/** 元信息表单 ref */
		const metaFormRef = ref();
		const metaRules = {
			code: [{
				required: true,
				message: "请输入标签页编码",
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入标签页名称",
				trigger: "blur"
			}]
		};
		const loading = ref(false);
		const previewVisible = ref(false);
		/** 当前选中的标签项 */
		const selectedTab = computed(() => tabConfig.tabs.find((t) => t.id === selectedId.value) || null);
		/** 生成标签项对象 */
		function createTabItem(comp) {
			tabSeq++;
			const idx = tabSeq;
			return {
				id: `tab_${idx}`,
				title: `标签${idx}`,
				name: `tab${idx}`,
				lazy: true,
				disabled: false,
				pageType: comp.type,
				...comp.defaultProps || {}
			};
		}
		/** 添加项（点击组件库或拖拽放置） */
		function addComponent(comp) {
			const tab = createTabItem(comp);
			tabConfig.tabs.push(tab);
			selectedId.value = tab.id;
		}
		/** 删除项 */
		function removeItem(id) {
			tabConfig.tabs = tabConfig.tabs.filter((t) => t.id !== id);
			if (selectedId.value === id) selectedId.value = "";
		}
		/** 复制项 */
		function duplicateItem(id) {
			const tab = tabConfig.tabs.find((t) => t.id === id);
			if (!tab) return;
			tabSeq++;
			const copy = JSON.parse(JSON.stringify(tab));
			copy.id = `tab_${tabSeq}`;
			copy.name = `${tab.name}_copy`;
			copy.title = `${tab.title}_副本`;
			const idx = tabConfig.tabs.findIndex((t) => t.id === id);
			tabConfig.tabs.splice(idx + 1, 0, copy);
			selectedId.value = copy.id;
		}
		/** 上下移动 */
		function moveItem(id, direction) {
			const arr = tabConfig.tabs;
			const idx = arr.findIndex((t) => t.id === id);
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
		/** 画布 drop：组件库 → 添加 */
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
			const arr = tabConfig.tabs;
			const fromIdx = arr.findIndex((t) => t.id === dragItemId);
			const toIdx = arr.findIndex((t) => t.id === targetId);
			if (fromIdx < 0 || toIdx < 0) return;
			const [moved] = arr.splice(fromIdx, 1);
			arr.splice(toIdx, 0, moved);
			dragItemId = "";
		}
		function addTabProp(tab) {
			if (!tab.props) tab.props = {};
			const key = `prop_${Object.keys(tab.props).length + 1}`;
			tab.props[key] = "";
		}
		function removeTabProp(tab, key) {
			if (tab.props) delete tab.props[key];
		}
		function syncTabConfigToStr() {
			metaForm.tabConfig = JSON.stringify(tabConfig, null, 2);
		}
		function parseTabConfigFromStr() {
			try {
				var _parsed$title, _parsed$description, _parsed$type, _parsed$tabPosition, _parsed$closable, _parsed$addable, _parsed$editable;
				if (!metaForm.tabConfig) {
					tabConfig.tabs = [];
					return;
				}
				const parsed = JSON.parse(metaForm.tabConfig);
				tabConfig.title = (_parsed$title = parsed.title) !== null && _parsed$title !== void 0 ? _parsed$title : "";
				tabConfig.description = (_parsed$description = parsed.description) !== null && _parsed$description !== void 0 ? _parsed$description : "";
				tabConfig.type = (_parsed$type = parsed.type) !== null && _parsed$type !== void 0 ? _parsed$type : TabsType.BORDER_CARD;
				tabConfig.tabPosition = (_parsed$tabPosition = parsed.tabPosition) !== null && _parsed$tabPosition !== void 0 ? _parsed$tabPosition : TabPosition.TOP;
				tabConfig.closable = (_parsed$closable = parsed.closable) !== null && _parsed$closable !== void 0 ? _parsed$closable : false;
				tabConfig.addable = (_parsed$addable = parsed.addable) !== null && _parsed$addable !== void 0 ? _parsed$addable : false;
				tabConfig.editable = (_parsed$editable = parsed.editable) !== null && _parsed$editable !== void 0 ? _parsed$editable : false;
				tabConfig.tabs = parsed.tabs || [];
				tabSeq = 0;
				tabConfig.tabs.forEach((t) => {
					const m = /tab_(\d+)/.exec(t.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > tabSeq) tabSeq = n;
					}
				});
			} catch (e) {
				ElMessage.error("标签页配置 JSON 解析失败：" + e.message);
			}
		}
		/**
		* 撤销/重做历史栈：对整个 tabConfig 做 JSON 快照。
		*
		* <p>采用 watch 深度监听 tabConfig 自动推历史（400ms 防抖合并连续输入，
		* 避免 50 步栈被逐字符吞掉）；undo/redo 时反向同步快照回 reactive，
		* 保持 tabConfig 引用不变以兼容现有 UI 双向绑定。</p>
		*/
		const history = useUndoRedo(JSON.parse(JSON.stringify(tabConfig)));
		const { present: historyPresent, canUndo, canRedo } = history;
		/** 抑制标志：undo/redo 同步快照回 tabConfig 时关闭 watch 推历史，避免循环 */
		let suppressHistory = false;
		/** 防抖计时器：连续输入合并为一次历史入栈 */
		let historyDebounce = null;
		function commitPendingHistory() {
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(tabConfig)));
			}
		}
		watch(tabConfig, () => {
			if (suppressHistory) return;
			if (historyDebounce) clearTimeout(historyDebounce);
			historyDebounce = setTimeout(() => {
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(tabConfig)));
			}, HISTORY_DEBOUNCE_MS);
		}, {
			deep: true,
			flush: "sync"
		});
		/** 将历史当前快照同步回 reactive tabConfig（保持引用不变，UI 自动更新） */
		function applyHistoryToTabConfig() {
			const snap = historyPresent.value;
			suppressHistory = true;
			try {
				const target = tabConfig;
				const src = snap;
				for (const key of Object.keys(target)) if (!(key in src)) delete target[key];
				for (const key of Object.keys(src)) target[key] = JSON.parse(JSON.stringify(src[key]));
				tabSeq = 0;
				for (const t of tabConfig.tabs) {
					const m = /tab_(\d+)/.exec(t.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > tabSeq) tabSeq = n;
					}
				}
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
			applyHistoryToTabConfig();
		}
		/** 重做 */
		function redo() {
			commitPendingHistory();
			if (!canRedo.value) return;
			history.redo();
			applyHistoryToTabConfig();
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
		async function loadTab(id) {
			loading.value = true;
			try {
				const data = await getTab(id);
				Object.assign(metaForm, data);
				parseTabConfigFromStr();
				if (historyDebounce) {
					clearTimeout(historyDebounce);
					historyDebounce = null;
				}
				history.reset(JSON.parse(JSON.stringify(tabConfig)));
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function handleSave() {
			if (!metaFormRef.value) return;
			await metaFormRef.value.validate(async (valid) => {
				if (!valid) return;
				if (tabConfig.tabs.length === 0) {
					ElMessage.warning("请至少添加一个标签");
					return;
				}
				syncTabConfigToStr();
				loading.value = true;
				try {
					if (metaForm.id) {
						await updateTab(metaForm.id, metaForm);
						ElMessage.success("保存成功");
					} else {
						const created = await createTab(metaForm);
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
			syncTabConfigToStr();
			loading.value = true;
			try {
				await updateTab(metaForm.id, metaForm);
				await publishTab(metaForm.id);
				metaForm.status = "PUBLISHED";
				ElMessage.success("发布成功");
			} catch (_unused3) {} finally {
				loading.value = false;
			}
		}
		async function handleArchive() {
			if (!metaForm.id) return;
			try {
				await ElMessageBox.confirm("确认归档此标签页？归档后不可再使用", "确认", { type: "warning" });
				await archiveTab(metaForm.id);
				metaForm.status = "ARCHIVED";
				ElMessage.success("归档成功");
			} catch (_unused4) {}
		}
		async function handleExport() {
			if (!metaForm.code) {
				ElMessage.warning("请先填写标签页编码");
				return;
			}
			syncTabConfigToStr();
			if (metaForm.id) try {
				await exportTab(metaForm.code);
				ElMessage.success("导出成功");
			} catch (_unused5) {}
			else {
				const blob = new Blob([JSON.stringify(metaForm, null, 2)], { type: "application/json" });
				const url = URL.createObjectURL(blob);
				const link = document.createElement("a");
				link.href = url;
				link.download = `tab-${metaForm.code}.json`;
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
					const imported = await importTab(text);
					ElMessage.success(`导入成功，编码：${imported.code}`);
					Object.assign(metaForm, imported);
					parseTabConfigFromStr();
				} catch (_unused6) {
					try {
						const parsed = JSON.parse(text);
						if (parsed.tabConfig && typeof parsed.tabConfig === "string") {
							Object.assign(metaForm, parsed);
							parseTabConfigFromStr();
							ElMessage.success("已加载到画布（本地解析，未提交后端）");
						} else if (Array.isArray(parsed.tabs)) {
							metaForm.tabConfig = text;
							parseTabConfigFromStr();
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
			syncTabConfigToStr();
			previewVisible.value = true;
		}
		function handleReset() {
			ElMessageBox.confirm("确认清空画布所有配置？此操作不可恢复", "确认", { type: "warning" }).then(() => {
				tabConfig.tabs = [];
				selectedId.value = "";
				tabSeq = 0;
				ElMessage.success("已重置画布");
			}).catch(() => {});
		}
		function goToList() {
			router.push("/lowcode/tab-list");
		}
		const editId = route.query.id ? Number(route.query.id) : 0;
		if (editId > 0) loadTab(editId);
		else {
			tabConfig.title = "未命名标签页";
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
			}
			history.reset(JSON.parse(JSON.stringify(tabConfig)));
		}
		onMounted(() => {
			window.addEventListener("keydown", onUndoRedoKeydown);
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
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_Plus = resolveComponent("Plus");
			const _component_el_button_group = resolveComponent("el-button-group");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_divider = resolveComponent("el-divider");
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
							default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("保存草稿", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							type: "success",
							icon: "Promotion",
							onClick: handlePublish
						}, {
							default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("发布", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Download",
							onClick: handleExport
						}, {
							default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("导出", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Upload",
							onClick: handleImport
						}, {
							default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("导入", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "View",
							onClick: handlePreview
						}, {
							default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("预览", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "RefreshLeft",
							disabled: !unref(canUndo),
							onClick: undo
						}, {
							default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("撤销", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "RefreshRight",
							disabled: !unref(canRedo),
							onClick: redo
						}, {
							default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("重做", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: handleReset
						}, {
							default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("重置", -1)])]),
							_: 1
						}),
						metaForm.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							icon: "FolderOpened",
							onClick: handleArchive
						}, {
							default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("归档", -1)])]),
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
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("返回列表", -1)])]),
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
								label: "标签页编码",
								prop: "code"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.code,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => metaForm.code = $event),
									placeholder: "如：tpl_project_detail_tabs",
									disabled: !!metaForm.id,
									style: { "width": "220px" }
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "标签页名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.name,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => metaForm.name = $event),
									placeholder: "请输入标签页名称",
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
									placeholder: "标签页描述",
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
						header: withCtx(() => [..._cache[31] || (_cache[31] = [createElementVNode("span", { class: "panel-title" }, "组件库", -1)])]),
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value, (group) => {
							return openBlock(), createElementBlock("div", {
								key: group.title,
								class: "comp-group"
							}, [createElementVNode("div", _hoisted_6, toDisplayString(group.title), 1), createElementVNode("div", _hoisted_7, [(openBlock(true), createElementBlock(Fragment, null, renderList(group.items, (comp) => {
								return openBlock(), createElementBlock("div", {
									key: comp.type,
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
						header: withCtx(() => [createElementVNode("div", _hoisted_9, [_cache[32] || (_cache[32] = createElementVNode("span", { class: "panel-title" }, "画布", -1)), createVNode(_component_el_form, {
							inline: "",
							size: "small",
							class: "canvas-config"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "el-tabs type" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: tabConfig.type,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => tabConfig.type = $event),
										style: { "width": "130px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "卡片 card",
												value: unref(TabsType).CARD
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "边框卡片 border-card",
												value: unref(TabsType).BORDER_CARD
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "无样式 plain",
												value: unref(TabsType).PLAIN
											}, null, 8, ["value"])
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签位置" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: tabConfig.tabPosition,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => tabConfig.tabPosition = $event),
										style: { "width": "90px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "顶部",
												value: unref(TabPosition).TOP
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "右",
												value: unref(TabPosition).RIGHT
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "底部",
												value: unref(TabPosition).BOTTOM
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "左",
												value: unref(TabPosition).LEFT
											}, null, 8, ["value"])
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "可关闭" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: tabConfig.closable,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => tabConfig.closable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "可新增" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: tabConfig.addable,
										"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => tabConfig.addable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "可编辑" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: tabConfig.editable,
										"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => tabConfig.editable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								})
							]),
							_: 1
						})])]),
						default: withCtx(() => [createElementVNode("div", {
							class: normalizeClass(["canvas-dropzone", { empty: tabConfig.tabs.length === 0 }]),
							onDragover: onCanvasDragOver,
							onDrop: onCanvasDrop
						}, [tabConfig.tabs.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_10, [createVNode(_component_el_icon, { size: 40 }, {
							default: withCtx(() => [createVNode(_component_Plus)]),
							_: 1
						}), _cache[33] || (_cache[33] = createElementVNode("p", null, "从左侧拖拽标签组件到此处", -1))])) : (openBlock(), createElementBlock("div", _hoisted_11, [(openBlock(true), createElementBlock(Fragment, null, renderList(tabConfig.tabs, (tab, idx) => {
							return openBlock(), createElementBlock("div", {
								key: tab.id,
								class: normalizeClass(["item-card", { active: selectedId.value === tab.id }]),
								draggable: "true",
								onDragstart: ($event) => onItemDragStart($event, tab.id),
								onDragover: onCanvasDragOver,
								onDrop: ($event) => onItemDrop($event, tab.id),
								onClick: ($event) => selectItem(tab.id)
							}, [createElementVNode("div", _hoisted_13, [
								createVNode(_component_el_tag, {
									size: "small",
									type: "info"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(tab.pageType), 1)]),
									_: 2
								}, 1024),
								createElementVNode("span", _hoisted_14, toDisplayString(tab.title), 1),
								createElementVNode("span", _hoisted_15, "name: " + toDisplayString(tab.name), 1),
								tab.pageCode ? (openBlock(), createElementBlock("span", _hoisted_16, "pageCode: " + toDisplayString(tab.pageCode), 1)) : createCommentVNode("", true),
								createElementVNode("div", _hoisted_17, [createVNode(_component_el_button_group, { size: "small" }, {
									default: withCtx(() => [
										createVNode(_component_el_button, {
											icon: "Top",
											disabled: idx === 0,
											onClick: withModifiers(($event) => moveItem(tab.id, -1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "Bottom",
											disabled: idx === tabConfig.tabs.length - 1,
											onClick: withModifiers(($event) => moveItem(tab.id, 1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "CopyDocument",
											onClick: withModifiers(($event) => duplicateItem(tab.id), ["stop"])
										}, null, 8, ["onClick"]),
										createVNode(_component_el_button, {
											icon: "Delete",
											type: "danger",
											onClick: withModifiers(($event) => removeItem(tab.id), ["stop"])
										}, null, 8, ["onClick"])
									]),
									_: 2
								}, 1024)])
							]), createElementVNode("div", _hoisted_18, [
								tab.lazy ? (openBlock(), createBlock(_component_el_tag, {
									key: 0,
									size: "small"
								}, {
									default: withCtx(() => [..._cache[34] || (_cache[34] = [createTextVNode("lazy", -1)])]),
									_: 1
								})) : createCommentVNode("", true),
								tab.disabled ? (openBlock(), createBlock(_component_el_tag, {
									key: 1,
									size: "small",
									type: "warning"
								}, {
									default: withCtx(() => [..._cache[35] || (_cache[35] = [createTextVNode("disabled", -1)])]),
									_: 1
								})) : createCommentVNode("", true),
								tab.icon ? (openBlock(), createBlock(_component_el_tag, {
									key: 2,
									size: "small",
									type: "success"
								}, {
									default: withCtx(() => [createTextVNode("icon: " + toDisplayString(tab.icon), 1)]),
									_: 2
								}, 1024)) : createCommentVNode("", true),
								tab.visible ? (openBlock(), createBlock(_component_el_tag, {
									key: 3,
									size: "small",
									type: "info"
								}, {
									default: withCtx(() => [createTextVNode("visible: " + toDisplayString(tab.visible), 1)]),
									_: 2
								}, 1024)) : createCommentVNode("", true)
							])], 42, _hoisted_12);
						}), 128))]))], 34)]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-right",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [..._cache[36] || (_cache[36] = [createElementVNode("span", { class: "panel-title" }, "属性面板", -1)])]),
						default: withCtx(() => [!selectedTab.value ? (openBlock(), createElementBlock("div", _hoisted_19, [createVNode(_component_el_empty, {
							description: "请选择一个标签项",
							"image-size": 80
						})])) : (openBlock(), createBlock(_component_el_form, {
							key: 1,
							model: selectedTab.value,
							"label-width": "100px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[37] || (_cache[37] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签标题" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.title,
										"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => selectedTab.value.title = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签标识 name" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.name,
										"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => selectedTab.value.name = $event),
										placeholder: "用于 v-model 绑定，必填且唯一"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "Element 图标" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.icon,
										"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => selectedTab.value.icon = $event),
										placeholder: "Element Plus 图标名，如 Document"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "懒加载 lazy" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedTab.value.lazy,
										"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => selectedTab.value.lazy = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "禁用 disabled" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedTab.value.disabled,
										"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => selectedTab.value.disabled = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[38] || (_cache[38] = [createTextVNode("页面引用", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "页面类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedTab.value.pageType,
										"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => selectedTab.value.pageType = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "表单 form",
												value: unref(TabPageType).FORM
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "列表 list",
												value: unref(TabPageType).LIST
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "关联页 related-page",
												value: unref(TabPageType).RELATED_PAGE
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "自定义 custom",
												value: unref(TabPageType).CUSTOM
											}, null, 8, ["value"])
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								selectedTab.value.pageType !== "custom" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "页面编码"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.pageCode,
										"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => selectedTab.value.pageCode = $event),
										placeholder: "引用的低代码页面编码"
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								selectedTab.value.pageType === "custom" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 1,
									label: "页面 URL"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.pageUrl,
										"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => selectedTab.value.pageUrl = $event),
										placeholder: "/project/{id}/basic 或 http(s)://..."
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[39] || (_cache[39] = [createTextVNode("显示条件", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "visible 表达式" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedTab.value.visible,
										"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => selectedTab.value.visible = $event),
										type: "textarea",
										rows: 2,
										placeholder: "如：row.status === 'IN_PROGRESS'，留空表示始终显示"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[40] || (_cache[40] = [createTextVNode("props 传参", -1)])]),
									_: 1
								}),
								createElementVNode("div", _hoisted_20, [(openBlock(true), createElementBlock(Fragment, null, renderList(Object.keys(selectedTab.value.props || {}), (key) => {
									return openBlock(), createElementBlock("div", {
										key,
										class: "prop-row"
									}, [
										createVNode(_component_el_input, {
											modelValue: selectedTab.value.props[key],
											"onUpdate:modelValue": ($event) => selectedTab.value.props[key] = $event,
											placeholder: key,
											size: "small"
										}, null, 8, [
											"modelValue",
											"onUpdate:modelValue",
											"placeholder"
										]),
										createVNode(_component_el_input, {
											"model-value": key,
											disabled: "",
											size: "small",
											style: { "width": "30%" }
										}, null, 8, ["model-value"]),
										createVNode(_component_el_button, {
											icon: "Delete",
											type: "danger",
											size: "small",
											onClick: ($event) => removeTabProp(selectedTab.value, key)
										}, null, 8, ["onClick"])
									]);
								}), 128)), createVNode(_component_el_button, {
									icon: "Plus",
									size: "small",
									onClick: _cache[18] || (_cache[18] = ($event) => addTabProp(selectedTab.value))
								}, {
									default: withCtx(() => [..._cache[41] || (_cache[41] = [createTextVNode("添加 props", -1)])]),
									_: 1
								})])
							]),
							_: 1
						}, 8, ["model"]))]),
						_: 1
					})
				]),
				createVNode(_component_el_dialog, {
					modelValue: previewVisible.value,
					"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => previewVisible.value = $event),
					title: "标签页预览",
					width: "90%",
					top: "3vh",
					"close-on-click-modal": false,
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[19] || (_cache[19] = ($event) => previewVisible.value = false) }, {
						default: withCtx(() => [..._cache[42] || (_cache[42] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(LowCodeTabRenderer_default, {
						config: tabConfig,
						"context-data": {
							row: {
								id: 1,
								status: "IN_PROGRESS",
								projectId: "P2025001",
								warrantyStatus: "ACTIVE"
							},
							context: {}
						}
					}, null, 8, ["config"])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-405aa9e0"]]);
//#endregion
export { tab_designer_default as default };

//# sourceMappingURL=tab-designer-Dxeodnqq.js.map